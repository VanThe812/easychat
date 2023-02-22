package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySigninBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerUser.setOnClickListener(view -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        });
    }

}