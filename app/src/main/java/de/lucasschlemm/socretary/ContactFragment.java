package de.lucasschlemm.socretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lucas.schlemm on 15.05.2015.
 */
public class ContactFragment extends Fragment
{
	// Zur Zuordnung wo Logs her kommen
	private static final String LOG_CALLER = "ContactFragment";

	// Zu nutzender Kontakt
	private Contact contact;

	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	public ContactFragment(Contact contact)
	{
		this.contact = contact;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View      view                = inflater.inflate(R.layout.fragment_contact, container, false);
		ImageView imageViewContact    = (ImageView) view.findViewById(R.id.iV_contact);
		TextView  textViewContactName = (TextView) view.findViewById(R.id.tV_contact_Name);

		imageViewContact.setImageBitmap(contact.getPicture());
		textViewContactName.setText(contact.getName());

		return view;
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
