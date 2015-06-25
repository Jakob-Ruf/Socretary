package de.lucasschlemm.socretary;

import com.google.android.gms.location.Geofence;

/**
 * Created by lucas.schlemm on 25.06.2015.
 */
public class GeofenceBuilder
{

	public static Geofence addGeofence(String id, double[] loc, int radius)
	{
		Geofence.Builder tempBuilder = new Geofence.Builder();
		tempBuilder.setRequestId(id);
		tempBuilder.setCircularRegion(loc[0], loc[1], radius);
		tempBuilder.setExpirationDuration(86400000);
		tempBuilder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);


		return tempBuilder.build();
	}

}
