package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.adapters.AboutTheAppAdapter;
import va.vanthe.app_chat_2.databinding.ActivityBeginBinding;
import va.vanthe.app_chat_2.entity.AboutTheApp;

public class BeginActivity extends AppCompatActivity {

    private ActivityBeginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityBeginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        List<AboutTheApp> data = new ArrayList<>();
        data.add(new AboutTheApp("About page 1", false));
        data.add(new AboutTheApp("About page 2", false));
        data.add(new AboutTheApp("About page 3", false));


        AboutTheAppAdapter adapter = new AboutTheAppAdapter(data);
        binding.viewPagerBegin.setAdapter(adapter);

        binding.circleIndicator.createIndicators(3,0);
        binding.circleIndicator.animatePageSelected(0);


        binding.viewPagerBegin.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.circleIndicator.animatePageSelected(binding.viewPagerBegin.getCurrentItem());

                if(binding.viewPagerBegin.getCurrentItem() == 2) {
                    binding.buttonGetStart.setVisibility(View.VISIBLE);
                } else {
                    binding.buttonGetStart.setVisibility(View.GONE);
                }
            }
        });
        binding.buttonGetStart.setOnClickListener(view -> {
            Intent intent = new Intent(BeginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }
}