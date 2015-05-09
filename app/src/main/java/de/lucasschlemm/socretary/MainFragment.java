package de.lucasschlemm.socretary;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lucas.schlemm on 04.03.2015.
 */
public class MainFragment extends Fragment
{

	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "MainFragment";

	private final static int REQUEST_CONTACTPICKER = 1;

	private ArrayList<Contact> contacts;

	private ListView listViewContacts;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		contacts = new ArrayList<>();
		listViewContacts = (ListView) v.findViewById(R.id.lvContacts);


		(v.findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACTPICKER);
			}
		});
		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CONTACTPICKER && resultCode == Activity.RESULT_OK)
		{
			// Auslesen der ContactID
			Uri contactUri = data.getData();
			Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
			cursor.moveToFirst();
			String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
			Log.v(LOG_CALLER, LOG_CALLER + " ID:- " + contactID);

			// Namen des Kontaktes auslesen
			String conName = readName(contactUri);

			// Telefonnummer des Kontaktes auslesen
			String conNumber = readNumber(contactUri);

			// Geburtstag auslesen
			String conBDay = readBirthday(contactID);

			// Adresse auslesen
			String[] conAdress = readAdress(contactID);

			Bitmap conPic = openDisplayPhoto(Long.valueOf(contactID));

			boolean readAll = false;
			if (readAll)
			{
				// Ausgabe jeglicher Daten eines Kontakts
				String[] colNames = cursor.getColumnNames();
				String inhalt = "";
				for (int i = 0; i <= 79; i++)
				{
					inhalt = cursor.getString(cursor.getColumnIndex(colNames[i]));
					Log.e(LOG_CALLER, LOG_CALLER + " Nr." + i + " - " + colNames[i] + " - " + inhalt);
				}
			}


			String lastContact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
			Long temp = (Long) Long.valueOf(contactID);
			//Date tempDate = new Date(temp);
			//Log.v(LOG_CALLER, LOG_CALLER + " letzter Kontakt:- " + tempDate.toString());

			cursor.close();


			Contact contact = new Contact();
			contact.setId(contactID);
			contact.setName(conName);
			contact.setNumber(conNumber);
			contact.setBirthday(conBDay);
			contact.setLocationHome(conAdress);
			contact.setPicture(conPic);

			//TODO @Lucas: Kontaktbild speichern oder dynamisches Laden??


			// Ausgabe der ausgewählte Kontakte
			contacts.add(contact);
			Contact con[] = new Contact[contacts.size()];
			for (Contact a : contacts)
			{
				Log.d(LOG_CALLER, "OnResult: Name " + a.getName() + " - Nummer " + a.getNumber() + " - Geburtstag " + a.getBirthday() + " - Wohnort " + a.getLocationHomeComplete());
				con[contacts.indexOf(a)] = a;
			}


			ContactAdapter adapter = new ContactAdapter(getActivity(), R.layout.listview_item_contac, con);
			listViewContacts.setAdapter(adapter);

            /*

            // TODO Abfrage ob der Kontakt hinzugefügt werden soll
            Log.d(LOG_CALLER, "Name: " + conName + " - Number: " + conNumber);
           // sendText(conNumber, conName);


			// Auslösen einer Benachrichtigung
            Intent intent = new Intent();
            intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
            intent.putExtra("type", "text");
            intent.putExtra("recipient", conName);
            getActivity().getBaseContext().sendBroadcast(intent);*/

		}
	}

	//Liest das volle Bild
	public Bitmap openDisplayPhoto(long contactId)
	{
		Uri contactUri      = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
		try
		{
			AssetFileDescriptor fd = getActivity().getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
			return BitmapFactory.decodeStream(fd.createInputStream());
		} catch (IOException e)
		{
			Log.e(LOG_CALLER, "openDisplayPhoto : Foto nicht gefunden, nutze Thumbnail");
			return readPicture(Long.toString(contactId));
		}
	}

	//Liest Thumbnails
	private Bitmap readPicture(String contactId)
	{
		Uri URI_PHOTO = ContactsContract.Data.CONTENT_URI;

		String SELECTION_PHOTO = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
		String[] SELECTION_ARRAY_PHOTO = new String[]{
				contactId,
				ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};

		Cursor currPhoto  = getActivity().getContentResolver().query(URI_PHOTO, null, SELECTION_PHOTO, SELECTION_ARRAY_PHOTO, null);
		int    indexPhoto = currPhoto.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);


		if (currPhoto.getCount() > 0)
		{
			currPhoto.moveToFirst();
			byte[] photoByte = currPhoto.getBlob(indexPhoto);
			if (photoByte != null)
			{
				Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
				currPhoto.close();
				return bitmap;
			}
		}
		else
		{
			//TODO @Lucas: Abfrage falls kein Foto gefunden wurde.
		}
		currPhoto.close();
		return null;
	}


	private String[] readAdress(String contactID)
	{
		String[] adress = new String[6];

		Uri uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;

		// Projektion
		String[] projection = null;

		// Where Bedingung
		String where = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ? AND " +
				ContactsContract.CommonDataKinds.StructuredPostal.MIMETYPE + " = ?";

		// Selektionsargumente
		String[] selectionArgs = new String[]{
				contactID,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

		// Sortierreihenfolge
		String sortOrder = null;

		// Erstellen des Cursors
		Cursor currAdress = getActivity().getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);


		// Wenn ein Event gefunden wurde, soll dieses ausgelesen werden und dann in die date Variable geschrieben werden.
		if (currAdress.getCount() > 0)
		{
			currAdress.moveToFirst();
			int indexAddType = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
			int indexAddLabel = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL);
			int indexStreet = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
			int indexNeighbor = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD);
			int indexCity = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY);
			int indexRegion = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION);
			int indexPostCode = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
			int indexCountry = currAdress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
			while (!currAdress.isAfterLast())
			{
				String street = currAdress.getString(indexStreet);
				String neighbor = currAdress.getString(indexNeighbor);
				String city = currAdress.getString(indexCity);
				String region = currAdress.getString(indexRegion);
				String postCode = currAdress.getString(indexPostCode);
				String country = currAdress.getString(indexCountry);
				int type = currAdress.getInt(indexAddType);
				String labelName = "";
				if (type == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
				{
					labelName = currAdress.getString(indexAddLabel);
				}
				adress[0] = street;
				adress[1] = postCode;
				adress[2] = city;
				adress[3] = country;
				adress[4] = region;
				adress[5] = neighbor;

				Log.v(LOG_CALLER, "readAdress " + "Address:- " + street + "," + "," + neighbor + "," + city + "," + region + "," + postCode + "," + country +
						"[" + type + "] " + labelName);
				currAdress.moveToNext();
			}
		}

		else
		{
			//TODO Kein Geburtstag eingespeichert
			Log.e(LOG_CALLER, "readAdress: " + " Es wurde keine Adresse gefunden");
		}
		// Schließen des Cursors und Rückgabe der Variable
		currAdress.close();

		return adress;
	}


	// TODO Ablage in gesonderter Acitivty/Sercice

	private void sendText(String phoneNumber, String name)
	{
		String smsContent = "Test des Telephony SmsManagers.";
		Log.d(LOG_CALLER, phoneNumber + " " + name);
		try
		{
			// SmsManager smsManager = SmsManager.getDefault();
			// smsManager.sendTextMessage(phoneNumber, null, smsContent, null, null);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Methode zum Auslesen des Namens
	 *
	 * @param contactUri
	 * @return
	 */
	private String readName(Uri contactUri)
	{
		Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
		cursor.moveToFirst();
		int    column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		String temp   = cursor.getString(column);
		cursor.close();
		return temp;
	}

	/**
	 * Methode zum Auslesen der Telefonnummer
	 *
	 * @param contactUri
	 * @return
	 */
	private String readNumber(Uri contactUri)
	{
		Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
		cursor.moveToFirst();
		int    column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
		String temp   = cursor.getString(column);
		cursor.close();
		return temp;
	}

	/**
	 * Methode zum Auslesen des Geburtstags eines Kontaktes.
	 *
	 * @param contactID String - Kontakt-ID der gewünschten Person
	 * @return Gibt den Geburtstag, bzw. einen leeren String zurück
	 */
	private String readBirthday(String contactID)
	{
		// Zur leichteren Zuordnung
		String METHOD = "readBirthday";

		Uri uri = ContactsContract.Data.CONTENT_URI;

		// Projektion der abgefragten Daten
		String[] projection = new String[]{
				ContactsContract.Data.CONTACT_ID,
				ContactsContract.CommonDataKinds.Event.START_DATE,
				ContactsContract.Data.MIMETYPE,
				ContactsContract.CommonDataKinds.Event.TYPE};

		// Where Bedingung
		String where = ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?" + " AND " + ContactsContract.CommonDataKinds.Event.TYPE + "=?";

		// Selektionsargumente
		String[] selectionArgs = new String[]{
				contactID,
				ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE,
				String.valueOf(ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)};

		// Sortierreihenfolge
		String sortOrder = null;

		// Erstellen des Cursors
		Cursor currEvent = getActivity().getContentResolver().query(uri, projection, where, selectionArgs, sortOrder);

		// Temporäre Stringvariable welche später zurückgegeben wird.
		String date = "";

		// Wenn ein Event gefunden wurde, soll dieses ausgelesen werden und dann in die date Variable geschrieben werden.
		if (currEvent.getCount() > 0)
		{
			currEvent.moveToNext();
			int indexEvent = currEvent.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
			date = currEvent.getString(indexEvent);
			Log.v(LOG_CALLER, METHOD + " Event:- " + date);
		}
		else
		{
			//TODO Kein Geburtstag eingespeichert
			Log.e(LOG_CALLER, METHOD + " Es wurde kein Geburtstag gefunden");
		}
		// Schließen des Cursors und Rückgabe der Variable
		currEvent.close();
		return date;
	}
}

