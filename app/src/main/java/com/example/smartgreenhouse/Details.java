package com.example.smartgreenhouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    int greenhouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getRequest();
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

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyCommand();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        shutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shut();
            }
        });




    }

    private void getRequest() {
        try{
            Intent intent = this.getIntent();
            if (intent != null) {
                greenhouse = intent.getExtras().getInt(getString(R.string.greenhouse));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getXmlItems() {
        ghName = (TextView)findViewById(R.id.ghName);
        celciusText = (TextView)findViewById(R.id.celciusText);
        setForLabel = (TextView)findViewById(R.id.setForLabel);
        slideBarLabel = (TextView)findViewById(R.id.slideBarLabel);
        backButton = (ImageButton)findViewById(R.id.backButton);
        applyButton = (Button)findViewById(R.id.applyButton);
        refreshButton = (ImageButton)findViewById(R.id.refreshButton);
        shutButton = (Button)findViewById(R.id.shutButton);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        shutButton = (Button)findViewById(R.id.shutButton);
        String txt = getString(R.string.Greenhouse) + " " + String.valueOf(greenhouse);
        ghName.setText(txt);

        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        txt = String.valueOf(greenhouse) + getString(R.string.Temp);
        int temperature = setting0.getInt(txt, 0);
        txt = String.valueOf(greenhouse) + getString(R.string.Goal);
        int goal = setting0.getInt(txt, 0);
        txt = String.valueOf(greenhouse) + getString(R.string.isOn);
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
        SharedPreferences setting0 = getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        editor0.putInt(getString(R.string.responseCounter), 3);
        editor0.apply();

        send sender = new send();
        sender.setPortIp(getString(R.string.ip), getString(R.string.port));
        String message = "0." + (getString(R.string.ask) + ".") + (String.valueOf(greenhouse) + ".") + ("0" + "-");
        sender.setMessage(message);
        sender.execute();

        //while (setting0.getInt(getString(R.string.responseCounter), 0) > 0 ){}
    }

    private void sendCommand(int value, String command){
        send sender = new send();
        sender.setPortIp(getString(R.string.ip), getString(R.string.port));
        String message = "0." + (command + ".") + (String.valueOf(greenhouse) + ".") + (String.valueOf(value) + "-");
        sender.setMessage(message);
        sender.execute();
    }

    private void applyCommand(){
        int newGoal = seekBar.getProgress();
        sendCommand(newGoal, getString(R.string.setCommand));
        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        editor0.putInt(String.valueOf(greenhouse) + getString(R.string.Goal), newGoal);
        editor0.apply();
        editor0.putBoolean(String.valueOf(greenhouse) + getString(R.string.isOn), true);
        editor0.apply();
        Toast(getString(R.string.approve1));
        refresh();
    }

    private void shut(){
        sendCommand(0, getString(R.string.shutCommand));
        SharedPreferences setting0 = this.getSharedPreferences(getString(R.string.memory), 0);
        SharedPreferences.Editor editor0 = setting0.edit();
        String txt = String.valueOf(greenhouse) + getString(R.string.isOn);
        editor0.putBoolean(txt, false);
        editor0.apply();
        Toast(getString(R.string.approve2));
        refresh();
    }

    private void refresh(){
        Intent intent = new Intent(Details.this, Details.class);
        intent.putExtra(getString(R.string.greenhouse), greenhouse);
        startActivity(intent);
    }

    private void goHome(){
        Intent intent = new Intent(Details.this, MainActivity.class);
        startActivity(intent);
    }

    public void Toast(String s) {
        Toast.makeText(Details.this, s, Toast.LENGTH_SHORT).show();
    }
}