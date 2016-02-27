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
import data.Value;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static Intent _intent = null;
    public static ApartmentListFragment _fullListFragment;
    public static ApartmentListFragment _favListFragment;
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

        Data.setDeletedApartments(new ArrayList<Apartment>());

        Data.setIsDataShared(true); // TODO: REMOVE/CHANGE!
        Data.setCity(new City("באר שבע","BG", 31.250919, 34.783916, 12.0f));

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
                    ApartmentDB appDB = ApartmentDB.getInstance();

                    for(Apartment apartment: ApartmentListFragment._apartments){
                        int id = apartment.getId();
                        ArrayList<Value> vals = Field.matchParmasToFields(apartment);
                        Apartment updated = new Apartment(id);
                        for(int i=0; i<fields.size(); i++){
                            updated.addValue(fields.get(i).getId(), vals.get(i));
                        }
                        updated.addValue(Data.ADDRESS_ID, apartment.getValue(Data.ADDRESS_ID));
                        updated.addValue(Data.APARTMENT_NUM, apartment.getValue(Data.APARTMENT_NUM));
                        updated.addValue(Data.ADDRESS_LAT, apartment.getValue(Data.ADDRESS_LAT));
                        updated.addValue(Data.ADDRESS_LAN, apartment.getValue(Data.ADDRESS_LAN));
                        updated.addValue(Data.ADDRESS_STR, apartment.getValue(Data.ADDRESS_STR));
                        updated.addValue(Data.CALC_PRICE, apartment.getValue(Data.CALC_PRICE));
                        updated.addValue(Data.GIVEN_PRICE, apartment.getValue(Data.GIVEN_PRICE));
                        updated.addValue(Data.FAVORITE, apartment.getValue(Data.FAVORITE));

                        appDB.deleteApartment(id);
                        appDB.addApartment(updated);
                    }
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

        adapter.addFragment(_fullListFragment, "ApartmentList");
        adapter.addFragment(_favListFragment, "FavoritesFragment");
        adapter.addFragment(new MapFragment(), "MapFragment");
        adapter.addFragment(new SettingsFragment(), "SettingsFragment");
        viewPager.setAdapter(adapter);
    }
}