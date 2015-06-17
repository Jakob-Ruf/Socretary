package de.lucasschlemm.socretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LocationSharer extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		requestLocation();
	}


	protected void requestLocation() {
		final String locationProvider = LocationManager.NETWORK_PROVIDER;
		final LocationManager locationManager = (LocationManager) ApplicationContext.getContext().getSystemService(Context.LOCATION_SERVICE);
		final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		final long elapsedRealTimeLocation = lastKnownLocation.getElapsedRealtimeNanos();
		final long elapsedRealTimeSystem = SystemClock.elapsedRealtime();
		final int minutes = 5;
		final long nanoSeconds = minutes * 60 * 1000 * 1000000;


		// if last known location is older than x minutes, poll for fresh location,
		// else use last known location
		if (Math.abs(elapsedRealTimeLocation - elapsedRealTimeSystem) < nanoSeconds) {
			SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
			sendLocationToBackend.execute(lastKnownLocation);

		} else {

			LocationListener locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					locationManager.removeUpdates(this);
					SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
					sendLocationToBackend.execute(location);
				}

				@Override
				public void onStatusChanged(String s, int i, Bundle bundle) {
					Log.d("LocationSharer", "onStatusChanged: " + "Status of locationListener changed to " + s);
				}

				@Override
				public void onProviderEnabled(String s) {
					Log.d("LocationSharer", "onProviderEnabled: " + "locationListener enabled: " + s);
				}

				@Override
				public void onProviderDisabled(String s) {
					Log.d("LocationSharer", "onProviderEnabled: " + "locationListener disabled: " + s);
				}
			};
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
	}


	private class SendLocationToBackend extends AsyncTask<Location, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(Location... locations) {
			final String url = Constants.BACKEND_URL + "shareLocation";
			final HttpClient httpClient = new DefaultHttpClient();
			final HttpPost httpPost = new HttpPost(url);
			final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getContext());
			final String number = prefs.getString(Constants.PREFS.PHONE_NUMBER, "404");
			String longitude = String.valueOf(locations[0].getLongitude());
			String latitude = String.valueOf(locations[0].getLatitude());

			Log.d("sendLocationToBackend", "doInBackground: " + "sharing location with backend");

			if (!number.equals("404")) {

				try {
					// POST the registration id
					List<NameValuePair> nameValuePairs = new ArrayList<>();
					nameValuePairs.add(new BasicNameValuePair("number", number));
					nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
					nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = httpClient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == 200) {
						Log.d("GcmUtils", "sendRegistrationIdToBackend: " + " Speicherung wurde im Backend erfolgreich durchgeführt");
					} else {
						Log.e("GcmUtils", "sendRegistrationIdToBackend: " + " Es ist ein Fehler bei der Speicherung aufgetreten");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Log.e("SendLocationToBackend", "doInBackground: " + "Nummer konnte nicht aus den Shared Preferences geladen werden");
				Toast.makeText(ApplicationContext.getContext(), ApplicationContext.getActivity().getString(R.string.warning_number_empty), Toast.LENGTH_LONG).show();
			}
			return null;
		}
	}
}