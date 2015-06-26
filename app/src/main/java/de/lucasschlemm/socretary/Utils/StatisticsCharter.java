package de.lucasschlemm.socretary.utils;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.database.DatabaseContract;

/**
 * Created by jakob.ruf on 26.06.2015.
 */
public class StatisticsCharter {
	public static boolean updateLineChart(int[] data, int id) {
		LineChart lineChart = (LineChart) ApplicationContext.getActivity().findViewById(id);

		ArrayList<Entry> vals1 = new ArrayList<>();
		ArrayList<Entry> values = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			values.add(new Entry(data[i], i));
		}

		LineData lineData = new LineData();

		for (Entry value : values) {
			vals1.add(value);
			lineData.addXValue(Constants.MONTHS_SHORT[values.indexOf(value)]);
		}

		LineDataSet set1 = new LineDataSet(vals1, ApplicationContext.getContext().getString(R.string.Stats_title_perMonth));
		set1.setColor(getColors().get(0));
		set1.setFillColor(getColors().get(1));
		lineData.addDataSet(set1);

		set1.setDrawCubic(false);
		set1.setDrawFilled(true);

		lineChart.setData(lineData);

		lineChart.setNoDataText(ApplicationContext.getContext().getString(R.string.notContactedYet));
		lineChart.disableScroll();
		lineChart.setDrawGridBackground(false);
		lineChart.animateY(1100, Easing.EasingOption.EaseInCubic);
		lineChart.setTouchEnabled(false);

		YAxis yAxis = lineChart.getAxisLeft();
		YAxis yAxis1 = lineChart.getAxisRight();
		XAxis xAxis = lineChart.getXAxis();
		yAxis.setDrawGridLines(false);
		yAxis1.setDrawGridLines(false);
		yAxis1.setEnabled(false);
		xAxis.setDrawGridLines(false);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		lineChart.setDescription("");
		lineChart.invalidate();
		return true;
	}

	public static boolean updatePieChart(int[] data, int id) {
		ArrayList<Entry> vals1 = new ArrayList<>();
		ArrayList<Entry> values = new ArrayList<>();
		for (int i = 0; i < data.length; i++) {
			values.add(new Entry(data[i], i));
		}

		PieChart lPieChart = (PieChart) ApplicationContext.getActivity().findViewById(id);

		PieData pieData = new PieData();
		pieData.setValueFormatter(new com.github.mikephil.charting.utils.DefaultValueFormatter(2));

		for (Entry value : values) {
			if (value.getVal() > 0) {
				vals1.add(value);
				if (id == R.id.piechart_contact_direction || id == R.id.piechart_overall_direction) {
					switch (values.indexOf(value)) {
						case DatabaseContract.EncounterEntry.DIRECTION_COINCIDENCE:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_direction_coincidence));
							break;
						case DatabaseContract.EncounterEntry.DIRECTION_INBOUND:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_direction_inbound));
							break;
						case DatabaseContract.EncounterEntry.DIRECTION_OUTBOUND:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_direction_outbound));
							break;
						case DatabaseContract.EncounterEntry.DIRECTION_MUTUAL:
						default:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_direction_mutual));
							break;
					}
				} else if(id == R.id.piechart_contact_means || id == R.id.piechart_overall_means) {
					switch (values.indexOf(value)) {
						case DatabaseContract.EncounterEntry.MEANS_MAIL:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_means_mail));
							break;
						case DatabaseContract.EncounterEntry.MEANS_MESSENGER:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_means_messenger));
							break;
						case DatabaseContract.EncounterEntry.MEANS_PERSONAL:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_means_personal));
							break;
						case DatabaseContract.EncounterEntry.MEANS_PHONE:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_means_phone));
							break;
						case DatabaseContract.EncounterEntry.MEANS_SOCIALNETWORK:
						default:
							pieData.addXValue(ApplicationContext.getActivity().getString(R.string.Encounter_means_socialnetwork));
							break;
					}
				}
			}
		}

		PieDataSet set1 = new PieDataSet(vals1, "");

		set1.setColors(getColors());

		pieData.addDataSet(set1);
		lPieChart.setData(pieData);

		lPieChart.setNoDataText(ApplicationContext.getActivity().getString(R.string.notContactedYet));
		lPieChart.disableScroll();
		lPieChart.animateY(1100, Easing.EasingOption.EaseInCubic);
		lPieChart.setTouchEnabled(true);
		lPieChart.setRotationEnabled(false);
		lPieChart.setDescription("");

		lPieChart.invalidate();

		return true;
	}

	private static ArrayList<Integer> getColors(){
		ArrayList<Integer> colors = new ArrayList<>();
		for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
		for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
		for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
		return colors;
	}
}
