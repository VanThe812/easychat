package va.vanthe.app_chat_2.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.adapters.ContactAdapter;
import va.vanthe.app_chat_2.database.FriendDatabase;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.LayoutFragmentFriendBinding;
import va.vanthe.app_chat_2.entity.Contact;
import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuFriendFragment extends Fragment  {

    private LayoutFragmentFriendBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private PreferenceManager account;
    private final List<Friend> mFriends = new ArrayList<>();
    private final List<User> mUserFriends = new ArrayList<>();


    private static final int REQUEST_CONTACTS_ASK_PERMISSIONS = 1001;
    private static final int REQUEST_SMS_ASK_PERMISSIONS = 1001;

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

    private void setListeners() {
        binding.imageAddFriend.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_add_friend, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            View parentView = (View) bottomSheetView.getParent();
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(parentView);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            bottomSheetDialog.show();

            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            //Trả về 1 cursor - quản lý dữ liệu contact trên điện thoại
            Cursor cursor  = getContext().getContentResolver().query(uri, null, null, null, null);
            List<Contact> mContacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                String tenCotName = ContactsContract.Contacts.DISPLAY_NAME;
                String tenCotPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;

                int viTriCotName = cursor.getColumnIndex(tenCotName);
                int viTriCotPhone = cursor.getColumnIndex(tenCotPhone);

                String name = cursor.getString(viTriCotName);
                String phone = cursor.getString(viTriCotPhone);
                Contact contact = new Contact(phone.replaceAll("[^0-9]", ""), name);
                mContacts.add(contact);
            }
//            database.
            ContactAdapter contactAdapter = new ContactAdapter(mContacts);
            RecyclerView contactRCV = bottomSheetView.findViewById(R.id.contactRCV);
            contactRCV.setAdapter(contactAdapter);
        });
    }

    private void getListFriend() {
        database.collection(Constants.KEY_FRIEND)
                .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                .get()
                .addOnSuccessListener(runnable -> {
                    List<DocumentSnapshot> friendSnapshots = runnable.getDocuments();
                    if (!friendSnapshots.isEmpty()) {
                        for (DocumentSnapshot friendSnapshot : friendSnapshots) {
                            Friend friend = friendSnapshot.toObject(Friend.class);
                            assert friend != null;
                            friend.setId(friendSnapshot.getId());
                            AtomicReference<User> user = new AtomicReference<>(UserDatabase.getInstance(getContext()).userDAO().getUser(friend.getUserId()));

                            if (user.get() == null) {
                                database.collection(Constants.KEY_USER)
                                        .document(friend.getUserId())
                                        .get()
                                        .addOnSuccessListener(userSnapshot -> {
                                            User user1 = userSnapshot.toObject(User.class);
                                            UserDatabase.getInstance(getContext()).userDAO().insertUser(user1);
                                            user.set(user1);
                                            mUserFriends.add(user1);
                                        })
                                        .addOnFailureListener(Throwable::printStackTrace);
                            } else {
                                mUserFriends.add(user.get());
                            }
                            mFriends.add(friend);
                            FriendDatabase.getInstance(getContext()).friendDAO().insertFriend(friend);
                        }
                        /// in list friend

                    }
                });

    }


}
