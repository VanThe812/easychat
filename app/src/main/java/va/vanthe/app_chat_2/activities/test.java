package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityTestBinding;

public class test extends AppCompatActivity {
    private ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Animation flyAnimation = AnimationUtils.loadAnimation(this, R.anim.icon_fly);
        binding.ivFly.startAnimation(flyAnimation);
    }
}