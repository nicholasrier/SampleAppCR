package com.plusqa.bc.sampleappcr;

import android.app.Application;

import com.plusqa.bc.crashreport.Reporter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();

        Reporter reporter = new Reporter(this);

    }
}