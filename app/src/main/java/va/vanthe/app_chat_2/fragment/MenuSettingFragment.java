package va.vanthe.app_chat_2.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import va.vanthe.app_chat_2.ulitilies.Constants;
import va.vanthe.app_chat_2.ulitilies.PreferenceManager;

public class MenuSettingFragment extends Fragment {

    private LayoutFragmentSettingsBinding binding;
    private PreferenceManager account;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentSettingsBinding.inflate(inflater, container, false);

        init();
        setListeners();

        return binding.getRoot();
    }
    private void init() {
        account = new PreferenceManager(getActivity().getApplicationContext());

        binding.imageProfile.setImageBitmap(changeStringToBitmap(account.getString(Constants.KEY_ACCOUNT_IMAGE)));
        binding.textName.setText(account.getString(Constants.KEY_ACCOUNT_FIRST_NAME) + " " + account.getString(Constants.KEY_ACCOUNT_LAST_NAME));
    }

    private void setListeners() {
        binding.buttonLogout.setOnClickListener(view -> {
            signOut();
        });
        binding.imageProfile.setOnClickListener(view -> {

        });
    }
    private void signOut() {
        showToast("Signing out...");
        account.clear();
        Intent intent = new Intent(getContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private Bitmap changeStringToBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
