package db_handle;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Juvy on 28/01/2016.
 */
public class PicturesDB
{
    private static DBHelper _db;
    private static PicturesDB _instance;
    private final int TEMP_ID = -1;

        public static void init(DBHelper db){
            _db = db;
        }

        private PicturesDB(){ }

        public static PicturesDB getInstance(){
            if(_instance == null)
                _instance = new PicturesDB();
            return _instance; }

        public ArrayList<String> getPicturePathList(int apartmentID){
            ArrayList<String> pics = new ArrayList<String>();
            SQLiteDatabase db = _db.getReadableDatabase();
            String col[] = {"apartment_id", "path"};
            Cursor curr = db.query("pics", col, "apartment_id=" + apartmentID , null, null, null, null);

            if(curr.moveToFirst()){
                int pathCol = curr.getColumnIndex("path");

                while(!curr.isAfterLast()){
                    String path = curr.getString(pathCol);
                    pics.add(path);
                    curr.moveToNext();
                }
            }
            return pics;
        }

    public ArrayList<String> getTempPics(){
        return getPicturePathList(TEMP_ID);
    }

    public void addPic(int apartmentID, String path){
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues pic = new ContentValues();
        pic.put("apartment_id", apartmentID);
        pic.put("path", path);
        db.insert("pics", null, pic);
    }

    public void addTempPic(String path){
        addPic(TEMP_ID, path);
    }

    public void saveTempPic(int apartmentID){
        SQLiteDatabase db = _db.getWritableDatabase();
        ContentValues updatedRow = new ContentValues();
        updatedRow.put("apartment_id", apartmentID);
        db.update("pics", updatedRow, "apartment_id=" + TEMP_ID, null);
    }

    public void removePic(String path){
        SQLiteDatabase db = _db.getWritableDatabase();
        db.delete("pics", "path='" + path + "'", null);
    }

    public void removePic(int apartmentID){
        SQLiteDatabase db = _db.getWritableDatabase();
        db.delete("pics", "apartment_id=" + apartmentID, null);
    }

    public void removeTempPics(){
        removePic(TEMP_ID);
    }
}
