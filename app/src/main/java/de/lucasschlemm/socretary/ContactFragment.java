package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lucas.schlemm on 15.05.2015.
 */
@SuppressLint("ValidFragment")
public class ContactFragment extends Fragment
{
	// Zur Zuordnung wo Logs her kommen
	private static final String LOG_CALLER = "ContactFragment";

	// Zu nutzender Kontakt
	private Contact contact;

	private FragmentTabHost tabHost;

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
		setHasOptionsMenu(true);
		Log.e(LOG_CALLER, "onCreate");
	}

	// Aufbauen der Ansicht
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

		tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
		tabHost.setup(getActivity().getApplicationContext(), getChildFragmentManager(), R.id.realtabcontent);
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Details"), FragmentTabDetails.class, null);
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Verlauf"), FragmentTabHistory.class, null);
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Statistik"), FragmentTabDetails.class, null);
		return rootView;
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.e(LOG_CALLER, "onViewCreated");
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
		menu.add(getActivity().getResources().getString(R.string.OptionsAddEncounter));
		menu.add(getActivity().getResources().getString(R.string.OptionsRemoveContact));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
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

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setMessage(getActivity().getResources().getString(R.string.AreYouSure));
			dialogBuilder.setTitle(getActivity().getResources().getString(R.string.OptionsRemoveContact));
			dialogBuilder.setPositiveButton(android.R.string.yes, dialogListener);
			dialogBuilder.setNegativeButton(android.R.string.cancel, dialogListener);
			dialogBuilder.show();
		}
		else if (item.toString().equals(getActivity().getResources().getString(R.string.OptionsAddEncounter)))
		{
			Log.d(LOG_CALLER, "Encounter hinzufügen");
		}
		return super.onOptionsItemSelected(item);
	}


	public Contact getUsedContact()
	{
		return contact;
	}
}
