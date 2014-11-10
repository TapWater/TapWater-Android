package com.example.cody.tapwater.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.adapters.DrinkAdapter;
import com.example.cody.tapwater.R;

import java.util.ArrayList;

public class Library extends Activity {

    private DataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library);
        datasource = new DataSource(getBaseContext());

        ListView drinksList = (ListView) findViewById(R.id.drinksList);
        ArrayList<Drink> drinksArray = datasource.drinksDisplay();
        DrinkAdapter adapter = new DrinkAdapter(getBaseContext(), drinksArray);
        drinksList.setAdapter(adapter);
        drinksList.setFastScrollEnabled(true);


    }
}
