package de.lucasschlemm.socretary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Klasse zum Erstellen von Benachrichtungen.
 * Die Initiale Erstellung benötigt einen übergebenen Kontext
 */
public class NotificationHelper extends BroadcastReceiver
{
    // String um Herkunft eines Logeintrages zu definieren
    private static final String LOG_CALLER = "NotificationHelper";

    private Context                 myContext;
    private Notification            myNotification;
    private NotificationManager     myNotificationManager;
    private int                     MY_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        myContext = context;
        myNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

        String type = intent.getStringExtra("type");
        if (type.equals("text"))
        {
            // Auslesen des Empfängers
            String recipient    = intent.getStringExtra("recipient");
            sendTextNotification(recipient);
        }
        else if (type.equals("reminder"))
        {
            Log.d(LOG_CALLER, "Reminder angekommen");
            String contactName = intent.getStringExtra("contactName");
            String timePassed = intent.getStringExtra("timePassed");

            reminderNotification(contactName, timePassed);
        }
        else if (type.equals("location"))
        {
            Log.d(LOG_CALLER, "Location angekommen mit Namen " + intent.getStringExtra("contactName"));
            postLocationNotification(intent);
        }
        else if (type.equals("cancel_Notification"))
        {
            Log.d(LOG_CALLER, "cancel angekommen");
            cancelNotifications();
        }
        else
        {
            Log.e(LOG_CALLER, "Keine passende Kategorie ausgewählt");
        }

    }

    private void postLocationNotification(Intent intent) {
        String name = intent.getStringExtra("contactName");
        String latitude = intent.getStringExtra("latitude");
        String longitude = intent.getStringExtra("longitude");
        String title = name + " in deiner Nähe";
        String content = "Dein Freund " + name + " befindet sich gerade in deiner Nähe. Trefft euch doch mal";

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        PendingIntent openNavigationPendingIntent = PendingIntent.getActivity(myContext, 0, mapIntent, 0);


        myNotification = new Notification.Builder(myContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentIntent(openNavigationPendingIntent)
                .build();
        myNotificationManager.notify(MY_NOTIFICATION_ID, myNotification);
    }

    /**
     * Methode zum Einblenden einer Benachrichtigung weil man sich mal wieder bei einem Freund melden könnte.
     * @param contactName String: Name des Kontakts
     * @param timePassed String: Zeit seit letztem Kontakt
     */
    private void reminderNotification(String contactName, String timePassed)
    {
        //TODO @Lucas Notification erstellen
    }

    /**
     * Methode zum Ausblenden der Benachrichtigungen
     */
    private void cancelNotifications()
    {
        myNotificationManager.cancelAll();
    }

    /**
     * Methode zur Anzeige von Notifications zu gesendeten SMS
     * @param recipient String: Name des Empfängers
     */
    private void sendTextNotification(String recipient)
    {
        // Daten zur Benachrichtigung
        String title = "Nachricht verschickt";
        String content = "Socretary hat in deinem Namen eine Nachricht an " + recipient + " verschickt.";

        // Intent zum Öffnen der Applikation in der MainActivity
        Intent intentMain = new Intent(myContext, MainActivity.class);
        PendingIntent openMainPendingIntent = PendingIntent.getActivity(myContext, 0, intentMain, 0);

        // Notification zusammenstellen
        //TODO @Lucas Aktionen hinzufügen
        myNotification = new Notification.Builder(myContext)
                .setContentTitle(title)
                .setContentText(content)
                        // TODO @Lucas Icon anpassen
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentIntent(openMainPendingIntent)
                .build();
        myNotificationManager.notify(MY_NOTIFICATION_ID,myNotification);
    }
}
