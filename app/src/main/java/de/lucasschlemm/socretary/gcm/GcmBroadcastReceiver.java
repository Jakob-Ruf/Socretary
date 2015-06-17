package de.lucasschlemm.socretary.gcm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import de.lucasschlemm.socretary.LocationShared;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	private static final String LOG_CALLER = "GcmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("GcmBroadcastReceiver", "onReceive: " + "Es ist eine Push-Benachrichtigung eingegangen.");
		Bundle extras = intent.getExtras();
		Log.d("GcmBroadcastReceiver", "onReceive: " + extras.toString());
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		if (intent.getExtras() != null){
			if (intent.getStringExtra("type") == null || !intent.getStringExtra("type").equals("friendClose")){
				Log.d("GcmBroadcastReceiver", "onReceive: " + "Unknown push message type was registered");
			} else {
				LocationShared locationShared = new LocationShared();
				locationShared.locationShared(intent);
				startWakefulService(context, (intent.setComponent(comp)));
			}
		}
	}
}