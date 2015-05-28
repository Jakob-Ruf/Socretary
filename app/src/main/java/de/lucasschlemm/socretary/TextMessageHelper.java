package de.lucasschlemm.socretary;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by lucas.schlemm on 28.05.2015.
 */
public class TextMessageHelper
{
	// TODO Ablage in gesonderter Acitivty/Sercice

	public static void sendText(String phoneNumber, String content)
	{
		String smsContent = content;
		Log.d("TextMessageHelper", "sendText: Zeile: 15: " + smsContent + " " + phoneNumber);
		try
		{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, smsContent, null, null);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
