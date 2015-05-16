package de.lucasschlemm.socretary.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;
import java.util.Calendar;

import de.lucasschlemm.socretary.Contact;
import de.lucasschlemm.socretary.DatabaseHelper;

/**
 * Created by Daniel on 09.05.15.
 */
public class BirthdayService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("Birthday Service", "started");

        // Aktuellen Tag herausfinden
        Calendar today = Calendar.getInstance();
        int curr_month = today.get(Calendar.MONTH)+1;
        int curr_day = today.get(Calendar.DAY_OF_MONTH);
        String currMonthString = String.valueOf(curr_month);
        String currDayString = String.valueOf(curr_day);
        if (curr_month < 10){
            currMonthString = "0"+String.valueOf(curr_month);
        }
        if (curr_day < 10){
            currDayString = "0"+String.valueOf(curr_day);
        }
        String todaystr = currMonthString+"-"+currDayString;

        // Kontakte abholen
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        ArrayList<Contact> lContacts = helper.getContactList();

        // Über jeden Kontakt gehen
        if (lContacts.size()>0){

            for(int i = 0; i < lContacts.size(); i++){
                // Prüfen ob der Kontakt Geburtstag hat
                if (lContacts.get(i).getBirthday().substring(5,10).equals(todaystr)){
                    Context context = getApplicationContext();
                    CharSequence text = lContacts.get(i).getName()+" hat Geburtstag!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                };
            };
        };

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
