package de.lucasschlemm.socretary.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;

import de.lucasschlemm.socretary.CallsFragment;
import de.lucasschlemm.socretary.Contact;
import de.lucasschlemm.socretary.DatabaseHelper;
import de.lucasschlemm.socretary.MainActivity;
import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.Utils;

/**
 * Created by Daniel on 09.05.15.
 */
public class DailyService extends Service {

    private ArrayList<String> pMessageContainer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        pMessageContainer = new ArrayList<String>();


        doaBirthdayCheck();
        doaCallSomeoneCheck();

        if (pMessageContainer.size() > 1){
            Intent NotificationIntent = new Intent(this, CallsFragment.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, NotificationIntent, 0);

            Notification n  = new Notification.Builder(this)
                    .setContentTitle("Socretary")
                    .setContentText("Du hast einige neue Benachrichtigungen ("+pMessageContainer.size()+")")
                    .setSmallIcon(R.drawable.abc_btn_radio_material)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.abc_btn_radio_material, "Nachsehen", pIntent).build();


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        }else if (pMessageContainer.size() == 1){
            Intent NotificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, NotificationIntent, 0);

            Notification n  = new Notification.Builder(this)
                    .setContentTitle("Socretary")
                    .setContentText(pMessageContainer.get(0))
                    .setSmallIcon(R.drawable.abc_btn_radio_material)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.abc_btn_radio_material, "Anrufen", pIntent)
                    .addAction(R.drawable.abc_btn_radio_material, "Simsen", pIntent).build();


            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        }


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

                    pMessageContainer.add(("Melde dich doch mal wieder bei " + lContacts.get(i).getName()));

                   /* Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

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
                */


                };

            };
        };

    }

    private void doaBirthdayCheck() {
        // Aktuellen Tag herausfinden


        DateTime now = new DateTime();
        LocalDate jodlocal = now.toLocalDate();
        DateTime jodaToday = jodlocal.toDateTimeAtStartOfDay(now.getZone());

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-mm-dd");

        // Kontakte abholen
        DatabaseHelper helper = DatabaseHelper.getInstance(this);
        ArrayList<Contact> lContacts = helper.getContactList();

        // Über jeden Kontakt gehen
        if (lContacts.size()>0){

            for(int i = 0; i < lContacts.size(); i++){

                LocalDate localbirthday = new LocalDate(lContacts.get(i).getBirthday());
                DateTime jodaBirthday = localbirthday.toDateTimeAtStartOfDay(now.getZone());



                if((jodaBirthday.getMonthOfYear()==jodaToday.getMonthOfYear())&&
                        (jodaBirthday.getDayOfMonth()==jodaToday.getDayOfMonth())){

                    pMessageContainer.add(lContacts.get(i).getName()+" hat Geburtstag!");

                   /* Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    Notification n  = new Notification.Builder(this)
                            .setContentTitle("Socretary")
                            .setContentText(lContacts.get(i).getName()+" hat Geburtstag!")
                            .setSmallIcon(R.drawable.abc_btn_radio_material)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .addAction(R.drawable.abc_btn_radio_material, "Anrufen", pIntent)
                            .addAction(R.drawable.abc_btn_radio_material, "Simsen", pIntent).build();


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);*/
                };
            };
        };
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
