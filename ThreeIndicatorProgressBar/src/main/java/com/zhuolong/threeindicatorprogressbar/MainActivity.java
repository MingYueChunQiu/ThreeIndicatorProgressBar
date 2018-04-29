package com.zhuolong.threeindicatorprogressbar;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.zhuolong.threeindicatorprogressbar.ui.ThreeIndicatorProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreeIndicatorProgressBar progressBar = new ThreeIndicatorProgressBar.Builder(this)
                .setBarHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        20, getResources().getDisplayMetrics()))
                .setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        30, getResources().getDisplayMetrics()))
                .setHeadBarColor(Color.GREEN)
                .setMiddleBarColor(Color.GRAY)
                .setTailBarColor(Color.RED)
                .setHeadCircleColor(Color.BLUE)
                .setTailCircleColor(Color.BLUE)
                .setMax(10)
                .setProgress(2)
                .setTailProgress(4)
                .build();
        progressBar.setTailProgress(2);
        ((LinearLayoutCompat)findViewById(R.id.cl_container))
                .addView(progressBar);
        LinearLayoutCompat.LayoutParams lpProgress = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        progressBar.setLayoutParams(lpProgress);
    }
}
