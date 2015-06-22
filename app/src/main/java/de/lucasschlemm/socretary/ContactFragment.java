package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Fragment welches die Details eines einzelnen Kontakt anzeigt.
 * Erscheint bei langem Klick auf ein Kontakt in der Kontaktliste
 * Created by lucas.schlemm on 15.05.2015.
 */
@SuppressLint("ValidFragment")
public class ContactFragment extends Fragment
{
	// Zu nutzender Kontakt
	private Contact contact;

	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	@SuppressLint("ValidFragment")
	public ContactFragment(Contact contact)
	{
		this.contact = contact;
	}

	// Erstes Erstellen des Fragments
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// OptionsMenu anzeigen
		setHasOptionsMenu(true);
	}

	// Aufbauen der Ansicht
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

		// Initialisieren vom FragmentTabHost
		FragmentTabHost tabHost;
		tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
		tabHost.setup(getActivity().getApplicationContext(), getChildFragmentManager(), R.id.realtabcontent);

		// Einfügen der einzelnen Tabs
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(getActivity().getResources().getString(R.string.TabDetails)), FragmentTabDetails.class, null);
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(getActivity().getResources().getString(R.string.TabHistory)), FragmentTabHistory.class, null);

		return rootView;
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Bild und Name des Kontaktes anzeigen
		ImageView imageViewContact    = (ImageView) view.findViewById(R.id.iV_contact);
		TextView  textViewContactName = (TextView) view.findViewById(R.id.tV_contact_Name);
		imageViewContact.setImageBitmap(contact.getPicture());
		textViewContactName.setText(contact.getName());
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
			throw new ClassCastException("Activity must implement ContactFragmentCallback");
		}
	}

	// Entfernen des Callbacks bei Detach
	@Override
	public void onDetach()
	{
		super.onDetach();
		callback = null;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
		// OptionsMenu befüllen
		menu.add(getActivity().getResources().getString(R.string.OptionsAddEncounter));
		menu.add(getActivity().getResources().getString(R.string.OptionsRemoveContact));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Löschen des Kontakts mit Abfrage
		if (item.toString().equals(getActivity().getResources().getString(R.string.OptionsRemoveContact)))
		{
			DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					switch (which)
					{
						case DialogInterface.BUTTON_POSITIVE:
							callback.removeContact(contact);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							break;
					}
				}
			};
			// Aufbauen des AlertDialog zur Bestätigung des Löschens
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setMessage(getActivity().getResources().getString(R.string.AreYouSure));
			dialogBuilder.setTitle(getActivity().getResources().getString(R.string.OptionsRemoveContact));
			dialogBuilder.setPositiveButton(android.R.string.yes, dialogListener);
			dialogBuilder.setNegativeButton(android.R.string.cancel, dialogListener);
			dialogBuilder.show();
		}
		else if (item.toString().equals(getActivity().getResources().getString(R.string.OptionsAddEncounter)))
		{
			// Encounter-Dialog zur Anzeige bringen
			DialogFragment dialog = new EncounterDialogFragment();
			dialog.show(getActivity().getSupportFragmentManager(), "EncounterDialogFragment");
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Methode wird genutzt um den aktuellen Kontakt an die darunterliegenden Tabs zu übergeben.
	 *
	 * @return Gibt den genutzten Kontakt zurück
	 */
	public Contact getUsedContact()
	{
		return contact;
	}

	/**
	 * Erstellt einen Encounter-Eintrag in der Datenbank.
	 *
	 * @param strings : StringArray mit [0] = Zeitstempel in ms, [1] = Art der Kommunikation als Wert von 0 bis 4, [2] = Richtung der Kommunikation als Wert von 0 bis 3, [3] = Länge/Dauer des Encounters
	 */
	public void addEncounter(String[] strings)
	{
		// Neuen Encounter erstellen und mit den richtigen Werten füllen
		Encounter tempEncounter = new Encounter();
		tempEncounter.setPersonId(contact.getId());
		tempEncounter.setTimestamp(strings[0]);
		tempEncounter.setMeans(Integer.valueOf(strings[1]));
		tempEncounter.setDirection(Integer.valueOf(strings[2]));
		tempEncounter.setLength(strings[3]);
		tempEncounter.setDescription("");

		// Instanz des Datenbankhelfers anfordern.
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity());
		databaseHelper.insertEncounterManual(tempEncounter);
	}
}
