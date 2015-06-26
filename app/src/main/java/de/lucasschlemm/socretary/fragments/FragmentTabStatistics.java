package de.lucasschlemm.socretary.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.classes.Contact;
import de.lucasschlemm.socretary.utils.Statistics;
import de.lucasschlemm.socretary.utils.StatisticsCharter;

/**
 * Tab mit den vergangenen Ereignissen
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabStatistics extends Fragment {
	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	private Contact contact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Genutzten Kontakt auslesen
		contact = callback.getContactNeeded();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tab_statistics, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		try {
			StatisticsCharter.updateLineChart(Statistics.getMonthsStatisticsLastYear(Long.parseLong(contact.getId())), R.id.linechart_contact);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StatisticsCharter.updatePieChart(Statistics.getEncounterStatisticsByDirection(Long.parseLong(contact.getId())), R.id.piechart_contact_direction);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StatisticsCharter.updatePieChart(Statistics.getEncounterStatisticsByMeans(Long.parseLong(contact.getId())), R.id.piechart_contact_means);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	// Setzen des Callbacks bei Attach
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			callback = (FragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement ContactFragmentCallback");
		}
	}

	// Entfernen des Callbacks bei Detach
	@Override
	public void onDetach() {
		super.onDetach();
		callback = null;
	}
}