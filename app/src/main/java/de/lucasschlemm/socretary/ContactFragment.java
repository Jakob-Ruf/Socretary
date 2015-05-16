package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lucas.schlemm on 15.05.2015.
 */
@SuppressLint("ValidFragment")
public class ContactFragment extends Fragment
{
	// Zur Zuordnung wo Logs her kommen
	private static final String LOG_CALLER = "ContactFragment";

	// Zu nutzender Kontakt
	private Contact contact;

	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	@SuppressLint("ValidFragment")
	public ContactFragment(Contact contact)
	{
		this.contact = contact;
	}

	// Erstes Erstellen des Fragments
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.e(LOG_CALLER, "onCreate");
	}

	// Aufbauen der Ansicht
	@Nullable
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		Log.e(LOG_CALLER, "onCreateView");
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	// Ansicht ist fertig aufgebaut und wird nun befüllt
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		Log.e(LOG_CALLER, "onViewCreated");
		ImageView imageViewContact    = (ImageView) view.findViewById(R.id.iV_contact);
		TextView  textViewContactName = (TextView) view.findViewById(R.id.tV_contact_Name);


		imageViewContact.setImageBitmap(contact.getPicture());
		textViewContactName.setText(contact.getName());
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
