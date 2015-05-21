package de.lucasschlemm.socretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabHistory extends Fragment
{
	private static final String LOG_CALLER = "FragmentTabHistory";

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

		//TODO anpassen. Ladeanimation
		encounters = DatabaseHelper.getInstance(getActivity()).getEncounterListForContact(Long.valueOf(contact.getId()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_tab_hitory, container, false);
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

	private void createListView()
	{
		Encounter enc[] = new Encounter[encounters.size()];

		for (Encounter encounter : encounters)
		{
			enc[encounters.indexOf(encounter)] = encounter;
			Log.d(LOG_CALLER,"Length: " + encounter.getLength());
		}

		FragmentTabHistoryAdapter adapter = new FragmentTabHistoryAdapter(getActivity(), R.layout.listview_item_encounter, enc);
		listViewEncounters.setAdapter(adapter);
		justifyListViewHeightBasedOnChildren(listViewEncounters);
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

	public void justifyListViewHeightBasedOnChildren(ListView listView)
	{

		ListAdapter adapter = listView.getAdapter();

		if (adapter == null)
		{
			return;
		}
		ViewGroup vg          = listView;
		int       totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++)
		{
			View listItem = adapter.getView(i, null, vg);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams par = listView.getLayoutParams();
		par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(par);
		listView.requestLayout();
	}
}
