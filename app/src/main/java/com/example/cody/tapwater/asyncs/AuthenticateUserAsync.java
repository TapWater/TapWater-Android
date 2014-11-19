/****************************************************************************************
 /*
 /* FILE NAME: AuthenticateUserAsync.java
 /*
 /* DESCRIPTION: Asynchronous method to POST User credentials to the server in order to log in.
 /*
 /* REFERENCE: Called from MainActivity Log In prompt.
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
import com.example.cody.tapwater.objects.Helper;
import com.example.cody.tapwater.objects.User;
import com.example.cody.tapwater.activities.MainActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthenticateUserAsync extends AsyncTask<String, Void, Integer> {

    private Helper helper;
    private DataSource datasource;
    private Context context;
    private String url;
    private StringBuffer buff;
    private String responseMessage;
    ProgressDialog progressDialog;

    private CallBackListenerMain mListener;

    /**
     *
     * Constructor for Async to execute task.
     *
     * @param c: Context from parent activity.
     * @param l: Callback from MainActivity.
     */
    public AuthenticateUserAsync(Context c, CallBackListenerMain l) {
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
        progressDialog = ProgressDialog.show(context, "Logging In", "Please wait...", true);
    }

    /**
     *
     * Task to be executed asynchronously. POSTs User credentials to server and inserts logged in User into DB.
     *
     * @param json: JSON string representing User.
     * @return integer code for success or failure.
     */
    @Override
    protected Integer doInBackground(String... json) {

        // Default value for code.
        int code = 0;

        // Object to parse json.
        Gson gson = new Gson();

        // If not connected to internet, return code = 0; else, continue task.
        if (helper.haveNetworkConnection() == false) {
            return code;
        } else {
            try {

                // Set up URL and connection headers.
                url = helper.AUTHENTICATE_USER_URL;
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

                // Get response from server and see if POST was successful.
                int responseCode = c.getResponseCode();
                responseMessage = c.getResponseMessage();
                if (responseCode == 200 || responseCode == 201) {

                    // Parse response from server into reader, append to buffer.
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    // Create user object, mark them as logged in, insert into DB.
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
     * Execute after doInBackground. Dismiss dialog and send code to callback.
     *
     * @param in: Code from doInBackground to send to MainActivity.
     */
    @Override
    protected void onPostExecute(Integer in) {
        super.onPostExecute(in);
        mListener.callbackAuthenticateUser(in, responseMessage);
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
        super.onCancelled();
        responseMessage = "Cancelled Log In";
        mListener.callbackAuthenticateUser(2, responseMessage);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }
}