package de.lucasschlemm.socretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;

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

	private TextView tvNoSMSTemplates;
	private ListView lvSMSTemplates;

	private DatabaseHelper databaseHelper;

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
		tvNoSMSTemplates = (TextView) rootView.findViewById(R.id.tV_con_tab_noTextTemplates);
		lvSMSTemplates = (ListView) rootView.findViewById(R.id.lvTextTemplate);

		databaseHelper = DatabaseHelper.getInstance(getActivity());

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

		ImageButton btn_configTextTemplates = (ImageButton) view.findViewById(R.id.btn_configTextTemplates);
		btn_configTextTemplates.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getTemplateDialog().show();
			}
		});


		ArrayList<AutomatedMessage> textTemplates = databaseHelper.getAutoTextList();

		String[] txtIDs = contact.getPossibleTextArray();

		String[] txtsToSend = new String[txtIDs.length];

		for (int i = 0; i < txtIDs.length - 1; i++)
		{
			for (AutomatedMessage message : textTemplates)
			{
				if (message.getId() == Long.valueOf(txtIDs[i]))
				{
					txtsToSend[i] = message.getText();
				}
			}
		}

		ArrayAdapter<String> adapterSMS = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, txtIDs);
		lvSMSTemplates.setAdapter(adapterSMS);
		Utils.justifyListView(lvSMSTemplates);
		//TODO ListView einfügen...

		if (txtIDs.length == 0 || txtIDs[0].equals(""))
		{
			tvNoSMSTemplates.setVisibility(View.VISIBLE);
		}

	}

	private AlertDialog.Builder getTemplateDialog()
	{
		final ArrayList<Integer> selectedTemplates = new ArrayList();

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getString(R.string.TempleTextRemoveTitle));
		alert.setMultiChoiceItems(getTemplates(), null, new DialogInterface.OnMultiChoiceClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked)
			{
				if (isChecked)
				{
					selectedTemplates.add(which);
				}
				else if (selectedTemplates.contains(which))
				{
					selectedTemplates.remove(Integer.valueOf(which));
				}

			}
		});
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				//setTemplates(getTemplates(), selectedTemplates);
				//buildListView();
			}
		});

		return alert;

	}

	private String[] getTemplates()
	{
		ArrayList<AutomatedMessage> templateTextArrayList = databaseHelper.getAutoTextList();
		String[]                    templateTextArray     = new String[templateTextArrayList.size()];

		for (AutomatedMessage message : templateTextArrayList)
		{
			templateTextArray[templateTextArrayList.indexOf(message)] = message.getText();
		}

		return templateTextArray;
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
