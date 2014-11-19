/****************************************************************************************
 /*
 /* FILE NAME: TapOpenHelper.java
 /*
 /* DESCRIPTION: Oversees creation and update of DB on device, contains information for all tables and columns.
 /*
 /* REFERENCE: Used throughout application.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/24/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLData;

public class TapOpenHelper extends SQLiteOpenHelper {

    // Current DB version. Iterate to reset DB.
    private static final int DATABASE_VERSION = 5;

    // Name of DB.
    private static final String DATABASE_NAME = "tapwater.db";

    // Refers to names of tables in DB.
    public static final String DRINK_TABLE_NAME = "drinks";
    public static final String USER_TABLE_NAME = "user";

    // Refers to names of columns in DB.
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DRINK_DATE = "drink_date";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DEVICE_TOKEN = "device_token";
    public static final String COLUMN_LOGGED_IN = "logged_in";

    // String used to create Drink table via SQlite.
    public static final String CREATE_DRINK_TABLE = "CREATE TABLE " + DRINK_TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_UUID + " TEXT NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_DRINK_DATE + " TEXT NOT NULL)";

    // String used to create User table via SQlite
    public static final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE_NAME + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USERNAME + " TEXT NOT NULL, " +
            COLUMN_DEVICE_TOKEN + " TEXT NOT NULL, " +
            COLUMN_LOGGED_IN + " INTEGER NOT NULL)";

    /**
     * Constructor used rarely for certain DB operations outside of this class.
     *
     * @param context: context of parent activity.
     */
    public TapOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * Creates tables of DB.
     *
     * @param db: Database object used to execute SQL
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DRINK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        System.out.println("ALL TABLES CREATED");
    }

    /**
     * Upgrades DB when version number is iterated.
     *
     * @param db:         Database object used to execute SQL.
     * @param oldVersion: Previous DB version number.
     * @param newVersion: New DB version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DRINK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        System.out.println("UPDATING ALL TABLES");
        onCreate(db);
    }

    // TODO: Implement usage for this method
//    public void resetDrinks(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE IF EXISTS " + DRINK_TABLE_NAME);
//        db.execSQL(CREATE_DRINK_TABLE);
//        System.out.println("DRINKS RESET");
//    }

    /**
     * Drop all tables in DB
     *
     * @param db: Database object used to execute SQL.
     */
    public void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DRINK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        System.out.println("DROPPED ALL TABLES");
    }

    /**
     * Delete Drinks prior to sync with server
     *
     * @param db: Database object used to execute SQL.
     */
    public void resetDrinks(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DRINK_TABLE_NAME);
        db.execSQL(CREATE_DRINK_TABLE);
        System.out.println("RESET DRINKS");
    }

    /**
     * Called when logging out. Drops all tables and recreates them empty.
     *
     * @param db: Database object used to execute SQL.
     */
    public void logOut(SQLiteDatabase db) {
        dropAllTables(db);
        onCreate(db);
    }
}
