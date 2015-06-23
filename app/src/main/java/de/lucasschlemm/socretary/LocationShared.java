package de.lucasschlemm.socretary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import de.lucasschlemm.socretary.gcm.GcmBroadcastReceiver;


/**
 * Created by jakob.ruf
 */
public class LocationShared
{
	private Intent   mIntent;
	private Location mFriendsLocation;

	public void locationShared(Intent intent)
	{
		String   latitude        = intent.getStringExtra("latitude");
		String   longitude       = intent.getStringExtra("longitude");
		Location friendsLocation = new Location("friendsLocation");
		try
		{
			friendsLocation.setLatitude(Double.parseDouble(latitude));
			friendsLocation.setLongitude(Double.parseDouble(longitude));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		mIntent = intent;
		mFriendsLocation = friendsLocation;


		String                locationProvider = LocationManager.NETWORK_PROVIDER;
		final LocationManager locationManager  = (LocationManager) ApplicationContext.getContext().getSystemService(Context.LOCATION_SERVICE);

		Location lastKnownLocation       = locationManager.getLastKnownLocation(locationProvider);
		long     elapsedRealTimeLocation = lastKnownLocation.getElapsedRealtimeNanos();
		long     elapsedRealTimeSystem   = SystemClock.elapsedRealtime();


		// if last known location is older than 5 minutes, poll
		// else use last known location
		int  minutes     = 5;
		long nanoSeconds = minutes * 60 * 1000 * 1000000;

		if (Math.abs(elapsedRealTimeLocation - elapsedRealTimeSystem) < nanoSeconds)
		{
			Log.d("LocationShared", "locationShared: " + " location is still fresh");
			checkIfCurrentLocationIsInRangeOfFriend(lastKnownLocation);

		}
		else
		{
			Log.d("LocationShared", "locationShared: " + "location is old, polling for new one");
			LocationListener locationListener = new LocationListener()
			{
				@Override
				public void onLocationChanged(Location location)
				{
					locationManager.removeUpdates(this);
					checkIfCurrentLocationIsInRangeOfFriend(location);
				}

				@Override
				public void onStatusChanged(String s, int i, Bundle bundle)
				{
					Log.d("LocationSharer", "onStatusChanged: " + "Status of locationListener changed to " + s);
				}

				@Override
				public void onProviderEnabled(String s)
				{
					Log.d("LocationSharer", "onProviderEnabled: " + "locationListener enabled: " + s);
				}

				@Override
				public void onProviderDisabled(String s)
				{
					Log.d("LocationSharer", "onProviderEnabled: " + "locationListener disabled: " + s);
				}
			};
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
	}


	private void checkIfCurrentLocationIsInRangeOfFriend(Location ownLocation)
	{
		SharedPreferences prefs           = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getContext());
		int               maxDistance     = prefs.getInt(Constants.PREFS.MAX_DISTANCE, 1000);
		Location          friendsLocation = mFriendsLocation;
		Intent            intent          = mIntent;
		String            number          = intent.getStringExtra("number");
		Log.d("LocationShared", "checkIfCurrentLocationIsInRangeOfFriend: " + "ownLocation: " + ownLocation.getLatitude() + " - " + ownLocation.getLongitude());
		Log.d("LocationShared", "checkIfCurrentLocationIsInRangeOfFriend: " + "friendsLocation: " + friendsLocation.getLatitude() + " - " + friendsLocation.getLongitude());
		Log.d("LocationShared", "checkIfCurrentLocationIsInRangeOfFriend: " + String.valueOf(ownLocation.distanceTo(friendsLocation)));
		if (ownLocation.distanceTo(friendsLocation) > maxDistance)
		{
			Log.d("LocationSharer", "checkIfCurrentLocationIsInRangeOfFriend: " + "Friend is too far away. Doing nothing");
			// do nothing since friend is too far away
		}
		else
		{
			Log.d("LocationSharer", "checkIfCurrentLocationIsInRangeOfFriend: " + "Posting notification to encourage meeting");
			DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
			ArrayList<Contact> contacts = helper.getContactListNameNumberId();
			String contactName = "PLATZHALTER";
			Log.d("LocationShared", "checkIfCurrentLocationIsInRangeOfFriend: " + number);
			for (Contact contact : contacts)
			{
				if (contact.getNumber().equals(number))
				{
					contactName = contact.getName();
					break;
				}
			}
			Intent notificationIntent = new Intent();
			notificationIntent.setAction("de.lucasschlemm.CUSTOM_INTENT");
			notificationIntent.putExtra("type", "location");
			notificationIntent.putExtra("contactName", contactName);
			notificationIntent.putExtra("friendLoc", getArrayFromLocation(friendsLocation));
			notificationIntent.putExtra("ownLoc", getArrayFromLocation(ownLocation));
			notificationIntent.putExtra("number", number);
			ApplicationContext.getActivity().sendBroadcast(notificationIntent);
		}
		// send device to sleep
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Methode um ein DoubleArray aus einer Location zu erstellen
	private static double[] getArrayFromLocation(Location loc)
	{
		double[] tempArray = new double[2];
		tempArray[0] = loc.getLatitude();
		tempArray[1] = loc.getLongitude();
		return tempArray;
	}
}
