/****************************************************************************************
 /*
 /* FILE NAME: MainActivity.java
 /*
 /* DESCRIPTION: The main menu for the application where drinks are added and the library can be accessed.
 /*
 /* REFERENCE: Starts when application begins
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/24/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.activities;

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

import com.example.cody.tapwater.asyncs.AuthenticateUserAsync;
import com.example.cody.tapwater.callbacks.CallBackListenerMain;
import com.example.cody.tapwater.asyncs.CreateDrinkAsync;
import com.example.cody.tapwater.asyncs.CreateUserAsync;
import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.objects.Helper;
import com.example.cody.tapwater.asyncs.LoadDrinksAsync;
import com.example.cody.tapwater.R;
import com.example.cody.tapwater.objects.ServerDrink;
import com.example.cody.tapwater.objects.ServerUser;
import com.example.cody.tapwater.database.TapOpenHelper;
import com.example.cody.tapwater.objects.User;
import com.google.gson.Gson;

import java.util.Calendar;


public class MainActivity extends Activity {

    private Context context = this;
    public static final String TAG = "MainActivity";
    private DataSource datasource;
    private SwipeRefreshLayout swipeLayout;
    private User user;
    private TextView cups;
    private Helper helper;
    private AlertDialog alert;
    private Gson gson = new Gson();

    /**
     * Callback class that responds to actions done to the server
     */
    public class CallBack implements CallBackListenerMain {

        /**
         * Called upon completion of CreateUserAsync. Toasts to user that a user was successfully created.
         *
         * @param response:        integer response that indicates success or failure.
         * @param responseMessage: Server's response.
         */
        @Override
        public void callbackCreateUser(Integer response, String responseMessage) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                alert.dismiss();
                String message = "User created";
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Called upon completion of CreateDrinkAsync. Updates total number of cups in DB and Toasts to user that the drink was successfully created.
         *
         * @param response:        integer response that indicates success or failure.
         * @param d:               drink object returned to determine category of created drink.
         * @param responseMessage: Server's response.
         */
        @Override
        public void callbackCreateDrink(Integer response, Drink d, String responseMessage) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                String message = d.getCategory() + " created";
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Called upon completion of AuthenticateUserAsync. Calls LoadDrinksAsync on success and Toasts to user that log in was successful.
         *
         * @param response:        integer response that indicates success or failure.
         * @param responseMessage: Server's response.
         */
        @Override
        public void callbackAuthenticateUser(Integer response, String responseMessage) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                alert.dismiss();
                String message = "User logged in";
                LoadDrinksAsync async = new LoadDrinksAsync(context, new CallBack());
                async.execute();
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), responseMessage, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Called upon completion of LoadDrinkAsync. Toasts to user that drinks were loaded successfully. Updates total cups in DB accordingly.
         *
         * @param response:        integer response that indicates success or failure.
         * @param responseMessage: Server's response.
         */
        @Override
        public void callbackLoadDrinks(Integer response, String responseMessage) {
            Log.i(TAG, "callback called " + response);
            if (response == 1) {
                String message = "Drinks loaded";
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
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
        setContentView(R.layout.activity_main);

        // Instantiate datasource and helper objects
        datasource = new DataSource(context);
        helper = new Helper(context);

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

        // Instantiate buttons to create drink, glass, and bottle of water.
        View drink = findViewById(R.id.drinkButton);
        View glass = findViewById(R.id.glassButton);
        View bottle = findViewById(R.id.bottleButton);

        // Instantiate total cups field and set it to the current total cups in the DB.
        cups = (TextView) findViewById(R.id.cupsText);
        cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");

        // Instantiate timer indicating time since last drink was created.
        final TextView timer = (TextView) findViewById(R.id.timeSinceLastDrink);

        // Listener for drink button. Posts a "drink" category Drink to server.
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("drink");
            }
        });

        // Listener for glass button. Posts a "glass" category Drink to server.
        glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("glass");
            }
        });

        // Listener for bottle button. Posts a "bottle" category Drink to server.
        bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drinkPost("bottle");
            }
        });

        // Thread runs every second to update the text of the timer.
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
                    e.printStackTrace();
                }
            }
        };
        t.start();

        // Call login method, which displays login prompt if user is not logged in.
        login();
    }


    /**
     * Inflates the options menu from the layout in the res/menu folder.
     *
     * @param menu: Menu layout contained in the res/menu folder.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Determines what action to take when a menu item is selected.
     *
     * @param item: Menu item as defined in menu's xml file.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Starts activity to view drinks in the DB.
        if (id == R.id.library) {
            Intent library = new Intent(getBaseContext(), Library.class);
            startActivity(library);
            return true;

            // Displays log out alert asking if user really wants to log out.
        } else if (id == R.id.logout) {
            logOutAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Calculates the time since a drink was created based on the current date and the last recorded drink date in the database.
     *
     * @return formatted string detailed time since last drink.
     */
    public String timerTextCalc() {

        // Get calendars for current time and for last time recorded in Drinks table.
        Calendar now = Calendar.getInstance();
        Calendar latest = datasource.getLatestDate();

        // Get time difference in milliseconds. Instantiate hour, minute, and second strings.
        long difference = now.getTimeInMillis() - latest.getTimeInMillis();
        String hoursString, minutesString, secondsString;

        // Calculation for number of hours.
        int hours = (int) difference / (1000 * 60 * 60);
        hoursString = (hours < 10) ? 0 + "" + hours : "" + hours;

        // Calculation for number of minutes.
        int minutes = (int) difference / (60 * 1000) % 60;
        minutesString = (minutes < 10) ? 0 + "" + minutes : "" + minutes;

        // Calculation for number of seconds.
        int seconds = (int) difference / 1000 % 60;
        secondsString = (seconds < 10) ? 0 + "" + seconds : "" + seconds;

        // return formatted string.
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    /**
     * If there is no logged in user, displays the log in prompt.
     */
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

            // Instantiate EditTexts for username and password.
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
                                // Create User object and set fields needed for async
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

                    // If user clicks register, bring up the registration prompt.
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

    /**
     * Creates the dialog for a user to register with the application.
     */
    public void register() {

        // Instantiate dialog.
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Register");

        // Get register dialog layout
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.register, null);

        // Instantiate EditTexts for username, password, and password confirmation.
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
                            // Create User object and set fields needed for async
                            Gson gson = new Gson();
                            User us = new User();
                            us.setUsername(u.getText().toString());
                            us.setPassword(p.getText().toString());
                            us.setPasswordConfirmation(cp.getText().toString());

                            // Create object fit to satisfy json transfer to server.
                            ServerUser s = new ServerUser(us);

                            // Create json string of ServerUser object
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

    /**
     * Shows an alert asking the user if they wish to log out. If no, cancels. If yes, logs the user out.
     */
    public void logOutAlert() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setTitle(Html.fromHtml("Are you sure you want to log out?\n" + "<b>" + "Unsynced data will be lost." + "</b>"));
        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Reset total cups text and log out, then call login prompt.
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

    /**
     * Posts drink object to the server.
     *
     * @param cat: String detailing the category of the drink to be sent to the server
     */
    public void drinkPost(String cat) {
        Drink d = new Drink(cat);
        ServerDrink s = new ServerDrink(datasource.getUser().getDeviceToken(), d);
        String json = gson.toJson(s, ServerDrink.class);
        CreateDrinkAsync async = new CreateDrinkAsync(context, new CallBack());
        async.execute(json);
    }
}
