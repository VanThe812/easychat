package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.io.FileNotFoundException;
import java.io.InputStream;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ActivityInfoChatBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class InfoChatActivity extends AppCompatActivity {

    private ActivityInfoChatBinding binding;
    private Conversation mConversation = new Conversation();
    private PreferenceManager account;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }
    private void init() {
        database = FirebaseFirestore.getInstance();
        account = new PreferenceManager(getApplicationContext());
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
            } catch (Exception e) {
                onBackPressed();
            }


        } else if (mConversation.getStyleChat() == Constants.KEY_TYPE_CHAT_GROUP) {
            if (mConversation.getBackgroundImage() != null) {
                binding.imageProfile.setImageBitmap(HelperFunction.getBitmapFromEncodedImageString(mConversation.getBackgroundImage()));
            }
            binding.textViewConversationName.setText(mConversation.getConversationName());

            binding.buttonCreateGroupChat.setVisibility(View.GONE);
            binding.buttonSearchInConversation.setBackgroundResource(R.drawable.bg_content_top);
        }
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageChangeImage.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
        binding.buttonQuickEmotions.setOnClickListener(view -> {

        });
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
                            mConversation.setBackgroundImage(encodedImage);
                            documentReference.update(mConversation.toHashMap());

                            ConversationDatabase.getInstance(getApplicationContext()).conversationDAO().updateConversation(mConversation);
                            Toast.makeText(this, "Thay đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}