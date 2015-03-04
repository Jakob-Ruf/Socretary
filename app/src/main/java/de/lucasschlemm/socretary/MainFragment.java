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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            contact.setId(Integer.parseInt(contactID));

            // Namen des Kontaktes auslesen
            String conName = readName(contactUri);
            contact.setName(conName);

            // Telefonnummer des Kontaktes auslesen
            String conNumber = readNumber(contactUri);
            contact.setNumber(conNumber);

            // TODO Abfrage ob der Kontakt hinzugefügt werden soll
            //        Log.d(LOG_CALLER, "Name: " + conName + " - Number: " + conNumber + " - Birthday: " + conBirthD);
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

