package de.lucasschlemm.socretary.utils;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Date;

import de.lucasschlemm.socretary.classes.Encounter;
import de.lucasschlemm.socretary.database.DatabaseHelper;

public class Statistics {


	public static int[] getMonthsStatisticsLastYear(long id){
		DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
		ArrayList<Encounter> encounters;
		JodaTimeAndroid.init(ApplicationContext.getContext());
		DateTime today = new DateTime(new Date());

		if (id == 0){
			encounters = helper.getEncounterListFull();
		} else {
			encounters = helper.getEncounterListForContact(id);
		}

		int[] months = new int[12];
		for (Encounter encounter: encounters){
			DateTime date = new DateTime(new Date(Long.parseLong(encounter.getTimestamp())));
			if (Days.daysBetween(date, today).getDays() < 365){
				months[date.getMonthOfYear()-1]++;
			}
		}

		return months;
	}

	public static int[] getEncounterStatisticsByMeans(long id){
		DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
		ArrayList<Encounter> encounters;

		if (id == 0){
			encounters = helper.getEncounterListFull();
		} else {
			encounters = helper.getEncounterListForContact(id);
		}

		int[] means = new int[5];
		for (Encounter encounter: encounters){
			means[encounter.getMeans()]++;
		}

		return means;
	}

	public static int[] getEncounterStatisticsByDirection(long id){
		DatabaseHelper helper = DatabaseHelper.getInstance(ApplicationContext.getContext());
		ArrayList<Encounter> encounters;

		if (id == 0){
			encounters = helper.getEncounterListFull();
		} else {
			encounters = helper.getEncounterListForContact(id);
		}
		int[] directions = new int[4];
		for (Encounter encounter: encounters){
			directions[encounter.getDirection()]++;
		}
		return directions;
	}
}
