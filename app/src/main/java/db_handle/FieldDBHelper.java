package db_handle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juvy on 12/01/2016.
 */
class FieldDBHelper extends SQLiteOpenHelper{
    private static final String _DB_NAME = "CheckLease.db";
    private static final String _SQL_MAKE_QUERY = "CREATE TABLE fields(id INTEGER, " +
                                                                        "city VARCHAR(100), " +
                                                                        "name VARCHAR(255), " +
                                                                        "type INTEGER, " +
                                                                        "formula TEXT, " +
                                                                        "extra1 TEXT, " +
                                                                        "extra2 INTEGER, " +
                                                                        "order_i INTEGER);";
    private static final String _SQL_DROP_QUERY = "DROP TABLE IF EXISTS fields;";

    public FieldDBHelper(Context context) {
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