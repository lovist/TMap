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
    private static final String DATABASE_NAME = "PointsDB";

    // Books table name
    private static final String TABLE_MAPPOINTS = "mappoints";

    // Books Table Columns names
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
                "name TEXT PRIMARY KEY, " +
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
        ArrayList<MapPoint> books = new ArrayList<MapPoint>();

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
                point.setPointname(cursor.getString(0));
                point.setPointaddress(cursor.getString(1));
                point.setPointlatitude(Double.parseDouble(cursor.getString(2)));
                point.setPointlongitude(Double.parseDouble(cursor.getString(2)));

                // Add point to books
                books.add(point);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", books.toString());

        // return books
        return books;
    }
}
