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

    public CreateUserAsync(Context c, CallBackListenerMain l) {
        context = c;
        mListener = l;
    }

    @Override
    protected void onPreExecute() {
        datasource = new DataSource(context);
        helper = new Helper(context);
        helper.lockScreenRotation();
        progressDialog = ProgressDialog.show(context, "Logging In", "Please wait...", true);
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
                url = helper.CREATE_USER_URL;
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

                    User user = gson.fromJson(buff.toString(), User.class);
                    user.setLoggedIn(1);
                    datasource.insertUser(user);

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
        mListener.callbackCreateUser(in);
        progressDialog.dismiss();
        helper.enableScreenRotation();
    }

    @Override
    protected void onCancelled() {
        progressDialog.dismiss();
        mListener.callbackCreateUser(2);
        helper.enableScreenRotation();
    }
}