package de.lucasschlemm.socretary.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.lucasschlemm.socretary.ApplicationContext;
import de.lucasschlemm.socretary.Constants;

public class GcmUtils {
	public static final String LOG_CALLER = "GcmUtils";
	// Google Cloud Messaging
	private GoogleCloudMessaging 	gcm;
	private String 					regId;
	private Context 				context;
	private Activity				activity;
	private static final int 		PLAY_SERVICES_RESOLUTION_REQUEST 	= 9000;
	private String 					SENDER_ID							= "990473206247";
	private AtomicInteger 			msgId								= new AtomicInteger();


	public GcmUtils(Context p_context, Activity p_activity){
		this.context = p_context;
		this.activity = p_activity;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	public void storeRegistrationId(Context context, String regId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(LOG_CALLER, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.PREFS.REG_ID, regId);
		editor.putInt(Constants.PREFS.APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	public String getRegistrationId(Context context) {
		final SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		String registrationId = prefs.getString(Constants.PREFS.REG_ID, "");
		try {
			if (registrationId.isEmpty()) {
				Log.i(LOG_CALLER, "Registration not found.");
				return "";
			}
		} catch (NullPointerException e){
			Log.e(LOG_CALLER, "getRegistrationId: registrationId is null");
			e.printStackTrace();
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(Constants.PREFS.APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(LOG_CALLER, "App version changed.");
			return "";
		}
		return registrationId;
	}

	public void register(){
		String regId;
//		de.lucasschlemm.socretary.gcm.GcmUtils gcmUtils = new de.lucasschlemm.socretary.gcm.GcmUtils(ApplicationContext.getContext(), ApplicationContext.getActivity());
		if (checkPlayServices()){
			Log.d(LOG_CALLER, "Play services detected");
			regId = getRegistrationId(ApplicationContext.getContext());
			try {
				if (regId.isEmpty()){
					registerInBackground();
				}
			} catch (NullPointerException e){
				e.printStackTrace();
			}
		} else {
			Log.e(LOG_CALLER, "No Play Services APK detected");
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	public void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;

					sendRegistrationIdToBackend();

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
					Log.e("GcmUtils", "doInBackground: " + msg);
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		}.execute(null, null, null);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
	 * messages to your app. Not needed for this demo since the device sends upstream messages
	 * to a server that echoes back the message using the 'from' address in the message.
	 */
	public void sendRegistrationIdToBackend() {
		String url = Constants.BACKEND_URL + "registrationID";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getActivity());
		String number = prefs.getString(Constants.PREFS.PHONE_NUMBER, "404");

		Log.d("GcmUtils", "sendRegistrationIdToBackend: " + "Sending registration id to backend");

		if (!number.equals("404")){
			try {
				// POST the registration id
				List<NameValuePair> nameValuePairs = new ArrayList<>();
				nameValuePairs.add(new BasicNameValuePair("regId", regId));
				nameValuePairs.add(new BasicNameValuePair("number", number ));
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = httpClient.execute(httpPost);
				if (response.getStatusLine().getStatusCode() == 200){
					Log.d("GcmUtils", "sendRegistrationIdToBackend: " + " Speicherung wurde im Backend erfolgreich durchgeführt");
				} else {
					Log.e("GcmUtils", "sendRegistrationIdToBackend: " + " Es ist ein Fehler bei der Speicherung aufgetreten");
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		} else {
			Log.d("GcmUtils", "sendRegistrationIdToBackend: " + "number empty in preferences");
		}
	}

	public boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.e(LOG_CALLER, "This device is not supported.");
				activity.finish();
			}
			return false;
		}
		return true;
	}
}