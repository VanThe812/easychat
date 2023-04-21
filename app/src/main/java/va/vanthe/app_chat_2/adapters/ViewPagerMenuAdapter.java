package va.vanthe.app_chat_2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import va.vanthe.app_chat_2.fragment.MenuChatFragment;
import va.vanthe.app_chat_2.fragment.MenuFriendFragment;
import va.vanthe.app_chat_2.fragment.MenuSettingFragment;


public class ViewPagerMenuAdapter extends FragmentStateAdapter {
    public ViewPagerMenuAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MenuChatFragment();
            case 1:
                return new MenuFriendFragment();
            case 2:
                return new MenuSettingFragment();
            default:
                return new MenuChatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
