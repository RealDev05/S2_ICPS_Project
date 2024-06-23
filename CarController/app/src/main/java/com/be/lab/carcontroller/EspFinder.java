package com.be.lab.carcontroller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

class EspFinder extends AsyncTask<Void, Void, Void> {

    EditText ipBox;
    Context context;
    String macAddress="a0:b7:65:4a:42:cc";

    EspFinder(EditText ipBox, Context context) {
        this.ipBox = ipBox;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int deviceIp = wifiManager.getDhcpInfo().gateway; // Typically the IP of the router or DHCP server

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            Log.i("IP Grab", "Started");
            for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
                Log.i("IP Grab", networkInterface.toString());
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress != null) {
                    Log.i("IP Grab", "Started3");
                    StringBuilder macStringBuilder = new StringBuilder();
                    for (byte b : hardwareAddress) {
                        macStringBuilder.append(String.format("%02X:", b));
                    }
                    if (macStringBuilder.length() > 0) {
                        macStringBuilder.deleteCharAt(macStringBuilder.length() - 1);
                    }
                    String macString = macStringBuilder.toString();
                    if (macString.equalsIgnoreCase(macAddress)) {
                        Log.i("IP Grab", "Started4");
                        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                        while (inetAddresses.hasMoreElements()) {
                            InetAddress inetAddress = inetAddresses.nextElement();
                            Log.i("IP Grab", "Host: (" + inetAddress.getHostAddress() + ") is reachable!");
                            if (!inetAddress.isLoopbackAddress()) {
                                Log.i("IP Grab", "Host: (" + inetAddress.getHostAddress() + ") is reachable!");
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}
