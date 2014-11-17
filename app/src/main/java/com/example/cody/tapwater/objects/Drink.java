/****************************************************************************************
 /*
 /* FILE NAME: Drink.java
 /*
 /* DESCRIPTION: Drink object used for DB and server interaction. Foundational to application.
 /*
 /* REFERENCE: Used throughout application for DB and server interaction.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/24/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class Drink {

    private String uuid;
    private String category;
    private String drink_date;

    private int ounces;

    /**
     * Empty constructor.
     */
    public Drink() {
    }

    /**
     *
     * Creates drink based on category passed.
     *
     * @param cat: Category of Drink to create. Can be "drink", "glass", or "bottle".
     */
    public Drink(String cat) {
        category = cat;

        // Generate random, unique UUID
        uuid = UUID.randomUUID().toString();

        // Set drink date to formatted current time.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz", Locale.US);
        Calendar cal = Calendar.getInstance();
        drink_date = sdf.format(cal.getTime()).toString();

        // Set ounces based on category passed.
        if (category.equals("drink")) {
            ounces = 4;
        } else if (category.equals("glass")) {
            ounces = 8;
        } else if (category.equals("bottle")) {
            ounces = 16;
        }
    }

    /**
     * Get UUID.
     *
     * @return UUID.
     */
    public String getUUID() {
        return uuid;
    }

    /**
     *
     * Set UUID.
     *
     * @param u: passed UUID.
     */
    public void setUUID(String u) {
        uuid = u;
    }

    /**
     *
     * Get Category.
     *
     * @return Category.
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * Set Category.
     *
     * @param cat: Category passed.
     */
    public void setCategory(String cat) {
        category = cat;
    }

    /**
     *
     * Get Drink date.
     *
     * @return Drink date.
     */
    public String getDrinkDate() {
        return drink_date;
    }

    /**
     *
     * Set Drink date.
     *
     * @param dr: Drink date passed.
     */
    public void setDrinkDate(String dr) {
        drink_date = dr;
    }

    /**
     *
     * String representation of Drink.
     *
     * @return String detailing fields of Drink.
     */
    @Override
    public String toString() {
        return uuid + " " + category + " " + drink_date;
    }
}
