package de.lucasschlemm.socretary.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jascha on 20.06.15.
 */

public class UtilsGeo
{

	private static final String LOG_CALLER = "UtilsGeo";


	public static double[] geocodeTranslation(Context context, String address)
	{
		double   latitude  = 0.0;
		double   longitude = 0.0;
		Geocoder geoCoder  = new Geocoder(context);
		try
		{
			List<Address> adressList = geoCoder.getFromLocationName(address, 1);
			if (adressList.size() > 0)
			{
				latitude = adressList.get(0).getLatitude();
				longitude = adressList.get(0).getLongitude();
			}
		} catch (IOException e)
		{
			e.printStackTrace();

		}

		double[] temp = new double[2];
		temp[0] = latitude;
		temp[1] = longitude;
		return temp;

	}

	public static void latLongTranslation(Context context, double latitude, double longitude)
	{
		Geocoder geoCoder = new Geocoder(context);
		try
		{
			List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
			String address = addresses.get(0).getAddressLine(0);
			String plz = addresses.get(0).getPostalCode();
			String city = addresses.get(0).getLocality();
			String country = addresses.get(0).getCountryName();
			Log.i(LOG_CALLER, "Lat/Long: '" + latitude + "/" + longitude + "' ergibt folgende Adresse: " + address + " in " + plz + " " + city + " " + country);
		} catch (IOException e)
		{
			e.printStackTrace();
			Log.e(LOG_CALLER, "Kombination Lat:" + latitude + " / Long: " + longitude + " ergab keine Ergebnisse");
		}
	}
}
