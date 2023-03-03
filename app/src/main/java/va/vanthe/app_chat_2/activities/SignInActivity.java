package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySigninBinding;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private PreferenceManager account; //Bảng tài khoản trong db của máy
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

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
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
        binding.buttonSignIn.setOnClickListener(view -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }
    @NonNull
    private Boolean isValidSignInDetails() {
        if(binding.inputEmail.getText().toString().isEmpty()) {
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }else {
            return true;
        }
    }

    private void signIn() {
        loading(true);

        database.collection(Constants.KEY_USER)
                .whereEqualTo(Constants.KEY_ACCOUNT_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(binding.inputPassword.getText().toString()))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        account.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        account.putString(Constants.KEY_ACCOUNT_USER_ID, documentSnapshot.getId());
                        account.putString(Constants.KEY_ACCOUNT_EMAIL, documentSnapshot.getString(Constants.KEY_ACCOUNT_EMAIL));
//                        account.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
//                        account.putString(Constants.KEY_STATUS_LANGUAGE, "en");
                        account.putString(Constants.KEY_ACCOUNT_PASSWORD, documentSnapshot.getString(Constants.KEY_ACCOUNT_PASSWORD));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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