package de.lucasschlemm.socretary.utils;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Helferklasse welche den Versand von SMS übernimmt.
 * Created by lucas.schlemm on 28.05.2015.
 */
public class TextMessageHelper
{
	public static void sendText(String phoneNumber, String content)
	{
		try
		{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, content, null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.e("TextMessageHelper", "sendText: Zeile: 22: " + e.toString());
		}
	}
}
