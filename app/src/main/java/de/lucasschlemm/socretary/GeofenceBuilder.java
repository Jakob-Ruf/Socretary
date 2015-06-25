package de.lucasschlemm.socretary;

import android.util.Log;

import com.google.android.gms.location.Geofence;

/**
 * Created by lucas.schlemm on 25.06.2015.
 */
public class GeofenceBuilder
{
	private static Geofence.Builder builder;

	public GeofenceBuilder()
	{
		this.builder = new Geofence.Builder();
	}

	public static boolean addGeofence(String id, double[] loc, int radius)
	{
		try
		{
			builder.setRequestId(id);
			builder.setCircularRegion(loc[0], loc[1], radius);
			builder.setExpirationDuration(86400000);
			builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
			builder.build();
			return true;
		} catch (Exception e)
		{
			Log.d("GeofenceBuilder", "addGeofence: Zeile: 30: " + e.getStackTrace());
			return false;
		}
	}

}
