package db_handle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import data.Apartment;
import data.Data;
import data.Value;

/**
 * Created by Juvy on 30/12/2015.
 */
public class ApartmentDB {
    private static DBHelper _db;
    private static ApartmentDB _instance;

    /**
     * creates a FieldDBHelper object to connect with the db.
     * should be called only once!
     * @param db
     */
    public static void init(DBHelper db){
        _db = db;
    }

    private ApartmentDB(){ }

    /**
     * @return the one and only instance of FieldDB to be used.
     */
    public static ApartmentDB getInstance(){
        if(_instance == null)
            _instance = new ApartmentDB();
        return _instance; }

    public ArrayList<Apartment> getApartmentList(){
        HashMap<Integer, Apartment> apartments = new HashMap<Integer, Apartment>();
        SQLiteDatabase db = _db.getReadableDatabase();
        String col[] = {"apartment_id", "field_id", "int_val", "str_val"};
        Cursor curr = db.query("apartments", col, "city='" + Data.getCity() + "'", null, null, null, null);

        if(curr.moveToFirst()){
            int colApartmentID = curr.getColumnIndex("apartment_id");
            int colFieldID = curr.getColumnIndex("field_id");
            int colIntVal = curr.getColumnIndex("int_val");
            int colStrVal = curr.getColumnIndex("str_val");

            while(!curr.isAfterLast()){
                // get value
                Integer apartment_id = new Integer(curr.getInt(colApartmentID));
                int field_id = curr.getInt(colFieldID);
                int intVal = curr.getInt(colIntVal);
                String strVal = curr.getString(colStrVal);

                // add a new apartment if it dosn't exist.
                if(!apartments.containsKey(apartment_id))
                    apartments.put(apartment_id, new Apartment(apartment_id));

                // add value to existing apartment
                Apartment currApp = apartments.get(apartment_id);
                if(strVal.equals("NULL"))
                    currApp.addValue(field_id, intVal);
                else
                    currApp.addValue(field_id, strVal);

                curr.moveToNext();
            }
        }
        ArrayList<Apartment> apartmentList = new ArrayList<Apartment>();
        for(Map.Entry<Integer, Apartment> apartment: apartments.entrySet())
            apartmentList.add(apartment.getValue());
        return apartmentList;
    }

    public ArrayList<Apartment> getFavoriteList(){
        ArrayList<Apartment> raw = this.getApartmentList();
        ArrayList<Apartment> filtered = new ArrayList<Apartment>();
        for(Apartment curr: raw)
            if(curr.getValue(Data.FAVORITE).getIntValue() == 1)
                filtered.add(curr);
        return filtered;
    }

    public void addApartment(Apartment toAdd){
        SQLiteDatabase db = _db.getWritableDatabase();
        // get features iterator
        Iterator<Map.Entry<Integer, Value>> apartmentFeature = toAdd.getFeatureIterator();
        while(apartmentFeature.hasNext()){ // iterate through features and add them to db
            Map.Entry<Integer, Value> currEntry = apartmentFeature.next();
            Value val = currEntry.getValue();
            int fieldID = currEntry.getKey().intValue();
            int intVal = val.getIntValue();
            String strVal = val.getStrValue();

            ContentValues feature = new ContentValues();
            feature.put("city", Data.getCity());
            feature.put("apartment_id", Data.getCurrApartmentCounter());
            feature.put("field_id", fieldID);
            feature.put("int_val", intVal);
            feature.put("str_val", strVal);
            db.insert("apartments", null, feature);
        }
        Data.increaseApartmentCounter();
    }

    public void setFavorite(int apartmentID ,boolean state){
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues updatedRow = new ContentValues();
        if(state)
            updatedRow.put("int_val", 1);
        else
            updatedRow.put("int_val", 0);
        db.update("apartments", updatedRow, "apartment_id=" + apartmentID + " AND field_id=" + Data.FAVORITE, null);
    }

    public void deleteApartment(int apartmentID){
        SQLiteDatabase db = _db.getWritableDatabase();
        db.delete("apartments","apartment_id=" + apartmentID, null);
    }
}