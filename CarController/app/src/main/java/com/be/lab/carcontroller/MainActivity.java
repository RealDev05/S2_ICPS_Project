package com.be.lab.carcontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    EditText ipBox;
    Button button;

    void init(){
        ipBox=findViewById(R.id.ip_box);
        button=findViewById(R.id.ip_button);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();
        button.setOnClickListener(v -> {
            if(!ipBox.getText().toString().trim().isEmpty()){
                startActivity(new Intent(MainActivity.this,ControllerActivity.class).putExtra("IP_ADDRESS",ipBox.getText().toString()));
            }
        });

        new EspFinder(ipBox,this).execute();
    }

    public static void showIpNeigh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("ip", "neigh", "show");
                    processBuilder.redirectErrorStream(true);
                    Process process = processBuilder.start();
                    process.waitFor();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Handle each line of output here
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}