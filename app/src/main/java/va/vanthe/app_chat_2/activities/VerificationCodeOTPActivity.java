package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityVerificationCodeOtpBinding;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class VerificationCodeOTPActivity extends AppCompatActivity {

    private ActivityVerificationCodeOtpBinding binding;
    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String mVerificationId, phoneNumber, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationCodeOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();


    }
    private void init() {
        account = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        mVerificationId = intent.getStringExtra("mVerificationId");
        phoneNumber = intent.getStringExtra("phoneNumber");
        password = intent.getStringExtra("password");
        mAuth = FirebaseAuth.getInstance();
    }
    private void setListeners() {
        binding.editTextDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before > count) {
                    binding.editTextDigit1.clearFocus();
                } else if (s.length() == 1) { // nếu đã nhập đủ 1 ký tự
                    // focus đến edittext tiếp theo
                    binding.editTextDigit2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (i1 > i2) {
                    binding.editTextDigit1.requestFocus();
                } else if (s.length() == 1) {
                    binding.editTextDigit3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (i1 > i2) {
                    binding.editTextDigit2.requestFocus();
                } else if (s.length() == 1) {
                    binding.editTextDigit4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (i1 > i2) {
                    binding.editTextDigit3.requestFocus();
                } else if (s.length() == 1) {
                    binding.editTextDigit5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextDigit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (i1 > i2) {
                    binding.editTextDigit4.requestFocus();
                } else if (s.length() == 1) {
                    binding.editTextDigit6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editTextDigit6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (i1 > i2) {
                    binding.editTextDigit5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.buttonVerify.setOnClickListener(view -> {
            String code = binding.editTextDigit1.getText().toString()+
                    binding.editTextDigit2.getText().toString()+
                    binding.editTextDigit3.getText().toString()+
                    binding.editTextDigit4.getText().toString()+
                    binding.editTextDigit5.getText().toString()+
                    binding.editTextDigit6.getText().toString();

            if (code.length() == 6) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        addAccountToDB();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            HelperFunction.showToast(getString(R.string.the_verification_code_entered_was_invalid), getApplicationContext());
                        }
                    }
                });
    }
    private void addAccountToDB() {
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_ACCOUNT_PHONE_NUMBER, phoneNumber);
        user.put(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(password));
        database.collection(Constants.KEY_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    HelperFunction.showToast(getString(R.string.account_successfully_created), getApplicationContext());

                    account.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    account.putString(Constants.KEY_ACCOUNT_USER_ID, documentReference.getId());
                    account.putString(Constants.KEY_ACCOUNT_PHONE_NUMBER, phoneNumber);
                    account.putString(Constants.KEY_ACCOUNT_PASSWORD, SHA256Encryptor.encrypt(password));

                    Intent intent = new Intent(getApplicationContext(), InformationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    exception.printStackTrace();
                });
    }

    private void loading(@SuppressLint("SupportAnnotationUsage") @NonNull Boolean loading) {
        if(loading) {
            binding.buttonVerify.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonVerify.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}