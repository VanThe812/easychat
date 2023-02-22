package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }
    private void setListeners() {
        binding.bottomNav.setSelectedItemId(R.id.action_setting);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.action_friend:
                    startActivity(new Intent(getApplicationContext(), FriendActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.action_setting:
                    return true;
            }
            return  false;
        });
        binding.buttonLogout.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });
    }
}