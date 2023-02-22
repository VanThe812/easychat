package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityFriendBinding;

public class FriendActivity extends AppCompatActivity {

    private ActivityFriendBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners() {
        binding.bottomNav.setSelectedItemId(R.id.action_friend);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return true;
                case R.id.action_friend:
                    return true;
                case R.id.action_setting:
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
            }
            return  false;
        });
    }

}