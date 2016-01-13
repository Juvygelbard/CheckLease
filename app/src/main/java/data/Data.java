package data;

import java.util.ArrayList;

/**
 * Created by user on 30/12/2015.
 */
public class Data {
    private static ArrayList<Field> _allFields;
    private static String _city = "BG";
    private static int _apartmentCounter = 0; // TODO: get current count from properties.

    public static final int STREET = 0;
    public static final int BUILDING = 1;
    public static final int NUM_APARTMENT = 2;
    public static final int PHONE = 3;

    private Data(){}

    public static String getCity(){ return _city; }

    public static void setFieldList(ArrayList<Field> fields){
        _allFields = fields;
    }

    public static ArrayList<Field> getAllFields(){
        return _allFields;
    }

    public static void setAllFields(ArrayList<Field> allFields){
        _allFields = allFields;
    }

    public static int getCurrApartmentCounter(){
        return _apartmentCounter;
    }

    public static void increaseApartmentCounter(){
        _apartmentCounter++;
    }
}