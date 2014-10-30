package com.example.cody.tapwater;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private DataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        datasource = new DataSource(getBaseContext());
        View drink = findViewById(R.id.drinkButton);
        View glass = findViewById(R.id.glassButton);
        View bottle = findViewById(R.id.bottleButton);

        final TextView cups = (TextView) findViewById(R.id.cupsText);
        cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");

        // TODO: Figure out how to implement timer since last drink
        final TextView timer = (TextView) findViewById(R.id.timeSinceLastDrink);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
        Calendar now = Calendar.getInstance();
        Calendar latest = datasource.getLatestDate();

        Log.i(TAG, now.toString());
        Log.i(TAG, latest.toString());

        long difference = now.getTimeInMillis() - latest.getTimeInMillis();
        int hours = (int) difference / (1000 * 60 * 60);
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(difference);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(difference);
        timer.setText(hours + ":" + minutes + ":" + seconds);

        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "drink");
                Drink drink = new Drink("drink");
                Log.i(TAG, drink.getDrinkDate());
                datasource.insertDrink(drink);
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
            }
        });

        glass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "glass");
                Drink drink = new Drink("glass");
                Log.i(TAG, drink.getDrinkDate());
                datasource.insertDrink(drink);
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
            }
        });

        bottle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "bottle");
                Drink drink = new Drink("bottle");
                Log.i(TAG, drink.getDrinkDate());
                datasource.insertDrink(drink);
                cups.setText(String.valueOf(datasource.getTotalCups()) + " cups");
            }
        });
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
