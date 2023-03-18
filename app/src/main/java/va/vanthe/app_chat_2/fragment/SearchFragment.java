package va.vanthe.app_chat_2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import va.vanthe.app_chat_2.activities.ChatMessageActivity;
import va.vanthe.app_chat_2.adapters.UserChatAdapter;
import va.vanthe.app_chat_2.adapters.UsersAdapter;
import va.vanthe.app_chat_2.databinding.LayoutFragmentSearchBinding;
import va.vanthe.app_chat_2.listeners.UserListener;
import va.vanthe.app_chat_2.entity.User;
import va.vanthe.app_chat_2.ulitilies.Constants;

public class SearchFragment extends Fragment implements UserListener {
    public static LayoutFragmentSearchBinding binding;
    private String IMAGE_GROUP_CHAT_DEFAULT = "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";


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFragmentSearchBinding.inflate(inflater, container, false);

        setListeners();

        dataTest();
        dataTest2();
        return binding.getRoot();
    }
    private void setListeners() {

    }
    private void dataTest() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i<8; i++) {
            User user = new User();
            user.setFirstName("Vu Van");
            user.setLastName("The" + (i +1));
            user.setImage(IMAGE_GROUP_CHAT_DEFAULT);
            user.setId(""+i);
            users.add(user);
        }
        UserChatAdapter userChatAdapter = new UserChatAdapter(users, getContext());
        binding.recentSearchRCV.setAdapter(userChatAdapter);
        binding.recentSearchRCV.setVisibility(View.VISIBLE);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        binding.recentSearchRCV.setLayoutManager(gridLayoutManager);
    }
    private void dataTest2() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i<8; i++) {
            User user = new User();
            user.setFirstName("Vu Van");
            user.setLastName("The" + (i +1));
            user.setImage(IMAGE_GROUP_CHAT_DEFAULT);
            user.setId(""+i);
            users.add(user);
        }
        UsersAdapter usersAdapter = new UsersAdapter(users, this);
        binding.suggestRCV.setAdapter(usersAdapter);
        binding.suggestRCV.setVisibility(View.VISIBLE);

    }


    @Override
    public void onUserClicked(User user) {

    }


}
