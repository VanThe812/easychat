package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.databinding.ActivityChatBinding;
import va.vanthe.app_chat_2.models.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setDataTest();
        setListeners();

    }



    private void init() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages
        );
        binding.chatRCV.setAdapter(chatAdapter);

    }
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void setListeners() {
        binding.imageback.setOnClickListener(v -> onBackPressed());
        binding.imageInfo.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InfoChatActivity.class);
            startActivity(intent);
        });
    }
    private void setDataTest() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.message = "Xin chào bạn.";
        chatMessage.datatime = "1";
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.message = "Chào cậu";
        chatMessage2.datatime = "2";
        chatMessages.add(chatMessage2);

        ChatMessage chatMessage3 = new ChatMessage();
        chatMessage3.message = "Cho mình làm quen nhé";
        chatMessage3.datatime = "1";
        chatMessages.add(chatMessage3);

        chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());

        binding.chatRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

}