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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.database.ChatMessageDatabase;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ActivityChatMessageBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class ChatMessageActivity extends AppCompatActivity {

    private ActivityChatMessageBinding binding;
    private FirebaseFirestore database;
    private PreferenceManager account;
    private Conversation conversation;
    private List<ChatMessage> chatMessageList;
    private ChatAdapter chatAdapter;
    private User userChat;
    private int typeChat;
    private boolean statusNewChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        listenChatMessage();
    }


    private void init() {
        database = FirebaseFirestore.getInstance();
        account = new PreferenceManager(getApplicationContext());
        conversation = new Conversation();

        typeChat = getIntent().getIntExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_SINGLE); // Lấy kiểu chat - mặc định sẽ là chat single
        if (typeChat == Constants.KEY_TYPE_CHAT_SINGLE) {
            userChat = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
            statusNewChat = getIntent().getBooleanExtra(Constants.KEY_IS_NEW_CHAT, false);
            // set data
            loadUserAvatar(userChat);
            if (statusNewChat) {
                binding.layoutNewChat.setVisibility(View.VISIBLE);
            }
            else {
                String conversationId = getIntent().getStringExtra(Constants.KEY_CONVERSATION_ID);
                conversation = ConversationDatabase.getInstance(this).conversationDAO().getOneConversation(conversationId);
            }

        }
        else if (typeChat == Constants.KEY_TYPE_CHAT_GROUP) {
            //Xử lý các dữ liệu cần thiết khi là chat group
            conversation = (Conversation) getIntent().getSerializableExtra(Constants.KEY_CONVERSATION);
            if (conversation.getBackgroundImage() != null) {
                byte[] bytes = Base64.decode(conversation.getBackgroundImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
            }
            binding.textName.setText(conversation.getConversationName());
        }

        // set adapter rỗng cho RCV, sẽ thêm tin nhắn vào sau
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageList, account.getString(Constants.KEY_ACCOUNT_USER_ID), typeChat);
        binding.chatRCV.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        binding.chatRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }

    private void setListeners() {
        binding.imageback.setOnClickListener(v -> {
            onBackPressed();
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        binding.imageInfo.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), InfoChatActivity.class);
            startActivity(intent);
        });
        // tùy chỉnh giao diện của inputMessage(co giãn khi có text)
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
        //gửi tin nhắn
        binding.textviewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSend;
                if (binding.inputMessage.getText().toString().trim().equals("")) {
                    textSend = binding.textviewSend.getText().toString();
                }
                else {
                    textSend = binding.inputMessage.getText().toString().trim();
                }

                if (typeChat == Constants.KEY_TYPE_CHAT_SINGLE) {
                    if (statusNewChat) { // true la new chat
                        conversation.setCreateTime(new Date());
                        conversation.setNewMessage(textSend);
                        conversation.setSenderId(userChat.getId());
                        conversation.setMessageTime(new Date());
                        conversation.setStyleChat(Constants.KEY_TYPE_CHAT_SINGLE);

                        // Chuyển qua Hashmap
                        HashMap<String, Object> conversationMap = conversation.toHashMap();


                        database.collection(Constants.KEY_CONVERSATION)
                                .add(conversationMap)
                                .addOnSuccessListener(documentReference -> {
                                    // Sau khi tạo mới thành công một hội thoại => tạo mới 2 bản ghi groupMember
                                    // truoc do them conversation vao room da
                                    conversation.setId(documentReference.getId());
                                    ConversationDatabase.getInstance(getApplicationContext()).conversationDAO().insertConversation(conversation);

                                    GroupMember groupMember1 = new GroupMember();
                                    groupMember1.setUserId(userChat.getId());
                                    groupMember1.setConversationId(documentReference.getId());
                                    groupMember1.setTimeJoin(new Date());
                                    groupMember1.setStatus(1);

                                    GroupMember groupMember2 = new GroupMember();
                                    groupMember2.setUserId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                                    groupMember2.setConversationId(documentReference.getId());
                                    groupMember2.setTimeJoin(new Date());
                                    groupMember2.setStatus(1);

                                    // Khởi tạo batch write (Đe them cung luc 2 ban ghi)
                                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                                    DocumentReference docRef1 = FirebaseFirestore.getInstance().collection(Constants.KEY_GROUP_MEMBER).document();
                                    HashMap<String, Object> groupMember1Map = groupMember1.toHashMap();
                                    batch.set(docRef1, groupMember1Map);

                                    DocumentReference docRef2 = FirebaseFirestore.getInstance().collection(Constants.KEY_GROUP_MEMBER).document();
                                    HashMap<String, Object> groupMember2Map = groupMember2.toHashMap();
                                    batch.set(docRef2, groupMember2Map);

                                    // Commit batch write
                                    batch.commit()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Thao tác thành công
                                                    // insert 2 group member vao room
                                                    groupMember1.setId(docRef1.getId());
                                                    groupMember2.setId(docRef2.getId());
                                                    GroupMemberDatabase.getInstance(getApplicationContext()).groupMemberDAO().insertGroupMember(groupMember1);
                                                    GroupMemberDatabase.getInstance(getApplicationContext()).groupMemberDAO().insertGroupMember(groupMember2);

                                                    //Thêm user đó và Room
                                                    UserDatabase.getInstance(getApplicationContext()).userDAO().insertUser(userChat);

                                                    sendMessage(textSend, conversation.getId());
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
                    sendMessage(textSend, conversation.getId());
                    }
                }
                else if(typeChat == Constants.KEY_TYPE_CHAT_GROUP) {
                    sendMessage(textSend, conversation.getId());
                }

            }
        });
    }
    private void sendMessage(String textSend, String conversationId) {

        binding.inputMessage.setText("");
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
        chatMessage.setMessage(textSend);
        chatMessage.setDataTime(new Date());
        chatMessage.setConversationId(conversationId);
        chatMessage.setStyleMessage(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NORMAL);
        
        HashMap<String, Object> message = chatMessage.toHashMap();
        
        database.collection(Constants.KEY_CHAT_MESSAGE)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    chatMessage.setId(documentReference.getId());
                    //ChatMessageDatabase.getInstance(getApplicationContext()).chatMessageDAO().insertChatMessage(chatMessage);

//                    addOneMessageToBelow(chatMessage);
                    updateConversaion(chatMessage.getMessage());
                })
                .addOnFailureListener(runnable -> {

                });
    }


    private void addOneMessageToBelow(ChatMessage chatMessage) {
        chatMessageList.add(chatMessage);
        int count = chatMessageList.size();
        Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getDataTime().compareTo(obj2.getDataTime()));
        if(count == 0) {
            chatAdapter.notifyDataSetChanged();
        }else{
            chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
            binding.chatRCV.smoothScrollToPosition(chatMessageList.size() - 1);
        }
    }
    private void updateConversaion(String message) {
        DocumentReference conversationReference = database.collection(Constants.KEY_CONVERSATION).document(conversation.getId());
        conversation.setMessageTime(new Date());
        conversation.setNewMessage(message);
        conversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));

        conversationReference.update(conversation.toHashMap());
    }
    private void listenChatMessage() {
        database.collection(Constants.KEY_CHAT_MESSAGE)
                .whereEqualTo(Constants.KEY_CHAT_MESSAGE_CONVERSATION_ID, conversation.getId())
//                .orderBy(Constants.KEY_CHAT_MESSAGE_DATA_TIME, Query.Direction.DESCENDING)
//                .limit(10)
                .addSnapshotListener(eventListenerChatMessage);
    }
    private final EventListener<QuerySnapshot> eventListenerChatMessage = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) { //nếu có thêm dữ liệu hoặc là khi vừa mở app
                    DocumentSnapshot chatMessageSnapshot = documentChange.getDocument();
                    ChatMessage chatMessage = chatMessageSnapshot.toObject(ChatMessage.class);
                    chatMessage.setId(chatMessageSnapshot.getId());
//
//                    int checkChatMessage = ChatMessageDatabase.getInstance(this).chatMessageDAO().checkChatMessage(chatMessage.getId());
//
//                    if (checkChatMessage == 0) {
//                        ChatMessageDatabase.getInstance(this).chatMessageDAO().insertChatMessage(chatMessage);
//                    }


                    addOneMessageToBelow(chatMessage);

                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
                    Log.d("LogChatMessage", "MODIFIED");
                }
            }
        }
    };
    private void  loadUserAvatar(@NonNull User user) {
        binding.textName.setText(user.getLastName());
        byte[] bytes = Base64.decode(user.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.imageProfile2.setImageBitmap(bitmap);
    }
    @NonNull
    private String getReadableDateTIme(Date date) {
        return new SimpleDateFormat("MMMM dd, YYYY - hh:mm a", Locale.getDefault()).format(date);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
