package de.lucasschlemm.socretary;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilsGeo{

    private static final String LOG_CALLER = "UtilsGeo";


    public static void geocodeTranslation(Context context, String address, ArrayList<Contact> contacts, int counter){
        double latitude = 0.0;
        double longitude = 0.0;



        Geocoder geoCoder = new Geocoder(context);
        try {
            List<Address> adressList = geoCoder.getFromLocationName(address,1);
            if (adressList.size()>0){
                latitude  = adressList.get(0).getLatitude();
                longitude = adressList.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_CALLER, "ID: "+counter+" - Adresse war nicht eindeutig!");
        }

        Log.i(LOG_CALLER, "LLTest Nr." + counter+" :Lat/Long von '"+address+"': Lat: " + latitude + " // Long: " + longitude);
    }
}
