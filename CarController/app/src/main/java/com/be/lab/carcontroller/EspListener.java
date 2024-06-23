package com.be.lab.carcontroller;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

class EspListener extends AsyncTask<Void, String, Void> {
    Socket socket;
    Context context;
    ScanHandler scanHandler;
    EspListener(Socket socket,Context context,ScanHandler scanHandler){
        this.socket=socket;
        this.context=context;
        this.scanHandler=scanHandler;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            // Continuously listen for messages
            while ((message = in.readLine()) != null) {
                publishProgress(message);
            }

            in.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        String[] arr=values[0].split("/");
        scanHandler.asyncUpdateScan(Integer.parseInt(arr[0]),Float.parseFloat(arr[1]));
    }
}
