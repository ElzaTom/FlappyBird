package com.example.elzatom.flappybird;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    CanvasView  canvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity currentActivity = this;
        canvasView = new CanvasView(this,currentActivity);
        setContentView(canvasView);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        canvasView.resume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        canvasView.pause();
    }

}
