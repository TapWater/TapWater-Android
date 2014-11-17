/****************************************************************************************
 /*
 /* FILE NAME: DrinkAdapter.java
 /*
 /* DESCRIPTION: Adapter to display Drinks in specified format for use in Library activity.
 /*
 /* REFERENCE: Attached to ListView in Library.java.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/25/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cody.tapwater.database.DataSource;
import com.example.cody.tapwater.objects.Drink;
import com.example.cody.tapwater.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DrinkAdapter extends BaseAdapter {

    private DataSource datasource;
    private LayoutInflater mInflater;
    ArrayList<Drink> drinks = new ArrayList<Drink>();


    /**
     * Constructor to instantiate Adapter. Inflates layout from context and sets drinks field.
     *
     * @param context: context passed from calling activity.
     * @param dr:      ArrayList of drinks passed from calling activity.
     */
    public DrinkAdapter(Context context, ArrayList<Drink> dr) {
        mInflater = LayoutInflater.from(context);
        drinks = dr;
    }

    /**
     * Get the number of Drinks in the ArrayList.
     *
     * @return integer number of Drinks.
     */
    @Override
    public int getCount() {
        return drinks.size();
    }

    /**
     * Return Drink corresponding to specified position.
     *
     * @param position: int of index desired from ArrayList
     * @return Drink object from specified index.
     */
    @Override
    public Drink getItem(int position) {
        return drinks.get(position);
    }

    /**
     * Included only to override BaseAdapter. Not used.
     *
     * @param position
     * @return: position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Instantiates and displays row of ListView specified.
     *
     * @param position:    index of ListView to be created.
     * @param convertView: Inflated layout of ListView row.
     * @param parent:      Parent activity used to object context.
     * @return Created View.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Initialize ViewHolder and instantiate Datasource object from context provided by parent.
        ViewHolder holder;
        datasource = new DataSource(parent.getContext());

        // If View doesn't exist, create it; if it does, set ViewHolder to latest instance of View.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drink_row, null);

            holder = new ViewHolder();
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get drink from specified index of ArrayList.
        Drink d = getItem(position);

        // Determine category of Drink and set text accordingly.
        if (d.getCategory().equals("drink")) {
            holder.category.setText("4oz " + d.getCategory());
        } else if (d.getCategory().equals("glass")) {
            holder.category.setText("8oz " + d.getCategory());
        } else if (d.getCategory().equals("bottle")) {
            holder.category.setText("16oz " + d.getCategory());
        }

        // Calculate time since particular drink was created.
        Calendar now = Calendar.getInstance();
        Calendar drinkTime = datasource.getDateByUuid(d.getUUID());

        long difference = now.getTimeInMillis() - drinkTime.getTimeInMillis();

        int hours = (int) difference / (1000 * 60 * 60);
        int minutes = (int) difference / (60 * 1000);
        int seconds = (int) difference / 1000;

        if (hours >= 24) {
            int number = hours / 24;
            if (number > 1) {
                holder.time.setText(number + " days ago");
            } else {
                holder.time.setText("Yesterday");
            }
        } else if (hours < 24) {
            if (minutes >= 60) {
                int number = minutes / 60;
                if (number > 1) {
                    holder.time.setText(number + " hours ago");
                } else {
                    holder.time.setText(number + " hour ago");
                }
            } else if (minutes < 60) {
                if (seconds >= 60) {
                    int number = seconds / 60;
                    if (number > 1) {
                        holder.time.setText(number + " minutes ago");
                    } else {
                        holder.time.setText(number + " minute ago");
                    }
                } else if (seconds < 60) {
                    holder.time.setText(seconds + " seconds ago");
                }
            }
        }

        return convertView;
    }

    /**
     * Class containing UI components for ListView row.
     */
    static class ViewHolder {
        TextView category;
        TextView time;
    }
}