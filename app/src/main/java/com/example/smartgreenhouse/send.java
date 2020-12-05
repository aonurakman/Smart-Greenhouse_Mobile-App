package com.example.smartgreenhouse;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static android.net.Uri.encode;
import static java.lang.Thread.sleep;

public class send extends AsyncTask<Void, Void, Void> {
    String FORMAT = "utf-8";

    Socket s;
    String ip;
    int port;

    PrintWriter pw;
    public String message;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            s = new Socket(ip, port);

            pw = new PrintWriter(s.getOutputStream());
            String sendLength = encode(String.valueOf(message.length()));
            int i;
            String blanks = "";
            for (i=0; i<(64 - sendLength.length()); i++){
                blanks = blanks + " ";
            }
            sendLength = sendLength + blanks;
            pw.write(sendLength);
            pw.flush();


            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();

            this.cut();

            pw = new PrintWriter(s.getOutputStream());
            sendLength = encode(String.valueOf(message.length()));
            blanks = "";
            for (i=0; i<(64 - sendLength.length()); i++){
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
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOError");
            e.printStackTrace();
        }
        return null;
    }

    public void setMessage(String message) {
        this.message = encode(message);
    }

    public void setPortIp(String ip, String port) {
        this.ip = ip;
        this.port = Integer.parseInt(port);
    }

    public void cut() {
        this.setMessage("!DISCONNECT");
    }
}
