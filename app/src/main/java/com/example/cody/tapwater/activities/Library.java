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
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cody.tapwater.asyncs.LoadDrinksAsync;
import com.example.cody.tapwater.callbacks.CallBackListenerLibrary;
import com.example.cody.tapwater.callbacks.CallBackListenerMain;
import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.adapters.DrinkAdapter;
import com.example.cody.tapwater.R;

import java.util.ArrayList;

public class Library extends Activity {

    private Context context = this;
    private SwipeRefreshLayout swipeLayout;
    private DataSource datasource;

    /**
     * Callback class that responds to actions done to the server
     */
    public class CallBack implements CallBackListenerLibrary {

        /**
         * Called upon completion of LoadDrinkAsync. Toasts to user that drinks were loaded successfully. Updates total cups in DB accordingly.
         *
         * @param response:        integer response that indicates success or failure.
         * @param responseMessage: Server's response.
         */
        @Override
        public void callbackLoadDrinks(Integer response, String responseMessage) {
            if (response == 1) {
                String message = "Drinks loaded";
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

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

        // Instantiate swipe layout for refresh actions and establish behavior.
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadDrinksAsync async = new LoadDrinksAsync(context, new CallBack());
                async.execute();
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
