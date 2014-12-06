package com.example.lovasistvn.tmap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lovas Istvan on 2014.12.03..
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "MapPointsDB";

    // Books table name
    private static final String TABLE_MAPPOINTS = "mappoints";

    // Books Table Columns names
    private static final String KEY_ID ="pointid";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDR = "address";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_POINTS_TABLE = "CREATE TABLE mappoints ( " +
                "pointid INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " +
                "address TEXT, "+
                "latitude TEXT, "+"longitude TEXT )";

        // create books table
        db.execSQL(CREATE_POINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS mappoints");

        // create fresh books table
        this.onCreate(db);
    }


    public void addPoint(MapPoint point){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, point.getPointname()); // get title
        values.put(KEY_ADDR, point.getPointaddress()); // get author
        values.put(KEY_LAT, point.getPointlatitude()); // get author
        values.put(KEY_LON, point.getPointlongitude()); // get author

        // 3. insert
        db.insert(TABLE_MAPPOINTS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public ArrayList<MapPoint> getAllPoints() {
        ArrayList<MapPoint> points = new ArrayList<MapPoint>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_MAPPOINTS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build point and add it to list
        MapPoint point = null;
        if (cursor.moveToFirst()) {
            do {
                point = new MapPoint();
                point.setPointid(Integer.parseInt(cursor.getString(0)));
                point.setPointname(cursor.getString(1));
                point.setPointaddress(cursor.getString(2));
                point.setPointlatitude(Double.parseDouble(cursor.getString(3)));
                point.setPointlongitude(Double.parseDouble(cursor.getString(4)));

                // Add point to books
                points.add(point);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", points.toString());

        // return books
        return points;
    }

    // Updating single mappoint
    public int updatePoint(MapPoint point) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, point.getPointname()); // get title
        values.put(KEY_ADDR, point.getPointaddress()); // get author
        values.put(KEY_LAT, point.getPointlatitude()); // get author
        values.put(KEY_LON, point.getPointlongitude()); // get author

        // updating row
        return db.update(TABLE_MAPPOINTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(point.getPointid()) });
    }

    // Deleting single contact
    public void deletePoint(MapPoint point) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MAPPOINTS, KEY_ID + " = ?",
                new String[] { String.valueOf(point.getPointid())});
        db.close();
    }

    public int GetMaxID(){
        int id = 0;
        final String query = "SELECT MAX(pointid) FROM "+TABLE_MAPPOINTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(query, null);

        if(mCursor.moveToFirst())
            do{
                id = mCursor.getInt((0));
            }while(mCursor.moveToNext());


        return id;
    }
}
