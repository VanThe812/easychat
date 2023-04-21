package va.vanthe.app_chat_2.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager account = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        try {
            documentReference = database.collection(Constants.KEY_USER)
                    .document(account.getString(Constants.KEY_ACCOUNT_USER_ID));
        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            documentReference.update(Constants.KEY_AVAILABILITY, 0);
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            documentReference.update(Constants.KEY_AVAILABILITY, 1);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
