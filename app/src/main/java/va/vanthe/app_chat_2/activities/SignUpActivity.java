package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginUser.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        });
    }
}