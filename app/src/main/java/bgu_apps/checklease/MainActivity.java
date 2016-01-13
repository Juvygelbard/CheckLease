package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import Fragments.ApartmentListFragment;
import Fragments.FavouritesFragment;
import Fragments.SettingsFragment;
import Fragments.MapFragment;

import adapters.ViewPagerAdapter;
import data.Data;
import db_handle.ApartmentDB;
import db_handle.FieldDB;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.list_gray_hdpi,
            R.drawable.star_full_gray_hdpi,
            R.drawable.map_gray_hdpi,
            R.drawable.settings_grey2_hdpi
    };

    // trying out
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        // initiating db:
        FieldDB.init(this.getApplicationContext());
        ApartmentDB.init(this.getApplicationContext());

        FieldDB fieldDB = FieldDB.getInstance();
        fieldDB.dummy(); // TODO: REMOVE!
        Data.setAllFields(fieldDB.getFieldList());
    }

    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter= new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ApartmentListFragment(), "ApartmentList");
        adapter.addFragment(new FavouritesFragment(), "FavouritesFragment");
        adapter.addFragment(new MapFragment() , "MapFragment");
        adapter.addFragment(new SettingsFragment(), "SettingsFragment");
        viewPager.setAdapter(adapter);
    }
}