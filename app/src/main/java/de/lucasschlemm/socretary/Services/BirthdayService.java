package de.lucasschlemm.socretary.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.Console;
import java.util.Calendar;

import de.lucasschlemm.socretary.Contact;

/**
 * Created by Daniel on 09.05.15.
 */
public class BirthdayService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Birthday Service", "started");

        // Aktuellen Tag herausfinden
        Calendar today = Calendar.getInstance();
        int curr_month = today.get(Calendar.MONTH);
        int curr_day = today.get(Calendar.DAY_OF_MONTH);

        // Über jeden Kontakt gehen

        // Prüfen ob der Kontakt Geburtstag hat
        Contact con = new Contact();
        String birthday = con.getBirthday();



        if ((birthday.substring(0,1).equals(String.valueOf(curr_day)))&&(birthday.substring(3,4).equals(String.valueOf(curr_month)))){
            Log.v("Birthday Service", con.getName()+" hat Geburtstag!");
        }

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
