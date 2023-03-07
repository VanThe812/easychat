package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
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

import va.vanthe.app_chat_2.ulitilies.Constants;

import va.vanthe.app_chat_2.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    private String encodedImage;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    public static final String TAG = "SentOTP";

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
        binding.loginUser.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });

        binding.buttonSignUp.setOnClickListener(view -> {
            if (isValidSignUpDetails()) {
                signUp();
            }
        });
    }

    @NonNull
    private Boolean isValidSignUpDetails() {
        if(binding.inputPhoneNumber.getText().toString().isEmpty()) {
            showToast("Enter phone number");
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

    private CompletableFuture<Boolean> checkIfAccountExists(String phoneNumber) {

        // Tạo một CompletableFuture để trả về kết quả kiểm tra
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
                Log.d("checkAcc", "Account exists");
                showToast("Account already exists");
                loading(false);
                binding.inputPhoneNumber.findFocus();
            } else {
                // Account does not exist
                Log.d("checkAcc", "Account does not exists");
                // Send OTP
                mAuth.setLanguageCode("vi");
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(binding.inputPhoneNumber.getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                // This callback is invoked in an invalid request for verification is made,
                                // for instance if the the phone number format is not valid.
                                Log.w(TAG, "onVerificationFailed", e);
                                Toast.makeText(SignUpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                Log.d(TAG, "onCodeSent" + verificationId);

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
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();
                            // Update UI

                            // CHuyển người dùng tới Activity Information
                            Toast.makeText(SignUpActivity.this, "Sign Up Success", Toast.LENGTH_SHORT).show();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUpActivity.this, "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
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