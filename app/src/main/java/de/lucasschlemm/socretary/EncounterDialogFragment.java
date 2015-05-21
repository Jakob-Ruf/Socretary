package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by lucas.schlemm on 21.05.2015.
 */
public class EncounterDialogFragment extends DialogFragment implements View.OnClickListener
{

	private EditText eTxtDate;
	private EditText eTxtTime;
	private Spinner  spinnerType;
	private Spinner  spinnerDirection;
	private EditText eTxtLength;

	private String[] datePicked;
	private String[] timePicked;
	private String   lengthPicked;

	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	private static final DecimalFormat df = new DecimalFormat("00");

	private boolean lengthNeeded = true;

	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialogNow;
	private TimePickerDialog timePickerDialogNull;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater                      inflater = getActivity().getLayoutInflater();
		@SuppressLint("InflateParams") View view     = inflater.inflate(R.layout.fragment_encounter_dialog, null);

		// View festlegen und Titel setzen
		builder.setView(view);
		builder.setTitle(getResources().getString(R.string.OptionsAddEncounter));

		// Datum und Uhrzeit bekommen OnClickListener
		eTxtDate = (EditText) view.findViewById(R.id.etEncDialogDate);
		eTxtDate.setOnClickListener(this);
		eTxtTime = (EditText) view.findViewById(R.id.etEncDialogTime);
		eTxtTime.setOnClickListener(this);

		// Aktuelle Uhrzeit auslesen
		DateTime today = DateTime.now();

		// DatePicker vorbereiten
		datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
		{
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				eTxtDate.setText(df.format(dayOfMonth) + "." + df.format(monthOfYear) + "." + year);
				datePicked = new String[]{
						String.valueOf(year),
						String.valueOf(df.format(monthOfYear)),
						String.valueOf(df.format(dayOfMonth))};
			}
		}, today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());

		// TimePickerNow vorbereiten
		timePickerDialogNow = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				eTxtTime.setText(df.format(hourOfDay) + ":" + df.format(minute));
				timePicked = new String[]{
						String.valueOf(hourOfDay),
						String.valueOf(minute)};
			}
		}, today.getHourOfDay(), today.getMinuteOfHour(), true);

		// Spinner für Art des Treffens
		spinnerType = (Spinner) view.findViewById(R.id.spEncDialogType);

		// Arten in eine ArrayList laden
		ArrayList<String> typeStrings = new ArrayList<>();
		typeStrings.add(getActivity().getResources().getString(R.string.Personal));
		typeStrings.add(getActivity().getResources().getString(R.string.Phone));
		typeStrings.add(getActivity().getResources().getString(R.string.Messenger));
		typeStrings.add(getActivity().getResources().getString(R.string.Mail));
		typeStrings.add(getActivity().getResources().getString(R.string.SocialNetwork));

		// Adapter aufbauen
		ArrayAdapter<String> spinnAdapterType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, typeStrings);
		spinnAdapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerType.setAdapter(spinnAdapterType);

		eTxtLength = (EditText) view.findViewById(R.id.etEncDialogLength);
		eTxtLength.setOnClickListener(this);
		spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				switch (position)
				{
					case 0:
						eTxtLength.setEnabled(true);
						lengthNeeded = true;
						break;
					case 1:
						eTxtLength.setEnabled(true);
						lengthNeeded = true;
						break;
					default:
						eTxtLength.setEnabled(false);
						lengthNeeded = false;
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		// TimePickerNull vorbereiten
		timePickerDialogNull = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				eTxtLength.setText(hourOfDay + ":" + df.format(minute));
				lengthPicked = String.valueOf(hourOfDay * 60 * 60 + minute * 60);
			}
		}, 0, 0, true);

		spinnerDirection = (Spinner) view.findViewById(R.id.spEncDialogDirection);
		// Richtungen in eine ArrayList laden
		ArrayList<String> directionStrings = new ArrayList<>();
		directionStrings.add(getActivity().getResources().getString(R.string.Coincidence));
		directionStrings.add(getActivity().getResources().getString(R.string.Inbound));
		directionStrings.add(getActivity().getResources().getString(R.string.Outbund));
		directionStrings.add(getActivity().getResources().getString(R.string.Mutual));
		// Adapter aufbauen
		ArrayAdapter<String> spinnAdapterDirection = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, directionStrings);
		spinnAdapterDirection.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerDirection.setAdapter(spinnAdapterDirection);

		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (eTxtDate.getText().toString().trim().matches(""))
				{
					Toast.makeText(getActivity(), "Du hast kein Datum angegeben", Toast.LENGTH_LONG).show();
					eTxtDate.requestFocus();
				}
				else if (eTxtTime.getText().toString().trim().matches(""))
				{
					Toast.makeText(getActivity(), "Du hast keine Uhrzeit angegeben", Toast.LENGTH_LONG).show();
					eTxtTime.requestFocus();
				}
				else if (lengthNeeded && eTxtLength.getText().toString().trim().matches(""))
				{
					Toast.makeText(getActivity(), "Du hast keine Dauer angegeben", Toast.LENGTH_LONG).show();
					eTxtLength.requestFocus();
				}
				else
				{
					String[] answer = new String[4];
					if (lengthNeeded)
					{
						answer[3] = lengthPicked;
					}
					else
					{
						answer[3] = null;
					}
					DateTime dateTimeHelper = new DateTime(datePicked[0] + "-" + datePicked[1] + "-" + datePicked[2]);
					DateTime result = dateTimeHelper.plusHours(Integer.valueOf(timePicked[0]));
					dateTimeHelper = result.plusMinutes(Integer.valueOf(timePicked[1]));
					answer[0] = String.valueOf(dateTimeHelper.getMillis());
					answer[1] = String.valueOf(spinnerType.getSelectedItemPosition());
					answer[2] = String.valueOf(spinnerDirection.getSelectedItemPosition());
					callback.addEncounter(answer);
				}
			}
		});


		return builder.create();
	}

	@Override
	public void onClick(View v)
	{
		if (v == eTxtDate)
		{
			datePickerDialog.show();
		}
		else if (v == eTxtTime)
		{
			timePickerDialogNow.show();
		}
		else if (v == eTxtLength)
		{
			timePickerDialogNull.show();
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
			throw new ClassCastException("Activity must implement EncounterFragmentCallback");
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
