package de.lucasschlemm.socretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

/**
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabDetails extends Fragment
{
	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;

	private Contact contact;

	private TextView tvNumb;
	private TextView tvBDay;
	private TextView tvAdd1;
	private TextView tvAdd2;
	private TextView tvAdd3;
	private TextView tvAdd4;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_tab_details, container, false);
		tvNumb = (TextView) rootView.findViewById(R.id.tV_con_tab_number);
		tvBDay = (TextView) rootView.findViewById(R.id.tV_con_tab_birthday);
		tvAdd1 = (TextView) rootView.findViewById(R.id.tV_con_tab_address1);
		tvAdd2 = (TextView) rootView.findViewById(R.id.tV_con_tab_address2);
		tvAdd3 = (TextView) rootView.findViewById(R.id.tV_con_tab_address3);
		tvAdd4 = (TextView) rootView.findViewById(R.id.tV_con_tab_address4);


		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		// Genutzten Kontakt auslesen
		contact = callback.getContactNeeded();

		// Telefonnummer setzen
		tvNumb.setText(contact.getNumber());
		// TODO Soll Kontaktdialog öffnen mit Anruf/SMS


		//TODO Auslagern der Formatierung in Util Methode
		DateTime bday = new DateTime(contact.getBirthday());

		// Formatierung auf führende Null
		DecimalFormat df = new DecimalFormat("00");

		// Geburtstagsetzen
		tvBDay.setText(df.format(bday.getDayOfMonth()) + "." + df.format(bday.getMonthOfYear()) + "." + bday.getYear());

		//TODO Hinzufügen von Tagen bis Geburtstag?

		//TODO Nicht anzeigen falls Adresse leer
		// Adresse setzen
		String[] address = contact.getLocationHome();
		tvAdd1.setText(address[0]);
		tvAdd2.setText(address[1] + " " + address[2]);
		tvAdd3.setText(address[3]);
		tvAdd4.setText(address[4]);


		//TODO Karte anzeigen falls Adresse hinterlegt....
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
