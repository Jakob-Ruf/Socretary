package de.lucasschlemm.socretary;

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
import android.widget.NumberPicker;

/**
 * Numberpicker Dialog zur Wahl der Frequenz des Kontakts
 * Created by lucas.schlemm on 13.05.2015.
 */
public class FrequencyDialogFragment extends DialogFragment
{
	private static final String LOG_CALLER = "FrequencyDialogFragment";

	private NumberPicker nP;

	// Callbacks einrichten
	private FragmentListener callback;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstance)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_frequency_dialog, null);

		// NumberPicker initialisieren
		nP = (NumberPicker) view.findViewById(R.id.nP_frequency);
		nP.setMaxValue(99);
		nP.setMinValue(1);
		nP.setValue(14);

		builder.setView(view);
		builder.setMessage(R.string.dialog_frequency_text);
		builder.setTitle(R.string.dialog_frequency_title);

		// Bestätigung der Eingabe
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-positiv");
				String[] answer = new String[]{
						"1",
						String.valueOf(nP.getValue())};
				callback.onFrequencyDialogPressed(answer);
			}
		});

		// Abbrechen der Eingabe
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-negativ: " + which);
				String[] answer = new String[]{
						"0",
						"0"};
				callback.onFrequencyDialogPressed(answer);
			}
		});

		return builder.create();
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try
		{
			// Instantiate the NoticeDialogListener so we can send events to the host
			callback = (FragmentListener) activity;
		} catch (ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}


}
