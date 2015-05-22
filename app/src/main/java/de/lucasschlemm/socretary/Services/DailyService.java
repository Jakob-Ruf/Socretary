package de.lucasschlemm.socretary.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.lucasschlemm.socretary.Contact;
import de.lucasschlemm.socretary.DatabaseHelper;
import de.lucasschlemm.socretary.MainActivity;
import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.Utils;

/**
 * Created by Daniel on 09.05.15.
 */
public class DailyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        doaBirthdayCheck();
        doaCallSomeoneCheck();


        return Service.START_STICKY;
    }

    private void doaCallSomeoneCheck() {

        // Aktuellen Tag herausfinden
        Calendar today = Calendar.getInstance();
        int curr_month = today.get(Calendar.MONTH)+1;
        int curr_day = today.get(Calendar.DAY_OF_MONTH);
        int curr_y = today.get(Calendar.YEAR);
        String currMonthString = String.valueOf(curr_month);
        String currDayString = String.valueOf(curr_day);
        if (curr_month < 10){
            currMonthString = "0"+String.valueOf(curr_month);
        }
        if (curr_day < 10){
            currDayString = "0"+String.valueOf(curr_day);
        }
        String todaystr = currMonthString+"-"+currDayString+"-"+String.valueOf(curr_y);


        // Kontakte abholen
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        ArrayList<Contact> lContacts = helper.getContactList();

        // Über jeden Kontakt gehen
        if (lContacts.size()>0){

            for(int i = 0; i < lContacts.size(); i++){

                Utils myhelper = new Utils();
                String ldaysleft = myhelper.getDaysLeft(lContacts.get(i).getLastContact(), lContacts.get(i).getFrequency());

                if (ldaysleft.equals("!")){
                    /*Context context = getApplicationContext();
                    CharSequence text = "Melde dich bei "+lContacts.get(i).getName();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    */
                    // prepare intent which is triggered if the
// notification is selected
                    Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
                    Notification n  = new Notification.Builder(this)
                            .setContentTitle("Socretary")
                            .setContentText("Melde dich doch mal wieder bei " + lContacts.get(i).getName())
                            .setSmallIcon(R.drawable.abc_btn_radio_material)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .addAction(R.drawable.abc_btn_radio_material, "Anrufen", pIntent)
                            .addAction(R.drawable.abc_btn_radio_material, "Simsen", pIntent).build();


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);




                };

            };
        };

    }

    private void doaBirthdayCheck() {
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
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
