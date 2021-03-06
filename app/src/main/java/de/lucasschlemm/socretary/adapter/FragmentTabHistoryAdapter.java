package de.lucasschlemm.socretary.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.classes.Encounter;
import de.lucasschlemm.socretary.database.DatabaseContract;

/**
 * Adapter für die Ereignisse
 * Created by lucas.schlemm on 20.05.2015.
 */
public class FragmentTabHistoryAdapter extends ArrayAdapter<Encounter>
{

	Context     context;
	int         resource;
	Encounter[] encounters;


	public FragmentTabHistoryAdapter(Context context, int resource, Encounter[] encounters)
	{
		super(context, resource, encounters);
		this.resource = resource;
		this.context = context;
		this.encounters = encounters;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View            row = convertView;
		EncounterHolder encounterHolder;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			encounterHolder = new EncounterHolder();
			encounterHolder.txtLength = (TextView) row.findViewById(R.id.tV_hist_length);
			encounterHolder.txtDate = (TextView) row.findViewById(R.id.tV_hist_date);
			row.setTag(encounterHolder);
		}
		else
		{
			encounterHolder = (EncounterHolder) row.getTag();
		}

		Encounter encounter = encounters[position];

		// TODO Hier müsste noch besser unterschieden werden
		String tempLength = getLength(encounter.getMeans(), encounter.getDirection(), encounter.getLength());
		encounterHolder.txtLength.setText(tempLength);
		if (encounter.getMeans() == DatabaseContract.EncounterEntry.MEANS_MESSENGER)
		{
			encounterHolder.txtContent = (TextView) row.findViewById(R.id.tV_hist_cont);
			encounterHolder.txtContent.setVisibility(View.VISIBLE);
			encounterHolder.txtContent.setText(encounter.getLength());
		}

		encounterHolder.txtDate.setText(getDate(encounter.getTimestamp()));


		return row;
	}

	private String getDate(String time)
	{
		String   temp;
		String   day      = "";
		DateTime dateTime = new DateTime(Long.valueOf(time));
		switch (dateTime.getDayOfWeek())
		{
			case DateTimeConstants.MONDAY:
				day = context.getResources().getString(R.string.Monday);
				break;
			case DateTimeConstants.TUESDAY:
				day = context.getResources().getString(R.string.Tuesday);
				break;
			case DateTimeConstants.WEDNESDAY:
				day = context.getResources().getString(R.string.Wednesday);
				break;
			case DateTimeConstants.THURSDAY:
				day = context.getResources().getString(R.string.Thursday);
				break;
			case DateTimeConstants.FRIDAY:
				day = context.getResources().getString(R.string.Friday);
				break;
			case DateTimeConstants.SATURDAY:
				day = context.getResources().getString(R.string.Saturday);
				break;
			case DateTimeConstants.SUNDAY:
				day = context.getResources().getString(R.string.Sunday);
				break;
		}
		temp = day + ", " + dateTime.getDayOfMonth() + "." + dateTime.getMonthOfYear() + "." + dateTime.getYear() + " - " + dateTime.getHourOfDay() + ":" + dateTime.getMinuteOfHour();
		return temp;

	}

	private String getLength(int type, int direction, String length)
	{
		String  tempType      = "";
		String  tempDirection = "";
		String  tempLength    = "";
		boolean tried         = false;
		String  temp          = null;


		switch (type)
		{
			case DatabaseContract.EncounterEntry.MEANS_PHONE:
				if (Integer.valueOf(length) <= 5)
				{
					tried = true;
					if (direction == DatabaseContract.EncounterEntry.DIRECTION_OUTBOUND)
					{
						tempType = "ausgehender ";
					}
					else
					{
						tempType = "eingehender ";
					}
				}
				else
				{
					if (direction == DatabaseContract.EncounterEntry.DIRECTION_OUTBOUND)
					{
						tempType = "Ausgehender ";
					}
					else
					{
						tempType = "Eingehender ";
					}
					Duration duration = new Duration(Long.valueOf(length) * 1000);
					Duration duration1 = duration.minus(duration.getStandardMinutes() * 1000 * 60);
					tempLength = duration.getStandardMinutes() + "m " + duration1.getStandardSeconds() + "s";
				}
				tempType += context.getResources().getString(R.string.Phone);
				if (tried)
				{
					temp = "Versuchter " + tempDirection + tempType;
				}
				else
				{
					temp = tempDirection + tempType + ": " + tempLength;
				}
				break;
			case DatabaseContract.EncounterEntry.MEANS_MAIL:
				tempType = context.getResources().getString(R.string.Mail);
				break;
			case DatabaseContract.EncounterEntry.MEANS_MESSENGER:
				if (direction == DatabaseContract.EncounterEntry.DIRECTION_OUTBOUND)
				{
					tempType = "Ausgehende ";
				}
				else
				{
					tempType = "Eingehende ";
				}
				tempType += context.getResources().getString(R.string.Messenger);
				temp = tempType;
				break;
			case DatabaseContract.EncounterEntry.MEANS_PERSONAL:
				tempType = context.getResources().getString(R.string.Personal);
				break;
			case DatabaseContract.EncounterEntry.MEANS_SOCIALNETWORK:
				tempType = context.getResources().getString(R.string.SocialNetwork);
				break;
		}

		return temp;
	}

	static class EncounterHolder
	{
		TextView txtLength;
		TextView txtDate;
		TextView txtContent;
	}
}
