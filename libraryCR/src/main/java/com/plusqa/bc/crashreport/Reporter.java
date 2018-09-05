package com.plusqa.bc.crashreport;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public class Reporter {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private final Application application;

    private WeakReference<Activity> activityWeakReference;

    private SensorManager sensorManager;

    private Sensor accelerometer;

    private ShakeDetector shakeDetector;

    private boolean registered = false;

    private String image_name = "screenshot.jpg";
    private String log_name = "logcat.txt";

    private class simpleActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityResumed(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);

            String className = activity.getLocalClassName();

            if (!registered && !className.equals(ScreenShotMarkUp.class.getName()) && !className.equals(FormatAndSend.class.getName()) ) {

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

        //intentionally unimplemented methods
        @Override
        public void onActivityStopped(Activity activity) {}
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

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

    }

    public void handleUncaughtException (Thread thread, Throwable e)
    {
        e.printStackTrace();

        Utils.saveLog(application.getApplicationContext(), "crash_log");

        Intent intent = new Intent (application.getApplicationContext(), FormatAndSend.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CalledFrom", "CRASH");

        PendingIntent myActivity = PendingIntent.getActivity(application.getApplicationContext(),
                192837, intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager;
        alarmManager = (AlarmManager) application.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    15000, myActivity);
        }


        System.exit(2);
        defaultUEH.uncaughtException(thread, e);
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
