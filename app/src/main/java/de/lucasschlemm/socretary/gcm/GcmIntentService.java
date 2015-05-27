package de.lucasschlemm.socretary.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	static final String TAG = "GcmIntentService";
	public static final int NOTIFICATION_ID = 1;
	private static final String LOG_CALLER = "GcmIntentService";

	public GcmIntentService(){
		super("GcmIntentService");

	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("GcmIntentService", "onHandleIntent: " + "Received an intent");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle

			// Pruefung ob Fehlernachricht
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// This loop represents the service doing some work.

			}
			switch (intent.getStringExtra("type")){
				case "reminder":
					Log.d(LOG_CALLER, "Es ist eine Erinnerung eingegangen");
					break;
				case "friendClose":
					Log.d(LOG_CALLER, "Du befindest Dich in der Nähe eines Freundes");
					break;
				case "atFriend":
					Log.d(LOG_CALLER, "Du befindest Dich in der Nähe der Wohnung eines Freunde");
					break;
				case "test":
					Log.d(LOG_CALLER, "Dies ist eine Test-Benachrichtigung");
					break;
				default:
					Log.d(LOG_CALLER, "Unbekannter Push-Typ");
					break;
			}
		}
		// wakeLock entfernen, sodass das Handy wieder schlafen gehen kann
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}