package de.lucasschlemm.socretary.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.utils.Statistics;
import de.lucasschlemm.socretary.utils.StatisticsCharter;


public class StatsFragment extends Fragment
{

	private static final String LOG_CALLER = "PrefsFragment";

	private static StatsFragment instance;

	public static StatsFragment getInstance()
	{
		if (instance == null)
		{
			instance = new StatsFragment();
		}
		return instance;
	}

	// Erstellen des Fragments
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.e(LOG_CALLER, "onCreate");
	}

	// Aufbauen der Ansicht
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		return inflater.inflate(R.layout.fragment_stats, container, false);
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		try {
			StatisticsCharter.updateLineChart(Statistics.getMonthsStatisticsLastYear(0), R.id.linechart_overall);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StatisticsCharter.updatePieChart(Statistics.getEncounterStatisticsByDirection(0), R.id.piechart_overall_direction);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			StatisticsCharter.updatePieChart(Statistics.getEncounterStatisticsByMeans(0), R.id.piechart_overall_means);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
