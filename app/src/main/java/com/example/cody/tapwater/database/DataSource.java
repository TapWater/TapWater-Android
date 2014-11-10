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

    //All methods used to write to or pull from database are defined here

    SQLiteOpenHelper db;
    SQLiteDatabase qdb;

    public static final String[] drinkColumns = {
            TapOpenHelper.COLUMN_ID,
            TapOpenHelper.COLUMN_UUID,
            TapOpenHelper.COLUMN_CATEGORY,
            TapOpenHelper.COLUMN_DRINK_DATE,
    };

    public static final String[] userColumns = {
            TapOpenHelper.COLUMN_ID,
            TapOpenHelper.COLUMN_USERNAME,
            TapOpenHelper.COLUMN_DEVICE_TOKEN,
            TapOpenHelper.COLUMN_LOGGED_IN
    };

    //Datasource object needed to use methods in other classes
    public DataSource(Context context) {
        db = new TapOpenHelper(context);
    }

    //Needed for cursor/database operation that alters DB.
    public void openWrite() {
        System.out.println("DB OPENED WRITABLE");
        qdb = db.getWritableDatabase();
    }

    //Needed for cursor/database operation that reads from DB.
    public void openRead() {
        System.out.println("DB OPENED READABLE");
        qdb = db.getReadableDatabase();
    }

    //closes database after read/write
    public void close() {
        System.out.println("DB CLOSED");
        db.close();
    }

    public void insertDrink(Drink drink) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(TapOpenHelper.COLUMN_UUID, drink.getUUID());
        values.put(TapOpenHelper.COLUMN_CATEGORY, drink.getCategory());
        values.put(TapOpenHelper.COLUMN_DRINK_DATE, drink.getDrinkDate());
        qdb.insert(TapOpenHelper.DRINK_TABLE_NAME, null, values);
        close();
    }

    public void insertUser(User user) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(TapOpenHelper.COLUMN_USERNAME, user.getUsername());
        values.put(TapOpenHelper.COLUMN_DEVICE_TOKEN, user.getDeviceToken());
        values.put(TapOpenHelper.COLUMN_LOGGED_IN, user.getLoggedIn());
        qdb.insert(TapOpenHelper.USER_TABLE_NAME, null, values);
        close();
    }

    // TODO: Make it return only the cups for a specified day instead of all cups in the DB
    public double getTotalCups() {
        int ounces = 0;
        double cups;
        openRead();
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {
            String cat = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_CATEGORY));
            if (cat.equals("drink")) {
                ounces += 4;
            } else if (cat.equals("glass")) {
                ounces += 8;
            } else if (cat.equals("bottle")) {
                ounces += 16;
            }
        }
        c.close();
        close();
        cups = ounces / 8.0;
        return cups;
    }

    public Calendar getLatestDate() {
        Calendar saved = null;
        Calendar check = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        openRead();
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {
            try {
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
        if (saved == null) {
            saved = Calendar.getInstance();
        }
        c.close();
        close();
        return saved;
    }

    public Calendar getDateByUuid(String uuid) {
        Calendar saved = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        openRead();
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, TapOpenHelper.COLUMN_UUID + " = '" + uuid + "'", null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                String d = c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE));
                saved = Calendar.getInstance();
                saved.setTime(sdf.parse(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        close();
        return saved;
    }

    public Drink display(String uuid) {
        Drink d = new Drink();
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, TapOpenHelper.COLUMN_UUID + " = '" + uuid + "'", null, null, null, null, null);
        while (c.moveToNext()) {
            d.setUUID(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_UUID)));
            d.setCategory(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_CATEGORY)));
            d.setDrinkDate(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DRINK_DATE)));
        }
        c.close();
        return d;
    }

    public ArrayList<Drink> drinksDisplay() {
        openRead();
        ArrayList<Drink> drinks = new ArrayList<Drink>();
        Cursor c = qdb.query(true, TapOpenHelper.DRINK_TABLE_NAME, drinkColumns, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Drink d = display(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_UUID)));
            System.out.println(d.toString());
            drinks.add(d);
        }
        c.close();
        close();

        return drinks;
    }

    public User getUser() {
        openRead();
        User user = new User();
        Cursor c = qdb.query(true, TapOpenHelper.USER_TABLE_NAME, userColumns, TapOpenHelper.COLUMN_LOGGED_IN + " = " + 1, null, null, null, null, null);
        while (c.moveToNext()) {
            user.setUsername(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_USERNAME)));
            user.setDeviceToken(c.getString(c.getColumnIndex(TapOpenHelper.COLUMN_DEVICE_TOKEN)));
            user.setLoggedIn(c.getInt(c.getColumnIndex(TapOpenHelper.COLUMN_LOGGED_IN)));
        }
        c.close();
        close();
        return user;
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        openRead();
        return qdb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
