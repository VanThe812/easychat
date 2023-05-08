package va.vanthe.app_chat_2.thiandroidNC.hoten_lop;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import va.vanthe.app_chat_2.R;
import va.vanthe.app_chat_2.activities.BeginActivity;
import va.vanthe.app_chat_2.activities.EditImageActivity;
import va.vanthe.app_chat_2.activities.InformationActivity;
import va.vanthe.app_chat_2.databinding.ActivityTaiKhoanBinding;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.HelperFunction;

public class TaiKhoanActivity extends AppCompatActivity {
    private ActivityTaiKhoanBinding binding;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaiKhoanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setListener();
    }
    private void init() {

    }
    private void setListener() {
        binding.inputImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.button.setOnClickListener(view -> {
            if(binding.inputTen.getText().toString().trim().isEmpty()) {
                HelperFunction.showToast("Nhập tên!", getApplicationContext());
                binding.inputTen.findFocus();
                return;
            }else if(binding.inputDiaChi.getText().toString().trim().isEmpty()) {
                HelperFunction.showToast("Nhập địa chỉ!", getApplicationContext());
                binding.inputDiaChi.findFocus();
                return;
            }else if(binding.inputPhone.getText().toString().trim().isEmpty()) {
                HelperFunction.showToast("Nhập phone!", getApplicationContext());
                binding.inputPhone.findFocus();
                return;
            }else if(binding.inputEmail.getText().toString().trim().isEmpty()) {
                HelperFunction.showToast("Nhập email!", getApplicationContext());
                binding.inputEmail.findFocus();
                return;
            }
            else if(image == null) {
                HelperFunction.showToast("Nhập ảnh!", getApplicationContext());
                binding.inputPhone.findFocus();
                return;
            }

            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setTen(binding.inputTen.getText().toString().trim());
            taiKhoan.setDiachi(binding.inputDiaChi.getText().toString().trim());
            taiKhoan.setPhone(binding.inputPhone.getText().toString().trim());
            taiKhoan.setEmail(binding.inputEmail.getText().toString().trim());

            database.collection(va.vanthe.app_chat_2.thiandroidNC.hoten_lop.Constants.KEY_TAIKHOAN)
                    .add(taiKhoan.toHashMap())
                    .addOnSuccessListener(documentReference -> {
                        String typeFile = image.getPath().substring(image.getPath().lastIndexOf(".")); //VD: jpg, png , ....

                        String fileName = String.format("avatar_%s.%s", documentReference.getId(), typeFile);

                        StorageReference imagesRef = storage.getReference()
                                .child("thi")
                                .child("avatar")
                                .child(fileName);
                        imagesRef.putFile(image)
                                .addOnSuccessListener(taskSnapshot -> {

                                    taiKhoan.setImage(fileName);
                                    documentReference.update(taiKhoan.toHashMap());
                                    HelperFunction.showToast("Thêm thành công", getApplicationContext());
                                    Intent intent = new Intent(getApplicationContext(), DanhSachTaiKhoanActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    });


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
                        intent.putExtra("style", va.vanthe.app_chat_2.ulitilies.Constants.KEY_REQUEST_CODE_IMAGE_AVATAR);
                        startActivityForResult(intent, Constants.KEY_REQUEST_CODE_IMAGE_AVATAR);
                    }
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == Constants.KEY_REQUEST_CODE_IMAGE_AVATAR) {
            assert data != null;
            String result = data.getStringExtra("RESULT");
            image = null;
            if (result != null) {
                image = Uri.parse(result);
            }

            Picasso.get()
                    .load(image)
                    .into(binding.inputImage);
        }
    }
}