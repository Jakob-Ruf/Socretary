package de.lucasschlemm.socretary.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String LOG_CALLER = "GcmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GcmBroadcastReceiver", "onReceive: " + "Es ist eine Push-Benachrichtigung eingegangen.");
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
	}
}