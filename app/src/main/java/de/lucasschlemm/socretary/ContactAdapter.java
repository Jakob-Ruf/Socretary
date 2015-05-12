package de.lucasschlemm.socretary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

			row.setTag(contactHolder);
		}
		else
		{
			contactHolder = (ContactHolder) row.getTag();
		}

		Contact contact = contacts[position];
		contactHolder.txtTitle.setText(contact.getName());
		contactHolder.imgContact.setImageBitmap(contact.getPicture());
		if (contact.getLastContact().equals(""))
		{
			contactHolder.txtDetails.setText("Ihr habt ja noch gar nicht kommuniziert...");
		}
		else
		{
			contactHolder.txtDetails.setText("Letzter kontakt: " + contact.getLastContact());
		}

		return row;
	}

	static class ContactHolder
	{
		ImageView imgContact;
		TextView  txtTitle;
		TextView  txtDetails;
	}
}
