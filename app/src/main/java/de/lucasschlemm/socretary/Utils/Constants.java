package de.lucasschlemm.socretary.utils;

import android.graphics.Color;

import de.lucasschlemm.socretary.R;

public class Constants {
	public static final String BACKEND_URL = "http://personalchef.ddns.net:546/";

	public static final int GEOFENCE_RADIUS_IN_METERS = 1000000;
	public static final String INTENT_NOTIFICATION = "de.lucasschlemm.CUSTOM_INTENT";
	public static final String INTENT_SHARELOCATION = "de.lucasschlemm.socretary.SHARELOCATION";

	public abstract class PREFS {
		public static final String MAX_DISTANCE = "max_distance";
		public static final String PHONE_NUMBER = "phone_number";
		public static final String REG_ID = "registration_id";
		public static final String APP_VERSION = "app_version";
	}

	public static final String[] MONTHS_SHORT = {
			ApplicationContext.getContext().getString(R.string.Month_1_short),
			ApplicationContext.getContext().getString(R.string.Month_2_short),
			ApplicationContext.getContext().getString(R.string.Month_3_short),
			ApplicationContext.getContext().getString(R.string.Month_4_short),
			ApplicationContext.getContext().getString(R.string.Month_5_short),
			ApplicationContext.getContext().getString(R.string.Month_6_short),
			ApplicationContext.getContext().getString(R.string.Month_7_short),
			ApplicationContext.getContext().getString(R.string.Month_8_short),
			ApplicationContext.getContext().getString(R.string.Month_9_short),
			ApplicationContext.getContext().getString(R.string.Month_10_short),
			ApplicationContext.getContext().getString(R.string.Month_11_short),
			ApplicationContext.getContext().getString(R.string.Month_12_short)
	};

	public abstract class COLORS{
		public static final int lineChartMain = Color.BLUE;
		public static final int lineChartAccent = Color.RED;
	}

}
