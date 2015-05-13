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
import android.support.v4.app.DialogFragment;
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
 * Hauptfragment mit der Liste der Kontakte
 */
public class MainFragment extends Fragment
{

	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "MainFragment";

	private final static int REQUEST_CONTACTPICKER = 1;

	private FragmentListener callback;

	private Contact contact;

	private ArrayList<Contact> contacts;

	private ListView listViewContacts;

	private DatabaseHelper dbHelper;

	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		contacts = new ArrayList<>();
		listViewContacts = (ListView) v.findViewById(R.id.lvContacts);
		dbHelper = DatabaseHelper.getInstance(getActivity());
		contacts = dbHelper.getContactList();

		createListView();


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
			contact = new Contact();
			contact.setId(contactID);
			contact.setName(conName);
			contact.setNumber(conNumber);
			if (!checkForDuplicate())
			{
				;
			}
			{
				String lastContact = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
				contact.setLastContact(lastContact);

				boolean readAll = false;
				if (readAll)
				{
					// Ausgabe jeglicher Daten eines Kontakts
					String[] colNames = cursor.getColumnNames();
					String inhalt;
					for (int i = 0; i <= 79; i++)
					{
						inhalt = cursor.getString(cursor.getColumnIndex(colNames[i]));
						Log.e(LOG_CALLER, LOG_CALLER + " Nr." + i + " - " + colNames[i] + " - " + inhalt);
					}
				}

				// Cursor schließen
				cursor.close();

				// Öffnen des FrequencyDialogs
				callback.onDialogNeeded("Frequency");
			}


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

	private void createListView()
	{
		Contact con[] = new Contact[contacts.size()];
		for (Contact tempContact : contacts)
		{
			Log.d(LOG_CALLER, "OnResult: Name " + tempContact.getName() + " - Nummer " + tempContact.getNumber() + " - Geburtstag " + tempContact.getBirthday() + " - Wohnort " + tempContact.getLocationHomeComplete());
			con[contacts.indexOf(tempContact)] = tempContact;
		}


		ContactAdapter adapter = new ContactAdapter(getActivity(), R.layout.listview_item_contac, con);
		listViewContacts.setAdapter(adapter);
	}

	//Liest das volle Bild
	public void openDisplayPhoto(long contactId)
	{
		Uri contactUri      = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
		try
		{
			AssetFileDescriptor fd = getActivity().getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
			contact.setPicture(BitmapFactory.decodeStream(fd.createInputStream()));
			insertContact();
		} catch (IOException e)
		{
			Log.e(LOG_CALLER, "openDisplayPhoto : Foto nicht gefunden, nutze Thumbnail");
			readPicture(Long.toString(contactId));
		}
	}

	private void insertContact()
	{
		contacts.add(contact);
		dbHelper.insertContact(contact);
		createListView();
	}

	//Liest Thumbnails
	private void readPicture(String contactId)
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
				contact.setPicture(bitmap);
			}
		}
		else
		{
			//TODO @Lucas: Abfrage falls kein Foto gefunden wurde.
		}
		currPhoto.close();
		contact.setPicture(null);
		insertContact();
	}


	private void readAdress(String contactID)
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
			contact.setLocationHome(adress);
			openDisplayPhoto(Long.valueOf(contactID));
		}

		else
		{
			//TODO Kein Geburtstag eingespeichert
			Log.e(LOG_CALLER, "readAdress: " + " Es wurde keine Adresse gefunden");
			// Dialog notwendig
			callback.onDialogNeeded("Address");
		}
		// Schließen des Cursors und Rückgabe der Variable
		currAdress.close();

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
	private void readBirthday(String contactID)
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


			// Geburtstag gefunden... Lese jetzt Adresse aus
			readAdress(contactID);
		}
		else
		{
			//TODO Kein Geburtstag eingespeichert
			Log.e(LOG_CALLER, METHOD + " Es wurde kein Geburtstag gefunden");
			callback.onDialogNeeded("Birthday");
			//TODO Geburtstag-Dialog muss sich öffnen
		}
		// Schließen des Cursors und Rückgabe der Variable
		currEvent.close();
	}

	public void showNoticeDialog(String type)
	{
		if (type.equals("Frequency"))
		{
			DialogFragment dialog = new FrequencyDialogFragment();
			dialog.show(getActivity().getSupportFragmentManager(), "FrequencyDialogFragment");
		}
		else if (type.equals("Birthday"))
		{
			DialogFragment dialog = new BirthdayDialogFragment();
			dialog.show(getActivity().getSupportFragmentManager(), "BirthdayDialogFragment");
		}
		else if (type.equals("Address"))
		{
			//TODO....
			//DialogFragment dialog = new AddressDialogFragment();
			//dialog.show(getActivity().getSupportFragmentManager(), "AddressDialogFragment");
		}

	}

	// Setzen des Callbacks bei Attach
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			callback = (FragmentListener) activity;
		} catch (ClassCastException e)
		{
			throw new ClassCastException("Activity must implement MainFragmentCallback");
		}
	}

	// Entfernen des Callbacks bei Detach
	@Override
	public void onDetach()
	{
		super.onDetach();
		callback = null;
	}

	public void dialogAnswer(String type, String[] vals)
	{
		if (type.equals("Frequency"))
		{
			if (vals[0].equals(0))
			{

			}
			else
			{
				contact.setFrequency(vals[1]);
				readBirthday(contact.getId());
			}
		}
		else if (type.equals("Birthday"))
		{
			contact.setBirthday(vals[0] + "-" + vals[1] + "-" + vals[2]);
			readAdress(contact.getId());
		}
		else if (type.equals("Address"))
		{

		}

	}

	private boolean checkForDuplicate()
	{
		boolean conInDB = false;
		for (Contact tempCon : contacts)
		{
			if (tempCon.getName().equals(contact.getName()))
			{
				conInDB = true;
			}
		}
		if (!conInDB)
		{
			Log.e(LOG_CALLER, "MainFragment: Kontakt nicht in der Datenbank");
		}
		else
		{
			Log.e(LOG_CALLER, "MainFragment: Kontakt bereits ind der Datenbank");
		}
		return conInDB;
	}
}

