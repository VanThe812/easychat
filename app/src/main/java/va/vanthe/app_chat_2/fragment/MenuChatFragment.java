package va.vanthe.app_chat_2.fragment;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import va.vanthe.app_chat_2.MyApplication;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.activities.CreateAGroupActivity;
import va.vanthe.app_chat_2.activities.SearchActivity;
import va.vanthe.app_chat_2.activities.test;
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
    private List<Conversation> mConversations;
    private ConversionsAdapter conversionsAdapter;
    private PreferenceManager account;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentChatBinding.inflate(inflater, container, false);

//        binding.imageProfile.setOnClickListener(view -> {
//            sendCustomNotification();
//        });

        init();
        loadUserDetails();
        setListeners();
        listenConversations();
        return binding.getRoot();

    }

    private void sendCustomNotification() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; //giảm kích thước ảnh xuống còn 1/2
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inTempStorage = new byte[16 * 1024];
        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.test, options);
        // Cài âm thanh thông báo
        Uri sound = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.messaging);

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(getContext(), test.class);
        resultIntent.putExtra("hii", "xin chao");
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(getNotificationId(),
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        RemoteViews notificationLayout = new RemoteViews(getActivity().getPackageName(), R.layout.layout_custom_natification);
        notificationLayout.setTextViewText(R.id.textViewTitleCustomNotification, "Title custom notification");
        notificationLayout.setTextViewText(R.id.textViewMessageCustomNotification, "Văn Thế đã gửi một hình ảnh");
        notificationLayout.setImageViewBitmap(R.id.imageMessageImage, bitmapImage);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_camera)
                .setSound(sound)
                .setCustomContentView(notificationLayout)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(getNotificationId(), builder.build());

    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }



    private void init() {
        account = new PreferenceManager(getActivity().getApplicationContext());
        mConversations = new ArrayList<>();
        conversionsAdapter = new ConversionsAdapter(mConversations, account.getString(Constants.KEY_ACCOUNT_USER_ID));
        binding.conversionsRCV.setAdapter(conversionsAdapter);
        conversionsAdapter.notifyDataSetChanged();
        binding.conversionsRCV.smoothScrollToPosition(0);
        binding.conversionsRCV.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void setListeners() {

        binding.inputSearch.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
        binding.imageCreateGroupChat.setOnClickListener(v -> {
            DialogFragment dialogFragment = new DialogFragment();
            dialogFragment.show(getActivity().getSupportFragmentManager(), "my_dialog");
        });

    }




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
                            .document(groupMemberSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(DocumentSnapshot conversationSnapshot, FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(MenuChatFragment.class.toString(), "Lỗi khi lắng nghe sự thay đổi của bản ghi", e);
                                        return;
                                    }

                                    if (conversationSnapshot != null && conversationSnapshot.exists()) {
                                        Log.d(MenuChatFragment.class.toString(), "Dữ liệu của bản ghi đã thay đổi: " + conversationSnapshot.getData());
//
                                        int checkConversation = ConversationDatabase.getInstance(getContext()).conversationDAO().checkConversation(conversationSnapshot.getId());
                                        Conversation conversation = conversationSnapshot.toObject(Conversation.class);
                                        conversation.setId(conversationSnapshot.getId());

                                        if (checkConversation == 0) {
                                            ConversationDatabase.getInstance(getContext()).conversationDAO().insertConversation(conversation);

                                            getGroupMembers(new OnTaskCompleted() {
                                                @Override
                                                public void onTaskCompleted() {
                                                    addOneConversationToList(conversation);
                                                }
                                            }, conversation.getId());

                                        } else {
                                            addOneConversationToList(conversation);
                                        }
                                    } else {
                                        Log.d(MenuChatFragment.class.toString(), "Bản ghi đã bị xóa");
                                    }
                                }
                            });
