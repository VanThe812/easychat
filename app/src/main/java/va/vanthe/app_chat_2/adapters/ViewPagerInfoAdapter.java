package va.vanthe.app_chat_2.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import va.vanthe.app_chat_2.fragment.InformationFragment;
import va.vanthe.app_chat_2.fragment.SearchFragment;

public class ViewPagerInfoAdapter extends FragmentPagerAdapter {

    public ViewPagerInfoAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return InformationFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
