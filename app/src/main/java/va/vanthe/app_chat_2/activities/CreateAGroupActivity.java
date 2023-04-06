package va.vanthe.app_chat_2.activities;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.databinding.ActivityCreateAGroupBinding;

public class CreateAGroupActivity extends AppCompatActivity {


    private ActivityCreateAGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }

}