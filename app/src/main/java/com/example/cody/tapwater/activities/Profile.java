package com.example.cody.tapwater.activities;

/****************************************************************************************
 /*
 /* FILE NAME: Profile.java
 /*
 /* DESCRIPTION: Options for a particular device
 /*
 /* REFERENCE: Starts when "Profile" is selected in menu from MainActivity
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 11/23/14
 /*
 /****************************************************************************************/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cody.tapwater.R;
import com.example.cody.tapwater.adapters.DrinkAdapter;
import com.example.cody.tapwater.asyncs.LoadDrinksAsync;
import com.example.cody.tapwater.callbacks.CallBackListenerLibrary;
import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.fragments.SettingsFragment;
import com.example.cody.tapwater.objects.Drink;

import java.util.ArrayList;

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

public class Profile extends Activity {

    private Context context = this;
    private SwipeRefreshLayout swipeLayout;
    private DataSource datasource;


    /**
     * Instantiates the activity, creates its views, and sets listeners for all of the UI components.
     *
     * @param savedInstanceState: Last saved state of the system.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
