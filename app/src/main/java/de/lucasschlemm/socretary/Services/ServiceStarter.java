package de.lucasschlemm.socretary.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Daniel on 22.05.15.
 */
public class ServiceStarter {

    private Context pContext;

    public ServiceStarter(Context iContext){
        this.pContext = iContext;
    }

    public void startDailyService(){

        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();

        calSet.set(Calendar.HOUR_OF_DAY, 8);
        calSet.set(Calendar.MINUTE, 00);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        Intent intentBirthdayService = new Intent(pContext, DailyService.class);
        PendingIntent pintent = PendingIntent.getService(pContext, 0, intentBirthdayService, 0);

        AlarmManager alarm = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pintent);

        // For Testing

        /*Calendar cal = Calendar.getInstance();

        Intent intentBirthdayService = new Intent(pContext, DailyService.class);
        PendingIntent pintent = PendingIntent.getService(pContext, 0, intentBirthdayService, 0);

        AlarmManager alarm = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10*1000, pintent);*/



    }
}
