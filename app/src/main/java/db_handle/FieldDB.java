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
        Cursor curr = db.query("fields", col, "city='" + Data.getCityName() + "'", null, null, null, "order_i");

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


    public void updateFieldList(ArrayList<Field> newList){
        SQLiteDatabase db = _db.getWritableDatabase();
        db.delete("fields", null, null);
        for(Field field: newList){
            ContentValues item = new ContentValues();
            item.put("id", field.getId());
            item.put("order_i", field.getOrder());
            item.put("name", field.getName());
            item.put("type", field.getType());
            item.put("formula", field.getFormula());
            item.put("extra1", field.getEx1());
            item.put("extra2", field.getEx2());
            db.insert("fields", null, item);
        }
    }
}
