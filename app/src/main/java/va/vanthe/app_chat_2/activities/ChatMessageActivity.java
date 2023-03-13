package va.vanthe.app_chat_2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.databinding.ActivityChatMessageBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class ChatMessageActivity extends AppCompatActivity {

    private ActivityChatMessageBinding binding;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;
    private PreferenceManager account;
    //
    private User userChat;
    //
    private String conversationId;
    private int typeChat = 1; // để mặc định là chat giữa 2 user
    private boolean STATUS_USER_NEW_CHAT = false;



    private String TAG = "ChatActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();

    }


    private void init() {
        database = FirebaseFirestore.getInstance();
        account = new PreferenceManager(getApplicationContext());

        conversationId = getIntent().getStringExtra(Constants.KEY_CONVERSATION_ID);

        typeChat = getIntent().getIntExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_SINGLE);

        if (typeChat == Constants.KEY_TYPE_CHAT_SINGLE) { // nếu là chat đơn
//            userChat = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
            // lấy user từ trong db ra
            loadUserAvatar();
            if (conversationId.matches("")) {
                STATUS_USER_NEW_CHAT = true;
                binding.layoutNewChat.setVisibility(View.VISIBLE);
                // Check friend....
                //
                //
            }
        }

        // set adapter rỗng cho RCV, sẽ thêm tin nhắn vào sau
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        binding.chatRCV.setAdapter(chatAdapter);

    }
    private void  loadUserAvatar() {
        binding.textName.setText(userChat.getLastName());
        byte[] bytes = Base64.decode(userChat.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.imageProfile2.setImageBitmap(bitmap);
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
        binding.textviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.layoutNewChat.getVisibility() == View.VISIBLE) { // Đoạn này làm mẹo, nếu layoutNewChat hiện lên thì có nghĩa là chưa có cuộc hội thoại
                    // Tạo mới 1 cuộc hội thoại: Conversation
                    HashMap<String, Object> conversation = new HashMap<>();
                    conversation.put(Constants.KEY_CONVERSATION_CREATE_TIME, new Date());
                    database.collection(Constants.KEY_CONVERSATION)
                            .add(conversation)
                            .addOnSuccessListener(documentReference -> {
                                // Sau khi tạo mới thành công một hội thoại => tạo mới 2 bản ghi groupMember
                                // Khởi tạo batch write
                                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                                // Thêm bản ghi 1
                                DocumentReference docRef1 = FirebaseFirestore.getInstance().collection(Constants.KEY_GROUP_MEMBER).document();
                                HashMap<String, Object> groupMember1 = new HashMap<>();
                                groupMember1.put(Constants.KEY_GROUP_MEMBER_USER_ID, userChat.getId());
                                groupMember1.put(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID, documentReference.getId());
                                groupMember1.put(Constants.KEY_GROUP_MEMBER_STATUS, true);
                                batch.set(docRef1, groupMember1);

                                DocumentReference docRef2 = FirebaseFirestore.getInstance().collection(Constants.KEY_GROUP_MEMBER).document();
                                HashMap<String, Object> groupMember2 = new HashMap<>();
                                groupMember2.put(Constants.KEY_GROUP_MEMBER_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID));
                                groupMember2.put(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID, documentReference.getId());
                                groupMember2.put(Constants.KEY_GROUP_MEMBER_STATUS, true);
                                batch.set(docRef2, groupMember2);

                                // Commit batch write
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Thao tác thành công
                                        sendMessage(binding.inputMessage.getText().toString().trim(), documentReference.getId());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Thao tác thất bại
                                    }
                                });
                            })
                            .addOnFailureListener(runnable -> {
                                Toast.makeText(ChatMessageActivity.this, "Có một số lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            });
                } else { // tiếp tục nhắn tin
//                    sendMessage(binding.inputMessage.getText().toString().trim(), );
                }
            }
        });
    }
    private void sendMessage(String textSend, String conversationId) {

        binding.inputMessage.setText("");
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_CHAT_MESSAGE_SENDER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID));
        message.put(Constants.KEY_CHAT_MESSAGE_MESSAGE, textSend);
        message.put(Constants.KEY_CHAT_MESSAGE_DATA_TIME, new Date());
        message.put(Constants.KEY_CHAT_MESSAGE_CONVERSATION_ID, conversationId);
        message.put(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE, Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NORMAL);
        database.collection(Constants.KEY_CHAT_MESSAGE)
                .add(message)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(runnable -> {

                });
    }

    private String getReadableDateTIme(Date date) {
        return new SimpleDateFormat("MMMM dd, YYYY - hh:mm a", Locale.getDefault()).format(date);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
