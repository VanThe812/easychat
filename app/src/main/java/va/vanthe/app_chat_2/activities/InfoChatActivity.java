package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import va.vanthe.app_chat_2.databinding.ActivityInfoChatBinding;

public class InfoChatActivity extends AppCompatActivity {

    private ActivityInfoChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }
}