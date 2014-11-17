/****************************************************************************************
 /*
 /* FILE NAME: LoadDrinksAsync.java
 /*
 /* DESCRIPTION: Asynchronous method to GET List of Drink objects from the server.
 /*
 /* REFERENCE: Called from MainActivity After log in or user-activated sync.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/28/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.asyncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cody.tapwater.callbacks.CallBackListenerMain;
import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drinks;
import com.example.cody.tapwater.objects.Helper;
import com.example.cody.tapwater.activities.MainActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadDrinksAsync extends AsyncTask<Void, Void, Integer> {

    private Helper helper;
    private DataSource datasource;
    private Context context;
    private String url;
    private StringBuffer buff;
    ProgressDialog progressDialog;

    private CallBackListenerMain mListener;

    /**
     *
     * Constructor for Async to execute task.
     *
     * @param c: Context from parent Activity.
     * @param l: Callback from MainActivity.
     */
    public LoadDrinksAsync(Context c, CallBackListenerMain l) {
        context = c;
        mListener = l;
    }

    /**
     *
     * Execute before task begins. Prepare up dialog and objects.
     *
     */
    @Override
    protected void onPreExecute() {
        datasource = new DataSource(context);
        helper = new Helper(context);
        helper.lockScreenRotation();
        progressDialog = ProgressDialog.show(context, "Retrieving Tickets", "Please wait...", true);
    }

    /**
     *
     * Execute asynchronous task. GET Drinks from server and indicate success or failure. Void param is not passed to task.
     *
     * @return Integer code indicating success or failure.
     */
    @Override
    protected Integer doInBackground(Void... json) {

        // Default value of code.
        int code = 0;

        // Object to parse json.
        Gson gson = new Gson();

        // If no internet connection, code = 0; else, continue.
        if (helper.haveNetworkConnection() == false) {
            return code;
        } else {
            try {

                // Set up URL and connection headers.
                url = helper.GET_DRINKS_URL + datasource.getUser().getDeviceToken();
                HttpURLConnection c = (HttpURLConnection) new URL(url)
                        .openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-Type", "application/json");

                // Establish connection and get response. Continue if successful.
                int responseCode = c.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {

                    // Parse response to reader, append to buffer.
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    // create ArrayList to store List of Drinks from server. Insert each drink into DB.
                    Drinks drinks = gson.fromJson(buff.toString(), Drinks.class);
                    for (int i = 0; i < drinks.drinks.size(); i++) {
                        datasource.insertDrink(drinks.drinks.get(i));
                    }

                    // Indicate success.
                    code = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }
    }

    /**
     *
     * Execute after doInBackground. Dismiss dialog and activate callback.
     *
     * @param in: Code from doInBackground to send to callback.
     */
    @Override
    protected void onPostExecute(Integer in) {
        super.onPostExecute(in);
        mListener.callbackLoadDrinks(in);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }

    /**
     *
     * Execute if task is cancelled. Dismiss dialog and indicate failure to callback.
     *
     */
    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        mListener.callbackLoadDrinks(2);
        helper.enableScreenRotation();
    }
}