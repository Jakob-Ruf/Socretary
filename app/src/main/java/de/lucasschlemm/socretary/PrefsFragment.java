package de.lucasschlemm.socretary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PrefsFragment extends Fragment
{

	private static final String LOG_CALLER = "PrefsFragment";

	private static PrefsFragment instance;

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
		//addPreferencesFromResource(R.xml.preferences);
	}

	// Aufbauen der Ansicht
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		return inflater.inflate(R.layout.fragment_prefs, container, false);
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.e(LOG_CALLER, "onViewCreated");
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
