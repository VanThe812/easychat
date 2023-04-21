package va.vanthe.app_chat_2.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityMainBinding;
import va.vanthe.app_chat_2.adapters.ViewPagerMenuAdapter;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;


    private PreferenceManager account;

    private static final int ACTION_CHAT = R.id.action_chat;
    private static final int ACTION_FRIEND = R.id.action_friend;
    private static final int ACTION_SETTING = R.id.action_setting;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        getToken();
    }
    private void init() {
        account = new PreferenceManager(getApplicationContext());
        checkLogin();
    }

    private void setListeners() {
        binding.bottomNav.setSelectedItemId(R.id.action_chat);

        ViewPagerMenuAdapter viewPagerMenuAdapter = new ViewPagerMenuAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPagerMenu.setAdapter(viewPagerMenuAdapter);
        binding.viewPagerMenu.setOffscreenPageLimit(3);
        binding.viewPagerMenu.setUserInputEnabled(false);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case ACTION_CHAT:
                    HelperFunction.hideSoftKeyboard(MainActivity.this);
                    binding.viewPagerMenu.setCurrentItem(0);
                    break;
                case ACTION_FRIEND:
                    HelperFunction.hideSoftKeyboard(MainActivity.this);
                    binding.viewPagerMenu.setCurrentItem(1);
                    break;
                case ACTION_SETTING:
                    HelperFunction.hideSoftKeyboard(MainActivity.this);
                    binding.viewPagerMenu.setCurrentItem(2);
                    break;
            }
            return true;
        });


    }
    private void checkLogin() {
        if(!account.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token) {
        account.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        try {
            DocumentReference documentReference = database.collection(Constants.KEY_USER).
                    document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
            documentReference.update(Constants.KEY_FCM_TOKEN, token)
                    .addOnFailureListener(e -> HelperFunction.showToast("Unable to update token", getApplicationContext()));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}