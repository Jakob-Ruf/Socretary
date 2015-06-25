package de.lucasschlemm.socretary;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {
	public static final String TAG = "GeofenceTransitionsIntentService";

	public GeofenceTransitionsIntentService() {
		super(TAG);
	}



	/**
	 * handles the intent sent by geofence service
	 * @param intent sent by Geofence Service
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Toast.makeText(ApplicationContext.getContext(), "Geofence was triggered", Toast.LENGTH_LONG).show();

		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
		if (geofencingEvent.hasError()){
			Log.e("GeofenceTransitionsInte", "onHandleIntent: " + geofencingEvent.getErrorCode());
			return;
		}
		int geofenceTransition = geofencingEvent.getGeofenceTransition();

		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
			Log.d("GeofenceTransitionsInte", "onHandleIntent: " + "Geofence wurde betreten");

			List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();


			String geofenceNotification = buildNotificationString(triggeringGeofences);
			sendNotification(geofenceNotification);
			Log.d("GeofenceTransitionsInte", "onHandleIntent: " + geofenceNotification);
		} else {
			Log.e("GeofenceTransitionsInte", "onHandleIntent: " + "Invalid transition type");
		}
	}

	private String buildNotificationString(List<Geofence> triggeringGeofences) {
		DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
		String notificationString = "Schau doch mal bei ";
		boolean isFirst = true;


		ArrayList<Contact> contacts = new ArrayList<>();
		for (Geofence geofence: triggeringGeofences){
			try {
				contacts.add(helper.getContact(Long.parseLong(geofence.getRequestId())));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		if (contacts.size() > 1){
			for (Contact contact: contacts){
				if (!isFirst) notificationString += " und ";
				notificationString += contact.getName();
				isFirst = false;
			}
		} else {
			Log.e("GeofenceTransitionsInte", "onHandleIntent: " + "Es wurde kein Kontakt mit der ID gefunden");
		}
		notificationString += " vorbei.";
		return notificationString;
	}

	private void sendNotification(String msg){
		Log.d("GeofenceTransitionsInte", "sendNotification: " + msg);
		Toast.makeText(ApplicationContext.getContext(), msg, Toast.LENGTH_LONG).show();
//		Intent intent = new Intent("de.lucasschlemm.socretary.CUSTOM_INTENT");
//		intent.putExtra("type", "locationHome");
//		ApplicationContext.getContext().sendBroadcast(intent);
	}
}
