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

        // initiating db:
        DBHelper db = new DBHelper(this.getApplicationContext());
        ApartmentDB.init(db);
        FieldDB.init(db); // try
        PicturesDB.init(db);
        AzureHelper.init(this.getApplicationContext());
        final FieldDB fieldDB = FieldDB.getInstance();
        Data.setAllFields(fieldDB.getFieldList());

        AsyncTask<Void, Void, Void> updateFields = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                // update fields
                try {
                    AzureHelper azureDB = AzureHelper.getInstance();
                    ArrayList<Field> fields = azureDB.getFieldList(Data.getCity());
                    // fieldDB.updateFieldList(fields);
                    Data.setAllFields(fields);
                }
                catch (MobileServiceException e) {}
                catch (ExecutionException e) {}
                catch (InterruptedException e) {}

                return null;
            }
        };
        updateFields.execute();
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