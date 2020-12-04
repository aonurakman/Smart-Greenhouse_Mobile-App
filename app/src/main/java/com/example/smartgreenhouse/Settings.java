package com.example.smartgreenhouse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class Settings extends AppCompatActivity {

    private ToggleButton darkModeToggle;
    private Button homeButton;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getXmlItems();

        darkModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                apply();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });


    }

    private void apply() {
        SharedPreferences setting1 = getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor1 = setting1.edit();
        editor1.putBoolean(getString(R.string.darkMode), darkModeToggle.isChecked());
        editor1.apply();
        if (darkModeToggle.isChecked()) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void getXmlItems() {
        homeButton = (Button)findViewById(R.id.homeButton);
        settingsButton = (Button)findViewById(R.id.settingsButton);
        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        boolean isDark = setting0.getBoolean(getString(R.string.darkMode), false);
        darkModeToggle = (ToggleButton)findViewById(R.id.darkModeToggle);
        darkModeToggle.setChecked(isDark);
    }

    private void refresh(){
        Intent intent = new Intent(Settings.this, Settings.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(Settings.this, MainActivity.class);
        startActivity(intent);
    }
}