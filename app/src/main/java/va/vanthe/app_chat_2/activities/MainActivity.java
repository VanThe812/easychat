package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityMainBinding;
import va.vanthe.app_chat_2.adapters.ViewPagerMenuAdapter;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private ViewPagerMenuAdapter viewPagerMenuAdapter;
    private PreferenceManager account; //Bảng tài khoản trong db của máy


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        checkLogin();
        getToken();
        setListeners();

    }
    private void init() {
        account = new PreferenceManager(getApplicationContext());

    }

    private void checkLogin() {
        if(!account.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
    }



    private void setListeners() {
        binding.bottomNav.setSelectedItemId(R.id.action_chat);

        viewPagerMenuAdapter = new ViewPagerMenuAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPagerMenu.setAdapter(viewPagerMenuAdapter);
        binding.viewPagerMenu.setOffscreenPageLimit(2);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    hideSoftKeyboard();
                    binding.viewPagerMenu.setCurrentItem(0);
                    break;
                case R.id.action_friend:
                    hideSoftKeyboard();
                    binding.viewPagerMenu.setCurrentItem(1);
                    break;
                case R.id.action_setting:

//                    Drawable d = new BitmapDrawable(getResources(), changeStringToBitmap(account.getString(Constants.KEY_ACCOUNT_IMAGE)));
//                    item.setIcon(d);
                    hideSoftKeyboard();
                    binding.viewPagerMenu.setCurrentItem(2);
                    break;
            }
            return  true;
        });

//        binding.viewPagerMenu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                binding.viewPagerMenu.setOnTouchListener((view, motionEvent) -> {
//                    binding.viewPagerMenu.setCurrentItem(0);
//                    return true;
//                });
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

    }

    private Bitmap changeStringToBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                    .addOnFailureListener(e -> showToast("Unable to update token"));
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }



}