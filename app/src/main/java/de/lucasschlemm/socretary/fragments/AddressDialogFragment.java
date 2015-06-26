package de.lucasschlemm.socretary.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.lucasschlemm.socretary.R;

/**
 * Created by lucas.schlemm on 13.05.2015.
 * DialogFragment für die Eingabe einer Adresse
 */
public class AddressDialogFragment extends DialogFragment
{
	// Zuordnung des Logs
	private static final String LOG_CALLER = "AddressDialogFragment";

	// Callbacks einrichten
	private FragmentListener callback;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstance)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_address_dialog, null);

		final EditText eT_street  = (EditText) view.findViewById(R.id.eT_adr_street);
		final EditText eT_number  = (EditText) view.findViewById(R.id.eT_adr_number);
		final EditText eT_code    = (EditText) view.findViewById(R.id.eT_adr_code);
		final EditText eT_city    = (EditText) view.findViewById(R.id.eT_adr_city);
		final EditText eT_country = (EditText) view.findViewById(R.id.eT_adr_country);
		final EditText eT_region  = (EditText) view.findViewById(R.id.eT_adr_region);

		builder.setView(view);
		builder.setMessage(R.string.dialog_address_text);
		builder.setTitle(R.string.dialog_address_title);

		// Bestätigung der Eingabe
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-positiv");
				String[] answer = new String[]{
						eT_street.getText().toString() + " " + eT_number.getText().toString(),
						eT_code.getText().toString(),
						eT_city.getText().toString(),
						eT_country.getText().toString(),
						eT_region.getText().toString(),
						""};
				callback.onAddressDialogPressed(answer);
			}
		});
		// Später die Adresse eingeben
		builder.setNeutralButton(R.string.dialog_skip, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-neutral: " + which);
				callback.onAddressDialogPressed(new String[]{"skip"});
			}
		});
		// Abbrechen der Eingabe
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-negativ: " + which);
				callback.onAddressDialogPressed(new String[]{"abort"});
			}
		});

		return builder.create();
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
			throw new ClassCastException("Activity must implement BirthdayListenerCallback");
		}
	}

	// Entfernen des Callbacks bei Detach
	@Override
	public void onDetach()
	{
		super.onDetach();
		callback = null;
	}

}
