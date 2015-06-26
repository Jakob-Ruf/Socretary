package de.lucasschlemm.socretary.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.fragments.NavFragment;

/**
 * Adapter für die Navigationsleiste
 * Created by lucas.schlemm on 29.05.2015.
 */
public class NavAdapter extends ArrayAdapter<String>
{
	private Context  context;
	private String[] items;
	private int      resource;

	public NavAdapter(Context context, int resource, String[] objects)
	{
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View      view = convertView;
		NavHolder navHolder;

		if (view == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			view = inflater.inflate(resource, parent, false);

			navHolder = new NavHolder();
			navHolder.txtTitle = (TextView) view.findViewById(R.id.tv_navItem);

			view.setTag(navHolder);
		}
		else
		{
			navHolder = (NavHolder) view.getTag();
		}
		if (position == NavFragment.getiCurPos())
		{
			navHolder.txtTitle.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_dark));
			navHolder.txtTitle.setTextColor(Color.WHITE);
		}
		else
		{
			navHolder.txtTitle.setTextColor(context.getResources().getColor(android.R.color.secondary_text_light));
			navHolder.txtTitle.setBackgroundColor(Color.WHITE);
		}

		navHolder.txtTitle.setText(items[position]);

		return view;
	}

	private class NavHolder
	{
		TextView txtTitle;
	}
}
