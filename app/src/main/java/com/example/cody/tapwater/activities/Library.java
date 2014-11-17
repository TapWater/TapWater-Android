/****************************************************************************************
 /*
 /* FILE NAME: Library.java
 /*
 /* DESCRIPTION: Lists all drinks in the current logged in user's local DB.
 /*
 /* REFERENCE: Starts when "Library" is selected in menu from MainActivity
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/25/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.adapters.DrinkAdapter;
import com.example.cody.tapwater.R;

import java.util.ArrayList;

public class Library extends Activity {

    private DataSource datasource;

    /**
     * Instantiates the activity, creates its views, and sets listeners for all of the UI components.
     *
     * @param savedInstanceState: Last saved state of the system.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Create activity based on last saved system state. Then instantiate layout.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);

        // Instantiate datasource object.
        datasource = new DataSource(getBaseContext());

        // Instantiate ListView from id specified in xml
        ListView drinksList = (ListView) findViewById(R.id.drinksList);

        // Populate ArrayList of Drinks with Drinks from the DB.
        ArrayList<Drink> drinksArray = datasource.drinksDisplay();

        // Provide adapter with context and ArrayList of Drinks
        DrinkAdapter adapter = new DrinkAdapter(getBaseContext(), drinksArray);

        // Set Adapter and allow fast scrolling.
        drinksList.setAdapter(adapter);
        drinksList.setFastScrollEnabled(true);
    }
}
