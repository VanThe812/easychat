package va.vanthe.app_chat_2.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import va.vanthe.app_chat_2.activities.SignInActivity;
import va.vanthe.app_chat_2.databinding.LayoutFragmentSettingsBinding;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuSettingFragment extends Fragment {

    private LayoutFragmentSettingsBinding binding;
    private PreferenceManager account;

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("Vanthe", "Setting");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentSettingsBinding.inflate(inflater, container, false);

        account = new PreferenceManager(getActivity().getApplicationContext());
        setListeners();
        // Trả về View đã được inflate
        return binding.getRoot();
    }

    private void setListeners() {
        binding.buttonLogout.setOnClickListener(view -> {
            signOut();
        });
    }
    private void signOut() {
        showToast("Signing out...");
        account.clear();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        DocumentReference documentReference =
//                database.collection(Constants.KEY_USER).document(
//                        account.getString(Constants.KEY_ACCOUNT_USER_ID)
//                );
//        HashMap<String, Object> update = new HashMap<>();
//        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
//        documentReference.update(update)
//                .addOnSuccessListener(unused -> {
//
//
//                })
//                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
