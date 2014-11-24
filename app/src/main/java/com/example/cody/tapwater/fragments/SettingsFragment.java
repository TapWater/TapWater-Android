package com.example.cody.tapwater.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.example.cody.tapwater.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference prompts = (EditTextPreference) findPreference("prompts");
        EditTextPreference goal = (EditTextPreference) findPreference("goal");
        prompts.setSummary(sp.getString("prompts", "Number of prompts in time period"));
        goal.setSummary(sp.getString("goal", "Number of ounces to drink per day"));
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
        }
    }
}