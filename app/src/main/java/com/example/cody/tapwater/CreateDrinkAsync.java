package com.example.cody.tapwater;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    private Drink drink = new Drink();
    ProgressDialog progressDialog;

    private CallBackListenerMain mListener;

    public CreateDrinkAsync(Context c, CallBackListenerMain l) {
        context = c;
        mListener = l;
    }

    @Override
    protected void onPreExecute() {
        datasource = new DataSource(context);
        helper = new Helper(context);
        helper.lockScreenRotation();
        progressDialog = ProgressDialog.show(context, "Creating Drink", "Please wait...", true);
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
                url = helper.CREATE_DRINK_URL;
                HttpURLConnection c = (HttpURLConnection) new URL(url)
                        .openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "application/json");
                c.setRequestProperty("Content-type", "application/json");
                c.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream());
                out.write(json[0]);
                out.close();

                int responseCode = c.getResponseCode();
                Log.i(MainActivity.TAG, json[0] + " " + responseCode + " " + c.getResponseMessage());
                if (responseCode == 200 || responseCode == 201) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    String inputLine;
                    buff = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        buff.append(inputLine);
                    }
                    in.close();

                    drink = gson.fromJson(buff.toString(), Drink.class);
                    datasource.insertDrink(drink);

                    code = 1;
                } else {
                    code = 2;
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
        mListener.callbackCreateDrink(in, drink);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        mListener.callbackCreateDrink(2, drink);
        helper.enableScreenRotation();
    }
}