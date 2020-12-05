package com.example.smartgreenhouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button gh1Button;
    private Button gh2Button;
    private Button gh3Button;
    private Button gh4Button;
    private Button homeButton;
    private Button settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstTime();
        getConnections();
        getXmlItems();

        gh1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails(1);
            }
        });

        gh2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails(2);
            }
        });

        gh3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails(3);
            }
        });

        gh4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails(4);
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }

    private void firstTime() {
        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);
        boolean firstTime = setting0.getBoolean(getString(R.string.firstTime), true);

        if (firstTime) {
            SharedPreferences.Editor editor0 = setting0.edit();
            editor0.putBoolean(getString(R.string.firstTime), false);
            editor0.apply();

            editor0.putInt(getString(R.string.responseCounter), 0);
            editor0.apply();

            editor0.putBoolean(getString(R.string.darkMode), false);
            editor0.apply();

            int i;
            for(i=1;i<=4;i++){
                editor0.putInt(String.valueOf(i) + getString(R.string.Temp), 25+i);
                editor0.apply();

                editor0.putInt(String.valueOf(i) + getString(R.string.Goal), 27+i);
                editor0.apply();

                editor0.putBoolean(String.valueOf(i) + getString(R.string.isOn), false);
                editor0.apply();

                editor0.putBoolean(String.valueOf(i) + getString(R.string.isConnected), true);
                editor0.apply();
            }
        }

        boolean darkMode = setting0.getBoolean(getString(R.string.darkMode), false);
        if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }


    private void getXmlItems() {
        List<Button> buttonArray = new ArrayList<>();
        gh1Button = (Button)findViewById(R.id.greenhouse1);
        buttonArray.add(gh1Button);
        gh2Button = (Button)findViewById(R.id.greenhouse2);
        buttonArray.add(gh2Button);
        gh3Button = (Button)findViewById(R.id.greenhouse3);
        buttonArray.add(gh3Button);
        gh4Button = (Button)findViewById(R.id.greenhouse4);
        buttonArray.add(gh4Button);
        settingsButton = (Button)findViewById(R.id.settingsButton);
        homeButton = (Button)findViewById(R.id.homeButton);

        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);

        int i;
        for(i=1;i<=4;i++){
            int idx = i - 1;
            if (!(setting0.getBoolean(String.valueOf(i) + getString(R.string.isConnected), false))){
                buttonArray.get(idx).setEnabled(false);
                CharSequence txt = buttonArray.get(idx).getText();
                txt = txt + "\n" + getString(R.string.nconnected);
                buttonArray.get(idx).setText(txt);
            }
            else {
                CharSequence txt = buttonArray.get(idx).getText();
                txt = txt + "\n" + getString(R.string.connected);
                buttonArray.get(idx).setText(txt);
            }
        }
    }

    private void openDetails(int index){
        Intent intent = new Intent(MainActivity.this, Details.class);
        intent.putExtra(getString(R.string.greenhouse), index);
        startActivity(intent);
    }

    private void goToSettings(){
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
    }

    private void refresh(){
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void getConnections() {
        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        editor0.putInt(getString(R.string.responseCounter), 1);
        editor0.apply();

        send sender = new send();
        sender.setPortIp(getString(R.string.ip), getString(R.string.port));
        String message = "0." + (getString(R.string.list) + ".") + ("0" + ".") + ("0" + "-");
        sender.setMessage(message);
        sender.execute();

        //while (setting0.getInt(getString(R.string.responseCounter), 0) > 0 ){}
    }

    public void Toast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}