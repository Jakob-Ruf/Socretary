package de.lucasschlemm.socretary.Services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import de.lucasschlemm.socretary.Contact;

/**
 * Created by Daniel on 09.05.15.
 */
public class ServiceStarter {
    private static Activity pContext;
    private static Contact pContact;

    public ServiceStarter(Activity iContext, Contact iContact){
        this.pContact = iContact;
        this.pContext = iContext;
    };


    public static void startABirthdayServive(){
        // Frequenz: Einmal am Tag um 12 Uhr

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);

        Intent intentBirthdayService = new Intent(pContext, BirthdayService.class);
        PendingIntent pintent = PendingIntent.getService(pContext, 0, intentBirthdayService, 0);

        AlarmManager alarm = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pintent);

    };

    public static void startBirthdayServive(){
        // Frequenz: Alle 30 Sekunden

        Calendar cal = Calendar.getInstance();

        Intent intentBirthdayService = new Intent(pContext, BirthdayService.class);
        PendingIntent pintent = PendingIntent.getService(pContext, 0, intentBirthdayService, 0);

        AlarmManager alarm = (AlarmManager) pContext.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pintent);


    };
}
