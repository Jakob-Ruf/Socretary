package de.lucasschlemm.socretary;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;


public class MainActivity extends ActionBarActivity implements FragmentListener
{
	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "MainActivity";

	private DrawerLayout mDrawerLayout;
	private NavFragment  nfNavDrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Orientierung auf Portrait festlegen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// JodaTime initialisieren
		JodaTimeAndroid.init(this);

		// Ansicht festlegen
		setContentView(R.layout.activity_main);

		// Laden des MainFragments
		getSupportFragmentManager().beginTransaction().add(R.id.content_frame, new MainFragment()).commit();

		// Implementierung der Toolbar
		Toolbar tbToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tbToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Initialisierung des NavFragments
		nfNavDrawer = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer);

		// Festlegen des DrawerLayouts
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

		// Setup des Nav Drawers
		nfNavDrawer.setUp(R.id.nav_drawer, mDrawerLayout, tbToolbar);
	}

	// TODO Fehlende Standardaktionen hinzufügen

	// TODO Zustände verwalten!!!!


	@Override
	protected void onResume()
	{
		super.onResume();

		// Bei Start der Anwendung werden die Notifications gelöscht
		//Intent intent = new Intent();
		//intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
		//intent.putExtra("type", "cancel_Notification");
		//sendBroadcast(intent);
	}

	@Override
	public void onDialogNeeded(String type)
	{
		MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
		mainFragment.showNoticeDialog(type);
	}

	@Override
	public void onFrequencyDialogPressed(String[] answer)
	{
		MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Frequency", answer);
	}

	@Override
	public void onBirthdayDialogPressed(String[] answer)
	{
		MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Birthday", answer);
	}

	/**
	 * Callback vom Navigation Drawer Fragment. Hier wird der Titel der Toolbar angepasst und es wird das entsprechende Fragement geladen
	 *
	 * @param position Id des ausgewählten Eintrags im Nav Drawer
	 */
	@Override
	public void onNavSelected(int position)
	{
		// TODO Feste Strings auf dynamische Strings ändern.
		// TODO Fragments entsprechend laden.
		switch (position)
		{
			case 0:
				setTitle("Socretary");
				Log.d(LOG_CALLER, "Übersicht"); // TODO Log entfernen
				break;
			case 1:
				setTitle("Statistiken");
				Log.d(LOG_CALLER, "Statistik"); // TODO Log entfernen
				break;
			case 2:
				setTitle("Einstellungen");
				Log.d(LOG_CALLER, "Einstellungen"); // TODO Log entfernen
				break;
		}
	}
}