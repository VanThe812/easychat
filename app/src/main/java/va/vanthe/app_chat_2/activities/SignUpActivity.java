package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.ulitilies.Constants;

import va.vanthe.app_chat_2.databinding.ActivitySignupBinding;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }
    private void init() {

        mAuth = FirebaseAuth.getInstance();
    }
    private void setListeners() {
        binding.loginUser.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));

        binding.buttonSignUp.setOnClickListener(view -> {
            if (isValidSignUpDetails()) {
                signUp();
            }
        });
    }

    @NonNull
    private Boolean isValidSignUpDetails() {
        if(binding.inputPhoneNumber.getText().toString().isEmpty()) {
            HelperFunction.showToast(getString(R.string.enter_phone_number), getApplicationContext());
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()) {
            HelperFunction.showToast(getString(R.string.enter_password), getApplicationContext());
            return false;
        }else if(binding.inputRePassword.getText().toString().trim().isEmpty()) {
            HelperFunction.showToast(getString(R.string.confirm_password), getApplicationContext());
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputRePassword.getText().toString())) {
            HelperFunction.showToast(getString(R.string.password_and_confirm_password_must_be_same), getApplicationContext());
            return false;
        }else {
            return true;
        }
    }

    private CompletableFuture<Boolean> checkIfAccountExists(String phoneNumber) {

        /* Tạo một CompletableFuture để trả về kết quả kiểm tra */
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // Thực hiện truy vấn để kiểm tra xem có tài khoản nào chứa email này hay không
        database.collection(Constants.KEY_USER)
                .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, phoneNumber)
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




    private void signUp(){
        loading(true);

        //check phone number tồn tại
        CompletableFuture<Boolean> future = checkIfAccountExists(binding.inputPhoneNumber.getText().toString().trim());
        future.thenAccept(accountExists -> {
            if (accountExists) {
                // Account exists
                loading(false);
                binding.inputPhoneNumber.findFocus();
            } else {
                // Account does not exist
                // Send OTP
                mAuth.setLanguageCode("vi");
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(binding.inputPhoneNumber.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                HelperFunction.showToast(getString(R.string.verification_failed), getApplicationContext());
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);

                                Intent intent = new Intent(SignUpActivity.this, VerificationCodeOTPActivity.class);
                                intent.putExtra("mVerificationId", verificationId);
                                intent.putExtra("phoneNumber", binding.inputPhoneNumber.getText().toString().trim());
                                intent.putExtra("password", binding.inputPassword.getText().toString().trim());

                                startActivity(intent);
                            }
                        })
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });
    }

    // Dành cho những máy tự động xác nhận ma OTP
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        HelperFunction.showToast(getString(R.string.sign_up_success), getApplicationContext());
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            HelperFunction.showToast(getString(R.string.the_verification_code_entered_was_invalid), getApplicationContext());
                        }
                    }
                });
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