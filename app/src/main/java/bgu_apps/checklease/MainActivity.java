package bgu_apps.checklease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.microsoft.windowsazure.mobileservices.MobileServiceException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import Fragments.ApartmentListFragment;
import Fragments.SettingsFragment;
import Fragments.MapFragment;
import adapters.ViewPagerAdapter;
import data.Apartment;
import data.City;
import data.Data;
import data.Field;
import db_handle.ApartmentDB;
import db_handle.AzureHelper;
import db_handle.FieldDB;
import db_handle.DBHelper;
import db_handle.PicturesDB;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static Intent _intent = null;
    public static ApartmentListFragment _fullListFragment;
    public static ApartmentListFragment _favListFragment;
    public static MapFragment _mapFragment;
    public static int _currTab;

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
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            public void onTabSelected(TabLayout.Tab tab) {
                _currTab = tab.getPosition();
                super.onTabSelected(tab);
            }
        });
        Data.initSharedPreferences(getSharedPreferences("SETTINGS", 0));
        Data.setDeletedApartments(new ArrayList<Apartment>());

        // initiating db:
        DBHelper db = new DBHelper(this.getApplicationContext());
        ApartmentDB.init(db);
        FieldDB.init(db);
        PicturesDB.init(db);
        AzureHelper.init(this.getApplicationContext());
        final FieldDB fieldDB = FieldDB.getInstance();
        Data.setAllFields(fieldDB.getFieldList());

        ArrayList<City> tempCityList = new ArrayList<City>();
        tempCityList.add(Data.getCity());
        Data.setAllCities(tempCityList); // so user won't get an error if citylist is not updated.

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                // update fields
                try {
                    AzureHelper azureDB = AzureHelper.getInstance();

                    // get cities
                    Data.setAllCities(azureDB.getCityList());

                    // update fields
                    ArrayList<Field> fields = azureDB.getFieldList(Data.getCityID());
                    fieldDB.updateFieldList(fields);
                    Data.setAllFields(fields);

                    // update apartments after fields update
                    ApartmentListFragment.reCalcApartmentList(fields);
                }
                catch (MobileServiceException e) {}
                catch (ExecutionException e) {}
                catch (InterruptedException e) {}

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                _fullListFragment.refreshList();
                _favListFragment.refreshList();
                _mapFragment.refreshMap();
            }
        }.execute();
    }

    private void setupTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    public static int getCurrTabIndex(){
        return _currTab;
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        _fullListFragment = new ApartmentListFragment(false);
        _favListFragment = new ApartmentListFragment(true);
        _mapFragment = new MapFragment();

        adapter.addFragment(_fullListFragment, "ApartmentList");
        adapter.addFragment(_favListFragment, "FavoritesFragment");
        adapter.addFragment(_mapFragment, "MapFragment");
        adapter.addFragment(new SettingsFragment(), "SettingsFragment");
        viewPager.setAdapter(adapter);
    }
}