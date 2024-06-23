package com.be.lab.carcontroller;

import static java.lang.Math.min;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ScanHandler extends RelativeLayout {

    View[] scans=new View[91];
    LayoutParams params;
    public ScanHandler(Context context) {
        super(context);
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        this.setBackgroundColor(getResources().getColor(R.color.white));
        params=new LayoutParams(7,400);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        for(int i=0;i<91;i++){
            asyncAddScan(context,i,1);
        }
    }

    void asyncAddScan(Context context,int index,float scale) {
        new Thread(() -> {
            View v=new View(context);
            v.setLayoutParams(params);
            v.setBackgroundColor(getResources().getColor(R.color.black));
            v.setRotation(index-45);
            v.setPivotX(0);
            v.setPivotY(400);
            v.setScaleY(scale);
            scans[index]=v;
            this.addView(scans[index]);
        }).start();
    }

    public void asyncUpdateScan(int index,float scale){
        if(index==-1){return;}
        scale=scale<0?1f:min(scale/40,1f);
        scans[index].setScaleY(scale);
    }

}
