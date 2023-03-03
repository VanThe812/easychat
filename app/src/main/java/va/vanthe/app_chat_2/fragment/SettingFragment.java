package va.vanthe.app_chat_2.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import va.vanthe.app_chat_2.activities.SignInActivity;
import va.vanthe.app_chat_2.databinding.LayoutFragmentSettingsBinding;

public class SettingFragment extends Fragment {

    LayoutFragmentSettingsBinding binding;
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("Vanthe", "Setting");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentSettingsBinding.inflate(inflater, container, false);

        setListeners();
        // Trả về View đã được inflate
        return binding.getRoot();
    }

    private void setListeners() {
        binding.buttonLogout.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), SignInActivity.class));
            getActivity().finish();
        });
    }
//    private void signOut() {
//        showToast("Signing out...");
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference =
//                database.collection(Constants.KEY_COLLECTION_USERS).document(
//                        preferenceManager.getString(Constants.KEY_USER_ID)
//                );
//        HashMap<String, Object> update = new HashMap<>();
//        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
//        documentReference.update(update)
//                .addOnSuccessListener(unused -> {
//                    preferenceManager.clear();
//                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
//                    finish();
//                })
//                .addOnFailureListener(e -> showToast("Unable to sign out"));
//    }

}
