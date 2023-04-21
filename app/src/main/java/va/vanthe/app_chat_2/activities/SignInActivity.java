package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySigninBinding;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }

    private void init() {
        account = new PreferenceManager(getApplicationContext());
    }

    private void setListeners() {

        binding.registerUser.setOnClickListener(view -> {
            Intent i = new Intent(SignInActivity.this,SignUpActivity.class);
            startActivity(i);
            finish();
        });
        binding.buttonSignIn.setOnClickListener(view -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }
    @NonNull
    private Boolean isValidSignInDetails() {
        if(binding.inputPhoneNumber.getText().toString().isEmpty()) {
            HelperFunction.showToast(getString(R.string.enter_phone_number), getApplicationContext());
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()) {
            HelperFunction.showToast(getString(R.string.enter_password), getApplicationContext());
            return false;
        }else {
            return true;
        }
    }

    private void signIn() {
        loading(true);

        database.collection(Constants.KEY_USER)
                .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, binding.inputPhoneNumber.getText().toString())
                .whereEqualTo(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(binding.inputPassword.getText().toString()))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        account.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        account.putString(Constants.KEY_ACCOUNT_USER_ID, documentSnapshot.getId());
                        account.putString(Constants.KEY_ACCOUNT_PHONE_NUMBER, documentSnapshot.getString(Constants.KEY_ACCOUNT_PHONE_NUMBER));
                        account.putString(Constants.KEY_ACCOUNT_PASSWORD, documentSnapshot.getString(Constants.KEY_ACCOUNT_PASSWORD));

                        account.putString(Constants.KEY_ACCOUNT_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        account.putString(Constants.KEY_ACCOUNT_FIRST_NAME, documentSnapshot.getString(Constants.KEY_ACCOUNT_FIRST_NAME));
                        account.putString(Constants.KEY_ACCOUNT_LAST_NAME, documentSnapshot.getString(Constants.KEY_ACCOUNT_LAST_NAME));
                        account.putBoolean(Constants.KEY_ACCOUNT_SEX, documentSnapshot.getBoolean(Constants.KEY_ACCOUNT_SEX));
                        account.putString(Constants.KEY_ACCOUNT_DateOfBirth, documentSnapshot.getString(Constants.KEY_ACCOUNT_DateOfBirth));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        HelperFunction.showToast(getString(R.string.unable_to_sign_in), getApplicationContext());
                    }
                });
    }

    private void loading(@NonNull Boolean isLoading) {
        if(isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}