package db_handle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juvy on 12/01/2016.
 */
class ApartmentDBHelper extends SQLiteOpenHelper{
    private static final String _DB_NAME = "CheckLease.db";
    private static final String _SQL_MAKE_QUERY = "CREATE TABLE apartments(city VARCHAR(100), " +
            "apartment_id INTEGER, " +
            "field_id INTEGER, " +
            "int_val INTEGER, " +
            "str_val TEXT);";
    private static final String _SQL_DROP_QUERY = "DROP TABLE IF EXISTS apartments;";

    public ApartmentDBHelper(Context context) {
        super(context, _DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(_SQL_MAKE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(_SQL_DROP_QUERY);
        onCreate(db);
    }
}