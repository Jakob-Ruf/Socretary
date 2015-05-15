package de.lucasschlemm.socretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;

/**
 * Created by lucas.schlemm on 13.05.2015.
 */
public class BirthdayDialogFragment extends DialogFragment
{
	private static final String LOG_CALLER = "BirthdayDialogFragment";

	private DatePicker dP;

	// Callbacks einrichten
	private FragmentListener callback;

	@Override
	public Dialog onCreateDialog(Bundle savedInstance)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_birthday_dialog, null);

		// NumberPicker initialisieren
		dP = (DatePicker) view.findViewById(R.id.dP_Birthday);

		DateTime dateTime = new DateTime();

		int year = dateTime.getYear();
		int month = dateTime.getMonthOfYear();
		int day = dateTime.getDayOfMonth();

		dP.updateDate(year, month, day);

		builder.setView(view);
		builder.setTitle(R.string.dialog_birthday_title);

		// Bestätigung der Eingabe
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-positiv");
				String[] answer = new String[]{
						String.valueOf(dP.getYear()),
						String.valueOf(dP.getMonth()),
						String.valueOf(dP.getDayOfMonth())};
				callback.onBirthdayDialogPressed(answer);
			}
		});

		// Abbrechen der Eingabe
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "onClick-negativ: " + which);
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
