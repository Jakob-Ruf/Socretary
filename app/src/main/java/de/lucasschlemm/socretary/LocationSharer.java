package de.lucasschlemm.socretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LocationSharer extends BroadcastReceiver {

	private String getPostData(Intent intent) {
		HashMap<String, String> params = (HashMap) intent.getSerializableExtra("hashmap");

		StringBuilder result = new StringBuilder();
		boolean first = true;
		try {
			for(Map.Entry<String, String> entry : params.entrySet()){
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}


		return result.toString();
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		DoIt doIt = new DoIt();
		doIt.execute(intent);
	}

	public class DoIt extends AsyncTask<Intent, String, String>{

		@Override
		protected String doInBackground(Intent... intent) {
			String response = "";
			try {
				URL url = new URL(Constants.BACKEND_URL + "testResponse");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

				urlConnection.setRequestMethod("POST");
				urlConnection.setReadTimeout(15000);
				urlConnection.setConnectTimeout(15000);
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);

				OutputStream outputStream = urlConnection.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				writer.write(getPostData(intent[0]));
				writer.flush();
				writer.close();
				outputStream.close();
				int responseCode = urlConnection.getResponseCode();

				if (responseCode == HttpURLConnection.HTTP_OK){
					String line;
					BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
					while ((line= br.readLine()) != null){
						response += line;
					}
				} else {
					throw new HttpException(String.valueOf(responseCode));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("LocationSharer", "onReceive: " + response);
			return null;
		}
	}
}