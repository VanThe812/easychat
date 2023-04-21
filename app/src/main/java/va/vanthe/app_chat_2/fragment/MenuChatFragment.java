package va.vanthe.app_chat_2.fragment;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import va.vanthe.app_chat_2.activities.SearchActivity;
import va.vanthe.app_chat_2.adapters.ConversionsAdapter;
import va.vanthe.app_chat_2.database.ConversationDatabase;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentChatBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.listeners.OnTaskCompleted;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuChatFragment extends Fragment {

    private LayoutFragmentChatBinding binding;
    private final List<Conversation> mConversations = new ArrayList<>();
    private ConversionsAdapter conversionsAdapter;
    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentChatBinding.inflate(inflater, container, false);


        init();
        loadUserDetails();
        setListeners();
        listenConversations();
        return binding.getRoot();

    }


    private void init() {
        if (getContext() != null) { // kiểm tra context có null hay không, tránh trường hợp NullPointerException
            account = new PreferenceManager(getContext()); // khởi tạo dữ liệu cho account
        } else { // nếu context null lập tức tải lại app
            requireActivity().recreate();
        }

        conversionsAdapter = new ConversionsAdapter(mConversations, account.getString(Constants.KEY_ACCOUNT_USER_ID));
        binding.conversionsRCV.setAdapter(conversionsAdapter);
//        binding.conversionsRCV.smoothScrollToPosition(0);
        binding.conversionsRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void setListeners() {

        // Mở Activity Search
        binding.inputSearch.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
        // Mở ra dialog dạng fragment tạo nhóm chat
        binding.imageCreateGroupChat.setOnClickListener(v -> {
            DialogFragment dialogFragment = new DialogFragment();
            dialogFragment.show(requireActivity().getSupportFragmentManager(), "dialog_create_group_chat");
        });

    }



    // Lắng nghe dữ liệu
    private void listenConversations() {
        database.collection(Constants.KEY_GROUP_MEMBER)
                .whereEqualTo(Constants.KEY_GROUP_MEMBER_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
//                .orderBy(Constants.KEY_GROUP_MEMBER_TIME_JOIN)
//                .limit(10)
                .addSnapshotListener(eventListenerConversation);
    }
    private final EventListener<QuerySnapshot> eventListenerConversation = (value, error) -> {
        if(error != null) {
            return;
        }
        if(value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) { //nếu có thêm dữ liệu hoặc là khi vừa mở app
                    DocumentSnapshot groupMemberSnapshot = documentChange.getDocument();

                    database.collection(Constants.KEY_CONVERSATION)
                            .document(Objects.requireNonNull(groupMemberSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID)))
                            .addSnapshotListener((conversationSnapshot, e) -> {
                                if (e != null) {
                                    Log.w(MenuChatFragment.class.toString(), "Lỗi khi lắng nghe sự thay đổi của bản ghi", e);
                                    return;
                                }

                                if (conversationSnapshot != null && conversationSnapshot.exists()) {
                                    int checkConversation = ConversationDatabase.getInstance(getContext()).conversationDAO().checkConversation(conversationSnapshot.getId());
                                    Conversation conversation = conversationSnapshot.toObject(Conversation.class);
                                    if (conversation != null) {
                                        conversation.setId(conversationSnapshot.getId());
                                        if (checkConversation == 0) {
                                            ConversationDatabase.getInstance(getContext()).conversationDAO().insertConversation(conversation);

                                            getGroupMembers(() -> addOneConversationToList(conversation), conversation.getId());

                                        } else {
                                            addOneConversationToList(conversation);
                                        }
                                    }



                                } else {
                                    Log.d(MenuChatFragment.class.toString(), "Bản ghi đã bị xóa");
                                }
                            });

                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
                    Log.d("MenuChatFragment", "MODIFIED");
                }
            }
        }
    };

    private void getGroupMembers(final OnTaskCompleted listener, String conversationId) {
        database.collection(Constants.KEY_GROUP_MEMBER)
                .whereEqualTo(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID, conversationId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot groupMemberSnapshots = task.getResult();
                        if (!groupMemberSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot groupMemberSnapshot : groupMemberSnapshots) {
                                GroupMember groupMember = groupMemberSnapshot.toObject(GroupMember.class);
                                groupMember.setId(groupMemberSnapshot.getId());
                                int checkGroupMember = GroupMemberDatabase.getInstance(getContext()).groupMemberDAO().checkGroupMember(groupMemberSnapshot.getId());
                                if (checkGroupMember == 0) {
                                    GroupMemberDatabase.getInstance(getContext()).groupMemberDAO().insertGroupMember(groupMember);
                                }

                                database.collection(Constants.KEY_USER)
                                        .document(groupMember.getUserId())
                                        .get()
                                        .addOnSuccessListener(userSnapshot -> {
                                            if (userSnapshot.exists()) {
                                                User user = userSnapshot.toObject(User.class);
                                                if (user != null) {
                                                    user.setId(userSnapshot.getId());
                                                    int checkUser = UserDatabase.getInstance(getContext()).userDAO().checkUser(userSnapshot.getId());
                                                    if (checkUser == 0) {
                                                        UserDatabase.getInstance(getContext()).userDAO().insertUser(user);
                                                    }
                                                }

                                            }
                                            if (groupMemberSnapshot.getId().equals(groupMemberSnapshots.getDocuments().get(groupMemberSnapshots.size() - 1).getId())) {
                                                // Tất cả các tác vụ bất đồng bộ đã hoàn thành, gọi interface
                                                listener.onTaskCompleted();
                                            }
                                        });
                            }
                        }
                    } else {
                        // Không có cuộc hội thoại nào
                        // In ra gian diện chào mừng
                        Toast.makeText(getContext(), "Hi, xin chào bạn đến với EasyChat, nơi bạn thỏa sức vui vẻ với bạn bè", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addOneConversationToList(Conversation conversation) {
//        int count = mConversations.size();
        if (mConversations.contains(conversation)) {
            mConversations.remove(conversation);
            mConversations.add(conversation);
        } else {
            mConversations.add(conversation);
        }
        mConversations.sort((obj1, obj2) -> obj2.getMessageTime().compareTo(obj1.getMessageTime()));
        conversionsAdapter.notifyDataSetChanged();
        binding.conversionsRCV.smoothScrollToPosition(0);

//        if(count == 0) {
//            conversionsAdapter.notifyDataSetChanged();
//        }else{
//            conversionsAdapter.notifyItemRangeInserted(mConversations.size(), mConversations.size());
//            binding.conversionsRCV.smoothScrollToPosition(mConversations.size() - 1);
//        }
    }

    private void loadUserDetails() {
        byte[] bytes = Base64.decode(account.getString(Constants.KEY_ACCOUNT_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }


}
