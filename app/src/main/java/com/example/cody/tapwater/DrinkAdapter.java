package com.example.cody.tapwater;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class DrinkAdapter extends BaseAdapter {

    private DataSource datasource;
    private LayoutInflater mInflater;
    ArrayList<Drink> drinks = new ArrayList<Drink>();

    public DrinkAdapter(Context context, ArrayList<Drink> dr) {
        mInflater = LayoutInflater.from(context);
        drinks = dr;
    }

    @Override
    public int getCount() {
        return drinks.size();
    }

    @Override
    public Drink getItem(int position) {
        return drinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        datasource = new DataSource(parent.getContext());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drink_row, null);

            holder = new ViewHolder();
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drink d = getItem(position);

        if (d.getCategory().equals("drink")) {
            holder.category.setText("4oz " + d.getCategory());
        } else if (d.getCategory().equals("glass")) {
            holder.category.setText("8oz " + d.getCategory());
        } else if (d.getCategory().equals("bottle")) {
            holder.category.setText("16oz " + d.getCategory());
        }

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

    static class ViewHolder {
        TextView category;
        TextView time;
    }
}