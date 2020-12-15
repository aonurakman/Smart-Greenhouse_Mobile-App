package com.example.smartgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Details extends AppCompatActivity {

    private TextView ghName;
    private ImageButton backButton;
    private ImageButton refreshButton;

    private TextView celciusText;
    private TextView setForLabel;
    private TextView slideBarLabel;

    private SeekBar seekBar;
    private Button applyButton;
    private Button shutButton;

    int greenhouseCode;
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getRequest();
        startListening();
        latchData();
        getXmlItems();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                slideBarLabel.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        applyButton.setOnClickListener(v -> applyCommand());

        refreshButton.setOnClickListener(v -> refresh());

        backButton.setOnClickListener(v -> goHome());

        shutButton.setOnClickListener(v -> shut());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.turnOff();
    }

    private void getRequest() {
        try{
            Intent intent = this.getIntent();
            if (intent != null) {
                greenhouseCode = intent.getExtras().getInt(getString(R.string.greenhouse));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getXmlItems() {
        ghName = findViewById(R.id.ghName);
        celciusText = findViewById(R.id.celciusText);
        setForLabel = findViewById(R.id.setForLabel);
        slideBarLabel = findViewById(R.id.slideBarLabel);
        backButton = findViewById(R.id.backButton);
        applyButton = findViewById(R.id.applyButton);
        refreshButton = findViewById(R.id.refreshButton);
        shutButton = findViewById(R.id.shutButton);
        seekBar = findViewById(R.id.seekBar);
        shutButton = findViewById(R.id.shutButton);
        String txt = getString(R.string.Greenhouse) + " " + String.valueOf(greenhouseCode);
        ghName.setText(txt);

        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);

        txt = String.valueOf(greenhouseCode) + getString(R.string.isConnected);
        Boolean isConn = setting0.getBoolean(txt, false);
        if (!isConn){ goHome(); }

        txt = String.valueOf(greenhouseCode) + getString(R.string.Temp);
        int temperature = setting0.getInt(txt, 0);
        txt = String.valueOf(greenhouseCode) + getString(R.string.Goal);
        int goal = setting0.getInt(txt, 0);
        txt = String.valueOf(greenhouseCode) + getString(R.string.isOn);
        boolean isOn = setting0.getBoolean(txt, false);

        if (isOn) {
            txt = String.valueOf(temperature) + getString(R.string.celciusSign);
            celciusText.setText(txt);
            txt = getString(R.string.crntlySet) + " " + String.valueOf(goal) + getString(R.string.celciusSign);
            setForLabel.setText(txt);
            seekBar.setProgress(goal);
            slideBarLabel.setText(String.valueOf(goal));
        }
        else {
            celciusText.setText(getString(R.string.off));
            setForLabel.setText(getString(R.string.sysInactive));
            seekBar.setProgress(0);
            slideBarLabel.setText("0");
            shutButton.setEnabled(false);
            shutButton.setVisibility(View.INVISIBLE);
        }
    }

    private void latchData(){
        String message = "X" + "." + (String.valueOf(greenhouseCode));
        client.waitForResponse = 1;
        client.send(message);
        while(client.waitForResponse > 0){}
        applyClientData();
    }

    private void applyClientData(){
        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();

        String txt = String.valueOf(client.gCode) + getString(R.string.isOn);
        editor0.putBoolean(txt, (client.goal < 51));
        editor0.apply();

        txt = String.valueOf(client.gCode) + getString(R.string.Temp);
        editor0.putInt(txt, (client.temperature));
        editor0.apply();

        txt = String.valueOf(client.gCode) + getString(R.string.isConnected);
        editor0.putBoolean(txt, (client.goal < 52));
        editor0.apply();

        txt = String.valueOf(client.gCode) + getString(R.string.Goal);
        editor0.putInt(txt, (client.goal));
        editor0.apply();

    }

    private void sendCommand(int value){
        String message = (String.valueOf(greenhouseCode) + ".") + (String.valueOf(value));
        client.send(message);
    }

    private void startListening(){
        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);
        client = new Client();
        client.setPortIp(setting0.getString(getString(R.string.ipSelection), ""), getString(R.string.port));
        client.turnOn();
        client.execute();
    }

    private void applyCommand(){
        int newGoal = seekBar.getProgress();
        sendCommand(newGoal);
        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        editor0.putInt(String.valueOf(greenhouseCode) + getString(R.string.Goal), newGoal);
        editor0.apply();
        editor0.putBoolean(String.valueOf(greenhouseCode) + getString(R.string.isOn), true);
        editor0.apply();
        Toast(getString(R.string.approve1));
        refresh();
    }

    private void shut(){
        sendCommand(51);
        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        String txt = String.valueOf(greenhouseCode) + getString(R.string.isOn);
        editor0.putBoolean(txt, false);
        editor0.apply();
        txt = String.valueOf(greenhouseCode) + getString(R.string.Goal);
        editor0.putInt(txt, 51);
        editor0.apply();
        Toast(getString(R.string.approve2));
        refresh();
    }

    private void refresh(){
        client.turnOff();
        Intent intent = new Intent(Details.this, Details.class);
        intent.putExtra(getString(R.string.greenhouse), greenhouseCode);
        while (client.isActive()) {}
        startActivity(intent);
    }

    private void goHome(){
        client.turnOff();
        Intent intent = new Intent(Details.this, MainActivity.class);
        while (client.isActive()) {}
        startActivity(intent);
    }

    public void Toast(String s) {
        Toast.makeText(Details.this, s, Toast.LENGTH_SHORT).show();
    }
}