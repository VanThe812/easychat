package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ChatAdapter;
import va.vanthe.app_chat_2.adapters.EmojiAdapter;
import va.vanthe.app_chat_2.adapters.UserCreateGroupAdapter;
import va.vanthe.app_chat_2.adapters.UserGroupMembersAdapter;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ActivityInfoChatBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.Emoji;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.fragment.DialogViewImageFragment;
import va.vanthe.app_chat_2.listeners.UserCreateGroupListener;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class InfoChatActivity extends BaseActivity {

    private ActivityInfoChatBinding binding;
    private Conversation mConversation = new Conversation();
    private PreferenceManager account;
    private FirebaseFirestore database;
    private FirebaseStorage storage;

    private ImageView imageViewBackground;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }
    private void init() {
        database = FirebaseFirestore.getInstance();
        account = new PreferenceManager(getApplicationContext());
        storage  = FirebaseStorage.getInstance();
        mConversation = (Conversation) getIntent().getSerializableExtra(Constants.KEY_CONVERSATION);
        if (mConversation == null)
            onBackPressed();

        if (mConversation.getStyleChat() == Constants.KEY_TYPE_CHAT_SINGLE) {
            try {
                GroupMember groupMember = GroupMemberDatabase.getInstance(getApplicationContext()).groupMemberDAO().getGroupMember(account.getString(Constants.KEY_ACCOUNT_USER_ID), mConversation.getId());
                User user = UserDatabase.getInstance(getApplicationContext()).userDAO().getUser(groupMember.getUserId());
                binding.imageProfile.setImageBitmap(HelperFunction.getBitmapFromEncodedImageString(user.getImage()));
                binding.textViewConversationName.setText(user.getFirstName() + " " + user.getLastName());
                binding.imageChangeImage.setVisibility(View.GONE);

                binding.layoutInfoChat.setVisibility(View.GONE);
                binding.buttonSeeGroupMember.setVisibility(View.GONE);
                binding.buttonLeaveConversation.setVisibility(View.GONE);
            } catch (Exception e) {
                onBackPressed();
            }


        } else if (mConversation.getStyleChat() == Constants.KEY_TYPE_CHAT_GROUP) {
            if (mConversation.getConversationAvatar() != null) {
                binding.imageProfile.setImageBitmap(HelperFunction.getBitmapFromEncodedImageString(mConversation.getConversationColor()));
            }
            binding.textViewConversationName.setText(mConversation.getConversationName());

            binding.buttonCreateGroupChat.setVisibility(View.GONE);
            binding.buttonRestrict.setVisibility(View.GONE);
            binding.buttonBlock.setVisibility(View.GONE);
            binding.buttonDeleteChat.setVisibility(View.GONE);
            binding.buttonSearchInConversation.setBackgroundResource(R.drawable.bg_content_top);
        }
        binding.textViewEmoji.setText(mConversation.getQuickEmotions());
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageChangeImage.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
        binding.buttonBackground.setOnClickListener(v -> {
            onClickButtonBackground();
        });
        binding.buttonQuickEmotions.setOnClickListener(view -> {
            onClickButtonQuickEmotions();
        });
        binding.buttonSeeGroupMember.setOnClickListener(view -> {
            onClickButtonSeeGroupMember();
        });
    }

    private void onClickButtonBackground() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_set_conversation_background, null);
        // để hiện dialog tính theo k/C từ dưới lên trên. 2000dp
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());

        bottomSheetBehavior.setPeekHeight(2000);
        bottomSheetDialog.show();

        TextView textViewClickToChangeThePicture = bottomSheetView.findViewById(R.id.textViewClickToChangeThePicture);
        textViewClickToChangeThePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickChangeBackground.launch(intent);
            }
        });
        imageViewBackground = bottomSheetView.findViewById(R.id.imageViewBackground);
        if (mConversation.getConversationBackground() != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("images")
                    .child("background")
                    .child(mConversation.getConversationBackground());
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Sử dụng thư viện Picasso để tải ảnh từ URL về và gán vào ImageView
                    Picasso.get()
                            .load(uri)
                            .into(imageViewBackground);
                    imageViewBackground.setOnClickListener(view -> {
                        Bundle args = new Bundle();
                        args.putString("image_path", uri.toString());

                        DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                        dialogViewImageFragment.setArguments(args);
                        dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Xử lý khi có lỗi xảy ra
                    Log.e(ChatAdapter.class.toString(), "Error getting image from Firebase Storage.", e);
                }
            });
        }
    }

    private void onClickButtonQuickEmotions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_set_quick_emotions, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetBehavior.setPeekHeight(2000);
        bottomSheetDialog.show();

        RecyclerView emojiRCV = bottomSheetView.findViewById(R.id.emojiRCV);
        List<Emoji> mEmojis = new ArrayList<>();
        for (int i = 0; i < Constants.EMOJI_LIST.size(); i++){

            String charEmoji = Constants.EMOJI_LIST.get(i);
            Emoji emoji = new Emoji();
            emoji.setEmoji(charEmoji);
            if (mConversation.getQuickEmotions().equals(charEmoji)) {
                emoji.setChecker(true);
            } else {
                emoji.setChecker(false);
            }
            mEmojis.add(emoji);
        }
        EmojiAdapter emojiAdapter = new EmojiAdapter(mEmojis, new EmojiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Emoji emoji) {
                //update quickEmotions on Conversation
                DocumentReference conversationReference = database.collection(Constants.KEY_CONVERSATION)
                        .document(mConversation.getId());
                mConversation.setQuickEmotions(emoji.getEmoji());
                mConversation.setMessageTime(new Date());
                mConversation.setNewMessage(getString(R.string.emotion_set_to) + " " + emoji.getEmoji());
                mConversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                conversationReference.update(mConversation.toHashMap());
                //insert chatMessage style notification(1 dòng thông báo trên đoạn chat)
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                chatMessage.setMessage(getString(R.string.emotion_set_to) + " " + emoji.getEmoji());
                chatMessage.setDataTime(new Date());
                chatMessage.setConversationId(mConversation.getId());
                chatMessage.setStyleMessage(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION);
                HashMap<String, Object> message = chatMessage.toHashMap();
                database.collection(Constants.KEY_CHAT_MESSAGE)
                        .add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                bottomSheetDialog.cancel();
                                Toast.makeText(InfoChatActivity.this, R.string.successful_emotional_change, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                binding.textViewEmoji.setText(mConversation.getQuickEmotions());
            }
        });
        emojiRCV.setAdapter(emojiAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        emojiRCV.setLayoutManager(gridLayoutManager);
    }

    private void onClickButtonSeeGroupMember() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_see_group_members, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        View parentView = (View) bottomSheetView.getParent();
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parentView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        bottomSheetDialog.show();

        List<User> users = new ArrayList<>();

        database.collection(Constants.KEY_GROUP_MEMBER)
                .whereEqualTo(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID, mConversation.getId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                        if (!documents.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot: documents) {
                                User user = UserDatabase.getInstance(getApplicationContext()).userDAO().getUser(documentSnapshot.getString(Constants.KEY_GROUP_MEMBER_USER_ID));
                                users.add(user);
                            }
                        }
                        UserGroupMembersAdapter userGroupMembersAdapter = new UserGroupMembersAdapter(users, new UserCreateGroupListener() {
                            @Override
                            public void onUserClicked(User user, boolean checker) {

                            }
                        });
                        RecyclerView membersRCV = bottomSheetView.findViewById(R.id.membersRCV);
                        membersRCV.setAdapter(userGroupMembersAdapter);
                    }
                });
        ImageView imageBack = bottomSheetView.findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
        });

        ImageView imageAddMember = bottomSheetView.findViewById(R.id.imageAddMember);
        imageAddMember.setOnClickListener(view -> {
            onClickButtonAddMember(users);
        });

    }

    private void onClickButtonAddMember(List<User> users) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_add_member_to_conversation, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetBehavior.setPeekHeight(2000);
        bottomSheetDialog.show();

        TextView textViewCancel = bottomSheetView.findViewById(R.id.textViewCancel);
        textViewCancel.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
        });
        database.collection(Constants.KEY_USER)
                .whereNotIn("userId", getUserIds(users))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<User> otherUsers = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            User user = documentSnapshot.toObject(User.class);
                            user.setId(documentSnapshot.getId());
                            if (!isUserInList(user, users)) { // isUserInList là phương thức kiểm tra user có nằm trong danh sách users hiện tại hay không
                                otherUsers.add(user);
                            }
                        }
                        UserCreateGroupAdapter userCreateGroupAdapter = new UserCreateGroupAdapter(otherUsers, new UserCreateGroupListener() {
                            @Override
                            public void onUserClicked(User user, boolean checker) {

                            }
                        });
                        RecyclerView membersRCV = bottomSheetView.findViewById(R.id.membersRCV);
                        membersRCV.setAdapter(userCreateGroupAdapter);
                    }
                });


    }
    private List<String> getUserIds(List<User> users) {
        List<String> userIds = new ArrayList<>();
        for (User user : users) {
            userIds.add(user.getId());
        }
        return userIds;
    }
    private boolean isUserInList(User user, List<User> users) {
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            String encodedImage = HelperFunction.getEncodedImageStringFromBitmap(bitmap);

                            DocumentReference documentReference = database.collection(Constants.KEY_CONVERSATION).document(mConversation.getId());
                            mConversation.setConversationAvatar(encodedImage);
                            documentReference.update(mConversation.toHashMap());

                            ConversationDatabase.getInstance(getApplicationContext()).conversationDAO().updateConversation(mConversation);
                            Toast.makeText(this, R.string.change_avatar_successfully, Toast.LENGTH_SHORT).show();
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickChangeBackground = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Intent intent = new Intent(InfoChatActivity.this, EditImageActivity.class);
                        intent.putExtra("DATA", imageUri.toString());
                        intent.putExtra("style", Constants.KEY_REQUEST_CODE_BACKGROUND);
                        startActivityForResult(intent, Constants.KEY_REQUEST_CODE_BACKGROUND);
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == Constants.KEY_REQUEST_CODE_BACKGROUND) {
            String result = data.getStringExtra("RESULT");
            Uri uri = null;
            if (result != null) {
                uri = Uri.parse(result);
            }
            imageViewBackground.setImageURI(uri);
            Toast.makeText(this, uri.getPath(), Toast.LENGTH_SHORT).show();
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            StorageReference storageRef = storage.getReference();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = String.format("%s_%s%s", mConversation.getId(), timestamp, uri.getPath().substring(uri.getPath().lastIndexOf(".")));
            String path = String.format("images/background/%s", fileName);
            StorageReference imagesRef = storageRef.child(path);
            Uri finalUri = uri;
            imagesRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Upload successful
                            DocumentReference conversationReference = database.collection(Constants.KEY_CONVERSATION).document(mConversation.getId());
                            mConversation.setMessageTime(new Date());
                            mConversation.setNewMessage(getString(R.string.changed_wallpaper));
                            mConversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                            mConversation.setConversationBackground(fileName);

                            conversationReference.update(mConversation.toHashMap());
                            //insert chatMessage style notification(1 dòng thông báo trên đoạn chat)
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                            chatMessage.setMessage(getString(R.string.changed_wallpaper));
                            chatMessage.setDataTime(new Date());
                            chatMessage.setConversationId(mConversation.getId());
                            chatMessage.setStyleMessage(Constants.KEY_CHAT_MESSAGE_STYLE_MESSAGE_NOTIFICATION);
                            HashMap<String, Object> message = chatMessage.toHashMap();
                            database.collection(Constants.KEY_CHAT_MESSAGE)
                                    .add(message)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(InfoChatActivity.this, R.string.changed_wallpaper_successfully, Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                            // Tạo file mới trong thư mục của ứng dụng
                            File destinationFile = new File(getFilesDir(), fileName);

//                            // Sao chép tập tin vào thư mục của ứng dụng
                            try {
                                FileUtils.copyFile(finalUri.getPath(), destinationFile.getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
}