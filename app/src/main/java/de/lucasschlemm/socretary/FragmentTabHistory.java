package de.lucasschlemm.socretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Tab mit den vergangenen Ereignissen
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabHistory extends Fragment
{
	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	private Contact contact;

	private ArrayList<Encounter> encounters;

	private ListView listViewEncounters;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Genutzten Kontakt auslesen
		contact = callback.getContactNeeded();
		encounters = DatabaseHelper.getInstance(getActivity()).getEncounterListForContact(Long.valueOf(contact.getId()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_tab_history, container, false);
		listViewEncounters = (ListView) rootView.findViewById(R.id.lvHistory);
		TextView txtNoHistory = (TextView) rootView.findViewById(R.id.tv_con_tab_noHistory);
		if (encounters.isEmpty())
		{
			txtNoHistory.setVisibility(View.VISIBLE);
		}
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		createListView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		encounters = DatabaseHelper.getInstance(getActivity()).getEncounterListForContact(Long.valueOf(contact.getId()));
		createListView();
	}

	private void createListView()
	{
		Encounter enc[] = new Encounter[encounters.size()];

		for (Encounter encounter : encounters)
		{
			enc[encounters.indexOf(encounter)] = encounter;
		}

		FragmentTabHistoryAdapter adapter = new FragmentTabHistoryAdapter(getActivity(), R.layout.listview_item_encounter, enc);
		listViewEncounters.setAdapter(adapter);
		Utils.justifyListView(listViewEncounters);
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

}
