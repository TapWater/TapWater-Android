package com.example.cody.tapwater.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.format.DateFormat;

import com.example.cody.tapwater.R;
import com.example.cody.tapwater.objects.TimePreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Calendar beginCal = Calendar.getInstance();
    private Calendar endCal = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference prompts = (EditTextPreference) findPreference("prompts");
        EditTextPreference goal = (EditTextPreference) findPreference("goal");

        prompts.setSummary(sp.getString("prompts", "Number of prompts in time period"));
        goal.setSummary(sp.getString("goal", "Number of ounces to drink per day"));

        try {
            System.out.println(sp.getString("begin_time", "00"));
            System.out.println(sp.getString("end_time", "00"));

            beginCal.setTime(sdf.parse(sp.getString("begin_time", "12:00 AM")));
            endCal.setTime(sdf.parse(sp.getString("end_time", "12:00 AM")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            pref.setSummary(etp.getText());
        } else if (pref instanceof  TimePreference) {
            try {
                beginCal.setTime(sdf.parse(sp.getString("begin_time", "00")));
                endCal.setTime(sdf.parse(sp.getString("end_time", "00")));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (beginCal.after(endCal)) {
                System.out.println("begin greater than end");
            } else {
                System.out.println("It's all good");
            }
        }
    }
}