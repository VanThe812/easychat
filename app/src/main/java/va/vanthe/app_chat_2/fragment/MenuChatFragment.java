package va.vanthe.app_chat_2.fragment;

import static va.vanthe.app_chat_2.adapters.UsersAdapter.userListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.adapters.RecentConversionsAdapter;
import va.vanthe.app_chat_2.adapters.UsersAdapter;
import va.vanthe.app_chat_2.adapters.ViewPagerSearchAdapter;
import va.vanthe.app_chat_2.database.GroupMemberDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentChatBinding;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.entity.ChatMessage;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuChatFragment extends Fragment  implements UserListener {

    private LayoutFragmentChatBinding binding;
    private List<ChatMessage> conversation;
    private RecentConversionsAdapter recentConversionsAdapter;
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

        init();
        loadUserDetails();
        setListeners();
        listenConversations();
//        dataTest();
        return binding.getRoot();

    }

    private void init() {
//        conversation = new ArrayList<>();
//        recentConversionsAdapter = new RecentConversionsAdapter(conversation, this);
//        binding.conversionsRCV.setAdapter(recentConversionsAdapter);
        account = new PreferenceManager(getActivity().getApplicationContext());

    }
    private void loadUserDetails() {
        byte[] bytes = Base64.decode(account.getString(Constants.KEY_ACCOUNT_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void setListeners() {
        binding.textviewCancel.setOnClickListener(view -> {
            //tạo hàm đưa vào
            binding.imageProfile.setVisibility(View.VISIBLE);
            binding.textName.setVisibility(View.VISIBLE);
            binding.imageCreateGroupChat.setVisibility(View.VISIBLE);
            binding.userChatRCV.setVisibility(View.VISIBLE);
            binding.conversionsRCV.setVisibility(View.VISIBLE);

            binding.textviewCancel.setVisibility(View.GONE);
            binding.viewpagerSearch.setVisibility(View.GONE);
            //
            binding.inputSearch.clearFocus();
            binding.inputSearch.setText("");
            //
            hideSoftKeyboard();
        });
        binding.inputSearch.setOnFocusChangeListener((view, b) -> {
            if(b) {
                //tạo hàm đưa vào
                binding.imageProfile.setVisibility(View.GONE);
                binding.textName.setVisibility(View.GONE);
                binding.imageCreateGroupChat.setVisibility(View.GONE);
                binding.userChatRCV.setVisibility(View.GONE);
                binding.conversionsRCV.setVisibility(View.GONE);

                binding.textviewCancel.setVisibility(View.VISIBLE);
                binding.viewpagerSearch.setVisibility(View.VISIBLE);
                //
                ViewPagerSearchAdapter adapter = new ViewPagerSearchAdapter(getActivity().getSupportFragmentManager());
                binding.viewpagerSearch.setAdapter(adapter);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.conversionsRCV.setLayoutManager(layoutManager);

        binding.conversionsRCV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (layoutManager.findFirstVisibleItemPosition() == 0) {
                        binding.layout1.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (layoutManager.findFirstVisibleItemPosition() == 0) {
                        binding.layout1.setVisibility(View.GONE);
                    }

                }
            }
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String textSearch = editable.toString().trim();
                if (textSearch.matches("")) {
                    SearchFragment.binding.layoutRecent.setVisibility(View.VISIBLE);
                    SearchFragment.binding.layoutSuggest.setVisibility(View.VISIBLE);
                    SearchFragment.binding.searchRCV.setVisibility(View.GONE);

                } else {
                    SearchFragment.binding.layoutRecent.setVisibility(View.GONE);
                    SearchFragment.binding.layoutSuggest.setVisibility(View.GONE);
                    SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);

                    if (textSearch.length() > 11) {
                        if (textSearch.substring(0, 3).equals("+84")) {
                            database.collection(Constants.KEY_USER)
                                    .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, textSearch)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot documentSnapshots) {
                                            List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                                            if (!documents.isEmpty()) {
                                                // check đã nhắn tin bao giờ chưa
                                                User user = documents.get(0).toObject(User.class);
                                                GroupMember groupMember = GroupMemberDatabase.getInstance(getContext()).groupMemberDAO().hasTextedUser(user.getId());
                                                if (groupMember.getId().equals("")) {
                                                    //chưa nhắn
                                                } else {
                                                    //da nhan

                                                    UsersAdapter usersAdapter = new UsersAdapter(List<user>, userListener);
                                                    SearchFragment.binding.searchRCV.setAdapter(usersAdapter);
                                                    SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);
                                                }
                                                database.collection(Constants.KEY_GROUP_MEMBER)
                                                        .whereEqualTo(Constants.KEY_GROUP_MEMBER_USER_ID, documents.get(0).getId())
                                                        .whereEqualTo(Constants.KEY_GROUP_MEMBER_RECEIVER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                                                        .get()
                                                        .addOnSuccessListener(queryDocumentSnapshots -> {
                                                            List<DocumentSnapshot> documentGroupMember = queryDocumentSnapshots.getDocuments();
                                                            if(!queryDocumentSnapshots.isEmpty()) { // Đã từng nhắn tin
                                                                Toast.makeText(getContext(), "Co ne", Toast.LENGTH_SHORT).show();
                                                                List<User> users = new ArrayList<>();
                                                                User user = new User();
                                                                user.setId(documents.get(0).getId());
                                                                user.setImage(documents.get(0).getString(Constants.KEY_ACCOUNT_IMAGE));
                                                                user.setFirstName(documents.get(0).getString(Constants.KEY_ACCOUNT_FIRST_NAME));
                                                                user.setLastName(documents.get(0).getString(Constants.KEY_ACCOUNT_LAST_NAME));
//                                                                user.conversationId = documentGroupMember.get(0).getString(Constants.KEY_CONVERSATION_ID);
                                                                users.add(user);

                                                                UsersAdapter usersAdapter = new UsersAdapter(users, userListener);
                                                                SearchFragment.binding.searchRCV.setAdapter(usersAdapter);
                                                                SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);

                                                            } else { // Chưa nhắn tin với người này
                                                                Toast.makeText(getContext(), "Hong nhan", Toast.LENGTH_SHORT).show();
                                                                List<User> users = new ArrayList<>();
                                                                User user = new User();
                                                                user = documents.get(0).toObject(User.class);
                                                                user.setId(documents.get(0).getId());
//                                                                user.setImage(documents.get(0).getString(Constants.KEY_ACCOUNT_IMAGE));
//                                                                user.setFirstName(documents.get(0).getString(Constants.KEY_ACCOUNT_FIRST_NAME));
//                                                                user.setLastName(documents.get(0).getString(Constants.KEY_ACCOUNT_LAST_NAME));
                                                                users.add(user);

                                                                UsersAdapter usersAdapter = new UsersAdapter(users, userListener);
                                                                SearchFragment.binding.searchRCV.setAdapter(usersAdapter);
                                                                SearchFragment.binding.searchRCV.setVisibility(View.VISIBLE);

                                                            }
                                                        });



                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });

    }




    private void listenConversations() {
    }

//    @Override
//    public void onConversionCLicked(User user) {
//        Intent intent = new Intent(getContext(), ChatActivity.class);
//        // chuyền thông tin của ng muốn nhắn tin qua activity chat
////        intent.putExtra(Constants.KEY_USER, user);
//        startActivity(intent);
//    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getContext(), ChatMessageActivity.class);
        intent.putExtra(Constants.KEY_USER, (CharSequence) user);
        startActivity(intent);
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
