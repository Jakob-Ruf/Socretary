package de.lucasschlemm.socretary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.ByteArrayOutputStream;

public class Utils
{

	/**
	 * Methode für das Einfügen eines Zeitstempels in der Datenbank
	 *
	 * @return aktuelle Zeit in ms
	 */
	public static long getCurrentTime()
	{

		return System.currentTimeMillis();
	}

	/**
	 * converts bitmap to BLOB for storing in DB
	 *
	 * @param picture Bitmap
	 * @return BLOB of picture
	 */
	public static byte[] blobify(Bitmap picture)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		picture.compress(Bitmap.CompressFormat.PNG, 100, bos);
		byte[] bArray = bos.toByteArray();
		return bArray;
	}

	/**
	 * converts BLOB to bitmap for retrieving from DB
	 *
	 * @param blob byte[]
	 * @return Bitmap of the image
	 */
	public static Bitmap bitmapify(byte[] blob)
	{
		Bitmap bm = BitmapFactory.decodeByteArray(blob, 0, blob.length);
		return bm;
	}

	/**
	 * Methode, welche eine Drawable farblich anpasst an die Dauer bis zum nächsten Melden
	 * @param context Context
	 * @param lastContact String in Millis
	 * @param frequency String in Tagen
	 * @return
	 */
	public static Drawable getNextContactBG(Context context, String lastContact, String frequency)
	{
		Drawable tempDrawable = context.getResources().getDrawable(R.drawable.btn_info);
		assert tempDrawable != null;

		if (lastContact == null)
		{
			tempDrawable.setColorFilter(Color.argb(250, 153, 0, 0), PorterDuff.Mode.SRC_ATOP);
			return tempDrawable;
		}
		else
		{
			DateTime lastCon = new DateTime(Long.valueOf(lastContact));
			DateTime today = new DateTime();
			Days diff = Days.daysBetween(lastCon, today);
			Double quote = diff.getDays() / Double.valueOf(frequency);
			if (quote <= 0.2)
			{
				tempDrawable.setColorFilter(Color.argb(150, 51, 102, 51), PorterDuff.Mode.SRC_ATOP);
			}
			else if (quote > 0.2 && quote <= 0.4)
			{
				tempDrawable.setColorFilter(Color.argb(150, 153, 204, 0), PorterDuff.Mode.SRC_ATOP);
			}
			else if (quote > 0.4 && quote <= 0.6)
			{
				tempDrawable.setColorFilter(Color.argb(150, 255, 204, 51), PorterDuff.Mode.SRC_ATOP);
			}
			else if (quote > 0.6 && quote <= 0.8)
			{
				tempDrawable.setColorFilter(Color.argb(150, 255, 153, 0), PorterDuff.Mode.SRC_ATOP);
			}
			else if (quote > 0.8)
			{
				tempDrawable.setColorFilter(Color.argb(250, 153, 0, 0), PorterDuff.Mode.SRC_ATOP);
			}
		}
		return tempDrawable;
	}

	/**
	 * Liefert den anzuzeigenden Wert, in wievielen Tagen man sich wieder melden muss.
	 * Ist das Limit schon überschritten, so wird ein Ausrufezeichen geliefert.
	 * @param lastContact String in Millis
	 * @param frequency String in Tagen
	 * @return String
	 */
	public static String getDaysLeft(String lastContact, String frequency)
	{

		if (lastContact == null)
		{
			return "!";
		}
		DateTime lastCon = new DateTime(Long.valueOf(lastContact));
		DateTime today = new DateTime();
		Days diff = Days.daysBetween(lastCon, today);
		Days left = diff.minus(Days.days(Integer.valueOf(frequency)));

		if (left.getDays() * -1 <= 0)
		{
			return "!";
		}
		else
		{
			return String.valueOf(left.getDays() * -1);
		}
	}


}
