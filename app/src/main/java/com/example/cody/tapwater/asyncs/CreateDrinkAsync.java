/****************************************************************************************
 /*
 /* FILE NAME: CreateDrinksAsync.java
 /*
 /* DESCRIPTION: Asynchronous method to POST a Drink object on the server.
 /*
 /* REFERENCE: Called from MainActivity.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/27/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.asyncs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cody.tapwater.callbacks.CallBackListenerMain;
import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.objects.Helper;
import com.example.cody.tapwater.activities.MainActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateDrinkAsync extends AsyncTask<String, Void, Integer> {

    private Helper helper;
    private DataSource datasource;
    private Context context;
    private String url;
    private StringBuffer buff;
    private String responseMessage;
    private Drink drink = new Drink();
    ProgressDialog progressDialog;
    private CallBackListenerMain mListener;

    /**
     *
     * Constructor for Async to execute task.
     *
     * @param c: Context from parent activity.
     * @param l: Callback from MainActivity.
     */
    public CreateDrinkAsync(Context c, CallBackListenerMain l) {
        context = c;
        mListener = l;
    }

    /**
     *
     * Called before task is executed to prepare progressDialog and objects.
     *
     */
    @Override
    protected void onPreExecute() {
        datasource = new DataSource(context);
        helper = new Helper(context);
        helper.lockScreenRotation();
        progressDialog = ProgressDialog.show(context, "Creating Drink", "Please wait...", true);
    }

    /**
     *
     * Task to be executed asynchronously. POSTs Drink to server and inserts into DB.
     *
     * @param json: JSON string representing Drink.
     * @return integer code for success or failure.
     */
    @Override
    protected Integer doInBackground(String... json) {

        // Default value for code.
        int code = 0;

        // object to parse JSON into object.
        Gson gson = new Gson();

        // If no internet connection, return code = 0, else, continue with task.
        if (helper.haveNetworkConnection() == false) {
            return code;
        } else {
            try {

                // Establish URL and set headers for Connection
                url = helper.CREATE_DRINK_URL;
                HttpURLConnection c = (HttpURLConnection) new URL(url)
                        .openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-type", "application/json");
                c.setDoOutput(true);

                // Write JSON to server.
                OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream());
                out.write(json[0]);
                out.close();

                // Get response from server to see if POST was successful.
                int responseCode = c.getResponseCode();
                responseMessage = c.getResponseMessage();
                if (responseCode == 200 || responseCode == 201) {

                    // Parse response from server to reader, append to StringBuffer
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    // Get same Drink object that was POSTed to server, insert that into device DB.
                    drink = gson.fromJson(buff.toString(), Drink.class);
                    datasource.insertDrink(drink);

                    // Indicate success.
                    code = 1;
                } else {

                    // Indicate failure.
                    code = 2;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }
    }

    /**
     *
     * Executed after doInBackground. Dismiss dialog and send code to callback.
     *
     * @param in: Code obtained from doInBackground.
     */
    @Override
    protected void onPostExecute(Integer in) {
        super.onPostExecute(in);
        mListener.callbackCreateDrink(in, drink, responseMessage);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }

    /**
     *
     * Actions to take if task is cancelled. Send failure code to callback and dismiss dialog.
     *
     */
    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        responseMessage = "Cancelled drink creation";
        mListener.callbackCreateDrink(2, drink, responseMessage);
        helper.enableScreenRotation();
    }
}