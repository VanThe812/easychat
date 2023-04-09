package va.vanthe.app_chat_2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

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
    private Conversation mConversation;
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

            } catch (Exception e) {
                onBackPressed();
            }


        } else if (mConversation.getStyleChat() == Constants.KEY_TYPE_CHAT_GROUP) {
            if (mConversation.getBackgroundImage() != null) {
                binding.imageProfile.setImageBitmap(HelperFunction.getBitmapFromEncodedImageString(mConversation.getBackgroundImage()));
            }
            binding.textViewConversationName.setText(mConversation.getConversationName());

        }
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }
}