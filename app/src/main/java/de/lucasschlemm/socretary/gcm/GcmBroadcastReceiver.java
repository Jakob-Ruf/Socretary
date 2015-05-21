package de.lucasschlemm.socretary.gcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String LOG_CALLER = "GcmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
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
}
