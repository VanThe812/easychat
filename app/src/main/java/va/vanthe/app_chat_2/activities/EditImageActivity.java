package va.vanthe.app_chat_2.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

import va.vanthe.app_chat_2.databinding.ActivityEditImageBinding;
import va.vanthe.app_chat_2.ulitilies.Constants;

public class EditImageActivity extends AppCompatActivity {
    private ActivityEditImageBinding binding;
    String result;
    Uri imageUri;
    int style;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        readIntent();
        String dest_uri = new StringBuffer(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();
        if (style == Constants.KEY_REQUEST_CODE_BACKGROUND) {
            UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(), dest_uri)))
                    .withOptions(options)
                    .withAspectRatio(2, 4)
//                .useSourceImageAspectRatio()
                    .withMaxResultSize(2000, 2000)
                    .start(EditImageActivity.this);
        } else if (style == Constants.KEY_REQUEST_CODE_IMAGE_MESSAGE) {
            UCrop.of(imageUri,Uri.fromFile(new File(getCacheDir(), dest_uri)))
                    .withOptions(options)
                    .withAspectRatio(0, 0)
                    .useSourceImageAspectRatio()
                    .withMaxResultSize(2000, 2000)
                    .start(EditImageActivity.this);
        }


    }
    private void readIntent() {
        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            result = intent.getStringExtra("DATA");
            imageUri = Uri.parse(result);
            style = intent.getIntExtra("style", Constants.KEY_REQUEST_CODE_IMAGE_MESSAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Intent intent = new Intent();
            intent.putExtra("RESULT", resultUri.toString());
            setResult(-1, intent);
            finish();
        } else if (requestCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }

    }
}