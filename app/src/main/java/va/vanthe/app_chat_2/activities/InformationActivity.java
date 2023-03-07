package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import va.vanthe.app_chat_2.databinding.ActivityInformationBinding;
import va.vanthe.app_chat_2.dataencrypt.SHA256Encryptor;
import va.vanthe.app_chat_2.fragment.DatePickerFragment;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class InformationActivity extends AppCompatActivity {

    private ActivityInformationBinding binding;
    private String encodedImage;
    private PreferenceManager account;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        account = new PreferenceManager(getApplicationContext());

        binding.inputDateOfBirth.setOnFocusChangeListener((view, b) -> {
            if (b) {
                showDatePickerDialog();
                binding.inputDateOfBirth.clearFocus();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.buttonNext.setOnClickListener(view -> {
            if (isValidInforDetails()) {
                DocumentReference documentReference =
                        database.collection(Constants.KEY_USER).document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                documentReference.update(
                        Constants.KEY_ACCOUNT_IMAGE, encodedImage,
                        Constants.KEY_ACCOUNT_FIRST_NAME, binding.inputFirstName.getText().toString().trim(),
                        Constants.KEY_ACCOUNT_LAST_NAME, binding.inputLastName.getText().toString().trim(),
                        Constants.KEY_ACCOUNT_SEX, binding.maleRadioButton.isChecked(),
                        Constants.KEY_ACCOUNT_DateOfBirth, binding.inputDateOfBirth.getText().toString().trim()
                );
                account.putString(Constants.KEY_ACCOUNT_IMAGE, encodedImage);
                account.putString(Constants.KEY_ACCOUNT_FIRST_NAME, binding.inputFirstName.getText().toString().trim());
                account.putString(Constants.KEY_ACCOUNT_LAST_NAME, binding.inputLastName.getText().toString().trim());
                account.putBoolean(Constants.KEY_ACCOUNT_SEX, binding.maleRadioButton.isChecked());
                account.putString(Constants.KEY_ACCOUNT_DateOfBirth, binding.inputDateOfBirth.getText().toString().trim());

                Intent intent = new Intent(InformationActivity.this, BeginActivity.class);
                startActivity(intent);
            }
        });
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private Boolean isValidInforDetails() {
        if(encodedImage == null) {
            showToast("Select profile image");
            return false;
        }else if(binding.inputFirstName.getText().toString().isEmpty()) {
            showToast("Enter first name");
            return false;
        }else if(binding.inputLastName.getText().toString().isEmpty()) {
            showToast("Enter last email");
        }else if(binding.inputDateOfBirth.getText().toString().isEmpty()) {
            showToast("Enter date of birth");
            return false;
        }else {
            return true;
        }
        return true;
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}