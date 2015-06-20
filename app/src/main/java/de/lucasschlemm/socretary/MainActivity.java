package de.lucasschlemm.socretary;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import de.lucasschlemm.socretary.Services.DailyService;
import de.lucasschlemm.socretary.Services.GeofenceService;
import de.lucasschlemm.socretary.Services.ServiceStarter;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;


public class MainActivity extends ActionBarActivity implements FragmentListener
{
	// String um Herkunft eines Logeintrages zu definieren
	private static final String LOG_CALLER = "MainActivity";

	private DrawerLayout mDrawerLayout;
	private NavFragment  nfNavDrawer;

	private FragmentTabHost tabHost;

	private FragmentManager     fragmentManager;
	private FragmentTransaction fragmentTransaction;


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

		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		if (savedInstanceState == null)
		{
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
		nfNavDrawer = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.nav_drawer);

		// Festlegen des DrawerLayouts
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

		// Setup des Nav Drawers
		nfNavDrawer.setUp(R.id.nav_drawer, mDrawerLayout, tbToolbar);


		// Setup der Services
		ServiceStarter services = new ServiceStarter(this);
		//services.startDailyService();
		//services.startGeofenceService();
		startTestFence();




		// Initialisierung des AppicationContexts (möglich, da nur eine Activity verwendet wird)
		ApplicationContext.setContext(this);

		onNewIntent(getIntent());


	}

	public void startTestFence(){
		// Starten des Testsgeofence

		// Lat Long Wert als Dummy für die Korrdinate
		LatLng lln = new LatLng(5,5);

		// Neuer Geofence: Event soll beim Betreten gefeuert werden,
		// Latitude und Longitude von lln werden übergeben, Radius des Fence 500 Meter,
		// Geofence soll nie ablaufen
		Geofence fence = new Geofence.Builder()
				.setRequestId("Testfence")
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
				.setCircularRegion(lln.latitude,lln.longitude,500)
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.build();

		// In einer Liste werden alle Geofences hinzugefügt, zunächst nur ein einziger.
		// Liste wird erstellt
		ArrayList<Geofence> fencelist = new ArrayList<Geofence>();
		// Und ein Geofence hinzugefügt
		fencelist.add(fence);

		// Gemäß der Anleitung der Library wird ein neuer LocationProdiver erstellt
		ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(this);
		// Es wird ein Intent definiert, der den Service GeofenceService starten soll
		Intent geoIntent = new Intent(this, GeofenceService.class);
		// Ein neuer PendingIntent für den eben definierten Intent wird erstellt
		PendingIntent ptent = PendingIntent.getService(this, 0, geoIntent, 0);
		// Letztendlich wird der geofencingRequest erstellt, die Liste der Fences wird übergeben
		GeofencingRequest geofenceRequest = new GeofencingRequest.Builder().addGeofences(fencelist).build();
		// der Request wird mit dem PendingIntent an den LocationProvider übergeben
		locationProvider.addGeofences(ptent, geofenceRequest);


	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			if (extras.get("fragment") != null){
				if(extras.get("fragment").equals("CallsFragment")){
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
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.showNoticeDialog(type);
	}


	@Override
	public void onFrequencyDialogPressed(String[] answer)
	{
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Frequency", answer);
	}

	@Override
	public void onBirthdayDialogPressed(String[] answer)
	{
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Birthday", answer);
	}

	@Override
	public void onAddressDialogPressed(String[] answer)
	{
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.dialogAnswer("Address", answer);
	}

	@Override
	public void onContactDialogNeeded(Contact contact)
	{
		MainFragment mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.content_frame);
		mainFragment.contactDialogNeeded(contact);
	}

	/**
	 * Callback vom Navigation Drawer Fragment. Hier wird der Titel der Toolbar angepasst und es wird das entsprechende Fragement geladen
	 *
	 * @param position Id des ausgewählten Eintrags im Nav Drawer
	 */
	@Override
	public void onNavSelected(int position)
	{
		// TODO Fragments entsprechend laden.
		Fragment fragment;
		setTitle(getResources().getStringArray(R.array.drawer_list)[position]);
		switch (position)
		{
			case 0:
				setTitle("Socretary");
				// Laden des neues MainFragment
				fragment = MainFragment.getInstance();
				// Neue Transaktion einleiten
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.content_frame, fragment);
				// Transaktion durchführen
				fragmentTransaction.commit();
				break;
			case 1:
				// TODO Statistik implementieren, Log entfernen
				Log.d("MainActivity", "onNavSelected: Zeile: 164: " + "Statistik wurde gewählt");
				break;
			case 2:
				fragment = PrefsFragment.getInstance();
				// Neue Transaktion einleiten
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.addToBackStack("Einstellungen");
				fragmentTransaction.replace(R.id.content_frame, fragment);
				// Transaktion durchführen
				fragmentTransaction.commit();
				break;
			case 3:
				fragment = TemplateTextFragment.getInstance();
				fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.addToBackStack("SMS-Vorlagen");
				fragmentTransaction.replace(R.id.content_frame, fragment);
				fragmentTransaction.commit();
				break;
		}
	}

	@Override
	public void onContactLongClick(Contact contact)
	{
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
	public Contact getContactNeeded()
	{
		ContactFragment fragment = (ContactFragment) fragmentManager.findFragmentById(R.id.content_frame);
		return fragment.getUsedContact();
	}

	@Override
	public void removeContact(Contact contact)
	{
		fragmentManager.popBackStackImmediate();
		MainFragment.getInstance().removeContact(contact);
	}

	@Override
	public void addEncounter(String[] encounter)
	{
		ContactFragment fragment = (ContactFragment) fragmentManager.findFragmentById(R.id.content_frame);
		fragment.addEncounter(encounter);
	}

}