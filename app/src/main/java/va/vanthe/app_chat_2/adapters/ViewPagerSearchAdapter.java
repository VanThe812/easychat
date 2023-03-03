package va.vanthe.app_chat_2.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import va.vanthe.app_chat_2.fragment.SearchFragment;

public class ViewPagerSearchAdapter extends FragmentPagerAdapter {

    public ViewPagerSearchAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SearchFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
