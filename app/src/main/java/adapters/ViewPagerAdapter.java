package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 09/01/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter (FragmentManager manager){
        super(manager);
    }

    public Fragment getItem(int position){
        return fragmentList.get(position);
    }

    public int getCount(){
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    public CharSequence getPageTitle(int position) {
        // return null to display only the icon
        return null;
    }

}
