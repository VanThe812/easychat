package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.util.FileUtils;

import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.database.UserDatabase;
import va.vanthe.app_chat_2.databinding.ActivityUserProfileBinding;
import va.vanthe.app_chat_2.entity.Conversation;
import va.vanthe.app_chat_2.entity.Friend;
import va.vanthe.app_chat_2.entity.GroupMember;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.fragment.DialogViewImageFragment;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;

    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private User user = new User();
    private int ORDER_PHOTO_EDITING = 0;
    private int ORDER_BACKGROUND_IMAGE = 1;
    private int ORDER_AVATAR = 2;

    private  File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListeners();
    }



    @SuppressLint("SetTextI18n")
    private void init() {
        account = new PreferenceManager(getApplicationContext().getApplicationContext());
        user.setId(getIntent().getStringExtra(Constants.KEY_ACCOUNT_USER_ID));

        binding.imageBack.setVisibility(View.VISIBLE);
        if (user.getId().equals(account.getString(Constants.KEY_ACCOUNT_USER_ID))) {
            binding.imageConfig.setVisibility(View.VISIBLE);
        }

        if (user.getId() != null) {
            Task<DocumentSnapshot> userTask = database.collection(Constants.KEY_USER)
                    .document(user.getId())
                    .get();
            Tasks.whenAllSuccess(userTask).addOnSuccessListener(results -> {
                // Xử lý dữ liệu ở đây
                DocumentSnapshot userDocument = (DocumentSnapshot) results.get(0);

                if (userDocument.exists()) {
                    user = userDocument.toObject(User.class);
                    assert user != null;
                    user.setId(userDocument.getId());

                    //Avatar
                    if (user.getImage() != null) {
                        showImage("user", "avatar" ,user.getImage(), binding.imageAvatar);
                    }
                    //Background image
                    if (user.getBackgroundImage() != null) {

                        showImage("user", "backgroundImage", user.getBackgroundImage(), binding.imageViewBackgroundImage);
                    }
                    //Name
                    binding.textViewName.setText(user.getFirstName() + " " + user.getLastName());
                    //describe
                    binding.textViewDesc.setText(user.getDescribe());
                    //Sex
                    binding.textViewSex.setText((user.isSex()?"Nam":"Nữ"));
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), (user.isSex()? R.drawable.ic_man : R.drawable.ic_woman ));
                    //
                    assert drawable != null;
                    Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(getApplicationContext(), R.color.gray));

                    binding.textViewSex.setCompoundDrawablesRelativeWithIntrinsicBounds(wrappedDrawable, null, null, null);
                    //Date of birth
                    binding.textViewDateOfBirth.setText(user.getDateOfBrith());
                }
            }).addOnFailureListener(Throwable::printStackTrace);

        } else {
            onBackPressed();
        }
        //Nếu không phải là bản thân sẽ kiểm tra trạng thái có phải bạn bè hay không
        if (!user.getId().equals(account.getString(Constants.KEY_ACCOUNT_USER_ID))) {
            database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, user.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshots -> {
                        if (documentSnapshots.isEmpty()) { // chưa làm bạn
                            binding.buttonAddFriend.setVisibility(View.VISIBLE);
                        } else { // xem trạng thái
                            DocumentSnapshot friendDocument = documentSnapshots.getDocuments().get(0);
                            Friend friend = friendDocument.toObject(Friend.class);
                            assert friend != null;
                            friend.setId(friendDocument.getId());
                            if (friend.getStatus() == Constants.KEY_FRIEND_STATUS_NHANLOIMOI) {
                                binding.buttonAccept.setVisibility(View.VISIBLE);
                                binding.buttonRefuse.setVisibility(View.VISIBLE);
                            } else if (friend.getStatus() == Constants.KEY_FRIEND_STATUS_DAGUILOIMOI) {
                                binding.buttonCancelInvitation.setVisibility(View.VISIBLE);
                            } else if (friend.getStatus() == Constants.KEY_FRIEND_STATUS_DAKETBAN) {
                                binding.buttonChatMessage.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(Throwable::printStackTrace);
        }
    }
    private void showImage(@NonNull String dir, @NonNull String childDir, @NonNull String nameImage, ImageView imageView) {
        File folder = new File(getFilesDir(), childDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (nameImage != null) {
            File file = new File(folder, nameImage);
            if (file.exists()) {
                // File tồn tại trong thư mục của ứng dụng
                Picasso.get()
                        .load(file)
                        .into(imageView);
            }
            else {
                StorageReference imagesRef = storage.getReference()
                        .child(dir)
                        .child(childDir)
                        .child(nameImage);
                File storageDir = getApplicationContext().getExternalFilesDir(null);
                File localFile = new File(storageDir, nameImage);

                imagesRef.getFile(localFile)
                        .addOnSuccessListener(runnable -> {
                            Picasso.get().load(localFile).into(imageView);
                            File file2 = new File(folder, nameImage);
                            try {
                                FileUtils.copyFile(localFile.getPath(), file2.getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                        .addOnFailureListener(Throwable::printStackTrace);
            }
        }
    }
    @SuppressLint("ResourceAsColor")
    private void setListeners() {
        if (user.getId().equals(account.getString(Constants.KEY_ACCOUNT_USER_ID))) {
            binding.imageViewBackgroundImage.setOnClickListener(view -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_change_background_image, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                bottomSheetView.findViewById(R.id.textViewCancel).setOnClickListener(view2 -> bottomSheetDialog.dismiss());
                bottomSheetView.findViewById(R.id.textViewShowBackgroundImage).setOnClickListener(view2 -> {
                    Bundle args = new Bundle();

                    File folder = new File(getFilesDir(), "backgroundImage");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File file = new File(folder, user.getBackgroundImage());
                    args.putString("image_path", file.getAbsolutePath());
                    Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                    dialogViewImageFragment.setArguments(args);
                    dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");

                    bottomSheetDialog.dismiss();
                });
                bottomSheetView.findViewById(R.id.textViewNewPhotoShoot).setOnClickListener(view2 -> {

                });
                bottomSheetView.findViewById(R.id.textViewSelectPhotoFromDevice).setOnClickListener(view2 -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ORDER_PHOTO_EDITING = ORDER_BACKGROUND_IMAGE;
                    pickImage.launch(intent);
                    bottomSheetDialog.dismiss();

                });
            });
            binding.imageAvatar.setOnClickListener(view -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
                View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_change_avatar, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                bottomSheetView.findViewById(R.id.textViewCancel).setOnClickListener(view2 -> bottomSheetDialog.dismiss());
                bottomSheetView.findViewById(R.id.textViewShowAvatar).setOnClickListener(view2 -> {
                    Bundle args = new Bundle();

                    File folder = new File(getFilesDir(), "avatar");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File file = new File(folder, user.getImage());
                    args.putString("image_path", file.getAbsolutePath());
                    Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                    DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                    dialogViewImageFragment.setArguments(args);
                    dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");

                    bottomSheetDialog.dismiss();
                });
                bottomSheetView.findViewById(R.id.textViewNewPhotoShoot).setOnClickListener(view2 -> {

                });
                bottomSheetView.findViewById(R.id.textViewSelectPhotoFromDevice).setOnClickListener(view2 -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    ORDER_PHOTO_EDITING = ORDER_AVATAR;
                    pickImage.launch(intent);
                    bottomSheetDialog.dismiss();

                });
            });
        }
        else {
            binding.imageViewBackgroundImage.setOnClickListener(view -> {
                Bundle args = new Bundle();

                File folder = new File(getFilesDir(), "backgroundImage");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(folder, user.getBackgroundImage());
                args.putString("image_path", file.getAbsolutePath());
                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                dialogViewImageFragment.setArguments(args);
                dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");

            });
            binding.imageAvatar.setOnClickListener(view -> {
                Bundle args = new Bundle();

                File folder = new File(getFilesDir(), "avatar");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(folder, user.getImage());
                args.putString("image_path", file.getAbsolutePath());
                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                DialogViewImageFragment dialogViewImageFragment = new DialogViewImageFragment();
                dialogViewImageFragment.setArguments(args);
                dialogViewImageFragment.show(getSupportFragmentManager(), "DialogViewImage");

            });
        }
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        /// Sử lý mấy cái button
        binding.buttonAddFriend.setOnClickListener(view -> {
            // tạo ra 2 bản ghi trong friend 1 cho mk 1 cho ng kia vs trạng thái tương ứng với mỗi bên
            Friend friend1 = new Friend();
            friend1.setUserId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
            friend1.setUserFriendId(user.getId());
            friend1.setStatus(Constants.KEY_FRIEND_STATUS_DAGUILOIMOI);
            friend1.setTimeStamp(new Date());

            Friend friend2 = new Friend();
            friend2.setUserId(user.getId());
            friend2.setUserFriendId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
            friend2.setStatus(Constants.KEY_FRIEND_STATUS_NHANLOIMOI);
            friend2.setTimeStamp(new Date());

            Task<DocumentReference> friend1Task = database.collection(Constants.KEY_FRIEND).add(friend1.toHashMap());
            Task<DocumentReference> friend2Task = database.collection(Constants.KEY_FRIEND).add(friend2.toHashMap());

            Tasks.whenAllSuccess(friend1Task, friend2Task).addOnSuccessListener(result -> {
                HelperFunction.showToast("Đã gửi lời mời thành công!", getApplicationContext());
                binding.buttonAddFriend.setVisibility(View.GONE);
                binding.buttonCancelInvitation.setVisibility(View.VISIBLE);
            }).addOnFailureListener(Throwable::printStackTrace);
        });
        binding.buttonAccept.setOnClickListener(view -> {
            Task<QuerySnapshot> friend1Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, user.getId())
                    .get();

            Task<QuerySnapshot> friend2Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, user.getId())
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .get();

            Tasks.whenAllSuccess(friend1Task, friend2Task).addOnSuccessListener(result -> {
                QuerySnapshot friend1QuerySnapshot = (QuerySnapshot) result.get(0);
                DocumentSnapshot friend1Snapshot = friend1QuerySnapshot.getDocuments().get(0);
                DocumentReference friend1Reference = database.collection(Constants.KEY_FRIEND).document(friend1Snapshot.getId());
                friend1Reference.update(Constants.KEY_FRIEND_STATUS, Constants.KEY_FRIEND_STATUS_DAKETBAN);

                QuerySnapshot friend2QuerySnapshot = (QuerySnapshot) result.get(1);
                DocumentSnapshot friend2Snapshot = friend2QuerySnapshot.getDocuments().get(0);
                DocumentReference friend2Reference = database.collection(Constants.KEY_FRIEND).document(friend2Snapshot.getId());
                friend2Reference.update(Constants.KEY_FRIEND_STATUS, Constants.KEY_FRIEND_STATUS_DAKETBAN);

                // Tạo ra 1 cuộc hội thoại
                Conversation conversation = new Conversation();
                conversation.setCreateTime(new Date());
                conversation.setNewMessage("Hãy chào nhau \uD83D\uDC4B");
                conversation.setSenderId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                conversation.setMessageTime(new Date());
                conversation.setStyleChat(Constants.KEY_TYPE_CHAT_SINGLE);
                conversation.setQuickEmotions(getString(R.string.text_detail));
                database.collection(Constants.KEY_CONVERSATION)
                        .add(conversation.toHashMap())
                        .addOnSuccessListener(documentReference -> {
                            // Tạo ra các groupMember cho conversation
                            GroupMember groupMember1 = new GroupMember();
                            groupMember1.setUserId(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                            groupMember1.setConversationId(documentReference.getId());
                            groupMember1.setTimeJoin(new Date());
                            groupMember1.setStatus(1);

                            GroupMember groupMember2 = new GroupMember();
                            groupMember2.setUserId(user.getId());
                            groupMember2.setConversationId(documentReference.getId());
                            groupMember2.setTimeJoin(new Date());
                            groupMember2.setStatus(1);

                            Task<DocumentReference> groupMember1Task = database.collection(Constants.KEY_GROUP_MEMBER).add(groupMember1.toHashMap());
                            Task<DocumentReference> groupMember2Task = database.collection(Constants.KEY_GROUP_MEMBER).add(groupMember2.toHashMap());

                            Tasks.whenAllSuccess(groupMember1Task, groupMember2Task).addOnSuccessListener(result2 -> {
                                binding.buttonAccept.setVisibility(View.GONE);
                                binding.buttonRefuse.setVisibility(View.GONE);
                                binding.buttonChatMessage.setVisibility(View.VISIBLE);
                                HelperFunction.showToast("Kết bạn thành công, chúc 2 bạn nhắn tin vui vẻ \uD83C\uDF49", getApplicationContext());
                            }).addOnFailureListener(Throwable::printStackTrace);

                        }).addOnFailureListener(Throwable::printStackTrace);



            }).addOnFailureListener(Throwable::printStackTrace);
        });
        binding.buttonCancelInvitation.setOnClickListener(view -> {
            // xóa 2 bản ghi friend đi
            Task<QuerySnapshot> friend1Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, user.getId())
                    .get();

            Task<QuerySnapshot> friend2Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, user.getId())
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .get();

            Tasks.whenAllSuccess(friend1Task, friend2Task).addOnSuccessListener(result -> {
                QuerySnapshot friend1QuerySnapshot = (QuerySnapshot) result.get(0);
                DocumentSnapshot friend1Snapshot = friend1QuerySnapshot.getDocuments().get(0);
                DocumentReference friend1Reference = database.collection(Constants.KEY_FRIEND).document(friend1Snapshot.getId());
                friend1Reference.delete();

                QuerySnapshot friend2QuerySnapshot = (QuerySnapshot) result.get(1);
                DocumentSnapshot friend2Snapshot = friend2QuerySnapshot.getDocuments().get(0);
                DocumentReference friend2Reference = database.collection(Constants.KEY_FRIEND).document(friend2Snapshot.getId());
                friend2Reference.delete();

                binding.buttonCancelInvitation.setVisibility(View.GONE);
                binding.buttonAddFriend.setVisibility(View.VISIBLE);
                HelperFunction.showToast("Hủy yêu cầu kết bạn thành công. Buồn thế, mong 2 bạn sớm kết bạn với nhau nha", getApplicationContext());
            }).addOnFailureListener(Throwable::printStackTrace);
        });
        binding.buttonRefuse.setOnClickListener(view -> {
            // xóa 2 bản ghi friend đi
            Task<QuerySnapshot> friend1Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, user.getId())
                    .get();

            Task<QuerySnapshot> friend2Task = database.collection(Constants.KEY_FRIEND)
                    .whereEqualTo(Constants.KEY_FRIEND_USER_ID, user.getId())
                    .whereEqualTo(Constants.KEY_FRIEND_USER_FRIEND_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .get();

            Tasks.whenAllSuccess(friend1Task, friend2Task).addOnSuccessListener(result -> {
                QuerySnapshot friend1QuerySnapshot = (QuerySnapshot) result.get(0);
                DocumentSnapshot friend1Snapshot = friend1QuerySnapshot.getDocuments().get(0);
                DocumentReference friend1Reference = database.collection(Constants.KEY_FRIEND).document(friend1Snapshot.getId());
                friend1Reference.delete();

                QuerySnapshot friend2QuerySnapshot = (QuerySnapshot) result.get(1);
                DocumentSnapshot friend2Snapshot = friend2QuerySnapshot.getDocuments().get(0);
                DocumentReference friend2Reference = database.collection(Constants.KEY_FRIEND).document(friend2Snapshot.getId());
                friend2Reference.delete();

                binding.buttonRefuse.setVisibility(View.GONE);
                binding.buttonAccept.setVisibility(View.GONE);
                binding.buttonAddFriend.setVisibility(View.VISIBLE);
                HelperFunction.showToast("Đã từ chối yêu cầu kết bạn \uD83D\uDC94", getApplicationContext());
            }).addOnFailureListener(Throwable::printStackTrace);
        });
        binding.buttonChatMessage.setOnClickListener(view -> {
            Task<QuerySnapshot> groupMembers1Task = database.collection(Constants.KEY_GROUP_MEMBER)
                    .whereEqualTo(Constants.KEY_GROUP_MEMBER_USER_ID, account.getString(Constants.KEY_ACCOUNT_USER_ID))
                    .get();

            Task<QuerySnapshot> groupMembers2Task = database.collection(Constants.KEY_GROUP_MEMBER)
                    .whereEqualTo(Constants.KEY_GROUP_MEMBER_USER_ID, user.getId())
                    .get();

            Tasks.whenAllSuccess(groupMembers1Task, groupMembers2Task).addOnSuccessListener(res -> {
                QuerySnapshot groupMembers1 = (QuerySnapshot) res.get(0);
                QuerySnapshot groupMembers2 = (QuerySnapshot) res.get(1);

                List<String> conversationIdList1 = new ArrayList<>();
                List<String> conversationIdList2 = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : groupMembers1) {
                    conversationIdList1.add(documentSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID));
                }
                for (DocumentSnapshot documentSnapshot : groupMembers2) {
                    conversationIdList2.add(documentSnapshot.getString(Constants.KEY_GROUP_MEMBER_CONVERSATION_ID));
                }

                for (String a : conversationIdList1) {
                    for (String b : conversationIdList2) {
                        if (a.equals(b)) {
                            database.collection(Constants.KEY_CONVERSATION)
                                    .document(a)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.getLong(Constants.KEY_CONVERSATION_STYLE_CHAT) == Constants.KEY_TYPE_CHAT_SINGLE) {
                                            Conversation conversation = documentSnapshot.toObject(Conversation.class);
                                            conversation.setId(documentSnapshot.getId());
                                            Intent intent = new Intent(getApplicationContext(), ChatMessageActivity.class);
                                            intent.putExtra(Constants.KEY_USER, user);
                                            intent.putExtra(Constants.KEY_CONVERSATION, conversation);
                                            startActivity(intent);
                                        }
                                    });
                        }
                    }
                }

            }).addOnFailureListener(Throwable::printStackTrace);
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        Intent intent = new Intent(getApplicationContext(), EditImageActivity.class);
                        intent.putExtra("DATA", imageUri.toString());
                        if(ORDER_PHOTO_EDITING == ORDER_BACKGROUND_IMAGE) {
                            intent.putExtra("style", Constants.KEY_REQUEST_CODE_USER_BACKGROUND_IMAGE);
                            startActivityForResult(intent, Constants.KEY_REQUEST_CODE_USER_BACKGROUND_IMAGE);
                            } else if (ORDER_PHOTO_EDITING == ORDER_AVATAR) {
                            intent.putExtra("style", Constants.KEY_REQUEST_CODE_IMAGE_AVATAR);
                            startActivityForResult(intent, Constants.KEY_REQUEST_CODE_IMAGE_AVATAR);
                        }


                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == Constants.KEY_REQUEST_CODE_USER_BACKGROUND_IMAGE) {
            String result = data.getStringExtra("RESULT");
            Uri uri = null;
            if (result != null) {
                uri = Uri.parse(result);
            }
            //load ảnh nền ra giao diện
            Picasso.get().load(uri).into(binding.imageViewBackgroundImage);
            //Thêm file lên storage
            String fileName = account.getString(Constants.KEY_ACCOUNT_USER_ID) + uri.getPath().substring(uri.getPath().lastIndexOf("."));
            StorageReference imagesRef = storage.getReference()
                    .child("user")
                    .child("backgroundImage")
                    .child(fileName);
            Uri finalUri = uri;
            imagesRef.putFile(uri)
                    .addOnSuccessListener(runnable -> {
                        //Cập nhật dữ liệu của user
                        DocumentReference userReference = database.collection(Constants.KEY_USER).document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                        userReference.update(Constants.KEY_ACCOUNT_BACKGROUND_IMAGE, fileName);
                        account.putString(Constants.KEY_ACCOUNT_BACKGROUND_IMAGE, fileName);

                        File folder = new File(getFilesDir(), "backgroundImage");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        File file = new File(folder, fileName);

                        try {
                            FileUtils.copyFile(finalUri.getPath(), file.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }
        else if (resultCode == -1 && requestCode == Constants.KEY_REQUEST_CODE_IMAGE_AVATAR) {
            String result = data.getStringExtra("RESULT");
            Uri uri = null;
            if (result != null) {
                uri = Uri.parse(result);
            }
            //load ảnh nền ra giao diện
            Picasso.get().load(uri).into(binding.imageAvatar);
            //Thêm file lên storage
            String fileName = account.getString(Constants.KEY_ACCOUNT_USER_ID) + uri.getPath().substring(uri.getPath().lastIndexOf("."));
            StorageReference imagesRef = storage.getReference()
                    .child("user")
                    .child("avatar")
                    .child(fileName);
            Uri finalUri = uri;
            imagesRef.putFile(uri)
                    .addOnSuccessListener(runnable -> {
                        //Cập nhật dữ liệu của user
                        DocumentReference userReference = database.collection(Constants.KEY_USER).document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                        userReference.update(Constants.KEY_ACCOUNT_IMAGE, fileName);
                        account.putString(Constants.KEY_ACCOUNT_IMAGE, fileName);

                        File folder = new File(getFilesDir(), "avatar");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        File file = new File(folder, fileName);

                        try {
                            FileUtils.copyFile(finalUri.getPath(), file.getPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(Throwable::printStackTrace);
        }

    }
}