package com.plusqa.bc.crashreport;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.plusqa.bc.crashreport.CrashReport;
import com.plusqa.bc.crashreport.ScreenShotMarkUp;
import com.plusqa.bc.crashreport.ShakeDetector;
import com.plusqa.bc.crashreport.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class Reporter {

    private final Application application;

    private WeakReference<Activity> activityWeakReference;

    private SensorManager sensorManager;

    private Sensor accelerometer;

    private ShakeDetector shakeDetector;

    private boolean registered = false;

    private String image_name = "screenshot.jpg";
    private String log_name = "logcat.txt";

    static final int SEND_BUG_REPORT_REQUEST = 1;


    private class simpleActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityResumed(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);

            if (!registered) {
                sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
                registered = true;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

            if (registered) {
                sensorManager.unregisterListener(shakeDetector);
                registered = false;
            }

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (registered) {
                sensorManager.unregisterListener(shakeDetector);
                registered = false;
            }

        }

        //intentionally unimplemented methods
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
        @Override
        public void onActivityStarted(Activity activity) {}
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        @Override
        public void onActivityDestroyed(Activity activity) {}

    }


    public Reporter(@NonNull final Application application) {

        this.application = application;

        application.registerActivityLifecycleCallbacks(new simpleActivityCallback());

        shakeDetector = new ShakeDetector();

        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake() {

                Context context = application.getApplicationContext();
                Activity activity = activityWeakReference.get();
                Utils.saveBitmap(context, image_name, getScreenBitmap());

                Utils.saveLog(context, log_name);

                Intent intent = null;
                try {
                    intent = new Intent(activity, Class.forName("com.plusqa.bc.crashreport.ScreenShotMarkUp"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                activity.startActivity(intent);
            }
        });

        sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);

            registered = true;
        }

    }

    private Bitmap getScreenBitmap() {

        Activity activity = activityWeakReference.get();

        android.view.View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap b1 = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;

        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(size);

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, size.x, size.y - statusBarHeight);

        view.destroyDrawingCache();

        return b;
    }

}
