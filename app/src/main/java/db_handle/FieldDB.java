package db_handle;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import data.Data;
import data.Field;

/**
 * Created by Juvy on 30/12/2015.
 */
public class FieldDB {
    private static DBHelper _db;
    private static FieldDB _instance;

    /**
     * creates a FieldDBHelper object to connect with the db.
     * should be called only once!
     * @param db
     */
    public static void init(DBHelper db){
        _db = db;
    }

    private FieldDB(){ }

    /**
     * @return the one and only instance of FieldDB to be used.
     */
    public static FieldDB getInstance(){
        if(_instance == null)
            _instance = new FieldDB();
        return _instance; }

    public ArrayList<Field> getFieldList(){
        ArrayList<Field> fieldList = new ArrayList<Field>();
        SQLiteDatabase db = _db.getReadableDatabase();
        String col[] = {"id", "name", "type", "formula", "extra1", "extra2"};
        Cursor curr = db.query("fields", col, "city='" + Data.getCity() + "'", null, null, null, "order_i");

        if(curr.moveToFirst()){
            int colID = curr.getColumnIndex("id");
            int colName = curr.getColumnIndex("name");
            int colType = curr.getColumnIndex("type");
            int colFormula = curr.getColumnIndex("formula");
            int colExtra1 = curr.getColumnIndex("extra1");
            int colExtra2 = curr.getColumnIndex("extra2");

            while(!curr.isAfterLast()){
                int id = curr.getInt(colID);
                String name = curr.getString(colName);
                int type = curr.getInt(colType);
                String formula = curr.getString(colFormula);
                String extra1 = curr.getString(colExtra1);
                int extra2 = curr.getInt(colExtra2);
                Field toAdd = new Field(id, name, type, extra1, extra2, formula);
                fieldList.add(toAdd);
                curr.moveToNext();
            }
        }
        return fieldList;
    }

    // TODO: DELETE THIS!
    public void dummy(){
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues neighborhood = new ContentValues();
        neighborhood.put("id", 3);
        neighborhood.put("order_i", 1);
        neighborhood.put("city", "BG");
        neighborhood.put("name", "שכונה");
        neighborhood.put("type", Field.MULTISELECT);
        neighborhood.put("formula", "X+100Y");
        neighborhood.put("extra1", "א';ב';ג';ד';ו'");
        neighborhood.put("extra2", 4);
        db.insert("fields", null, neighborhood);

        ContentValues appartmentSize = new ContentValues();
        appartmentSize.put("id", 4);
        appartmentSize.put("order_i", 2);
        appartmentSize.put("city", "BG");
        appartmentSize.put("name", "גודל דירה");
        appartmentSize.put("type", Field.NUMBER);
        appartmentSize.put("formula", "(1+Y/100)*X");
        appartmentSize.put("extra1", "");
        appartmentSize.put("extra2", 100);
        db.insert("fields", null, appartmentSize);

        ContentValues washingMachine = new ContentValues();
        washingMachine.put("id", 5);
        washingMachine.put("order_i", 3);
        washingMachine.put("city", "BG");
        washingMachine.put("name", "מכונת כביסה");
        washingMachine.put("type", Field.CHECKBOX);
        washingMachine.put("formula", "X+200Y");
        washingMachine.put("extra1", "");
        washingMachine.put("extra2", 0);
        db.insert("fields", null, washingMachine);

        ContentValues phoneRes = new ContentValues();
        phoneRes.put("id", 2);
        phoneRes.put("order_i", 0);
        phoneRes.put("city", "BG");
        phoneRes.put("name", "טלפון דיירים");
        phoneRes.put("type", Field.PHONE);
        phoneRes.put("formula", "X");
        phoneRes.put("extra1", "");
        phoneRes.put("extra2", 0);
        db.insert("fields", null, phoneRes);
    }
}
