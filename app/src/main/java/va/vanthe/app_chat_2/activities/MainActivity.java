package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.RecentConversionsAdapter;
import va.vanthe.app_chat_2.databinding.ActivityMainBinding;
import va.vanthe.app_chat_2.listeners.ConversionListener;
import va.vanthe.app_chat_2.models.ChatMessage;
import va.vanthe.app_chat_2.models.User;

public class MainActivity extends AppCompatActivity implements ConversionListener {

    private ActivityMainBinding binding;
    private List<ChatMessage> conversation;
    private RecentConversionsAdapter recentConversionsAdapter;

    private String IMAGE_GROUP_CHAT_DEFAULT = "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";
    private String IMAGE_AVATAR = "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&#10;REAREQBERAEREAREQH//2Q==     ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
        init();
        listenConversations();
        loadUserDetails();

    }
    private void init() {
        // tạo ra 1 mảng để chứa các cuộc hội thoại của ng dùng
        conversation = new ArrayList<>();
        // khởi tạo rcv
        recentConversionsAdapter = new RecentConversionsAdapter(conversation, this);
        binding.conversionsRCV.setAdapter(recentConversionsAdapter);
    }
    private void loadUserDetails() {
//        binding.textName.setText("hii");
        byte[] bytes = Base64.decode(IMAGE_GROUP_CHAT_DEFAULT, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void setListeners() {
        binding.bottomNav.setSelectedItemId(R.id.action_chat);
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_chat:
                    return true;
                case R.id.action_friend:
                    startActivity(new Intent(getApplicationContext(), FriendActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations() {
        for (int i = 0; i<10; i++) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.receiverNickname = "Test";
            chatMessage.conversionImage = IMAGE_GROUP_CHAT_DEFAULT;
            chatMessage.message = "Tin nhan " + i;
            conversation.add(chatMessage);
        }

//        Collections.sort(conversation, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
        recentConversionsAdapter.notifyDataSetChanged();
        binding.conversionsRCV.smoothScrollToPosition(0);
        binding.conversionsRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onConversionCLicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        // chuyền thông tin của ng muốn nhắn tin qua activity chat
//        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
//        Toast.makeText(this, "Hiii", Toast.LENGTH_SHORT).show();
    }
}