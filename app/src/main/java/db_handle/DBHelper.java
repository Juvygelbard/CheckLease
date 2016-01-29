package db_handle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juvy on 12/01/2016.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String _DB_NAME = "CheckLease.db";
    private static final String _APARTMENT_MAKE_QUERY = "CREATE TABLE apartments(city VARCHAR(100), " +
            "apartment_id INTEGER, " +
            "field_id INTEGER, " +
            "int_val INTEGER, " +
            "str_val TEXT);";
    private static final String _FIELDS_MAKE_QUERY = "CREATE TABLE fields(id INTEGER, " +
            "city VARCHAR(100), " +
            "name VARCHAR(255), " +
            "type INTEGER, " +
            "formula TEXT, " +
            "extra1 TEXT, " +
            "extra2 INTEGER, " +
            "order_i INTEGER);";
    private static final String _PICTURES_MAKE_QUERY = "CREATE TABLE pics(apartment_id INTEGER, " +
            "path TEXT);";
    private static final String _APARTMENT_DROP_QUERY = "DROP TABLE IF EXISTS apartments;";
    private static final String _FIELDS_DROP_QUERY = "DROP TABLE IF EXISTS fields;";
    private static final String _PICTURES_DROP_QUERY = "DROP TABLE IF EXISTS pics;";

    public DBHelper(Context context) {
        super(context, _DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(_FIELDS_MAKE_QUERY);
        db.execSQL(_APARTMENT_MAKE_QUERY);
        db.execSQL(_PICTURES_MAKE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(_FIELDS_DROP_QUERY);
        db.execSQL(_APARTMENT_DROP_QUERY);
        db.execSQL(_PICTURES_DROP_QUERY);
        onCreate(db);
    }
}