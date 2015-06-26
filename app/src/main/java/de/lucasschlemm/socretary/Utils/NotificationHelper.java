package de.lucasschlemm.socretary.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import de.lucasschlemm.socretary.MainActivity;
import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.classes.Contact;
import de.lucasschlemm.socretary.database.DatabaseHelper;

/**
 * Klasse zum Erstellen von Benachrichtungen.
 * Die Initiale Erstellung benötigt einen übergebenen Kontext
 */
public class NotificationHelper extends BroadcastReceiver {
	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "NotificationHelper";
	long[] pattern = {
			500,
			110,
			500,
			110,
			450,
			110,
			200,
			110,
			170,
			40,
			450,
			110,
			200,
			110,
			170,
			40,
			500};

	private Context myContext;
	private Notification myNotification;
	private NotificationManager myNotificationManager;
	private int MY_NOTIFICATION_ID = 1;

	@Override
	public void onReceive(Context context, Intent intent) {
		myContext = context;
		myNotificationManager = (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);

		String type = intent.getStringExtra("type");
		switch (type) {
			case "text":
				// Auslesen des Empfängers
				String recipient = intent.getStringExtra("recipient");
				sendTextNotification(recipient);
				break;
			case "reminder":
				Log.d(LOG_CALLER, "Reminder angekommen");
				String contactName = intent.getStringExtra("contactName");
				String timePassed = intent.getStringExtra("timePassed");

				reminderNotification(contactName, timePassed);
				break;
			case "location":
				Log.d(LOG_CALLER, "Location angekommen mit Namen " + intent.getStringExtra("contactName"));
				postLocationNotification(intent);
				break;
			case "locationHome":
				Log.d("NotificationHelper", "onReceive: " + "LocationHome");
				postLocationHomeNotification(intent);
				break;
			case "cancel_Notification":
				Log.d(LOG_CALLER, "cancel angekommen");
				cancelNotifications();
				break;
			default:
				Log.e(LOG_CALLER, "Keine passende Kategorie ausgewählt");
				break;
		}

	}

	/**
	 * Methode zur Erstellung von Notifications wenn ein Freund in deiner Nähe seinen Standort teilt.
	 *
	 * @param intent Intent, welcher die notwendigen Extras enthält.
	 */
	private void postLocationNotification(Intent intent) {

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(myContext);

		if (sp.getBoolean("vibrate_on_notify", true)) {
			Vibrator vibrator = (Vibrator) myContext.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(pattern, -1);
		}
		// Auslesen der Extras des Intents
		String name = intent.getStringExtra("contactName");
		String number = intent.getStringExtra("number");
		double[] friendLoc = intent.getDoubleArrayExtra("friendLoc");
		double[] ownLoc = intent.getDoubleArrayExtra("ownLoc");

		// FriendLocation erstellen
		Location friendLocation = new Location("friendLocation");
		friendLocation.setLatitude(friendLoc[0]);
		friendLocation.setLongitude(friendLoc[1]);

		// OwnLocation erstellen
		Location ownLocation = new Location("ownLocation");
		ownLocation.setLatitude(ownLoc[0]);
		ownLocation.setLongitude(ownLoc[1]);

		// Entfernung berechnen
		float distance = ownLocation.distanceTo(friendLocation);

		// Titel und Text der Notification zusammenstellen
		String title = String.format(myContext.getString(R.string.Notify_loc_title), name);

		String content = String.format(myContext.getString(R.string.Notify_loc_txt), name, String.valueOf(Math.round(distance)));

		// Erstellen der Action zur Navigation zum Zielort
		Uri gmmIntentUri = Uri.parse("google.navigation:q=" + friendLoc[0] + "," + friendLoc[1] + "&mode=w");
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		PendingIntent openNavigationPendingIntent = PendingIntent.getActivity(myContext, 0, mapIntent, 0);
		Notification.Action actionNavigation = new Notification.Action(android.R.drawable.ic_menu_directions, myContext.getString(R.string.Notification_action_visit), openNavigationPendingIntent);

		// Erstellen der Action zur Nummernwahl
		Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
		PendingIntent openDialerPendingIntent = PendingIntent.getActivity(myContext, 0, callIntent, 0);
		Notification.Action actionDial = new Notification.Action(android.R.drawable.ic_menu_call, myContext.getString(R.string.Notification_action_call), openDialerPendingIntent);

		// Erstellen der Action zum Öffnen der App
		Intent mainIntent = new Intent(myContext, MainActivity.class);
		PendingIntent mainPendingIntent = PendingIntent.getActivity(myContext, 0, mainIntent, 0);


		// Actions funktionieren nur mit SDK > 19
		if (Build.VERSION.SDK_INT >= 20) {
			myNotification = new Notification.Builder(myContext)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(android.R.drawable.ic_menu_mylocation)
					.setContentIntent(mainPendingIntent)
					.setStyle(new Notification.BigTextStyle()
							.bigText(content))
					.addAction(actionDial)
					.addAction(actionNavigation)
					.setAutoCancel(true)
					.setPriority(Notification.PRIORITY_MAX)
					.setVibrate(pattern)
					.build();
		} else {
			myNotification = new Notification.Builder(myContext)
					.setContentTitle(title)
					.setContentText(content)
					.setSmallIcon(android.R.drawable.ic_menu_mylocation)
					.setContentIntent(openNavigationPendingIntent)
					.setStyle(new Notification.BigTextStyle()
							.bigText(content))
					.setAutoCancel(true)
					.build();
		}
		;

		myNotificationManager.notify(MY_NOTIFICATION_ID, myNotification);
	}

