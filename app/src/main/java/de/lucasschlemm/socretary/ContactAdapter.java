package de.lucasschlemm.socretary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.opengl.EGLExt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

/**
 * Created by lucas.schlemm on 09.05.2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact>
{
	Context   context;
	int       resource;
	Contact[] contacts;

	public ContactAdapter(Context context, int resource, Contact[] contacts)
	{
		super(context, resource, contacts);
		this.resource = resource;
		this.context = context;
		this.contacts = contacts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View          row           = convertView;
		ContactHolder contactHolder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			contactHolder = new ContactHolder();
			contactHolder.imgContact = (ImageView) row.findViewById(R.id.imgContactIcon);
			contactHolder.txtTitle = (TextView) row.findViewById(R.id.tvContactTitle);
			contactHolder.txtDetails = (TextView) row.findViewById(R.id.tvContactDetails);
			contactHolder.txtNextContact = (TextView) row.findViewById(R.id.tvNextContact);

			row.setTag(contactHolder);
		}
		else
		{
			contactHolder = (ContactHolder) row.getTag();
		}

		Contact contact = contacts[position];
		contactHolder.txtTitle.setText(contact.getName());
		contactHolder.imgContact.setImageBitmap(contact.getPicture());

		if (contact.getLastContact() == null)
		{
			contactHolder.txtDetails.setText("Ihr habt ja noch gar nicht kommuniziert...");
		}
		else
		{
			Long lastCon = Long.valueOf(contact.getLastContact());
			DateTime lastContact = new DateTime(lastCon);

			// Formatierung auf führende Null
			DecimalFormat df = new DecimalFormat("00");

			contactHolder.txtDetails.setText("Letzter Kontakt: " + df.format(lastContact.getDayOfMonth()) + "." + df.format(lastContact.getMonthOfYear()) + "." + lastContact.getYear());
		}

		contactHolder.txtNextContact.setBackground(Utils.getNextContactBG(context,contact.getLastContact(),contact.getFrequency()));
		contactHolder.txtNextContact.setText(Utils.getDaysLeft(contact.getLastContact(),contact.getFrequency()));

		return row;
	}

	static class ContactHolder
	{
		ImageView imgContact;
		TextView  txtTitle;
		TextView  txtDetails;
		TextView  txtNextContact;
	}
}
