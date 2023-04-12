package va.vanthe.app_chat_2.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import va.vanthe.app_chat_2.fragment.AddFriendFragment;


public class ViewPagerAddFriendAdapter extends FragmentPagerAdapter {

    public ViewPagerAddFriendAdapter(@NonNull FragmentManager fm ) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return AddFriendFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
