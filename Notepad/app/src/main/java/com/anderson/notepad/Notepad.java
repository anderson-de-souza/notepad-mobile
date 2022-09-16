package com.anderson.notepad;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;

import com.anderson.notepad.receiver.AlarmReceiver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

public class Notepad extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionHandler handler = new ExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

        public ExceptionHandler() {
            defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable exception) {
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.setAction("uncaughtException");
            intent.putExtra("stackTrace",stackTraceToString(exception));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Notepad.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager = getSystemService(AlarmManager.class);
            }else {
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            }
            AlarmManagerCompat.setExact(alarmManager,AlarmManager.ELAPSED_REALTIME_WAKEUP,1000,pendingIntent);
            defaultUncaughtExceptionHandler.uncaughtException(thread,exception);
        }

        @NonNull
        private String stackTraceToString(@NonNull Throwable exception) {

            final StringWriter writer = new StringWriter();
            final PrintWriter  printWriter = new PrintWriter(writer);

            do {

                exception.printStackTrace(printWriter);
                exception = exception.getCause();

            } while (exception != null);

            String stackTrace = writer.toString();

            printWriter.close();

            return stackTrace;

        }

    }
    
    public static CharSequence getTimeString(Context context) {

        Calendar calendar = Calendar.getInstance();

        if(DateFormat.is24HourFormat(context)) {

            return DateFormat.format(context.getString(R.string.is_24hours_format),calendar.getTimeInMillis());

        }else {

            return DateFormat.format(context.getString(R.string.is_12hours_format),calendar.getTimeInMillis());

        }
      
    }
    
}
