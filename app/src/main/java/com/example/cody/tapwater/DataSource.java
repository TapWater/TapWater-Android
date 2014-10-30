package com.example.cody.tapwater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    //Inserts ticket into database.
    public void insertDrink(Drink drink) {
        openWrite();
        ContentValues values = new ContentValues();
        values.put(TapOpenHelper.COLUMN_UUID, drink.getUUID());
        values.put(TapOpenHelper.COLUMN_CATEGORY, drink.getCategory());
        values.put(TapOpenHelper.COLUMN_DRINK_DATE, drink.getDrinkDate());
        qdb.insert(TapOpenHelper.DRINK_TABLE_NAME, null, values);
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
        c.close();
        close();
        return saved;
    }
}
