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
    public static final int ADDRESS_ID = 1;
    public static final int ADDRESS = 2;
    public static final int APARTMENT_NUM = 3;
    public static final int GIVEN_PRICE = 4;
    public static final int CALC_PRICE = 5;

    public static final float LOW_PRICE_BAR = 1.05f;
    public static final float HIGH_PRICE_BAR = 1.3f;

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
}