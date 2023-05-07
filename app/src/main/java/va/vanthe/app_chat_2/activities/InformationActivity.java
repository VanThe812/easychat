package va.vanthe.app_chat_2.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import va.vanthe.app_chat_2.databinding.ActivityInformationBinding;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.fragment.DatePickerFragment;
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class InformationActivity extends AppCompatActivity {

    private ActivityInformationBinding binding;
    private PreferenceManager account;
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private Uri imageAvatarUri;

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
            if (isValidInfoDetails()) {
                DocumentReference documentReference = database.collection(Constants.KEY_USER).document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
                User user = new User();
                user.setId(documentReference.getId());
                user.setPhoneNumber(account.getString(Constants.KEY_ACCOUNT_PHONE_NUMBER));
                user.setPassword(account.getString(Constants.KEY_ACCOUNT_PASSWORD));

                String typeFile = imageAvatarUri.getPath().substring(imageAvatarUri.getPath().lastIndexOf(".")); //VD: jpg, png , ....

                String fileName = String.format("avatar_%s.%s", user.getId(), typeFile);

                StorageReference imagesRef = storage.getReference()
                                .child("user")
                                .child("avatar")
                                .child(fileName);
                imagesRef.putFile(imageAvatarUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            user.setImage(fileName);
                            user.setFirstName(binding.inputFirstName.getText().toString().trim());
                            user.setLastName(binding.inputLastName.getText().toString().trim());
                            user.setSex(binding.maleRadioButton.isChecked());
                            user.setDateOfBrith(binding.inputDateOfBirth.getText().toString().trim());
                            documentReference.update(user.toHashMap());

                            account.putString(Constants.KEY_ACCOUNT_IMAGE, user.getImage());
                            account.putString(Constants.KEY_ACCOUNT_FIRST_NAME, user.getFirstName());
                            account.putString(Constants.KEY_ACCOUNT_LAST_NAME, user.getLastName());
                            account.putBoolean(Constants.KEY_ACCOUNT_SEX, user.isSex());
                            account.putString(Constants.KEY_ACCOUNT_DateOfBirth, user.getDateOfBrith());

                            Intent intent = new Intent(InformationActivity.this, BeginActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(Throwable::printStackTrace);

            }
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
                        intent.putExtra("style", Constants.KEY_REQUEST_CODE_IMAGE_AVATAR);
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
            imageAvatarUri = null;
            if (result != null) {
                imageAvatarUri = Uri.parse(result);
            }

            Picasso.get()
                    .load(imageAvatarUri)
                    .into(binding.imageProfile);
        }
    }

    private Boolean isValidInfoDetails() {
        if(imageAvatarUri == null) {
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
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}