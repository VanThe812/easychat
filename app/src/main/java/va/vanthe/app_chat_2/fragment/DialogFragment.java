package va.vanthe.app_chat_2.fragment;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.activities.UserProfileActivity;
import va.vanthe.app_chat_2.adapters.ContactAdapter;
import va.vanthe.app_chat_2.adapters.UserCreateGroupAdapter;
import va.vanthe.app_chat_2.adapters.UserCreateGroupCheckerAdapter;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentCreateAGroupBinding;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.UserCreateGroupListener;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    private LayoutFragmentCreateAGroupBinding binding;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private List<User> mUsersChecker = new ArrayList<>();
    private List<User> mUsers = new ArrayList<>();
    private UserCreateGroupCheckerAdapter userCreateGroupCheckerAdapter;
    private PreferenceManager account;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Tạo dialog với style không có title
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set layout cho dialog
        dialog.setContentView(R.layout.layout_fragment_create_a_group);

        // Set background cho dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Thiết lập kích thước dialog
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        // Lấy kích thước màn hình
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int margin = getResources().getDimensionPixelSize(R.dimen.dialog_top_margin);
        int width = size.x;
        int height = size.y;

        lp.width = width;
        lp.height = height - margin;
        lp.gravity = Gravity.BOTTOM;

        window.setAttributes(lp);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = LayoutFragmentCreateAGroupBinding.inflate(inflater, container, false);
        account = new PreferenceManager(getActivity().getApplicationContext());

        binding.textCancel.setOnClickListener(view -> {
            // Đóng Dialog
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        binding.inputNameGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!mUsersChecker.isEmpty() && mUsersChecker.size() >= 2 && editable.length() > 0) {
                    binding.textCreateGroup.setTextColor(Color.BLUE);
                }else {
                    binding.textCreateGroup.setTextColor(Color.GRAY);
                }
            }
        });

        binding.textCreateGroup.setOnClickListener(view -> {
            if (!mUsersChecker.isEmpty() && mUsersChecker.size() >= 2 && binding.inputNameGroup.length() > 0) {
                // bắt đầu tạo nhóm chat
                /// Tạo conversation
                Conversation conversation = new Conversation();
                conversation.setCreateTime(new Date());
                conversation.setNewMessage(account.getString(Constants.KEY_ACCOUNT_LAST_NAME) + " đã tạo nhóm.");
                conversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                conversation.setMessageTime(new Date());
                conversation.setStyleChat(Constants.KEY_TYPE_CHAT_GROUP);
                conversation.setConversationName(binding.inputNameGroup.getText().toString().trim());
                conversation.setQuickEmotions("\uD83C\uDF49");
                HashMap<String, Object> conversationMap = conversation.toHashMap();

                database.collection(Constants.KEY_CONVERSATION)
                        .add(conversationMap)
                        .addOnSuccessListener(documentReference -> {
                            conversation.setId(documentReference.getId());
                            ConversationDatabase.getInstance(getContext()).conversationDAO().insertConversation(conversation);

                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            List<GroupMember> mGroupMembers = new ArrayList<>();
                            User user_me = new User();
                            user_me.setId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                            mUsersChecker.add(user_me);

                            for (User user : mUsersChecker) {
                                DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.KEY_GROUP_MEMBER).document();

                                GroupMember groupMember = new GroupMember();
                                groupMember.setUserId(user.getId());
                                groupMember.setConversationId(documentReference.getId());
                                groupMember.setTimeJoin(new Date());
                                groupMember.setStatus(1);
                                groupMember.setId(docRef.getId());

                                mGroupMembers.add(groupMember);

                                HashMap<String, Object> groupMemberMap = groupMember.toHashMap();
                                batch.set(docRef, groupMemberMap);

                                // kiểm tra user đã có trong db trên máy hay chưa, chưa thì add vô
                                if (UserDatabase.getInstance(getContext()).userDAO().checkUser(user.getId()) == 0) {
                                    UserDatabase.getInstance(getContext()).userDAO().insertUser(user);
                                }
                            }
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            for (GroupMember groupMember : mGroupMembers) {
                                                GroupMemberDatabase.getInstance(getContext()).groupMemberDAO().insertGroupMember(groupMember);
                                            }
                                            ChatMessage chatMessage = new ChatMessage();
                                            chatMessage.setConversationId(conversation.getId());
                                            chatMessage.setDataTime(new Date());
                                            chatMessage.setMessage("");
                                            chatMessage.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                                            chatMessage.setStyleMessage(Constants.KEY_CHAT_MESSAGE_STYLE_NEW_CHAT);

                                            database.collection(Constants.KEY_CHAT_MESSAGE).add(chatMessage.toHashMap());

                                            // xong là đóng dialog
                                            Dialog dialog = getDialog();
                                            if (dialog != null) {
                                                dialog.dismiss();
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Thao tác thất bại
                                        }
                                    });
                        })
                        .addOnFailureListener(runnable -> {
                            Toast.makeText(getContext(), "Có một số lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        });
                /// Tạo các bản ghi groupMember
            }
        });
        userCreateGroupCheckerAdapter = new UserCreateGroupCheckerAdapter(mUsersChecker, user -> {
            mUsersChecker.remove(user);
//            int position = -1;
//            for (int i = 0; i < mUsers.size(); i++) {
//                User user1 = mUsers.get(i);
//                if (user1.getId() == user.getId()) {
//                    position = i;
//                    break;
//                }
//            }
            if (!mUsersChecker.isEmpty() && mUsersChecker.size() >= 2 && binding.inputNameGroup.length() > 0) {
                binding.textCreateGroup.setTextColor(Color.BLUE);
            }else {
                binding.textCreateGroup.setTextColor(Color.GRAY);
            }
            userCreateGroupCheckerAdapter.notifyDataSetChanged();
        });
        binding.usersCheckerRcv.setAdapter(userCreateGroupCheckerAdapter);
        binding.usersCheckerRcv.setVisibility(View.VISIBLE);
        getListFriend();
        return binding.getRoot();
    }



    private void getListFriend() {
        List<User> mUsers = new ArrayList<>();


        UserCreateGroupAdapter userCreateGroupAdapter = new UserCreateGroupAdapter(mUsers, (user, checker) -> {
            if (checker) {
                mUsersChecker.add(user);
            } else {
                mUsersChecker.remove(user);
            }
            userCreateGroupCheckerAdapter.notifyDataSetChanged();
            if (!mUsersChecker.isEmpty() && mUsersChecker.size() >= 2 && binding.inputNameGroup.length() > 0) {
                binding.textCreateGroup.setTextColor(Color.BLUE);
            }else {
                binding.textCreateGroup.setTextColor(Color.GRAY);
            }
        });
        binding.usersRcv.setAdapter(userCreateGroupAdapter);

        database.collection(Constants.KEY_FRIEND)
                .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                .whereEqualTo(Constants.KEY_FRIEND_STATUS, Constants.KEY_FRIEND_STATUS_DAKETBAN)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        return;
                    }
                    if(value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                DocumentSnapshot friendSnapshot = documentChange.getDocument();
                                Friend friend = friendSnapshot.toObject(Friend.class);
                                friend.setId(friendSnapshot.getId());

                                database.collection(Constants.KEY_USER)
                                        .document(friend.getUserFriendId())
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            User user = documentSnapshot.toObject(User.class);
                                            assert user != null;
                                            user.setId(documentSnapshot.getId());

                                            mUsers.add(user);

                                            mUsers.sort(Comparator.comparing(User::getLastName));

                                            userCreateGroupAdapter.notifyDataSetChanged();
                                            binding.usersRcv.invalidate();
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            }
                        }
                    }
                });

    }

}
