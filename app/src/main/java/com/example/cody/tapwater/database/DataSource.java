/****************************************************************************************
 /*
 /* FILE NAME: DataSource.java
 /*
 /* DESCRIPTION: Used to interact with data inside the database for any insert, update, or read purposes.
 /*
 /* REFERENCE: Created and used throughout the application.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/24/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.objects.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DataSource {

    SQLiteOpenHelper db;
    SQLiteDatabase qdb;

    // Columns for Drink table. Referenced from TapOpenHelper
    public static final String[] drinkColumns = {
            TapOpenHelper.COLUMN_ID,
            TapOpenHelper.COLUMN_UUID,
            TapOpenHelper.COLUMN_CATEGORY,
            TapOpenHelper.COLUMN_DRINK_DATE,
    };

    // Columns for User table. Referenced from TapOpenHelper
    public static final String[] userColumns = {
            TapOpenHelper.COLUMN_ID,
            TapOpenHelper.COLUMN_USERNAME,
            TapOpenHelper.COLUMN_DEVICE_TOKEN,
            TapOpenHelper.COLUMN_LOGGED_IN
    };

    /**
     * Constructor needed to user DataSource methods in other activities and classes.
     *
     * @param context: context of activity creating the constructor.
     */
    public DataSource(Context context) {
        db = new TapOpenHelper(context);
    }

    /**
     *
     * Opens DB in preparation for any write actions.
     *
     */
    public void openWrite() {
        System.out.println("DB OPENED WRITABLE");
        qdb = db.getWritableDatabase();
    }

    /**
     *
     * Opens DB in preparation for any read actions.
     *
     */
    public void openRead() {
        System.out.println("DB OPENED READABLE");
        qdb = db.getReadableDatabase();
    }

    /**
     *
     * Closes DB.
     *
     */
    public void close() {
        System.out.println("DB CLOSED");
        db.close();
    }

    /**
     *
     * Insert Drink object into DB.
     *
     * @param drink: object to be inserted.
     */
    public void insertDrink(Drink drink) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(TapOpenHelper.COLUMN_UUID, drink.getUUID());
        values.put(TapOpenHelper.COLUMN_CATEGORY, drink.getCategory());
        values.put(TapOpenHelper.COLUMN_DRINK_DATE, drink.getDrinkDate());
        qdb.insert(TapOpenHelper.DRINK_TABLE_NAME, null, values);
        close();
    }

    /**
     *
     * Insert User object into DB.
     *
     * @param user: object to be inserted.
     */
    public void insertUser(User user) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(TapOpenHelper.COLUMN_USERNAME, user.getUsername());
        values.put(TapOpenHelper.COLUMN_DEVICE_TOKEN, user.getDeviceToken());
        values.put(TapOpenHelper.COLUMN_LOGGED_IN, user.getLoggedIn());
        qdb.insert(TapOpenHelper.USER_TABLE_NAME, null, values);
        close();
    }

    // TODO: Make this return only the cups for a specified day instead of all cups in the DB

    /**
     * Get double value of the total number of cups recorded in the DB.
     *
     * @return number of cups in DB.
     */
    public double getTotalCups() {
        int ounces = 0;
        double cups;
        Calendar today = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000Z'", Locale.US);
        openRead();

        // Query Drinks table and iterate through all returned records
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {

            String date = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE));
            try {
                dateCal.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (today.get(Calendar.DAY_OF_YEAR) == dateCal.get(Calendar.DAY_OF_YEAR) &&
                today.get(Calendar.MONTH) == dateCal.get(Calendar.MONTH) &&
                today.get(Calendar.YEAR) == dateCal.get(Calendar.YEAR)) {

                // If category is drink, add 4 ounces; if glass, add 8 ounces; if bottle, add 16 ounces.
                String cat = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_CATEGORY));
                if (cat.equals("drink")) {
                    ounces += 4;
                } else if (cat.equals("glass")) {
                    ounces += 8;
                } else if (cat.equals("bottle")) {
                    ounces += 16;
                }
            }
        }

        // Close DB and Cursor
        c.close();
        close();

        // Number of cups is number of ounces divided by 8.
        cups = ounces / 8.0;
        return cups;
    }

    /**
     *
     * Get latest Drink date recorded in DB. Used to calculate time since a Drink was created.
     *
     * @return Calendar containing latest Drink date.
     */
    public Calendar getLatestDate() {
        // Establish initial values, set date format.
        Calendar saved = null;
        Calendar check = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000Z'", Locale.US);
        openRead();

        // Get drinks from DB, iterate though them.
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                // If date checked is after currently saved date, replace saved date with checked date.
                String d = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE));
                if (saved == null) {
                    saved = Calendar.getInstance();
                    saved.setTime(sdf.parse(d));
                }
                check.setTime(sdf.parse(d));
                if (check.after(saved)) {
                    saved.setTime(sdf.parse(d));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // If there are no saved dates in DB, use current date.
        if (saved == null) {
            saved = Calendar.getInstance();
        }

        // Close DB and Cursor and return latest saved date.
        c.close();
        close();
        return saved;
    }

    /**
     *
     * Return Drink date by the specified Drink UUID.
     *
     * @param uuid: UUID of Drink that the date is desired for.
     * @return Date of the Drink specified.
     */
    public Calendar getDateByUuid(String uuid) {
        // Initial values and set format.
        Calendar saved = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000Z'", Locale.US);
        openRead();

        // Iterate through records with matching UUID in Drinks Table. Should only be 1 match.
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, TapOpenHelper.COLUMN_UUID + " = '" + uuid + "'", null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                // Set saved to value of date.
                String d = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE));
                saved = Calendar.getInstance();
                saved.setTime(sdf.parse(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Close DB and Cursor, return date.
        c.close();
        close();
        return saved;
    }

    /**
     *
     * Create Drink object for specified UUID.
     *
     * @param uuid: UUID of Drink to be created.
     * @return Drink matching UUID specified.
     */
    public Drink display(String uuid) {
        // Create default Drink constructor
        Drink d = new Drink();

        // Return results from Drink table that match specified UUID. Should only return 1 match.
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, TapOpenHelper.COLUMN_UUID + " = '" + uuid + "'", null, null, null, null, null);
        while (c.moveToNext()) {

            // Set fields of drink object.
            d.setUUID(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_UUID)));
            d.setCategory(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_CATEGORY)));
            d.setDrinkDate(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE)));
        }

        // Close Cursor and return drink.
        c.close();
        return d;
    }

    /**
     *
     * Return ArrayList of all Drinks in DB.
     *
     * @return ArrayList of Drink objects.
     */
    public ArrayList<Drink> drinksDisplay() {
        // Open DB and set initial Drink ArrayList
        openRead();
        ArrayList<Drink> drinks = new ArrayList<Drink>();

        // Iterate through all records in Drinks table
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {

            // Create a drink object for every record and add it to ArrayList
            Drink d = display(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_UUID)));
            drinks.add(d);
        }

        // Close DB and Cursor, return ArrayList
        c.close();
        close();
        return drinks;
    }

    /**
     *
     * Get currently logged in User
     *
     * @return User object
     */
    public User getUser() {
        // Open DB and create initial User object.
        openRead();
        User user = new User();

        // Iterate through all users where logged in is 1/true on device. Should only return 1.
        Cursor c = qdb.query(true, TapOpenHelper.USER_TABLE_NAME, userColumns, TapOpenHelper.COLUMN_LOGGED_IN + " = " + 1, null, null, null, null, null);
        while (c.moveToNext()) {

            // Set fields for user.
            user.setUsername(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_USERNAME)));
            user.setDeviceToken(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DEVICE_TOKEN)));
            user.setLoggedIn(c.getInt(c.getColumnIndex(TapOpenHelper.COLUMN_LOGGED_IN)));
        }

        // Close DB and Cursor and return User.
        c.close();
        close();
        return user;
    }

    /**
     *
     * SQliteDatabase query method that can be used outside of DataSource. Refer to documentation for SQLiteDatabase query for more information.
     *
     * @param distinct
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        openRead();
        return qdb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
