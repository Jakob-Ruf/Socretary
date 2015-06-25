package de.lucasschlemm.socretary;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import net.danlew.android.joda.JodaTimeAndroid;


public class MainActivity extends ActionBarActivity implements FragmentListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "MainActivity";

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private GoogleApiClient mApiClient;

	@Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Orientierung auf Portrait festlegen
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// JodaTime initialisieren
		JodaTimeAndroid.init(this);

		// Ansicht festlegen
		setContentView(R.layout.activity_main);

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		if (savedInstanceState == null) {
			// Laden des MainFragments
			fragmentTransaction.add(R.id.content_frame, MainFragment.getInstance());
			fragmentTransaction.commitAllowingStateLoss();
			//fragmentTransaction.commit();
		}

		// Implementierung der Toolbar
		Toolbar tbToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(tbToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Initialisierung des NavFragments
		NavFragment nfNavDrawer = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer);

		// Festlegen des DrawerLayouts
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

		// Setup des Nav Drawers
		nfNavDrawer.setUp(R.id.nav_drawer, mDrawerLayout, tbToolbar);


		// Setup der Services
		//ServiceStarter services = new ServiceStarter(this);
		//services.startDailyService();

		// Initialisierung des AppicationContexts (möglich, da nur eine Activity verwendet wird)
		ApplicationContext.setContext(this);

		onNewIntent(getIntent());

		mApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.get("fragment") != null) {
				if (extras.get("fragment").equals("CallsFragment")) {
					Fragment fragment;
					fragment = CallsFragment.getInstance();
					fragment.setArguments(extras);


					// Neue Transaktion einleiten
					fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.addToBackStack("CallsFragment");
					fragmentTransaction.replace(R.id.content_frame, fragment);
					// Transaktion durchführen
					fragmentTransaction.commit();


				}
			}
		}

		setIntent(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mApiClient.disconnect();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mApiClient.connect();
	}

	@Override
	protected void onResume() {
		super.onResume();


		// Bei Start der Anwendung werden die Notifications gelöscht
		//Intent intent = new Intent();
		//intent.setAction("de.lucasschlemm.CUSTOM_INTENT");
		//intent.putExtra("type", "cancel_Notification");
		//sendBroadcast(intent);
	}

	@Override
	public void onDialogNeeded(String type) {
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.showNoticeDialog(type);
	}


	@Override
	public void onFrequencyDialogPressed(String[] answer) {
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Frequency", answer);
	}

	@Override
	public void onBirthdayDialogPressed(String[] answer) {
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Birthday", answer);
	}

	@Override
	public void onAddressDialogPressed(String[] answer) {
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Address", answer);
	}

	@Override
	public void onContactDialogNeeded(Contact contact) {
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.contactDialogNeeded(contact);
	}

	/**
	 * Callback vom Navigation Drawer Fragment. Hier wird der Titel der Toolbar angepasst und es wird das entsprechende Fragement geladen
	 *
	 * @param position Id des ausgewählten Eintrags im Nav Drawer
	 */
	@Override
	public void onNavSelected(int position) {
		Fragment fragment;
		setTitle(getResources().getStringArray(R.array.drawer_list)[position]);
		switch (position) {
			case 0:
				setTitle("Socretary");
				// Laden des neues MainFragment
				fragment = MainFragment.getInstance();
				// Neue Transaktion einleiten
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentManager.popBackStack();
				fragmentTransaction.replace(R.id.content_frame, fragment);
				// Transaktion durchführen
				fragmentTransaction.commit();
				break;
			case 1:
				fragment = PrefsFragment.getInstance();
				// Neue Transaktion einleiten
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentManager.popBackStack();
				fragmentTransaction.addToBackStack("Einstellungen");
				fragmentTransaction.replace(R.id.content_frame, fragment);
				// Transaktion durchführen
				fragmentTransaction.commit();
				break;
			case 2:
				fragment = TemplateTextFragment.getInstance();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentManager.popBackStack();
				fragmentTransaction.addToBackStack("SMS-Vorlagen");
				fragmentTransaction.replace(R.id.content_frame, fragment);
				fragmentTransaction.commit();
				break;
		}
	}

	@Override
	public void onContactLongClick(Contact contact) {
		// Setzen des Titels
		setTitle("Kontaktansicht");

		// Laden des neues ContactFragment
		Fragment fragment = new ContactFragment(contact);

		// Neue Transaktion einleiten
		fragmentTransaction = fragmentManager.beginTransaction();

		fragmentTransaction.replace(R.id.content_frame, fragment);
		fragmentTransaction.addToBackStack("Kontaktansicht");

		// Transaktion durchführen
		fragmentTransaction.commit();
	}

	@Override
	public Contact getContactNeeded() {
		ContactFragment fragment = (ContactFragment) fragmentManager.findFragmentById(R.id.content_frame);
		return fragment.getUsedContact();
	}

	@Override
	public void removeContact(Contact contact) {
		fragmentManager.popBackStackImmediate();
		MainFragment.getInstance().removeContact(contact);
	}

	@Override
	public void addEncounter(String[] encounter) {
		ContactFragment fragment = (ContactFragment) fragmentManager.findFragmentById(R.id.content_frame);
		fragment.addEncounter(encounter);
	}

	@Override
	public void reloadContactFragment(Contact contact) {
		// Laden des neues ContactFragment
		Fragment fragment = new ContactFragment(contact);

		fragmentManager.popBackStack();
		// Neue Transaktion einleiten
		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("MainActivity", "onConnected: " + "onCeonnected was called. Is here logic needed?");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("MainActivity", "onConnectionSuspended: " + "is here logic needed?");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e("MainActivity", "onConnectionFailed: " + "do I have to to anything in here?");
	}
}