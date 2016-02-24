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
import Fragments.FavouritesFragment;
import Fragments.SettingsFragment;
import Fragments.MapFragment;
import adapters.ViewPagerAdapter;
import data.City;
import data.Data;
import data.Field;
import db_handle.ApartmentDB;
import db_handle.AzureHelper;
import db_handle.FieldDB;
import db_handle.DBHelper;
import db_handle.PicturesDB;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean _showFav;

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
        _showFav = false;

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        // add fav button.
//        LinearLayout tabStrip = (LinearLayout)tabLayout.getChildAt(0);
//        tabStrip.getChildAt(0).setEnabled(false);
//        ImageButton favButton = new ImageButton(this.getApplicationContext());
//        favButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        favButton.setImageResource(R.drawable.star_empty_grey);
//        favButton.setBackgroundColor(Color.TRANSPARENT);
//        tabStrip.addView(favButton, 0);

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
                    // update fields
                    AzureHelper azureDB = AzureHelper.getInstance();
                    ArrayList<Field> fields = azureDB.getFieldList(Data.getCityID());
                    fieldDB.updateFieldList(fields);
                    Data.setAllFields(fields);

                    // get cities
                    Data.setAllCities(azureDB.getCityList());

//                    azureDB.addCity(new City("חולון","HIT", 32.015833, 34.787384, 12.0f));
//                    azureDB.addCity(new City("באר שבע","BG", 31.250919, 34.783916, 12.0f));
//                    azureDB.addCity(new City("תל אביב","TA", 32.085300, 34.781768, 12.0f));

                }
                catch (MobileServiceException e) {}
                catch (ExecutionException e) {}
                catch (InterruptedException e) {}

                return null;
            }
        }.execute();
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