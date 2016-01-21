package data;

import java.util.ArrayList;

/**
 * Created by user on 30/12/2015.
 */
public class Data {
    private static ArrayList<Field> _allFields;
    private static String _city = "BG";
    private static int _apartmentCounter = 0; // TODO: get current count from properties.

    public static final String LINE_SEPARATOR = String.valueOf((char)178);


    public static final int FAVORITE = 0;
    public static final int STREET = 1;
    public static final int BUILDING = 2;
    public static final int NUM_APARTMENT = 3;
    public static final int PHONE = 4;

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