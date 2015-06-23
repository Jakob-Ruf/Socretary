package de.lucasschlemm.socretary;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static Location currentBestLocation;
	private static boolean dialogOpen = false;
	private static boolean confirmSendBool = false;


	@Override
	public void onReceive(Context context, Intent intent) {
		requestLocation();
	}

	protected void requestLocation() {
		final Context context = ApplicationContext.getContext();
		Toast.makeText(context, context.getString(R.string.Location_acquiring_toast), Toast.LENGTH_LONG).show();

		final String locationProvider = LocationManager.GPS_PROVIDER;
		final LocationManager locationManager = (LocationManager) ApplicationContext.getContext().getSystemService(Context.LOCATION_SERVICE);
		final Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		if (isBetterLocation(lastKnownLocation, currentBestLocation)) {
			currentBestLocation = lastKnownLocation;
		}

		final LocationListener locationListener = new LocationListener() {
			Context context = ApplicationContext.getContext();
			LocationListener that = this;

			@Override
			public void onLocationChanged(final Location location) {

				if (isBetterLocation(location, currentBestLocation)) {
					currentBestLocation = location;
				}



				// accuracy is good, send to Backend
				if (currentBestLocation.getAccuracy() > 100) {
					if (dialogOpen){

					}
					AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationContext.getContext());
					builder
							.setNegativeButton(context.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									locationManager.removeUpdates(that);
									dialogOpen = false;
								}
							})
							.setPositiveButton(context.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									locationManager.removeUpdates(that);
									dialogOpen = false;
									confirmSend(currentBestLocation);
								}
							})
							.setNeutralButton(context.getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									dialogOpen = false;
								}
							})
							.setTitle("Wirklich abschicken?")
							.setMessage("Die Genauigkeit des Fixes beträgt " + Math.ceil(currentBestLocation.getAccuracy()) + "m. Willst du diese Position versenden oder es erneut versuchen?")
							.show();
					dialogOpen = true;

				} else {
					// time limit is not exceeded, keep on trying
					locationManager.removeUpdates(that);
					dialogOpen = false;
					confirmSend(currentBestLocation);
				}
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

	private boolean confirmSend(final Location location) {
		dialogOpen = true;
		Context context = ApplicationContext.getContext();
		AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationContext.getContext());
		builder
				.setNegativeButton(context.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						confirmSendBool = false;
						dialogOpen = false;
					}
				})
				.setPositiveButton(context.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
						sendLocationToBackend.execute(location);
						dialogOpen = false;
						confirmSendBool = true;
					}
				})
				.setTitle(context.getString(R.string.Location_confirm_send_title))
				.setMessage(context.getString(R.string.Location_confirm_send_message))
				.setCancelable(false)
				.show();
		return confirmSendBool;
	}


	/**
	 * Determines whether one Location reading is better than the current Location fix
	 *
	 * @param location            The new Location that you want to evaluate
	 * @param currentBestLocation The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		if (location == null){
			Log.e("LocationSharer", "isBetterLocation: " + "die erste Location ist null");
			try {
				throw new Exception("erste Location nicht da");
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
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