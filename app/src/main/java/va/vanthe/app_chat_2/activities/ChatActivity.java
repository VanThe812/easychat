package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.R;
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
        binding.imageback.setOnClickListener(v -> {
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        binding.imageInfo.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InfoChatActivity.class);
            startActivity(intent);
        });
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String mess = editable.toString().trim();
                if (!mess.matches("")) {
                    binding.imageImage.setVisibility(View.GONE);
                    binding.imageCamera.setVisibility(View.GONE);
                    binding.imageLocation.setVisibility(View.GONE);

                    binding.imageMore.setVisibility(View.VISIBLE);
                } else {
                    binding.imageImage.setVisibility(View.VISIBLE);
                    binding.imageCamera.setVisibility(View.VISIBLE);
                    binding.imageLocation.setVisibility(View.VISIBLE);

                    binding.imageMore.setVisibility(View.GONE);
                }
            }
        });
        binding.imageMore.setOnClickListener(view -> {
            binding.imageImage.setVisibility(View.VISIBLE);
            binding.imageCamera.setVisibility(View.VISIBLE);
            binding.imageLocation.setVisibility(View.VISIBLE);

            binding.imageMore.setVisibility(View.GONE);
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