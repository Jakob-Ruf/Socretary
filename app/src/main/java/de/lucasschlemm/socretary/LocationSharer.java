package de.lucasschlemm.socretary;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
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
    private boolean dialogOpen = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(ApplicationContext.getContext(), "Standort wird ermittelt", Toast.LENGTH_LONG).show();
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

        final long fixTimeStart = SystemClock.elapsedRealtime();


        // if last known location is older than x minutes, poll for fresh location,
        // else use last known location
        if (Math.abs(elapsedRealTimeLocation - elapsedRealTimeSystem) < nanoSeconds && lastKnownLocation.getAccuracy() < 200) {

            SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
            sendLocationToBackend.execute(lastKnownLocation);

        } else {
            Log.d("LocationSharer: ", "Location too old or not accurate enough");

            final LocationListener locationListener = new LocationListener() {
                LocationListener that = this;

                @Override
                public void onLocationChanged(final Location location) {
                    Log.d("LocationSharer: ", location.toString());
                    long fixTimeEnd = SystemClock.elapsedRealtime();

                    // Accuracy bad
                    if (location.getAccuracy() > 400) {
                        if ((fixTimeEnd - fixTimeStart) > 5000 && !dialogOpen){
                            Log.d("LocationSharer: ", "Accuracy not the best but time's up over bloah! Snap back to reality!");
                            AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationContext.getContext());
                            builder.setMessage("Genauigkeit beträgt " + location.getAccuracy() + " Meter. Willst du diese Position verschicken oder es erneut versuchen?");
                            builder.setCancelable(false);
                            builder.setPositiveButton(ApplicationContext.getActivity().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    locationManager.removeUpdates(that);
                                    SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
                                    sendLocationToBackend.execute(location);
                                    dialogOpen = false;
                                }
                            });
                            builder.setNegativeButton(ApplicationContext.getActivity().getString(R.string.dialog_deny_cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    locationManager.removeUpdates(that);
                                    dialogOpen = false;
                                }
                            });
                            builder.setNeutralButton(ApplicationContext.getActivity().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialogOpen = false;
                                }
                            });

                            AlertDialog alert = builder.create();
                            dialogOpen = true;
                            alert.show();
                        } else {
                            // do nothing because time's not up
                            Log.d("LocationSharer: ", "Acquired Location. Accuracy sucks. Retrying");
                        }
                    } else {
                        Log.d("LocationSharer: ", "Accuracy is sufficient. Submitting to backend");
                        locationManager.removeUpdates(that);
                        SendLocationToBackend sendLocationToBackend = new SendLocationToBackend();
                        sendLocationToBackend.execute(location);
                    }
                } // onLocationChanged


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
            String accuracy = String.valueOf(locations[0].getAccuracy());

            Log.d("sendLocationToBackend", "doInBackground: " + "sharing location with backend");

            if (!number.equals("404")) {

                try {
                    // POST the registration id
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("number", number));
                    nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
                    nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
                    nameValuePairs.add(new BasicNameValuePair("accuracy", accuracy));
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