package va.vanthe.app_chat_2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import va.vanthe.app_chat_2.fragment.MenuChatFragment;
import va.vanthe.app_chat_2.fragment.MenuFriendFragment;
import va.vanthe.app_chat_2.fragment.MenuSettingFragment;


public class ViewPagerMenuAdapter extends FragmentStatePagerAdapter {
    public ViewPagerMenuAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
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
    public int getCount() {
        return 3;
    }

}
