package de.lucasschlemm.socretary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

	private TextView     tvNoAutoSMS;
	private LinearLayout llAutoSMS;
	private ListView     lvAutoSMS;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_tab_details, container, false);

		// Standardkomponenten der Kontaktdetails
		tvNumb = (TextView) rootView.findViewById(R.id.tV_con_tab_number);
		tvBDay = (TextView) rootView.findViewById(R.id.tV_con_tab_birthday);
		tvAdd1 = (TextView) rootView.findViewById(R.id.tV_con_tab_address1);
		tvAdd2 = (TextView) rootView.findViewById(R.id.tV_con_tab_address2);
		tvAdd3 = (TextView) rootView.findViewById(R.id.tV_con_tab_address3);
		tvAdd4 = (TextView) rootView.findViewById(R.id.tV_con_tab_address4);

		// Komponenten fü automatisierte SMS
		tvNoAutoSMS = (TextView) rootView.findViewById(R.id.tV_con_tab_noAutoSMS);
		llAutoSMS = (LinearLayout) rootView.findViewById(R.id.ll_autoSMS);
		lvAutoSMS = (ListView) rootView.findViewById(R.id.lvAutosms);


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

		boolean tempBoolHelper1 = true;
		boolean tempBoolHelper2 = true;
		boolean tempBoolHelper3 = true;
		boolean tempBoolHelper4 = true;
		if (address[0].equals("") || address[0] == null || address[0].isEmpty())
		{
			tvAdd1.setVisibility(View.GONE);
			tempBoolHelper1 = false;
		}
		else
		{
			tvAdd1.setText(address[0]);
		}
		if (address[1].equals("") || address[1] == null || address[1].isEmpty() || address[2].equals("") || address[2] == null || address[2].isEmpty())
		{
			tvAdd2.setVisibility(View.GONE);
			tempBoolHelper2 = false;
		}
		else
		{
			tvAdd2.setText(address[1] + " " + address[2]);
		}
		if (address[3].equals("") || address[3] == null || address[3].isEmpty())
		{
			tvAdd3.setVisibility(View.GONE);
			tempBoolHelper3 = false;
		}
		else
		{
			tvAdd3.setText(address[3]);
		}
		if (address[4].equals("") || address[4] == null || address[4].isEmpty())
		{
			tvAdd4.setVisibility(View.GONE);
			tempBoolHelper4 = false;
		}
		else
		{
			tvAdd4.setText(address[4]);
		}

		if (!tempBoolHelper1 && !tempBoolHelper2 && !tempBoolHelper3 && !tempBoolHelper4)
		{
			TextView tvAddrHeader = (TextView) view.findViewById(R.id.tV_con_tab_addrHeader);
			tvAddrHeader.setVisibility(View.GONE);
		}
		if (!tempBoolHelper1 || !tempBoolHelper2 || !tempBoolHelper3 || !tempBoolHelper4)
		{
			//TODO @Lucas
			Toast.makeText(getActivity(), "Adresse nicht komplett gepflegt. Da muss der Lucas noch eine Abfrage einbauen", Toast.LENGTH_LONG).show();
		}

		//TODO Karte anzeigen falls Adresse hinterlegt....

		//TODO Pref abfragen ob AutoSMS aktiv
		//if (PrefAutoSMS)
		String[] txts = contact.getPossibleAutoTextArray();

		llAutoSMS.setVisibility(View.VISIBLE);
		lvAutoSMS.setVisibility(View.VISIBLE);
		ArrayAdapter<String> adapterSMS = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, txts);
		lvAutoSMS.setAdapter(adapterSMS);
		Utils.justifyListView(lvAutoSMS);
		//TODO ListView einfügen...

		if (txts.length == 0)
		{
			tvNoAutoSMS.setVisibility(View.VISIBLE);
		}

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
