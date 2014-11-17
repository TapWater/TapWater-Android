/****************************************************************************************
 /*
 /* FILE NAME: CreateUserAsync.java
 /*
 /* DESCRIPTION: Asynchronous method to POST User to the server in order to register.
 /*
 /* REFERENCE: Called from MainActivity Register prompt.
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
import com.example.cody.tapwater.objects.Helper;
import com.example.cody.tapwater.objects.User;
import com.example.cody.tapwater.activities.MainActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateUserAsync extends AsyncTask<String, Void, Integer> {

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
    public CreateUserAsync(Context c, CallBackListenerMain l) {
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
        progressDialog = ProgressDialog.show(context, "Logging In", "Please wait...", true);
    }

    /**
     *
     * Execute asynchronous task. POST User to server and indicate success or failure.
     *
     * @param json: JSON to send to server.
     * @return Integer code indicating success or failure.
     */
    @Override
    protected Integer doInBackground(String... json) {

        // Default value for code.
        int code = 0;

        // Object to parse json.
        Gson gson = new Gson();

        // If no internet connection, code = 0; else, continue task.
        if (helper.haveNetworkConnection() == false) {
            return code;
        } else {
            try {

                // Set up URL and connection headers.
                url = helper.CREATE_USER_URL;
                HttpURLConnection c = (HttpURLConnection) new URL(url)
                        .openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-type", "application/json");
                c.setDoOutput(true);

                // Write json to server.
                OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream());
                out.write(json[0]);
                out.close();

                // Get response from server, if successful, continue.
                int responseCode = c.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {

                    // Parse response into reader, append to buffer.
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    // Parse json into User object, set as logged in, insert into DB.
                    User user = gson.fromJson(buff.toString(), User.class);
                    user.setLoggedIn(1);
                    datasource.insertUser(user);

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
     * Execute after doInBackground. Dismiss dialog and activate callback.
     *
     * @param in: Code from doInBackground to send to callback.
     */
    @Override
    protected void onPostExecute(Integer in) {
        super.onPostExecute(in);
        mListener.callbackCreateUser(in);
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
        mListener.callbackCreateUser(2);
        helper.enableScreenRotation();
    }
}