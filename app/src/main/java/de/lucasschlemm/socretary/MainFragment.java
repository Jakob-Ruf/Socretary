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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

	private static MainFragment instance;

	public static MainFragment getInstance()
	{
		if (instance == null)
		{
			instance = new MainFragment();
		}
		return instance;
	}


	// Erstes Erstellen des Fragments
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		dbHelper = DatabaseHelper.getInstance(getActivity());
		contacts = new ArrayList<>();
		contacts = dbHelper.getContactList();
		setHasOptionsMenu(false);
		Log.e(LOG_CALLER, "onCreate");
		Utils.readCallLog(getActivity(), contacts);
	}

	// Aufbauen der Ansicht
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.e(LOG_CALLER, "onViewCreated");
		listViewContacts = (ListView) view.findViewById(R.id.lvContacts);
		createListView();
		(view.findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACTPICKER);
			}
		});
		//Utils.readSms(getActivity(), contacts);
	}

	// Fragment wird wieder aktiv
	@Override
	public void onResume()
	{
		super.onResume();
		Log.e(LOG_CALLER, "onResume");
		createListView();
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
		}
	}

	private void createListView()
	{
		Contact con[] = new Contact[contacts.size()];
		for (Contact tempContact : contacts)
		{
			con[contacts.indexOf(tempContact)] = tempContact;
		}

		ContactAdapter adapter = new ContactAdapter(getActivity(), R.layout.listview_item_contact, con);
		listViewContacts.setAdapter(adapter);
		listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Contact localContact = contacts.get(position);
				Log.d(LOG_CALLER, "Kurz geklickt: " + localContact.getName());
				callback.onContactDialogNeeded(localContact);
			}
		});
		listViewContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id)
			{

				Log.d("long clicked", "pos: " + pos);
				callback.onContactLongClick(contacts.get(pos));

				//contacts.remove(pos);
				//createListView();
				return true;
			}
		});
	}

	//Liest das volle Bild
	public void openDisplayPhoto(long contactId)
	{
		Uri contactUri      = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
		try
		{
			AssetFileDescriptor fd = getActivity().getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
			Bitmap bmp = BitmapFactory.decodeStream(fd.createInputStream());
			contact.setPicture(bmp);
			insertContact();
		} catch (IOException e)
		{
			Log.e(LOG_CALLER, "openDisplayPhoto : Foto nicht gefunden, nutze Thumbnail");
			readPicture(contact.getId());
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
				contact.setPicture(bitmap);
			}
		}
		else
		{
			Log.e(LOG_CALLER, "Es wurde kein thumbnail gefunden.");
			//TODO @Lucas: Abfrage falls kein Foto gefunden wurde.
			contact.setPicture(null);
		}
		currPhoto.close();
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

				Log.v(LOG_CALLER, "readAdress " + "Address:- " + street + "," + neighbor + "," + city + "," + region + "," + postCode + "," + country +
						"[" + type + "] " + labelName);
				currAdress.moveToNext();
			}
			contact.setLocationHome(adress);
			openDisplayPhoto(Long.valueOf(contactID));
		}
		else
		{
			// Dialog notwendig
			callback.onDialogNeeded("Address");
		}
		// Schließen des Cursors und Rückgabe der Variable
		currAdress.close();
	}


	/**
	 * Methode zum Auslesen des Namens
	 *
	 * @param contactUri Übergabe des ausgewählten Kontakts
	 * @return String - Mit dem Namen des Kontakts
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
	 * @param contactUri Übergabe des ausgewählte Kontakts
	 * @return String - Mit der Telefonnummer des Kontakts
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
	 * Methode zum Auslesen des Geburtstags eines Kontaktes. Speichert diesen in den aktuellen Kontakt
	 *
	 * @param contactID String - Kontakt-ID der gewünschten Person
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
		String date;

		// Wenn ein Event gefunden wurde, soll dieses ausgelesen werden und dann in die date Variable geschrieben werden.
		if (currEvent.getCount() > 0)
		{
			currEvent.moveToNext();
			int indexEvent = currEvent.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
			date = currEvent.getString(indexEvent);
			Log.v(LOG_CALLER, METHOD + " Event:- " + date);

			contact.setBirthday(date);
			// Geburtstag gefunden... Lese jetzt Adresse aus
			readAdress(contactID);
		}
		else
		{
			callback.onDialogNeeded("Birthday");
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
			DialogFragment dialog = new AddressDialogFragment();
			dialog.show(getActivity().getSupportFragmentManager(), "AddressDialogFragment");
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
		Log.d(LOG_CALLER, type);
		if (type.equals("Frequency"))
		{
			if (!vals[0].equals("0"))
			{
				contact.setFrequency(vals[1]);
				readBirthday(contact.getId());
			}
		}
		else if (type.equals("Birthday"))
		{
			contact.setBirthday(vals[0] + "-" + vals[1] + "-" + vals[2]);
			Log.d(LOG_CALLER, "OnResult: Name " + contact.getName() + " - Nummer " + contact.getNumber() + " - Geburtstag " + contact.getBirthday());
			readAdress(contact.getId());
		}
		else if (type.equals("Address"))
		{
			if (vals[0].equals("abort"))
			{
				Log.d(LOG_CALLER, "OnResult: Hinzufügen abgebrochen");
			}
			else if (vals[0].equals("skip"))
			{
				Log.d(LOG_CALLER, "OnResult: Adresse übersrpungen." + contact.getId());
				openDisplayPhoto(Long.valueOf(contact.getId()));
			}
			else
			{
				contact.setLocationHome(vals);
				openDisplayPhoto(Long.valueOf(contact.getId()));
			}
		}

	}

	/**
	 * Methode zur Prüfung ob der aktuelle Kontakt bereits in der Datenbank gespeichert ist
	 *
	 * @return boolean
	 */
	private boolean checkForDuplicate()
	{
		boolean conInDB = false;
		for (Contact tempCon : contacts)
		{
			if (tempCon.getName().equals(contact.getName()))
			{
				conInDB = true;
				Toast.makeText(getActivity().getBaseContext(), "Der Kontakt ist bereits in deiner Liste", Toast.LENGTH_LONG).show();
			}
		}
		return conInDB;
	}

	public void contactDialogNeeded(Contact localContact)
	{
		DialogFragment dialog = new ContactDialogFragment(localContact);
		dialog.show(getActivity().getSupportFragmentManager(), "AddressDialogFragment");
	}

	public void removeContact(Contact contact)
	{
		contacts.remove(contact);
		dbHelper.deleteContact(Long.valueOf(contact.getId()));
		createListView();
	}
}

