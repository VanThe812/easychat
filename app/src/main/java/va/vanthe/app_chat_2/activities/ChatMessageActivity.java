package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ActivityChatMessageBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.fragment.DialogViewImageFragment;
import va.vanthe.app_chat_2.network.ApiClient;
import va.vanthe.app_chat_2.network.ApiService;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class ChatMessageActivity extends BaseActivity {

    private ActivityChatMessageBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private PreferenceManager account;

    private Conversation mConversation = new Conversation();
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private User userChat = new User();
    private int styleChat;
    private boolean statusNewChat;

    private Boolean isReceiverAvailable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
        listenChatMessage();

    }



    private void init() {
        account  = new PreferenceManager(getApplicationContext());

        try {
            mConversation = (Conversation) getIntent().getSerializableExtra(Constants.KEY_CONVERSATION);
            userChat      = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
            styleChat     = getIntent().getIntExtra(Constants.KEY_TYPE, Constants.KEY_TYPE_CHAT_SINGLE);
        } catch (Exception e) {
            e.printStackTrace();
            onBackPressed();
        }


        if(mConversation == null) {
//            database.collection(Constants.KEY_GROUP_MEMBER)
//                    .whereIn(Constants.KEY_GROUP_MEMBER_USER_ID, )

            List<Conversation> conversations = ConversationDatabase.getInstance(getApplicationContext()).conversationDAO().getConversationStyleChat(Constants.KEY_TYPE_CHAT_SINGLE);

            if (conversations.isEmpty()) {
                Toast.makeText(this, "Chua nhan", Toast.LENGTH_SHORT).show();
                statusNewChat = true;
                binding.layoutNewChat.setVisibility(View.VISIBLE);
            }else {
                int i = 0;
                do {
                    Conversation conversation = conversations.get(i);
                    //
                    GroupMember groupMember = GroupMemberDatabase.getInstance(getApplicationContext()).groupMemberDAO().getGroupMember2(userChat.getId(), conversation.getId());
                    if (groupMember == null) {
                        Toast.makeText(this, "Chua nhan", Toast.LENGTH_SHORT).show();
                        statusNewChat = true;
                        binding.layoutNewChat.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(this, "Da Nhan", Toast.LENGTH_SHORT).show();
                        statusNewChat = false;
                        mConversation = conversation;
                        binding.imageInfo.setVisibility(View.VISIBLE);
                    }
                    //
                    i++;
                } while (i < conversations.size());
            }
            loadUserAvatar(userChat);



        }
        else {
            // từ bên giao diện chính gửi qua
            binding.imageInfo.setVisibility(View.VISIBLE);
            if (styleChat == Constants.KEY_TYPE_CHAT_SINGLE) {
                loadUserAvatar(userChat);
            }
            else if (styleChat == Constants.KEY_TYPE_CHAT_GROUP) {
                //Xử lý các dữ liệu cần thiết khi là chat group
//                conversation = (Conversation) getIntent().getSerializableExtra(Constants.KEY_CONVERSATION);
                if (mConversation.getConversationAvatar() != null) {
                    byte[] bytes = Base64.decode(mConversation.getConversationAvatar(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageProfile.setImageBitmap(bitmap);
                }
                binding.textName.setText(mConversation.getConversationName());
            }

            if (mConversation.getQuickEmotions() != null) {
                binding.textviewSend.setText(mConversation.getQuickEmotions());
            } else {
                binding.textviewSend.setText("\uD83C\uDF49");
            }

            if (mConversation.getConversationBackground() != null) {
                File file = new File(getFilesDir(), mConversation.getConversationBackground());
                if (file.exists()) {
                    // File tồn tại trong thư mục của ứng dụng
                    Picasso.get()
                            .load(file)
                            .into(binding.imageBackground);
                }
                else {
                    // File không tồn tại trong thư mục của ứng dụng
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                            .child("images")
                            .child("background")
                            .child(mConversation.getConversationBackground());
                    File storageDir = getApplicationContext().getExternalFilesDir(null);
                    File localFile = new File(storageDir, mConversation.getConversationBackground());
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // File tải về thành công
                            Picasso.get()
                                    .load(localFile)
                                    .into(binding.imageBackground);
                            File destinationFile = new File(getFilesDir(), mConversation.getConversationBackground());

//                      // Sao chép tập tin vào thư mục của ứng dụng
                            try {
                                FileUtils.copyFile(localFile.getPath(), destinationFile.getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Xử lý khi có lỗi xảy ra
                            exception.printStackTrace();
                        }
                    });

                }
            }
        }

        // set adapter rỗng cho RCV, sẽ thêm tin nhắn vào sau
        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageList, account.getString(Constants.KEY_ACCOUNT_USER_ID), styleChat, new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Uri uri) {
                Bundle args = new Bundle();
                args.putString("image_path", uri.toString());

                DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                dialogViewImageFragment.setArguments(args);
                dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");
            }
        });
        binding.chatRCV.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        binding.chatRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);

    }


    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
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
                if (!mess.matches("")) { // có text nhập vào
                    binding.textviewMore.setVisibility(View.GONE);
                    //
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_send);
                    Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(getApplicationContext(), R.color.blue));
                    binding.textviewSend.setText("");
                    binding.textviewSend.setCompoundDrawablesRelativeWithIntrinsicBounds(wrappedDrawable, null, null, null);


                } else { // ngược lại, đang rỗng
                    binding.textviewMore.setVisibility(View.VISIBLE);
                    binding.textviewSend.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                    if (mConversation != null) {
                        binding.textviewSend.setText(mConversation.getQuickEmotions());
                    } else {
                        binding.textviewSend.setText("\uD83C\uDF49");
                    }
                }
            }
        });

        binding.imageImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
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

                if (styleChat == Constants.KEY_TYPE_CHAT_SINGLE) {
                    if (statusNewChat) { // true la new chat
                        mConversation = new Conversation();
                        mConversation.setCreateTime(new Date());
                        mConversation.setNewMessage(textSend);
                        mConversation.setSenderId(userChat.getId());
                        mConversation.setMessageTime(new Date());
                        mConversation.setStyleChat(Constants.KEY_TYPE_CHAT_SINGLE);
                        mConversation.setQuickEmotions("\uD83C\uDF49");
                        // Chuyển qua Hashmap
                        HashMap<String, Object> conversationMap = mConversation.toHashMap();


                        database.collection(Constants.KEY_CONVERSATION)
                                .add(conversationMap)
                                .addOnSuccessListener(documentReference -> {
                                    // Sau khi tạo mới thành công một hội thoại => tạo mới 2 bản ghi groupMember
                                    // truoc do them conversation vao room da
                                    mConversation.setId(documentReference.getId());
                                    ConversationDatabase.getInstance(getApplicationContext()).conversationDAO().insertConversation(mConversation);

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
                                                    if (UserDatabase.getInstance(getApplicationContext()).userDAO().checkUser(userChat.getId()) == 0) {
                                                        UserDatabase.getInstance(getApplicationContext()).userDAO().insertUser(userChat);
                                                    }


                                                    sendMessage(textSend, mConversation.getId(), Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT);
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
                    sendMessage(textSend, mConversation.getId(), Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT);
                    }
                }
                else if(styleChat == Constants.KEY_TYPE_CHAT_GROUP) {
                    sendMessage(textSend, mConversation.getId(), Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT);
                }

            }
        });

        //chuyển qua InfoChat
        binding.imageInfo.setOnClickListener(view -> {
            if (!statusNewChat) { // nếu chưa nhắn tin bao h sẽ không cho qua
                Intent intent = new Intent(getApplicationContext(), InfoChatActivity.class);
                intent.putExtra(Constants.KEY_CONVERSATION, mConversation);
                startActivity(intent);
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
//                if(result.getResultCode() == RESULT_OK) {
//                    if(result.getData() != null) {
//                        Uri imageUri = result.getData().getData();
//                        ContentResolver cR = getContentResolver();
//                        MimeTypeMap mime = MimeTypeMap.getSingleton();
//                        StorageReference storageRef = storage.getReference();
//
//                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//                        String fileName = String.format("%s_%s.%s", account.getString(Constants.KEY_ACCOUNT_USER_ID), timestamp, mime.getExtensionFromMimeType(cR.getType(imageUri)));
//                        String path = String.format("images/conversation/%s/%s", mConversation.getId(), fileName);
//                        StorageReference imagesRef = storageRef.child(path);
//                        imagesRef.putFile(imageUri)
//                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        // Upload successful
//                                        sendMessage(fileName, mConversation.getId(), Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE);
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        // Upload failed
//                                    }
//                                });
//                    }
//                }
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Intent intent = new Intent(ChatMessageActivity.this, EditImageActivity.class);
                        intent.putExtra("DATA", imageUri.toString());
                        intent.putExtra("style", Constants.KEY_REQUEST_CODE_IMAGE_MESSAGE);
                        startActivityForResult(intent, Constants.KEY_REQUEST_CODE_IMAGE_MESSAGE);
                    }
                }
            }
    );
    private void sendMessage(String textSend, String conversationId, int styleSend) {

        if (styleSend == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT) {
            binding.inputMessage.setText("");

        } else if (styleSend == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE) {

        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
        chatMessage.setMessage(textSend);
        chatMessage.setDataTime(new Date());
        chatMessage.setConversationId(conversationId);
        chatMessage.setStyleMessage(styleSend);
        HashMap<String, Object> message = chatMessage.toHashMap();

        database.collection(Constants.KEY_CHAT_MESSAGE)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    chatMessage.setId(documentReference.getId());
//                    addOneMessageToBelow(chatMessage);
                    updateConversation(chatMessage);
                    List<GroupMember> groupMemberList = GroupMemberDatabase.getInstance(getApplicationContext()).groupMemberDAO().getListGroupMember(account.getString(Constants.KEY_ACCOUNT_USER_ID), mConversation.getId());
                    for (GroupMember groupMember : groupMemberList) {
                        User user = UserDatabase.getInstance(getApplicationContext()).userDAO().getUser(groupMember.getUserId());
                        if (user.getFcmToken()==null) {
                            database.collection(Constants.KEY_USER)
                                    .document(user.getId())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot userSnapshot) {
                                            if (userSnapshot.exists()) {
                                                User user = userSnapshot.toObject(User.class);
                                                UserDatabase.getInstance(getApplicationContext()).userDAO().updateUser(user);
                                                return;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(ChatMessageActivity.class.toString(), "get failed with ", e);
                                        }
                                    });
                        }
                        try {

                            JSONArray tokens = new JSONArray();
                            Log.e("FCM_TOKEN", user.getFcmToken());
                            tokens.put(user.getFcmToken());

                            JSONObject data = new JSONObject();
                            data.put(Constants.KEY_ACCOUNT_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID)); //người dùng hiện tại
                            data.put(Constants.KEY_CONVERSATION_ID, mConversation.getId()); //cuội hội thoại đang ở
//                            data.put(Constants.KEY_FCM_TOKEN, account.getString(Constants.KEY_FCM_TOKEN)); //thông
                            data.put(Constants.KEY_CHAT_MESSAGE_MESSAGE, chatMessage.getMessage()); // tin nhắn mới
                            data.put(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE, chatMessage.getStyleMessage()); // kiểu tin nhắn

                            JSONObject body = new JSONObject();
                            body.put(Constants.REMOTE_MSG_DATA, data);
                            body.put(Constants.REMOTE_MSG_REGISTRATION, tokens);

                            sendNotification(body.toString());
                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FCM_E", e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(runnable -> {

                });


    }

    private void listenChatMessage() {
        if (mConversation != null) {
            database.collection(Constants.KEY_CHAT_MESSAGE)
                    .whereEqualTo(Constants.KEY_CHAT_MESSAGE_CONVERSATION_ID, mConversation.getId())
//                .orderBy(Constants.KEY_CHAT_MESSAGE_DATA_TIME, Query.Direction.DESCENDING)
//                .limit(10)
                    .addSnapshotListener(eventListenerChatMessage);
        }

    }
    private final EventListener<QuerySnapshot> eventListenerChatMessage = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            int count = chatMessageList.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) { //nếu có thêm dữ liệu hoặc là khi vừa mở app
                    DocumentSnapshot chatMessageSnapshot = documentChange.getDocument();
                    ChatMessage chatMessage = chatMessageSnapshot.toObject(ChatMessage.class);
                    chatMessage.setId(chatMessageSnapshot.getId());

                    chatMessageList.add(chatMessage);

                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
                    Log.d("LogChatMessage", "MODIFIED");
                }
            }
            Collections.sort(chatMessageList, (obj1, obj2) -> obj1.getDataTime().compareTo(obj2.getDataTime()));
            if(count == 0) {
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessageList.size(), chatMessageList.size());
                binding.chatRCV.smoothScrollToPosition(chatMessageList.size() - 1);
            }
        }
    };
    private void addOneMessageToBelow(ChatMessage chatMessage) {
        if (chatMessage == null)
            return;
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
    private void updateConversation(ChatMessage chatMessage) {
        DocumentReference conversationReference = database.collection(Constants.KEY_CONVERSATION).document(mConversation.getId());
        mConversation.setMessageTime(new Date());
        if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_TEXT)
            mConversation.setNewMessage(chatMessage.getMessage());
        else if (chatMessage.getStyleMessage() == Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE)
            mConversation.setNewMessage(account.getString(Constants.KEY_ACCOUNT_LAST_NAME) + " đã gửi 1 ảnh");
        mConversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));

        conversationReference.update(mConversation.toHashMap());
    }

    private void  loadUserAvatar(@NonNull User user) {
        binding.textName.setText(user.getLastName());
        byte[] bytes = Base64.decode(user.getImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.imageProfile2.setImageBitmap(bitmap);
    }

    private void listenAvailabilityOfReceiver() {
        if (styleChat == Constants.KEY_TYPE_CHAT_SINGLE) {
            database.collection(Constants.KEY_USER).document(userChat.getId())
                    .addSnapshotListener(ChatMessageActivity.this, (value, error) -> {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                                int availability = Objects.requireNonNull(
                                        value.getLong(Constants.KEY_AVAILABILITY)
                                ).intValue();
                                isReceiverAvailable = availability == 1;
                            }
                            userChat.setFcmToken(value.getString(Constants.KEY_FCM_TOKEN));
                        }
                        if (isReceiverAvailable) {
                            binding.textStatus.setVisibility(View.VISIBLE);
                            binding.textStatus.setText(R.string.online);
                            binding.imageStatusOnline.setVisibility(View.VISIBLE);
                        } else {
                            binding.textStatus.setVisibility(View.INVISIBLE);
                            binding.imageStatusOnline.setVisibility(View.GONE);
                        }

                    });
        } else if (styleChat == Constants.KEY_TYPE_CHAT_GROUP) {
            /////
        }

    }

    private void sendNotification(String bodyString) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                bodyString
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
//                                Toast.makeText(ChatMessageActivity.this, error.getString("error"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(ChatMessageActivity.this, "Notification send successfully", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(ChatMessageActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(ChatMessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == Constants.KEY_REQUEST_CODE_IMAGE_MESSAGE) {
            String result = data.getStringExtra("RESULT");
            Uri imageUri = null;
            if (result != null) {
                imageUri = Uri.parse(result);
            }
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            StorageReference storageRef = storage.getReference();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = String.format("%s_%s.%s", account.getString(Constants.KEY_ACCOUNT_USER_ID), timestamp, mime.getExtensionFromMimeType(cR.getType(imageUri)));
            String path = String.format("images/conversation/%s/%s", mConversation.getId(), fileName);
            StorageReference imagesRef = storageRef.child(path);
            imagesRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Upload successful
                            sendMessage(fileName, mConversation.getId(), Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_IMAGE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Upload failed
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}
