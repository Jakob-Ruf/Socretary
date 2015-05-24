package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lucas.schlemm on 13.05.2015.
 */
public class ContactDialogFragment extends DialogFragment
{
	// Zuordnung des Logs
	private static final String LOG_CALLER = "ContactDialogFragment";

	// Callbacks einrichten
	private FragmentListener callback;

	private Contact contact;

	@SuppressLint("ValidFragment")
	public ContactDialogFragment(Contact localContact)
	{
		contact = localContact;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstance)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_contact_dialog, null);

		builder.setView(view);
		ImageView imageView = (ImageView) view.findViewById(R.id.iV_con_dialog);
		imageView.setImageBitmap(contact.getPicture());
		TextView textName = (TextView) view.findViewById(R.id.tV_con_dialogName);
		textName.setText(contact.getName());

		builder.setNeutralButton("Anrufen", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Log.d(LOG_CALLER, "Call");
			}
		});

		return builder.create();
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
			throw new ClassCastException("Activity must implement ContactDialogListenerCallback");
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
