package va.vanthe.app_chat_2.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.SignInActivity;
import va.vanthe.app_chat_2.activities.UserProfileActivity;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.FriendDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentSettingsBinding;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuSettingFragment extends Fragment {

    private LayoutFragmentSettingsBinding binding;
    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentSettingsBinding.inflate(inflater, container, false);

        init();
        setListeners();

        return binding.getRoot();
    }
    @SuppressLint("SetTextI18n")
    private void init() {
        account = new PreferenceManager(getActivity().getApplicationContext());

        //get avatar user
        if (account.getString(Constants.KEY_ACCOUNT_IMAGE) != null && !account.getString(Constants.KEY_ACCOUNT_IMAGE).equals("")) {
            StorageReference imagesRef = storage.getReference()
                    .child("user")
                    .child("avatar")
                    .child(account.getString(Constants.KEY_ACCOUNT_IMAGE));
            imagesRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(binding.imageProfile))
                    .addOnFailureListener(Throwable::printStackTrace);
        }


        binding.textName.setText(account.getString(Constants.KEY_ACCOUNT_FIRST_NAME) + " " + account.getString(Constants.KEY_ACCOUNT_LAST_NAME));
    }

    private void setListeners() {
        binding.buttonLogout.setOnClickListener(view -> {
            signOut();
        });
        binding.buttonUserProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra(Constants.KEY_ACCOUNT_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID));
            startActivity(intent);
        });
    }
    private void signOut() {
        HelperFunction.showToast(getString(R.string.signing_out), getContext());
        account.clear();
        GroupMemberDatabase.getInstance(getContext()).groupMemberDAO().deleteAllGroupMember();
        ConversationDatabase.getInstance(getContext()).conversationDAO().deleteAllConversation();
        UserDatabase.getInstance(getContext()).userDAO().deleteAllUser();
        FriendDatabase.getInstance(getContext()).friendDAO().deleteAllFriend();

        // Còn xóa ảnh nữa mà ko cần, hehehe
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
