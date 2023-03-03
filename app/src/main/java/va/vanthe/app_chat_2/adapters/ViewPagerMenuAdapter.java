package va.vanthe.app_chat_2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import va.vanthe.app_chat_2.fragment.ChatFragment;
import va.vanthe.app_chat_2.fragment.FriendFragment;
import va.vanthe.app_chat_2.fragment.SettingFragment;


public class ViewPagerMenuAdapter extends FragmentStatePagerAdapter {
    public ViewPagerMenuAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatFragment();
            case 1:
                return new FriendFragment();
            case 2:
                return new SettingFragment();
            default:
                return new ChatFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
