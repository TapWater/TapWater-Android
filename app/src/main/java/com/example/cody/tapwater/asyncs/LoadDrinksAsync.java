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

public class LoadDrinksAsync extends AsyncTask<String, Void, Integer> {

    private Helper helper;
    private DataSource datasource;
    private Context context;
    private String url;
    private StringBuffer buff;
    ProgressDialog progressDialog;

    private CallBackListenerMain mListener;

    public LoadDrinksAsync(Context c, CallBackListenerMain l) {
        context = c;
        mListener = l;
    }

    @Override
    protected void onPreExecute() {
        datasource = new DataSource(context);
        helper = new Helper(context);
        helper.lockScreenRotation();
        progressDialog = ProgressDialog.show(context, "Retrieving Tickets", "Please wait...", true);
    }

    ;

    @Override
    protected Integer doInBackground(String... json) {
        int code = 0;
        Gson gson = new Gson();
        if (helper.haveNetworkConnection() == false) {
            return code;
        } else {
            try {
                url = helper.GET_DRINKS_URL + datasource.getUser().getDeviceToken();
                Log.i(MainActivity.TAG, datasource.getUser().getDeviceToken());

                HttpURLConnection c = (HttpURLConnection) new URL(url)
                        .openConnection();
                c.setRequestMethod("GET");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-Type", "application/json");

                int responseCode = c.getResponseCode();
                Log.i(MainActivity.TAG, responseCode + " " + c.getResponseMessage());
                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    Log.i(MainActivity.TAG, "hello");
                    Drinks drinks = gson.fromJson(buff.toString(), Drinks.class);
                    for (int i = 0; i < drinks.drinks.size(); i++) {
                        datasource.insertDrink(drinks.drinks.get(i));
                    }

                    code = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return code;
        }
    }

    @Override
    protected void onPostExecute(Integer in) {
        super.onPostExecute(in);
        mListener.callbackLoadDrinks(in);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        mListener.callbackLoadDrinks(2);
        helper.enableScreenRotation();
    }
}