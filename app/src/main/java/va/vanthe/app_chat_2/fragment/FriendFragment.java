package va.vanthe.app_chat_2.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.adapters.UsersAdapter;
import va.vanthe.app_chat_2.databinding.LayoutFragmentFriendBinding;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.models.User;

public class FriendFragment extends Fragment  {

    LayoutFragmentFriendBinding binding;
    private boolean isFragmentVisible;


    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentFriendBinding.inflate(inflater, container, false);



        // Trả về View đã được inflate
        return binding.getRoot();

    }






//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && !isFragmentVisible) {
//            // The fragment is visible
//            isFragmentVisible = true;
//            // Load any data here or do anything that requires fragment to be visible
//        } else if (!isVisibleToUser && isFragmentVisible) {
//            // The fragment is no longer visible
//            isFragmentVisible = false;
//            // Unload any data here or do anything that is not required when fragment is not visible
//        }
//    }
}
