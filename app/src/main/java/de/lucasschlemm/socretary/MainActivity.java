package de.lucasschlemm.socretary;

import android.app.PendingIntent;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

import de.lucasschlemm.socretary.classes.Contact;
import de.lucasschlemm.socretary.database.DatabaseHelper;
import de.lucasschlemm.socretary.fragments.CallsFragment;
import de.lucasschlemm.socretary.fragments.ContactFragment;
import de.lucasschlemm.socretary.fragments.FragmentListener;
import de.lucasschlemm.socretary.fragments.MainFragment;
import de.lucasschlemm.socretary.fragments.NavFragment;
import de.lucasschlemm.socretary.fragments.PrefsFragment;
import de.lucasschlemm.socretary.fragments.StatsFragment;
import de.lucasschlemm.socretary.fragments.TemplateTextFragment;
import de.lucasschlemm.socretary.geofences.GeofenceBuilder;
import de.lucasschlemm.socretary.geofences.GeofenceTransitionsIntentService;
import de.lucasschlemm.socretary.utils.ApplicationContext;


public class MainActivity extends ActionBarActivity implements FragmentListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
	private PendingIntent mGeofencePendingIntent;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private ArrayList<Geofence> geofences;
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

		// Initialisierung des AppicationContexts (möglich, da nur eine Activity verwendet wird)
		ApplicationContext.setContext(this);

		onNewIntent(getIntent());

		// Intent für Geofences initialisieren
		mGeofencePendingIntent = null;

		// Verbindung zum GoogleApiClient aufbauen
		mApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}

	// Aufbauen der Geofences
	private GeofencingRequest getGeofencingRequest() {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
		if (!geofences.isEmpty()) {
			builder.addGeofences(geofences);
			return builder.build();
		} else {
			return null;
		}
	}

	private void isConnected() {
		if (!mApiClient.isConnected()) {
			Log.d("MainActivity", "isConnected: " + "Noch nicht verbunden");
		}

		try {
			LocationServices.GeofencingApi.addGeofences(mApiClient, getGeofencingRequest(), getGeofencePendingIntent()).setResultCallback(this);

		} catch (SecurityException securityException) {
			securityException.printStackTrace();
			Log.d("MainActivity", "isConnected: Zeile: 126: " + "SecurityException");
		}
	}

	private PendingIntent getGeofencePendingIntent() {
		// Reuse the PendingIntent if we already have it.
		if (mGeofencePendingIntent != null) {
			return mGeofencePendingIntent;
		}
		Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
		// addGeofences() and removeGeofences().
		return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
			case 3:
				fragment = StatsFragment.getInstance();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentManager.popBackStack();
				fragmentTransaction.addToBackStack("Statistiken");
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
	public void loadGeofences(int dist) {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
		ArrayList<Contact> contacts = databaseHelper.getContactList();

		geofences = new ArrayList<>();

		GeofenceBuilder gb = new GeofenceBuilder();
		for (Contact contact : contacts) {

			double[] tempLoc = {
					contact.getLocationHomeLat(),
					contact.getLocationHomeLong()};
			geofences.add(gb.addGeofence(contact.getId(), tempLoc, dist));

		}
		isConnected();
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

	@Override
	public void onResult(Status status) {

	}
}