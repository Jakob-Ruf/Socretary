package de.lucasschlemm.socretary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lucas.schlemm on 04.03.2015.
 */
public class MainFragment extends Fragment
{

    // String um Herkunft eines Logeintrages zu definieren
    private static final String LOG_CALLER = "MainFragment";

    private final static int REQUEST_CONTACTPICKER = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        TextView txt = (TextView) v.findViewById(R.id.txt_example);
        String inhalt = "Lucas ";
        String temptxt = "";

        for (int i = 0; i < 300; i++)
        {
            temptxt += inhalt;
        }


        txt.setText(temptxt);
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
            String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            // Neues Kontakt-Objekt erstellen und ID festlegen
            Contact contact = new Contact();
            contact.setId(contactID);
            Log.d(LOG_CALLER, contactID);

            // Namen des Kontaktes auslesen
            String conName = readName(contactUri);
            contact.setName(conName);

            // Telefonnummer des Kontaktes auslesen
            String conNumber = readNumber(contactUri);
            contact.setNumber(conNumber);

            readEvents(Integer.parseInt(contactID));

            // TODO Abfrage ob der Kontakt hinzugefügt werden soll
            Log.d(LOG_CALLER, "Name: " + conName + " - Number: " + conNumber);
           // sendText(conNumber, conName);

            Intent intent = new Intent();
            intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
            intent.putExtra("type", "text");
            intent.putExtra("recipient", conName);
            getActivity().getBaseContext().sendBroadcast(intent);

        }
    }

    private void readEvents(int systemContactId) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE,
                //Event.TYPE,
                ContactsContract.CommonDataKinds.Event.LABEL
        };

        final String filter = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.CONTACT_ID + " = ? ";
        final String parameters[] = {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE, String.valueOf(systemContactId)};

        Cursor cursor =  getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                projection,
                filter,
                parameters,
                null);

        if(cursor.moveToFirst())
        {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                final String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.CONTACT_ID));
                final String startDate = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                //final String type = cursor.getString(cursor.getColumnIndex(Event.TYPE));
                final String label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.LABEL));

                Log.e(LOG_CALLER,contact_id + " - " + startDate + " - " + label);
            }
        }
    }

    // TODO Ablage in gesonderter Acitivty/Sercice
    private void sendText(String phoneNumber, String name)
    {
        String smsContent   = "Test des Telephony SmsManagers.";
        Log.d(LOG_CALLER, phoneNumber + " " + name);
        try
        {
           // SmsManager smsManager = SmsManager.getDefault();
           // smsManager.sendTextMessage(phoneNumber, null, smsContent, null, null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private String readName(Uri contactUri)
    {
        Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
        cursor.moveToFirst();
        int column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String temp = cursor.getString(column);
        cursor.close();
        return temp;
    }

    private String readNumber(Uri contactUri)
    {
        Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
        cursor.moveToFirst();
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String temp = cursor.getString(column);
        cursor.close();
        return temp;
    }


}