	/**
	 * Methode zum Einblenden einer Benachrichtigung weil man sich mal wieder bei einem Freund melden könnte.
	 *
	 * @param contactName String: Name des Kontakts
	 * @param timePassed  String: Zeit seit letztem Kontakt
	 */
	private void reminderNotification(String contactName, String timePassed) {
		//TODO @Lucas Notification erstellen
	}

	/**
	 * Methode zum Ausblenden der Benachrichtigungen
	 */
	private void cancelNotifications() {
		myNotificationManager.cancelAll();
	}

	/**
	 * Methode zur Anzeige von Notifications zu gesendeten SMS
	 * @param recipient String: Name des Empfängers
	 */
	private void sendTextNotification(String recipient) {
		// Daten zur Benachrichtigung
		String title = "Nachricht verschickt";
		String content = "Socretary hat in deinem Namen eine Nachricht an " + recipient + " verschickt.";

		// Intent zum Öffnen der Applikation in der MainActivity
		Intent intentMain = new Intent(myContext, MainActivity.class);
		PendingIntent openMainPendingIntent = PendingIntent.getActivity(myContext, 0, intentMain, 0);

		// Notification zusammenstellen
		//TODO @Lucas Aktionen hinzufügen
		myNotification = new Notification.Builder(myContext).setContentTitle(title).setContentText(content)
				// TODO @Lucas Icon anpassen
				.setSmallIcon(android.R.drawable.ic_dialog_email).setContentIntent(openMainPendingIntent).build();
		myNotificationManager.notify(MY_NOTIFICATION_ID, myNotification);
	}

	/**
	 * Post a notification with the content that you're in the vicinity of a friend's home
	 * @param intent Intent containing the information to post
	 */
	private void postLocationHomeNotification(Intent intent) {
		String title = ApplicationContext.getContext().getString(R.string.Notification_LocationHome_Title);
		String message = intent.getStringExtra("message");
		if (message == null) {
			message = "Lucas Schlemm wohnt in der Nähe";
		}

		if (intent.getBooleanExtra("onlyOne", false)) {
			// Laden des Profilbildes des Kontakt
			DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
			long id = intent.getLongExtra("contactId", 0);
			Contact contact = helper.getContactNameImageById(id);
			Bitmap bmp = contact.getPicture();

			// Erstellen der Action zur Navigation zum Zielort
			Uri gmmIntentUri = Uri.parse("google.navigation:q=" + contact.getLocationHomeLong() + "," + contact.getLocationHomeLat() + "&mode=w");
			Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
			mapIntent.setPackage("com.google.android.apps.maps");
			PendingIntent openNavigationPendingIntent = PendingIntent.getActivity(myContext, 0, mapIntent, 0);
			Notification.Action actionNavigation = new Notification.Action(android.R.drawable.ic_menu_directions, myContext.getString(R.string.Notification_action_visit), openNavigationPendingIntent);

			// Erstellen der Action zur Nummernwahl
			Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contact.getNumber(), null));
			PendingIntent openDialerPendingIntent = PendingIntent.getActivity(myContext, 0, callIntent, 0);
			Notification.Action actionDial = new Notification.Action(android.R.drawable.ic_menu_call, myContext.getString(R.string.Notification_action_call), openDialerPendingIntent);

			// Erstellen der Action zum Öffnen der App
			Intent mainIntent = new Intent(myContext, MainActivity.class);
			PendingIntent mainPendingIntent = PendingIntent.getActivity(myContext, 0, mainIntent, 0);

			// Hinzufügen der Actions, falls SDK es unterstützt
			if (Build.VERSION.SDK_INT >= 20) {
				myNotification = new Notification.Builder(myContext)
						.setContentTitle(title)
						.setContentText(message)
						.setSmallIcon(android.R.drawable.ic_menu_mylocation)
						.setLargeIcon(bmp)
						.setContentIntent(mainPendingIntent)
						.setStyle(new Notification.BigTextStyle()
								.bigText(message))
						.addAction(actionDial)
						.addAction(actionNavigation)
						.setAutoCancel(true)
						.setPriority(Notification.PRIORITY_MAX)
						.setVibrate(pattern)
						.build();
			} else {
				myNotification = new Notification.Builder(myContext)
						.setContentText(message)
						.setContentTitle(title)
						.setLargeIcon(bmp)
						.setContentIntent(openNavigationPendingIntent)
						.setSmallIcon(android.R.drawable.ic_menu_mylocation)
						.setPriority(Notification.PRIORITY_MAX)
						.setAutoCancel(true)
						.setVibrate(pattern)
						.build();
			}

		} else {
			myNotification = new Notification.Builder(myContext)
					.setContentTitle(title)
					.setContentText(message)
					.setPriority(Notification.PRIORITY_MAX)
					.setSmallIcon(android.R.drawable.ic_menu_mylocation)
					.build();
		}
		myNotificationManager.notify(MY_NOTIFICATION_ID, myNotification);
	}
}
