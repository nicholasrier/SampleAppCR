package com.plusqa.bc.crashreport;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ApplicationCR extends Application {

    private Thread.UncaughtExceptionHandler defaultUEH;

    @Override
    public void onCreate () {
        // Setup handler for uncaught exceptions.
        super.onCreate();

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

        Utils.saveLog(getApplicationContext(), "crash_log");

        Intent intent = new Intent (getApplicationContext(), FormatAndSend.class);
        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("CalledFrom", "CRASH");

        PendingIntent myActivity = PendingIntent.getActivity(getApplicationContext(),
                192837, intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager;
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    15000, myActivity);
        }


        System.exit(2);
        defaultUEH.uncaughtException(thread, e);
    }

}
