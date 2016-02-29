package data;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Created by user on 30/12/2015.
 */
public class Data {
    private static ArrayList<Field> _allFields;
    private static ArrayList<City> _allCities;
    private static City _city;

    private static Hashtable<String, Integer> _phoneFields;

    private static ArrayList<Apartment> _deletedApartments;

    private static int _sortBy;
    private static boolean _shareToCloud;

    private static int _apartmentCounter = 0; // TODO: get current count from properties.

    public static final String VAL_SEPARATOR_A = String.valueOf((char)178);
    public static final String VAL_SEPARATOR_B = String.valueOf((char)179);

    public static final double ADDRESS_RADIUS = 0.35;

    public static final int FAVORITE = -1;
    public static final int ADDRESS_ID = -2;
    public static final int ADDRESS_STR = -3;
    public static final int ADDRESS_LAT = -4;
    public static final int ADDRESS_LAN = -5;
    public static final int APARTMENT_NUM = -6;
    public static final int GIVEN_PRICE = -7;
    public static final int CALC_PRICE = -8;

    public static final float LOW_PRICE_BAR = 1.05f;
    public static final float HIGH_PRICE_BAR = 1.3f;

    public static final int SORT_DEF = 0;
    public static final int SORT_FAV = 1;
    public static final int SORT_ID = 2;
    public static final int SORT_PRICE_DOWNTOUP = 3;
    public static final int SORT_PRICE_UPTODOWN = 4;



    private static SharedPreferences _settings;
    private static SharedPreferences.Editor _editor;


    private Data(){}

    public static void initSharedPreferences(SharedPreferences settings){
        _settings = settings;
        _editor = _settings.edit();
        boolean isFirstTime = settings.getBoolean("isFirstTime", true);
        if(isFirstTime){
            _editor.putInt("sortBy", SORT_DEF);
            _sortBy = SORT_DEF;
            _editor.putString("currCity", _allCities.get(0).getID());
            _city = _allCities.get(0);
            _editor.putBoolean("isDataShared" , false);
            _shareToCloud = false;
            _editor.putBoolean("isFirstTime", false);
            _editor.commit();
        }
        else{
            _sortBy = _settings.getInt("sortBy", SORT_DEF);
            _city = findCityByID(_allCities, _settings.getString("currCity", _allCities.get(0).getID()));
            _shareToCloud = _settings.getBoolean("isDataShared", false);
        }
    }

    public static City findCityByID(ArrayList<City> allCities, String id){
        for (int i = 0 ; i < allCities.size() ; i++){
            if(allCities.get(i).getID().equals(id))
                return allCities.get(i);
        }
        return allCities.get(0);
    }



    public static ArrayList<Apartment> getDeletedApartments(){ return _deletedApartments; }
    public static void setDeletedApartments (ArrayList<Apartment> deletedApartments){_deletedApartments = deletedApartments; }

    public static void setSortBy(int sortBy){
        _sortBy = sortBy;
        _editor.putInt("sortBy", sortBy);
    }

    public static int getSortBy(){ return _sortBy; }

    public static void setCity(City city){
        _city = city;
        _editor.putString("currCity", city.getID());
    }
    public static String getCityName(){return _city.getName(); }
    public static String getCityID(){ return _city.getID(); }
    public static LatLng getCityLatLan(){ return _city.getLatLan(); }
    public static float getCityZoom(){ return _city.getZoom(); }
    public static City getCity(){ return _city; }

    public static void setFieldList(ArrayList<Field> fields){
        _allFields = fields;
    }

    public static ArrayList<Field> getAllFields(){
        return _allFields;
    }

    public static void setAllFields(ArrayList<Field> allFields){
        // setup phone fields list
        _phoneFields = new Hashtable<String, Integer>();
        for(Field curr: allFields){
            if(curr.getType() == Field.PHONE)
                _phoneFields.put(curr.getName(), new Integer(curr.getId()));
        }
        _allFields = allFields;
    }

    public static ArrayList<String> getPhonefieldList(){
        return new ArrayList<String>(_phoneFields.keySet());
    }

    public static int getPhonefieldID(String name){
        return _phoneFields.get(name).intValue();
    }

    public static int getCurrApartmentCounter(){
        return _apartmentCounter;
    }

    public static void increaseApartmentCounter(){
        _apartmentCounter++;
    }

    public static int getRate(int calcPrice, int givenPrice){
        if(givenPrice == 0)
            return 0;
        float norm = (float) givenPrice / (float) calcPrice;
        if(norm <= LOW_PRICE_BAR)
            return 1;
        else if(norm <= HIGH_PRICE_BAR)
            return 2;
        else
            return 3;
    }

    public static boolean isDataShared(){
        return _shareToCloud;
    }

    public static void setIsDataShared(boolean shared){
        _shareToCloud = shared;
        _editor.putBoolean("isDataShared", shared);
    }

    public static void setAllCities(ArrayList<City> cities){
        _allCities = cities;
    }

    public static ArrayList<City> getAllCities(){
        return _allCities;
    }
}