package de.lucasschlemm.socretary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Utils
{
	private static final String LOG_CALLER = "Utils";

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
		return bos.toByteArray();
	}

	/**
	 * converts BLOB to bitmap for retrieving from DB
	 *
	 * @param blob byte[]
	 * @return Bitmap of the image
	 */
	public static Bitmap bitmapify(byte[] blob)
	{
		return BitmapFactory.decodeByteArray(blob, 0, blob.length);
	}

	/**
	 * Methode, welche eine Drawable farblich anpasst an die Dauer bis zum nächsten Melden
	 *
	 * @param context     Context
	 * @param lastContact String in Millis
	 * @param frequency   String in Tagen
	 * @return das angepasste Drawable
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
	 *
	 * @param lastContact String in Millis
	 * @param frequency   String in Tagen
	 * @return String
	 */
	public static String getDaysLeft(String lastContact, String frequency)
	{

		if (lastContact == null)
		{
			return "!";
		}
		DateTime lastCon   = new DateTime(Long.valueOf(lastContact));
		int      tempYear  = lastCon.getYear();
		int      tempMonth = lastCon.getMonthOfYear();
		int      tempDay   = lastCon.getDayOfMonth();
		lastCon = new DateTime(tempYear + "-" + tempMonth + "-" + tempDay);

		DateTime today = new DateTime();
		tempYear = today.getYear();
		tempMonth = today.getMonthOfYear();
		tempDay = today.getDayOfMonth();
		today = new DateTime(tempYear + "-" + tempMonth + "-" + tempDay);

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


	public static String normalizeBirthdate(Context context, String birthday)
	{
		JodaTimeAndroid.init(context);
		DateTime      dateTime = new DateTime(birthday);
		DecimalFormat df       = new DecimalFormat("00");

		return dateTime.getYear() + "-" + df.format(dateTime.getMonthOfYear()) + "-" + df.format(dateTime.getDayOfMonth());
	}

	public static void readCallLog(Context context, ArrayList<Contact> contacts)
	{
		ArrayList<String> numbers = new ArrayList<>();
		ArrayList<String> IDs     = new ArrayList<>();

		// Hilfsarrays befüllen
		for (Contact tempCon : contacts)
		{
			// Normalisierung der Nummer
			numbers.add(normalizeNumber(tempCon.getNumber()));
			IDs.add(tempCon.getId());
		}

		Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
		int    number        = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int    type          = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
		int    date          = managedCursor.getColumnIndex(CallLog.Calls.DATE);
		int    duration      = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
		while (managedCursor.moveToNext())
		{
			String phNumber = managedCursor.getString(number);
			String callType = managedCursor.getString(type);
			String tempCallDate = managedCursor.getString(date);
			DateTime callDate = new DateTime(Long.valueOf(tempCallDate));
			String callDuration = managedCursor.getString(duration);
			String dir = null;
			int dircode = Integer.parseInt(callType);
			switch (dircode)
			{
				case CallLog.Calls.OUTGOING_TYPE:
					dir = "OUTGOING";
					break;

				case CallLog.Calls.INCOMING_TYPE:
					dir = "INCOMING";
					break;

				case CallLog.Calls.MISSED_TYPE:
					dir = "MISSED";
					break;
			}
			if (numbers.contains(normalizeNumber(phNumber)))
			{
				Encounter encounter = new Encounter();
				int idHelper = numbers.indexOf(normalizeNumber(phNumber));
				encounter.setPersonId(IDs.get(idHelper));
				encounter.setTimestamp(String.valueOf(callDate.getMillis()));
				encounter.setLength(callDuration);
				encounter.setMeans(DatabaseContract.EncounterEntry.MEANS_PHONE);
				switch (dir)
				{
					case "OUTGOING":
						encounter.setDirection(DatabaseContract.EncounterEntry.DIRECTION_OUTBOUND);
						encounter.setDescription("Ausgehender Anruf + " + phNumber);
						break;
					case "INCOMING":
						encounter.setDirection(DatabaseContract.EncounterEntry.DIRECTION_INBOUND);
						encounter.setDescription("Eingehender Anruf + " + phNumber);
						break;
					default:
						encounter.setDirection(DatabaseContract.EncounterEntry.DIRECTION_INBOUND);
						encounter.setDescription("Eingehender Anruf verpasst + " + phNumber);
						break;
				}
				DatabaseHelper helper = DatabaseHelper.getInstance(context);
				if (helper.insertEncounterAutomated(encounter) != -1)
				{
					Log.d(LOG_CALLER, "Encounter in Datenbank geschrieben");
				}
				else
				{
					Log.e(LOG_CALLER, "Encounter konnte nicht in die Datenbank eingefügt werden");
				}
				Log.d(LOG_CALLER, "TELEFON - Person: " + encounter.getPersonId() + " Time: " + encounter.getTimestamp() + " Means: " + encounter.getMeans() + " Direction: " + encounter.getDirection() + " EncounterID: " + encounter.getEncounterId() + " Beschreibung: " + encounter.getDescription());
			}
		}
		managedCursor.close();
	}

	public static void readSms(Context context, ArrayList<Contact> contacts)
	{
		final String INBOX  = "content://sms/inbox";
		Cursor       cursor = context.getContentResolver().query(Uri.parse(INBOX), null, null, null, null);

		ArrayList<String> IDs     = new ArrayList<>();
		ArrayList<String> numbers = new ArrayList<>();


		int body   = cursor.getColumnIndex(Telephony.Sms.BODY);
		int person = cursor.getColumnIndex(Telephony.Sms.ADDRESS);
		int date   = cursor.getColumnIndex(Telephony.Sms.DATE);

		for (Contact tempCon : contacts)
		{
			numbers.add(normalizeNumber(tempCon.getNumber()));
			IDs.add(tempCon.getId());
		}

		if (cursor.getCount() > 0)
		{
			Log.d(LOG_CALLER, cursor.getCount() + "Eintraege gefunden SMS");
			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				String tempNumber = normalizeNumber(cursor.getString(person));
				if (numbers.contains(tempNumber))
				{
					Encounter encounter = new Encounter();
					String tempSmsDate = cursor.getString(date);
					DateTime smsDate = new DateTime(Long.valueOf(tempSmsDate));

					// SMS Länge
					// int smsLength = cursor.getString(body).toCharArray().length;
					int idHelper = numbers.indexOf(tempNumber);
					encounter.setPersonId(IDs.get(idHelper));
					encounter.setTimestamp(String.valueOf(smsDate.getMillis()));
					encounter.setMeans(DatabaseContract.EncounterEntry.MEANS_MESSENGER);
					encounter.setDescription("Eingehende SMS + " + tempNumber);
					encounter.setDirection(DatabaseContract.EncounterEntry.DIRECTION_INBOUND);

					Log.e(LOG_CALLER, "Person: " + encounter.getPersonId() + " Time: " + encounter.getTimestamp() + " Means: " + encounter.getMeans() + " Direction: " + encounter.getDirection() + " EncounterID: " + encounter.getEncounterId() + " Beschreibung: " + encounter.getDescription());

					DatabaseHelper helper = DatabaseHelper.getInstance(context);
					if (helper.insertEncounterAutomated(encounter) != -1)
					{
						Log.d(LOG_CALLER, "Encounter in Datenbank geschrieben {SMS}");
					}
					else
					{
						Log.e(LOG_CALLER, "Encounter konnte nicht in die Datenbank eingefügt werden {SMS}.");
					}
				}
				cursor.moveToNext();
			}
			Log.d(LOG_CALLER, "Beendet SMS");
		}
		else
		{
			Log.e(LOG_CALLER, "Keine SMS in der INBOX");
		}
	}

	public static String normalizeNumber(String number)
	{
		String beginning = number.substring(0,4);
		String end = number.substring(4, number.length());
		beginning = beginning.replace("+49", "0"); // TODO foreign numbers
		beginning = beginning.replace("0049", "0");
		beginning = beginning.replace("049", "0");
		number = beginning + end;
		number = number.replace(" ", "");
		number = number.replace("/", "");
		return number.replace("-", "");
	}

	/**
	 * this method should optimally be adjusted for the normalization of numbers on foreign SIM cards
	 */


	/**
	 * Optimiert die Höhe eines ListViews so, dass nicht gescrollt werden muss.
	 *
	 * @param listView anzupassende Listview
	 */
	public static void justifyListView(ListView listView)
	{
		ListAdapter adapter = listView.getAdapter();

		if (adapter == null)
		{
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++)
		{
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams par = listView.getLayoutParams();
		par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(par);
		listView.requestLayout();
	}

	public static String hashSHA1(String message)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(message.getBytes("iso-8859-1"), 0, message.length());
			byte[] sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static String convertToHex(byte[] sha1hash)
	{
		StringBuilder buf = new StringBuilder();
		for (byte b : sha1hash)
		{
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do
			{
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String stringifyMessageArray(String[] textMessages)
	{
		boolean first  = true;
		String  result = "";
		if (textMessages == null)
		{
			return "";
		}
		for (String textMessage : textMessages)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				result += ",";
			}
			result += textMessage;
		}
		return result;
	}


}
