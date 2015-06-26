package de.lucasschlemm.socretary.utils;

public class Constants {
	public static final String BACKEND_URL = "http://personalchef.ddns.net:546/";

	public static final int GEOFENCE_RADIUS_IN_METERS = 1000000;
	public static final String INTENT_NOTIFICATION = "de.lucasschlemm.CUSTOM_INTENT";
	public static final String INTENT_SHARELOCATION = "de.lucasschlemm.socretary.SHARELOCATION";

	public abstract class PREFS {
		public static final String MAX_DISTANCE 	= "max_distance";
		public static final String PHONE_NUMBER 	= "phone_number";
		public static final String REG_ID 			= "registration_id";
		public static final String APP_VERSION 		= "app_version";
	}
}
