package de.lucasschlemm.socretary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

/**
 * Created by jakob.ruf on 17.06.2015.
 */
public class ContactInserter extends AsyncTask<String, String, String>{

	@Override
	protected String doInBackground(String... strings) {
		Context context = ApplicationContext.getContext();
		if (strings.length != 0){
			String url = Constants.BACKEND_URL + "contactInserted";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getContext());

			String ownNumber = prefs.getString(Constants.PREFS.PHONE_NUMBER, "404");
			String friendsNumber = strings[0];

			if (!ownNumber.equals("404")){
				try {
					// POST the registration id
					List<NameValuePair> nameValuePairs = new ArrayList<>();
					nameValuePairs.add(new BasicNameValuePair("friendsNumber", friendsNumber));
					nameValuePairs.add(new BasicNameValuePair("ownNumber", ownNumber));
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = httpClient.execute(httpPost);

					if (response.getStatusLine().getStatusCode() == 200){
						Log.d("GcmUtils", "sendRegistrationIdToBackend: " + " Speicherung wurde im Backend erfolgreich durchgeführt");
						Toast.makeText(context, context.getString(R.string.Location_saved_success), Toast.LENGTH_LONG ).show();
					} else {
						Log.e("GcmUtils", "sendRegistrationIdToBackend: " + " Es ist ein Fehler bei der Speicherung aufgetreten");
						Toast.makeText(context, context.getString(R.string.Location_saved_error), Toast.LENGTH_LONG ).show();
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			} else {
				Log.d("ContactInserter", "doInBackground: " + ownNumber);
			}
		}
		return null;
	}
}
