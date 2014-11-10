package com.example.cody.tapwater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    private Context context = this;
    public static final String TAG = "MainActivity";
    private DataSource datasource;
    private SwipeRefreshLayout swipeLayout;
    private User user;
    private TextView cups;
    private boolean alertReady;
    private Helper helper;
    private AlertDialog alert;
    private Gson gson = new Gson();

    // Callback that controls actions after an async task completes
    public class CallBack implements CallBackListenerMain {

        // Activated after Login completes
        @Override
        public void callbackCreateUser(Integer response) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                alert.dismiss();
                String message = "User created";
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void callbackCreateDrink(Integer response, Drink d) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                String message = d.getCategory() + " created";
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void callbackAuthenticateUser(Integer response) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                alert.dismiss();
                String message = "User logged in";
                LoadDrinksAsync async = new LoadDrinksAsync(context, new CallBack());
                async.execute();
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void callbackLoadDrinks(Integer response) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                String message = "Drinks loaded";
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new DataSource(getBaseContext());
        helper = new Helper(context);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                    }
                }, 5000);
            }
        });
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        View drink = findViewById(R.id.drinkButton);
        View glass = findViewById(R.id.glassButton);
        View bottle = findViewById(R.id.bottleButton);

        cups = (TextView) findViewById(R.id.cupsText);
        cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");

        final TextView timer = (TextView) findViewById(R.id.timeSinceLastDrink);

        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("drink");
            }
        });

        glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("glass");
            }
        });

        bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("bottle");
            }
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText(timerTextCalc());
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        login();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.history) {
            Log.v(TAG, "history");
            Intent history = new Intent(getBaseContext(), Library.class);
            startActivity(history);
            return true;
        } else if (id == R.id.logout) {
            Log.v(TAG, "logout");
            logOutAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String timerTextCalc() {
        Calendar now = Calendar.getInstance();
        Calendar latest = datasource.getLatestDate();

        long difference = now.getTimeInMillis() - latest.getTimeInMillis();
        String hoursString, minutesString, secondsString;

        int hours = (int) difference / (1000 * 60 * 60);
        hoursString = (hours < 10) ? 0 + "" + hours : "" + hours;

        int minutes = (int) difference / (60 * 1000) % 60;
        minutesString = (minutes < 10) ? 0 + "" + minutes : "" + minutes;

        int seconds = (int) difference / 1000 % 60;
        secondsString = (seconds < 10) ? 0 + "" + seconds : "" + seconds;

        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    // Login dialog
    public void login() {

        // Check if a user exists in the database. If not, user is prompted to log in
        Cursor c = null;
        try {
            c = datasource.query(true, TapOpenHelper.USER_TABLE_NAME,
                    null, null, null, null, null, null, null);
        } catch (Exception e) {

            // If this call fails, this is first use. Create the database
            TapOpenHelper th = new TapOpenHelper(MainActivity.this);
            SQLiteDatabase db = th.getReadableDatabase();
            th.onCreate(db);
        }

        // No users exist in database
        if (c != null && c.getCount() == 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Login");

            // Get login dialog layout
            LayoutInflater inflater = this.getLayoutInflater();
            View v = inflater.inflate(R.layout.login, null);

            // Instantiate EditTexts for accessCode and Dealer Supplemental
            final EditText username = (EditText) v.findViewById(R.id.username);
            final EditText password = (EditText) v.findViewById(R.id.password);

            b.setView(v);
            b.setPositiveButton("Login",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            b.setNegativeButton("Register",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            alert = b.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button login = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button register = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Make sure EditTexts are not empty
                            if (username.getText().toString().equals("")) {
                                Toast.makeText(getBaseContext(), "Please enter email address",
                                        Toast.LENGTH_SHORT).show();
                            } else if (password.getText().toString().equals("")) {
                                Toast.makeText(getBaseContext(),
                                        "Please enter license number",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Create Activation object and set fields needed for async
                                Gson gson = new Gson();
                                User u = new User();
                                u.setUsername(username.getText().toString());
                                u.setPassword(password.getText().toString());

                                // Create json string of activation object
                                String json = gson.toJson(u, User.class);

                                // Begin async to authenticate the user's credentials
                                AuthenticateUserAsync async = new AuthenticateUserAsync(context, new CallBack());

                                // If internet connection exists, DO IT, else, tell user they need to connect
                                if (helper.haveNetworkConnection()) {
                                    async.execute(json);
                                } else {
                                    Toast.makeText(getBaseContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            register();
                        }
                    });
                }

            });

            // Show the prompt, if the user cancels, close the application
            alert.show();
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });

        } else {

            // If user is already logged in, get their information and set the textviews on the main activity
            user = datasource.getUser();
        }
    }

    public void register() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Register");

        // Get login dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.register, null);

        // Instantiate EditTexts for accessCode and Dealer Supplemental
        final EditText u = (EditText) v.findViewById(R.id.username);
        final EditText p = (EditText) v.findViewById(R.id.password);
        final EditText cp = (EditText) v.findViewById(R.id.confirm_password);

        b.setView(v);
        b.setPositiveButton("Register",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert = b.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button register = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Make sure EditTexts are not empty
                        if (u.getText().toString().equals("")) {
                            Toast.makeText(getBaseContext(), "Please enter email address",
                                    Toast.LENGTH_SHORT).show();
                        } else if (p.getText().toString().equals("") || cp.getText().toString().equals("")) {
                            Toast.makeText(getBaseContext(),
                                    "Please enter license number",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Create Activation object and set fields needed for async
                            Gson gson = new Gson();
                            User us = new User();
                            us.setUsername(u.getText().toString());
                            us.setPassword(p.getText().toString());
                            us.setPasswordConfirmation(cp.getText().toString());

                            ServerUser s = new ServerUser(us);

                            // Create json string of activation object
                            String json = gson.toJson(s, ServerUser.class);

                            // Begin async to authenticate the user's credentials
                            CreateUserAsync async = new CreateUserAsync(context, new CallBack());

                            // If internet connection exists, DO IT, else, tell user they need to connect
                            if (helper.haveNetworkConnection()) {
                                async.execute(json);
                            } else {
                                Toast.makeText(getBaseContext(), "No Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }

        });

        // Show the prompt, if the user cancels, close the application
        alert.show();
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                login();
            }
        });
    }

    public void logOutAlert() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle(Html.fromHtml("Are you sure you want to log out?\n" + "<b>" + "Unsynced data will be lost." + "</b>"));
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cups.setText("0.0 cups");
                TapOpenHelper th = new TapOpenHelper(MainActivity.this);
                SQLiteDatabase db = th.getWritableDatabase();
                th.logOut(db);
                login();
            }
        });
        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        b.show();
    }

    public void drinkPost(String cat) {
        Drink d = new Drink(cat);
        ServerDrink s = new ServerDrink(datasource.getUser().getDeviceToken(), d);
        String json = gson.toJson(s, ServerDrink.class);
        CreateDrinkAsync async = new CreateDrinkAsync(context, new CallBack());
        async.execute(json);
    }
}
