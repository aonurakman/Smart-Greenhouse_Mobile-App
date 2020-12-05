package com.example.smartgreenhouse;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static android.net.Uri.decode;
import static android.net.Uri.encode;

public class receive extends AsyncTask<Void, Void, Void> {

    String FORMAT = "utf-8";
    int BUFFER_SIZE = 2048;

    Socket s;
    String ip;
    int port;

    boolean shouldListen;

    BufferedReader in;
    PrintWriter pw;


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("HATA:", "Baglanamadi!");
        }

        while (shouldListen) {
            try {
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                int charsRead = 0;
                char[] buffer = new char[BUFFER_SIZE];

                charsRead = in.read(buffer);
                String serverMessage = new String(buffer).substring(0, charsRead);
                serverMessage = decode(serverMessage);

                if (serverMessage != null) {
                    Log.i("Message from Server", serverMessage.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String message = encode("!DISCONNECT");
        try {
            pw = new PrintWriter(s.getOutputStream());
            String sendLength = encode(String.valueOf(message.length()));
            String blanks = "";
            int i;
            for (i = 0; i < (64 - sendLength.length()); i++) {
                blanks = blanks + " ";
            }
            sendLength = sendLength + blanks;
            pw.write(sendLength);
            pw.flush();


            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();

            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void setPortIp(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    public void turnOn() {
        this.shouldListen = true;
    }

    public void turnOff() {
        this.shouldListen = false;
    }
}
