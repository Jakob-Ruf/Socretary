package de.lucasschlemm.socretary.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.classes.Contact;
import de.lucasschlemm.socretary.database.DatabaseHelper;
import de.lucasschlemm.socretary.utils.Utils;


public class PrefsFragment extends Fragment
{

	private static final String LOG_CALLER = "PrefsFragment";

	private static PrefsFragment instance;

	private static Switch  sw_vibrate;
	private static Switch  sw_notify;
	private static Spinner sp_distance;

	private static LinearLayout tv_licences;

	private static ArrayList<Contact> contacts;

	// Shared Prefs
	private static final String PREF_VIBRATE = "vibrate_on_notify";
	private static final String PREF_NOTIFY  = "notification";
	private static final String PREF_DIST    = "distance";

	public static PrefsFragment getInstance()
	{
		if (instance == null)
		{
			instance = new PrefsFragment();
		}
		return instance;
	}

	// Erstellen des Fragments
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.e(LOG_CALLER, "onCreate");
	}

	// Aufbauen der Ansicht
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		contacts = DatabaseHelper.getInstance(getActivity()).getContactList();
		return inflater.inflate(R.layout.fragment_prefs, container, false);
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Initialisieren der Elemente
		sw_notify = (Switch) view.findViewById(R.id.switch_notify);
		sw_vibrate = (Switch) view.findViewById(R.id.switch_vibrate);
		sp_distance = (Spinner) view.findViewById(R.id.pref_sp_distance);
		tv_licences = (LinearLayout) view.findViewById(R.id.tv_prefs_licences);
		Button readCalls = (Button) view.findViewById(R.id.pref_btn_calls);
		Button readSms   = (Button) view.findViewById(R.id.pref_btn_sms);

		String[] sp_items = new String[10];
		for (int i = 0; i < sp_items.length; i++)
		{
			sp_items[i] = String.valueOf(100 * (i + 1)) + "  m";
		}
		sp_distance.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sp_items));
		// Einstellungen laden
		loadPrefs();


		// Funktionen vergeben
		sw_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				sp.edit().putBoolean(PREF_NOTIFY, isChecked).apply();
			}
		});

		sw_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				sp.edit().putBoolean(PREF_VIBRATE, isChecked).apply();
			}
		});
		tv_licences.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Hier könnten ihre Lizenzen stehen", Toast.LENGTH_LONG).show(); // TODO Jakob Lizenzen einbauen
			}
		});

		readCalls.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Utils.CallReader callReader = new Utils.CallReader();
				callReader.execute(contacts);
			}
		});

		readSms.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Utils.SmsReader smsReader = new Utils.SmsReader();
				smsReader.execute(contacts);
			}
		});

		sp_distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				sp.edit().putString(PREF_DIST, String.valueOf((position + 1) * 100)).apply();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}


	/**
	 * Methode zum Laden der Einstellungen
	 */
	private void loadPrefs()
	{
		final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if (sp.getBoolean(PREF_NOTIFY, true))
		{
			sw_notify.setChecked(true);
		}
		else
		{
			sw_notify.setChecked(false);
		}

		if (sp.getBoolean(PREF_VIBRATE, true))
		{
			sw_vibrate.setChecked(true);
		}
		else
		{
			sw_vibrate.setChecked(false);
		}
		String tempDist = sp.getString(PREF_DIST, "500");
		sp_distance.setSelection((Integer.valueOf(tempDist) / 100) - 1);

	}

	// Fragment wird wieder aktiv
	@Override
	public void onResume()
	{
		super.onResume();
		Log.e(LOG_CALLER, "onResume");
	}

	@Override
	public void onPause()
	{
		super.onPause();

		// TODO @Jakob: Hier müssen die aktuellen Werte gespeichert werden, falls der Nutzer die Anwendung verlässt

		Log.e(LOG_CALLER, "onPause");
	}
}
