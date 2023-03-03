package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.adapters.InformationAdapter;
import va.vanthe.app_chat_2.databinding.ActivityBeginBinding;

public class BeginActivity extends AppCompatActivity {

    private ActivityBeginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityBeginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

//        List<String> data = new ArrayList<>();
//        data.add("Page 1");
//        data.add("Page 2");
//        data.add("Page 3");


//        InformationAdapter adapter = new InformationAdapter(data);
//        binding.viewPagerBegin.setAdapter(adapter);
//        binding.viewPagerBegin.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Chặn sự kiện vuốt bằng cách trả về true
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
    }
}