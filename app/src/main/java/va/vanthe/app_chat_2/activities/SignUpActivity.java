package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import va.vanthe.app_chat_2.adapters.ViewPagerInfoAdapter;
import va.vanthe.app_chat_2.adapters.ViewPagerSearchAdapter;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }
    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());

    }
    private void setListeners() {
        binding.loginUser.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });
        binding.buttonSignUp.setOnClickListener(view -> {
//            if (isValidSignUpDetails()) {
//                signUp();
//            }

            //
            ViewPagerInfoAdapter adapter = new ViewPagerInfoAdapter(getSupportFragmentManager());
            binding.viewPagerInfo.setAdapter(adapter);
        });
    }
    @NonNull
    private Boolean isValidSignUpDetails() {
        if(binding.inputEmail.getText().toString().isEmpty()) {
            showToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        }else if(binding.inputRePassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm your password");
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputRePassword.getText().toString())) {
            showToast("Password & confirm password must be same");
            return false;
        }else {
            return true;
        }
    }

    private CompletableFuture<Boolean> checkIfAccountExists(String email) {

        // Tạo một CompletableFuture để trả về kết quả kiểm tra
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // Thực hiện truy vấn để kiểm tra xem có tài khoản nào chứa email này hay không
        database.collection(Constants.KEY_USER)
                .whereEqualTo(Constants.KEY_ACCOUNT_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        future.complete(!documents.isEmpty());
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    private void addAccountToDB() {
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_ACCOUNT_EMAIL, binding.inputEmail.getText().toString().trim());
        user.put(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(binding.inputPassword.getText().toString()));
        database.collection(Constants.KEY_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_ACCOUNT_EMAIL, binding.inputEmail.getText().toString().trim());
                    preferenceManager.putString(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(binding.inputPassword.getText().toString()));
                    showToast("Account successfully created");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private void signUp(){
        loading(true);

        //check email
        CompletableFuture<Boolean> future = checkIfAccountExists(binding.inputEmail.getText().toString().trim());
        future.thenAccept(accountExists -> {
            if (accountExists) {
                // Account exists
                Log.d("checkAcc", "Account exists");
                showToast("Account already exists");
                loading(false);
                binding.inputEmail.findFocus();
            } else {
                // Account does not exist
                Log.d("checkAcc", "Account does not exists");
                addAccountToDB();
            }
        });



    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void loading(@NonNull Boolean isLoading) {
        if(isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}