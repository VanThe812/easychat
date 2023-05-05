package va.vanthe.app_chat_2.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.UserProfileActivity;
import va.vanthe.app_chat_2.adapters.ContactAdapter;
import va.vanthe.app_chat_2.databinding.LayoutFragmentFriendBinding;
import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuFriendFragment extends Fragment  {

    private LayoutFragmentFriendBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private PreferenceManager account;


    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentFriendBinding.inflate(inflater, container, false);

        init();
        setListeners();
        getListFriend();


        return binding.getRoot();

    }


    private void init() {
        if (getContext() != null) { // kiểm tra context có null hay không, tránh trường hợp NullPointerException
            account = new PreferenceManager(getContext()); // khởi tạo dữ liệu cho account
        } else { // nếu context null lập tức tải lại app
            requireActivity().recreate();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setListeners() {
        binding.imageAddFriend.setOnClickListener(view -> {
            /// Gọi ra BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_add_friend, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
            bottomSheetBehavior.setPeekHeight(2200);

            bottomSheetDialog.setCancelable(false);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();

            bottomSheetView.findViewById(R.id.textviewDone).setOnClickListener(view2 -> bottomSheetDialog.dismiss());

            // Bắt sự kiện của input search
            RecyclerView rcvRequestSearch = bottomSheetView.findViewById(R.id.rcvRequestSearch);
            EditText inputSearch = bottomSheetView.findViewById(R.id.searchContact);
            inputSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    //Xử lý dữ liệu ở đây
                    String textSearch = editable.toString().trim();
                    if (textSearch.length() > 11 && textSearch.startsWith("+84")) {
//                        if (!textSearch.equals(account.getString(Constants.KEY_ACCOUNT_PHONE_NUMBER))) {
                            database.collection(Constants.KEY_USER)
                                    .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, textSearch)
                                    .get()
                                    .addOnSuccessListener(documentSnapshots -> {
                                        List<DocumentSnapshot> documents = documentSnapshots.getDocuments();
                                        if (!documents.isEmpty()) {
                                            User user = documents.get(0).toObject(User.class);
                                            assert user != null;
                                            user.setId(documents.get(0).getId());
                                            List<User> users = new ArrayList<>();
                                            users.add(user);
                                            ContactAdapter userSearchAdapter = new ContactAdapter(users, user1 -> {
                                                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                                intent.putExtra(Constants.KEY_ACCOUNT_USER_ID, user1.getId());
                                                startActivity(intent);
                                            });
                                            rcvRequestSearch.setAdapter(userSearchAdapter);
                                            bottomSheetView.findViewById(R.id.layoutRequestSearch).setVisibility(View.VISIBLE);
                                        }
                                    });

//                        } else {
//                            // la chinh ban than mk
//                        }

                    }
                    else {
                        bottomSheetView.findViewById(R.id.layoutRequestSearch).setVisibility(View.GONE);
                    }
                }
            });
            /// Lấy dữ liệu cho BottomSheetDialog
            // Ở giao diện sẽ hiện những người đã gửi lời mời kết bạn cho mình và những người có thể biết lấy từ trong danh bạ
            // Đầu tiên sẽ lấy ra những người gửi lời mời kb
            List<User> mUsersNhanLoiMoi = new ArrayList<>();
            bottomSheetView.findViewById(R.id.layoutFriendRequest).setVisibility(View.GONE);
            ContactAdapter nhanLoiMoiContactAdapter = new ContactAdapter(mUsersNhanLoiMoi, user -> {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra(Constants.KEY_ACCOUNT_USER_ID, user.getId());
                startActivity(intent);
            });

            RecyclerView nhanLopMoiRCV = bottomSheetView.findViewById(R.id.contactRCV);
            nhanLopMoiRCV.setAdapter(nhanLoiMoiContactAdapter);

            database.collection(Constants.KEY_FRIEND)
                .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                .whereEqualTo(Constants.KEY_FRIEND_STATUS, Constants.KEY_FRIEND_STATUS_NHANLOIMOI)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        return;
                    }
                    if(value != null) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                Log.e("Log", "ADDED");
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
                                            mUsersNhanLoiMoi.add(user);
                                            bottomSheetView.findViewById(R.id.layoutFriendRequest).setVisibility(View.VISIBLE);
                                            mUsersNhanLoiMoi.sort(Comparator.comparing(User::getLastName));
                                            nhanLoiMoiContactAdapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            } else if(documentChange.getType() == DocumentChange.Type.REMOVED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
                                Log.e("Log", "REMOVED");
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
                                            mUsersNhanLoiMoi.remove(user);
                                            if (mUsersNhanLoiMoi.size() <= 0) {
                                                bottomSheetView.findViewById(R.id.layoutFriendRequest).setVisibility(View.GONE);
                                            }
                                            mUsersNhanLoiMoi.sort(Comparator.comparing(User::getLastName));
                                            nhanLoiMoiContactAdapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            }
                        }
                    }
                });

            // Tiếp theo sẽ lấy ra những ng trong danh bạ mà chưa kết bạn
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            @SuppressLint("Recycle") Cursor cursor  = requireContext().getContentResolver().query(uri, null, null, null, null);
            List<String> mPhones = new ArrayList<>();
            while (cursor.moveToNext()) {
                int viTriCotPhone = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phone = cursor.getString(viTriCotPhone);
                mPhones.add(HelperFunction.convertPhoneNumber(phone.replaceAll("[^0-9]", "")));
            }
            List<User> mUsersChuaLamGi = new ArrayList<>();
            ContactAdapter chuaLamGiContactAdapter = new ContactAdapter(mUsersChuaLamGi, user -> {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra(Constants.KEY_ACCOUNT_USER_ID, user.getId());
                startActivity(intent);
            });
            RecyclerView chuaLamGiRCV = bottomSheetView.findViewById(R.id.someoneKnowRCV);
            chuaLamGiRCV.setAdapter(chuaLamGiContactAdapter);

            for (String phone : mPhones) {
                database.collection(Constants.KEY_USER)
                        .whereEqualTo(Constants.KEY_ACCOUNT_PHONE_NUMBER, phone)
                        .get()
                        .addOnSuccessListener(documentSnapshots -> {
                            if (!documentSnapshots.isEmpty()) {
                                User user  = documentSnapshots.getDocuments().get(0).toObject(User.class);
                                assert user != null;
                                user.setId(documentSnapshots.getDocuments().get(0).getId());
                                database.collection(Constants.KEY_FRIEND)
                                        .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                                        .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, user.getId())
                                        .get()
                                        .addOnSuccessListener(documentSnapshots1 -> {
                                            if (documentSnapshots1.isEmpty()) {
                                                mUsersChuaLamGi.add(user);
                                                mUsersChuaLamGi.sort(Comparator.comparing(User::getLastName));
                                                chuaLamGiContactAdapter.notifyDataSetChanged();
                                            }
                                        }).addOnFailureListener(Throwable::printStackTrace);
                            }
                        }).addOnFailureListener(Throwable::printStackTrace);
            }

        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getListFriend() {
        List<User> mUsers = new ArrayList<>();
        binding.layoutListF.setVisibility(View.GONE);
        ContactAdapter contactAdapter = new ContactAdapter(mUsers, user -> {
            Intent intent = new Intent(getContext(), UserProfileActivity.class);
            intent.putExtra(Constants.KEY_ACCOUNT_USER_ID, user.getId());
            startActivity(intent);
        });
        binding.listFRCV.setAdapter(contactAdapter);
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
                                Log.e("Log", "ADDED");
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
                                            binding.layoutListF.setVisibility(View.VISIBLE);
                                            mUsers.sort(Comparator.comparing(User::getLastName));
                                            contactAdapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            } else if(documentChange.getType() == DocumentChange.Type.REMOVED)  { // nếu có thay đổi của dữ liệu trong 1 bản ghi nào đó
                                Log.e("Log", "REMOVED");
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

                                            mUsers.remove(user);
                                            mUsers.sort(Comparator.comparing(User::getLastName));
                                            contactAdapter.notifyDataSetChanged();

                                            if (mUsers.size() <= 0) {
                                                binding.layoutListF.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Bạn chưa có bạn bè, hãy tìm thêm bạn nhé", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            }
                        }
                    }
                });

    }

}
