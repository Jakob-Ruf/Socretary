package de.lucasschlemm.socretary;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Erstellt einen DialogFragment, welches die zwei Kontaktoptionen SMS und Anruf zur Wahl gibt.
 * Created by lucas.schlemm on 13.05.2015.
 */
@SuppressLint("ValidFragment")
public class ContactDialogFragment extends DialogFragment
{
	// Der zu nutzende Kontakt
	private Contact contact;

	@SuppressLint("ValidFragment")
	public ContactDialogFragment(Contact localContact)
	{
		contact = localContact;
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstance)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Ansicht aufbauen
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_contact_dialog, null);
		builder.setView(view);

		// Elemente zuweisen und befüllen
		ImageView imageView = (ImageView) view.findViewById(R.id.iV_con_dialog);
		imageView.setImageBitmap(contact.getPicture());
		TextView textName = (TextView) view.findViewById(R.id.tV_con_dialogName);
		textName.setText(contact.getName());

		// Button mit Anrufoption
		builder.setNeutralButton(getString(R.string.Phone), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// Dialer mit der Nummer des Kontakts öffnen
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse("tel:" + contact.getNumber()));
				startActivity(callIntent);
			}
		});

		// Button mit SMS-Option
		builder.setPositiveButton(getString(R.string.Messenger), new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// SMS-App öffnen mit der Konversation mit dem gewählten Kontakt
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setData(Uri.parse("sms:" + contact.getNumber()));
				startActivity(smsIntent);
			}
		});

		return builder.create();
	}
}
