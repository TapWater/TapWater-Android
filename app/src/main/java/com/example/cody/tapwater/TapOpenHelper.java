package com.example.cody.tapwater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TapOpenHelper extends SQLiteOpenHelper {

    //Basic helper class for database activity that contains constants
    //with table, column, database, and version information.

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "tapwater.db";

    public static final String DRINK_TABLE_NAME = "drinks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DRINK_DATE = "drink_date";

    public static final String CREATE_DRINK_TABLE = "CREATE TABLE " + DRINK_TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_UUID + " TEXT NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_DRINK_DATE + " TEXT NOT NULL)";

    public TapOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DRINK_TABLE);
        System.out.println("ALL TABLES CREATED");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DRINK_TABLE_NAME);
        System.out.println("UPDATING ALL TABLES");
        onCreate(db);
    }
}