//                    database.collection(Constants.KEY_CONVERSATION)
////                            .document(groupMemberSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID))
//                            .whereEqualTo(Constants.KEY_CONVERSATION_ID, groupMemberSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID))
//                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                                    if(error != null) {
//                                        return;
//                                    }
//                                    if(value != null) {
//                                        for (DocumentChange conversationChange : value.getDocumentChanges()) {
//                                            Log.e(MenuChatFragment.class.toString(), conversationChange.toString());
//                                            if (conversationChange.getType() == DocumentChange.Type.ADDED) { //nếu có thêm dữ liệu hoặc là khi vừa mở app
//                                                DocumentSnapshot conversationSnapshot = conversationChange.getDocument();
//
//                                                int checkConversation = ConversationDatabase.getInstance(getContext()).conversationDAO().checkConversation(conversationSnapshot.getId());
//
//                                                Conversation conversation = conversationSnapshot.toObject(Conversation.class);
//                                                conversation.setId(conversationSnapshot.getId());
//
//                                                if (checkConversation == 0) {
//                                                    ConversationDatabase.getInstance(getContext()).conversationDAO().insertConversation(conversation);
//
//                                                    getGroupMembers(new OnTaskCompleted() {
//                                                        @Override
//                                                        public void onTaskCompleted() {
//                                                            addOneConversationToList(conversation);
//                                                        }
//                                                    }, conversation.getId());
//
//                                                } else {
//                                                    addOneConversationToList(conversation);
//                                                }
//
//                                            }
//                                            else if(conversationChange.getType() == DocumentChange.Type.MODIFIED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
//
//                                                DocumentSnapshot conversationSnapshot = conversationChange.getDocument();
//                                                Conversation conversation = conversationSnapshot.toObject(Conversation.class);
//                                                conversation.setId(conversationSnapshot.getId());
//
//                                                for (int i = 0; i < mConversations.size(); i++) {
//                                                    if (conversation.getId().equals(mConversations.get(i).getId())) {
//
//                                                        mConversations.get(i).setMessageTime(conversation.getMessageTime());
//                                                        mConversations.get(i).setNewMessage(conversation.getNewMessage());
//                                                        mConversations.get(i).setStyleChat(conversation.getStyleChat());
//                                                        mConversations.get(i).setSenderId(conversation.getSenderId());
//
//                                                        Collections.sort(mConversations, (obj1, obj2) -> obj2.getMessageTime().compareTo(obj1.getMessageTime()));
//                                                        conversionsAdapter.notifyDataSetChanged();
//                                                        binding.conversionsRCV.smoothScrollToPosition(0);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            });

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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                    AtomicInteger count = new AtomicInteger();
                                    final int[] i = {0};
                                    database.collection(Constants.KEY_USER)
                                            .document(groupMember.getUserId())
                                            .get()
                                            .addOnSuccessListener(userSnapshot -> {
                                                if (userSnapshot.exists()) {
                                                    User user = userSnapshot.toObject(User.class);
                                                    user.setId(userSnapshot.getId());
                                                    int checkUser = UserDatabase.getInstance(getContext()).userDAO().checkUser(userSnapshot.getId());
                                                    if (checkUser == 0) {
                                                        UserDatabase.getInstance(getContext()).userDAO().insertUser(user);
                                                    }
                                                }
                                                if (groupMemberSnapshot.getId() == groupMemberSnapshots.getDocuments().get(groupMemberSnapshots.size()-1).getId()) {
                                                    // Tất cả các tác vụ bất đồng bộ đã hoàn thành, gọi interface
                                                    listener.onTaskCompleted();
                                                }
                                            });
                                }
                            } else {
                                // QuerySnapshot rỗng
                            }
                        } else {
                            // Xử lý lỗi
                        }
                    }
                });
    }

    private void addOneConversationToList(Conversation conversation) {

        if (mConversations.contains(conversation)) {
            mConversations.remove(conversation);
            mConversations.add(conversation);
        } else {
            mConversations.add(conversation);
        }
        Collections.sort(mConversations, (obj1, obj2) -> obj2.getMessageTime().compareTo(obj1.getMessageTime()));
        conversionsAdapter.notifyDataSetChanged();
        binding.conversionsRCV.smoothScrollToPosition(0);
    }

    private void loadUserDetails() {
        byte[] bytes = Base64.decode(account.getString(Constants.KEY_ACCOUNT_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void loading(@NonNull Boolean isLoading) {
        if(isLoading) {
            binding.conversionsRCV.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.conversionsRCV.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

}
