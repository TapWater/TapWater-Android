package com.example.cody.tapwater;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Cody on 10/29/2014.
 */
public class Drink {

    private String uuid;
    private String category;
    private String drink_date;

    private int ounces;

    public Drink() {
    }

    public Drink(String cat) {
        category = cat;

        uuid = UUID.randomUUID().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        Calendar cal = Calendar.getInstance();
        drink_date = sdf.format(cal.getTime()).toString();

        if (category.equals("drink")) {
            ounces = 4;
        } else if (category.equals("glass")) {
            ounces = 8;
        } else if (category.equals("bottle")) {
            ounces = 16;
        }
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String u) {
        uuid = u;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String cat) {
        category = cat;
    }

    public String getDrinkDate() {
        return drink_date;
    }

    public void setDrinkDate(String dr) {
        drink_date = dr;
    }

    public int getOunces() {
        return ounces;
    }

    public void setOunces(int o) {
        ounces = o;
    }

    public double getCups() {
        return ounces / 8.0;
    }

    public void addDrink() {
        ounces = ounces + 4;
    }

    public void addGlass() {
        ounces = ounces + 8;
    }

    public void addBottle() {
        ounces = ounces + 16;
    }

    @Override
    public String toString() {
        return uuid + " " + category + " " + drink_date;
    }
}
