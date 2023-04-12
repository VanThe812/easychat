package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.adapters.UserChatAdapter;
import va.vanthe.app_chat_2.adapters.UserSearchAdapter;
import va.vanthe.app_chat_2.adapters.UsersAdapter;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.databinding.ActivitySearchBinding;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class SearchActivity extends BaseActivity implements UserListener {

    private ActivitySearchBinding binding;
    private PreferenceManager account;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        dataTest();
        dataTest2();
    }
    private void init() {
        account = new PreferenceManager(getApplicationContext());

    }
    private void setListeners() {
        binding.textviewCancel.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textSearch = editable.toString().trim();
                if (textSearch.matches("")) {
                    binding.layoutRecent.setVisibility(View.VISIBLE);
                    binding.layoutSuggest.setVisibility(View.VISIBLE);
                    binding.searchRCV.setVisibility(View.GONE);

                } else {
                    binding.layoutRecent.setVisibility(View.GONE);
                    binding.layoutSuggest.setVisibility(View.GONE);
                    binding.searchRCV.setVisibility(View.VISIBLE);

                    if (textSearch.length() > 11 && textSearch.substring(0, 3).equals("+84")) {
                        if (!textSearch.equals(account.getString(Constants.KEY_ACCOUNT_PHONE_NUMBER))) {
                            database.collection(Constants.KEY_USER)
                                    .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, textSearch)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot documentSnapshots) {
                                            List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                                            if (!documents.isEmpty()) {
                                                User user = documents.get(0).toObject(User.class);
                                                user.setId(documents.get(0).getId());

                                                UserSearchAdapter userSearchAdapter = new UserSearchAdapter(getApplicationContext(),user);

                                                binding.searchRCV.setAdapter(userSearchAdapter);
                                                binding.searchRCV.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                        } else {
                            // la chinh ban than mk
                        }

                    }
                    else {

                    }

                }
            }
        });

    }

    private void dataTest() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i<8; i++) {
            User user = new User();
            user.setFirstName("Vu Van");
            user.setLastName("The" + (i +1));
            user.setImage(account.getString(Constants.KEY_ACCOUNT_IMAGE));
            user.setId(""+i);
            users.add(user);
        }
        UserChatAdapter userChatAdapter = new UserChatAdapter(users, this);
        binding.recentSearchRCV.setAdapter(userChatAdapter);
        binding.recentSearchRCV.setVisibility(View.VISIBLE);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        binding.recentSearchRCV.setLayoutManager(gridLayoutManager);
    }
    private void dataTest2() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i<8; i++) {
            User user = new User();
            user.setFirstName("Vu Van");
            user.setLastName("The" + (i +1));
            user.setImage(account.getString(Constants.KEY_ACCOUNT_IMAGE));
            user.setId(""+i);
            users.add(user);
        }
        UsersAdapter usersAdapter = new UsersAdapter(users, this);
        binding.suggestRCV.setAdapter(usersAdapter);
        binding.suggestRCV.setVisibility(View.VISIBLE);

    }

    @Override
    public void onUserClicked(User user) {

    }
}