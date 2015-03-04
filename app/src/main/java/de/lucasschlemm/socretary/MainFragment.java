package de.lucasschlemm.socretary;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        Button btnContact = (Button) v.findViewById(R.id.btn);
        View.OnClickListener btnCL = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Lädt den Intent mit dem ContactPicker
                // TODO Antwort verarbeiten
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACTPICKER);
                Log.d(LOG_CALLER, "Btn pressed");
            }
        };
        btnContact.setOnClickListener(btnCL);
        return v;
    }

}

