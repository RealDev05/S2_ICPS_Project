package com.be.lab.carcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ControllerActivity extends AppCompatActivity {

    boolean isManualMode=true;

    Button manualBtn,autoBtn,scanBtn;

    ImageButton fwdBtn,bwdBtn,leftBtn,rightBtn,stopBtn;

    Drawable modeBtnDrawable,modeChangeFwdDrawable,modeChangeBwdDrawable;

    View modeChangeView;

    AnimationDrawable modeChangeDrawable;
    static Socket socket=null;
    static OutputStream outputStream=null;

    LinearLayout scanArea;
    public ScanHandler scanHandler;

    Handler handler=new Handler();

    void init(){
        manualBtn=findViewById(R.id.manual_button);
        autoBtn=findViewById(R.id.auto_button);
        scanBtn=findViewById(R.id.scan_button);
        modeBtnDrawable= AppCompatResources.getDrawable(this,R.drawable.rectangle_4);
        modeChangeView=findViewById(R.id.mode_change_background_view);

        fwdBtn=findViewById(R.id.forward_button);
        bwdBtn=findViewById(R.id.backward_button);
        leftBtn=findViewById(R.id.left_button);
        rightBtn=findViewById(R.id.right_button);
        stopBtn=findViewById(R.id.stop_button);

        scanArea=findViewById(R.id.scan_area);
        scanHandler=new ScanHandler(this);
        scanArea.addView(scanHandler);

        modeChangeFwdDrawable=AppCompatResources.getDrawable(ControllerActivity.this,R.drawable.mode_change_background_gradient_animation);
        modeChangeBwdDrawable=AppCompatResources.getDrawable(ControllerActivity.this,R.drawable.mode_change_background_gradient_animation_reverse);
        modeChangeView.setBackground(modeChangeFwdDrawable);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_controller);

        init();

        String espIp=getIntent().getStringExtra("IP_ADDRESS");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            socket = new Socket(espIp, 80);
            outputStream = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace();
        }

        new EspListener(socket,this,scanHandler).execute();

        manualBtn.setOnClickListener(v -> {
            if(isManualMode){
                return;
            }
            isManualMode=true;
            manualBtn.setBackground(modeBtnDrawable);
            autoBtn.setBackground(null);
            try {
                modeChangeDrawable.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            modeChangeView.setBackground(modeChangeBwdDrawable);
            modeChangeView.post(()->{
                modeChangeDrawable=(AnimationDrawable)modeChangeView.getBackground();
                modeChangeDrawable.setEnterFadeDuration(300);
                modeChangeDrawable.setExitFadeDuration(500);
                modeChangeDrawable.start();
            });
        });
        autoBtn.setOnClickListener(v -> {
            if(!isManualMode){
                return;
            }
            isManualMode=false;
            autoBtn.setBackground(modeBtnDrawable);
            manualBtn.setBackground(null);
            try {
                modeChangeDrawable.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            modeChangeView.setBackground(modeChangeFwdDrawable);
            modeChangeView.post(()->{
                modeChangeDrawable=(AnimationDrawable)modeChangeView.getBackground();
                modeChangeDrawable.setEnterFadeDuration(300);
                modeChangeDrawable.setExitFadeDuration(500);
                modeChangeDrawable.start();
            });
        });
        scanBtn.setOnClickListener(v -> {
            try {
                outputStream.write("p".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        fwdBtn.setOnTouchListener((view, motionEvent) -> {

            handler.removeCallbacksAndMessages(null);
            if(outputStream==null){
                return true;
            }
            try {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        outputStream.write("w".getBytes());
                        break;
                    case MotionEvent.ACTION_UP:
                        outputStream.write("t".getBytes());
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        bwdBtn.setOnTouchListener((view, motionEvent) -> {

            handler.removeCallbacksAndMessages(null);
            if(outputStream==null){
                return true;
            }
            try {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        outputStream.write("s".getBytes());
                        break;
                    case MotionEvent.ACTION_UP:
                        outputStream.write("t".getBytes());
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        leftBtn.setOnTouchListener((view, motionEvent) -> {

            handler.removeCallbacksAndMessages(null);
            if(outputStream==null){
                return true;
            }
            try {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        outputStream.write("a".getBytes());
                        break;
                    case MotionEvent.ACTION_UP:
                        outputStream.write("t".getBytes());
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        rightBtn.setOnTouchListener((view, motionEvent) -> {

            handler.removeCallbacksAndMessages(null);
            if(outputStream==null){
                return true;
            }
            try {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        outputStream.write("d".getBytes());
                        break;
                    case MotionEvent.ACTION_UP:
                        outputStream.write("t".getBytes());
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        });

        stopBtn.setOnClickListener(v -> {
            if(outputStream==null){
                return;
            }
            try {
                outputStream.write("t".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    static void alert(String message, Context context){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static String getEspIP(){
        try {
            ProcessBuilder builder = new ProcessBuilder("arp", "-a");
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String[] temp;
            while ((line = reader.readLine()) != null && !line.contains("98:f4:ab:b2:5d:25".replaceAll(":","-"))) {}
            temp=line.split("\\s+");
            line=temp[0].trim()==""?temp[1]:temp[0];
            System.out.println("Device Found : "+line);
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}