package de.lucasschlemm.socretary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Fragment, welches die Details eines Kontaktes enthält. Zudem können von hier SMS Vorlagen verschickt werden.
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabDetails extends Fragment
{
	// Callback zur Kommunikation mit anderen Fragments
	private FragmentListener callback;
	
	private Contact contact;
	
	private String[] personalTextTemplates;
	
	private TextView tvNumb;
	private TextView tvBDay;
	private TextView tvDaysleft;
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
		tvDaysleft = (TextView) rootView.findViewById(R.id.tV_con_tab_birthday_days);
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
		tvNumb.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				DialogFragment dialog = new ContactDialogFragment(contact);
				dialog.show(getActivity().getSupportFragmentManager(), "AddressDialogFragment");
			}
		});

		DateTime bday  = new DateTime(contact.getBirthday());
		DateTime today = new DateTime();

		// Formatierung auf führende Null
		DecimalFormat df = new DecimalFormat("00");
		// Geburtstagsetzen
		tvBDay.setText(df.format(bday.getDayOfMonth()) + "." + df.format(bday.getMonthOfYear()) + "." + bday.getYear());

		// aktuelles Jahr wählen
		bday = bday.withYear(today.getYear());
		// Differenz berechnen
		int difference = Days.daysBetween(bday.toLocalDate(), today.toLocalDate()).getDays();

		// Unterscheidung ob Geburtstag gerade war oder in der Zukunft liegt
		if ((difference <= 14) && (difference >= 1))
		{
			tvDaysleft.setText(String.format(getString(R.string.Birthday_past), String.valueOf(difference)));
		}
		else if (difference == 0)
		{
			tvDaysleft.setText(getString(R.string.Birthday_today));
		}
		else if (difference < 0)
		{
			tvDaysleft.setText(String.format(getString(R.string.Birthday_future), String.valueOf(difference * -1)));
		}
		else
		{
			bday = bday.withYear(today.getYear() + 1);
			difference = Days.daysBetween(bday.toLocalDate(), today.toLocalDate()).getDays();
			tvDaysleft.setText(String.format(getString(R.string.Birthday_future), String.valueOf(difference * -1)));
		}

		String[] address = contact.getLocationHome();


		setupAdressListener();

		boolean tempBoolHelper1 = true;
		boolean tempBoolHelper2 = true;
		boolean tempBoolHelper3 = true;
		boolean tempBoolHelper4 = true;
		if (address[0] == null || address[0].equals("") || address[0].isEmpty())
		{
			tvAdd1.setVisibility(View.GONE);
			tempBoolHelper1 = false;
		}
		else
		{
			tvAdd1.setText(address[0]);
		}
		if (address[1] == null || address[2] == null || address[1].equals("") || address[1].isEmpty() || address[2].equals("") || address[2].isEmpty())
		{
			tvAdd2.setVisibility(View.GONE);
			tempBoolHelper2 = false;
		}
		else
		{
			tvAdd2.setText(address[1] + " " + address[2]);
		}
		if (address[3] == null || address[3].equals("") || address[3].isEmpty())
		{
			tvAdd3.setVisibility(View.GONE);
			tempBoolHelper3 = false;
		}
		else
		{
			tvAdd3.setText(address[3]);
		}
		if (address[4] == null || address[4].equals("") || address[4].isEmpty())
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
			DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					switch (which)
					{
						case DialogInterface.BUTTON_POSITIVE:
							onCreateDialog().show();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							break;
					}
				}
			};
			// Aufbauen des AlertDialog zur Bestätigung des Löschens
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
			dialogBuilder.setMessage(getActivity().getResources().getString(R.string.NoAdress));
			dialogBuilder.setTitle(getActivity().getResources().getString(R.string.NoAdressTitle));
			dialogBuilder.setPositiveButton(android.R.string.yes, dialogListener);
			dialogBuilder.setNegativeButton(android.R.string.cancel, dialogListener);
			dialogBuilder.show();
		}

		if (tempBoolHelper1 && tempBoolHelper2 && tempBoolHelper3 && tempBoolHelper4)
		{
			//TODO Karte anzeigen falls Adresse hinterlegt....
			// Kommt wahrscheinlich nicht..
		}


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
		if (txtIDs == null)
		{
			tvNoSMSTemplates.setVisibility(View.VISIBLE);
			Log.d("FragmentTabDetails", "onViewCreated: Zeile: 169: " + "TextArray ist leer");
		}
		else
		{
			personalTextTemplates = new String[txtIDs.length];
			for (int i = 0; i < txtIDs.length; i++)
			{
				for (AutomatedMessage message : textTemplates)
				{
					if (message.getId() == Long.valueOf(txtIDs[i]))
					{
						personalTextTemplates[i] = message.getText();
					}
				}
			}
			buildListView();
		}
	}

	private void setupAdressListener()
	{
		tvAdd1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToAdress();
			}
		});
		tvAdd2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToAdress();
			}
		});
		tvAdd3.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToAdress();
			}
		});
		tvAdd4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				goToAdress();
			}
		});
	}

	private void goToAdress()
	{

		//TODO Werte dynamisch füllen
		double lat    = 49.4166204;
		double lng    = 8.691796999999999;
		String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + contact.getName() + ")";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
		startActivity(intent);
	}

	private AlertDialog.Builder buildPromptDialog(final String string)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		String message = String.format(getString(R.string.SendTemplateSMSText), contact.getName());
		alert.setTitle(getString(R.string.SendTemplateSMSTitle));
		alert.setMessage(message);

		TextView textView = new TextView(getActivity());
		textView.setText(string);
		textView.setPadding(50, 24, 50, 24);
		textView.setTextSize(20);
		alert.setView(textView);
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				TextMessageHelper.sendText(contact.getNumber(), string);
			}
		});
		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(getActivity(), getString(R.string.AbortSendingSMS), Toast.LENGTH_LONG).show();
			}
		});
		return alert;
	}
	
	private AlertDialog.Builder getTemplateDialog()
	{
		final ArrayList<Integer> selectedTemplates = new ArrayList<>();
		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle(getString(R.string.SMSTemplatesToUse));
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
				setTemplates(getTemplates(), selectedTemplates);
			}
		});
		
		return alert;
		
	}
	
	private void setTemplates(String[] templates, ArrayList<Integer> selectedTemplates)
	{
		ArrayList<AutomatedMessage> templateTextArrayList = databaseHelper.getAutoTextList();
		
		personalTextTemplates = new String[selectedTemplates.size()];
		
		String templatesToUse = "";
		
		// Auslesen der gewählten Strings
		for (Integer selectedTemplate : selectedTemplates)
		{
			personalTextTemplates[selectedTemplates.indexOf(selectedTemplate)] = templates[selectedTemplate];
			Log.d("FragmentTabDetails", "setTemplates: Zeile: 239: " + templates[selectedTemplate]);
		}
		
		// Zuordnen der jeweiligen IDs
		for (AutomatedMessage automatedMessage : templateTextArrayList)
		{
			for (String personalTextTemplate : personalTextTemplates)
			{
				if (automatedMessage.getText().equals(personalTextTemplate))
				{
					if (templatesToUse.equals(""))
					{
						templatesToUse += automatedMessage.getId();
					}
					else
					{
						templatesToUse += ("," + automatedMessage.getId());
					}
				}
			}
		}

		contact.setPossibleAutoTextArray(templatesToUse);
		databaseHelper.updateContact(contact);
		buildListView();
	}
	
	private void buildListView()
	{
		ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, personalTextTemplates);
		if (!listAdapter.isEmpty())
		{
			tvNoSMSTemplates.setVisibility(View.GONE);
		}
		else
		{
			tvNoSMSTemplates.setVisibility(View.VISIBLE);
		}
		lvSMSTemplates.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Object object = lvSMSTemplates.getItemAtPosition(position);
				buildPromptDialog(object.toString()).show();
			}
		});
		lvSMSTemplates.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				Object object = lvSMSTemplates.getItemAtPosition(position);
				buildEditDialog(object.toString()).show();
				return true;
			}
		});
		lvSMSTemplates.setAdapter(listAdapter);
		Utils.justifyListView(lvSMSTemplates);
	}

	private AlertDialog.Builder buildEditDialog(String string)
	{
		AlertDialog.Builder alert   = new AlertDialog.Builder(getActivity());
		String              message = String.format(getString(R.string.SendTemplateSMSText), contact.getName());

		alert.setTitle(getString(R.string.SendTemplateSMSTitle));
		alert.setMessage(message);

		final EditText editText = new EditText(getActivity());
		editText.setText(string);

		alert.setView(editText);

		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				TextMessageHelper.sendText(contact.getNumber(), editText.getText().toString());
			}
		});
		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(getActivity(), getString(R.string.AbortSendingSMS), Toast.LENGTH_LONG).show();
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

	public Dialog onCreateDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View           view     = inflater.inflate(R.layout.fragment_address_dialog, null);

		final EditText eT_street  = (EditText) view.findViewById(R.id.eT_adr_street);
		final EditText eT_number  = (EditText) view.findViewById(R.id.eT_adr_number);
		final EditText eT_code    = (EditText) view.findViewById(R.id.eT_adr_code);
		final EditText eT_city    = (EditText) view.findViewById(R.id.eT_adr_city);
		final EditText eT_country = (EditText) view.findViewById(R.id.eT_adr_country);
		final EditText eT_region  = (EditText) view.findViewById(R.id.eT_adr_region);

		builder.setView(view);
		builder.setMessage(R.string.dialog_address_text);
		builder.setTitle(R.string.dialog_address_title);

		// Bestätigung der Eingabe
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				String[] answer = new String[]{
						eT_street.getText().toString() + " " + eT_number.getText().toString(),
						eT_code.getText().toString(),
						eT_city.getText().toString(),
						eT_country.getText().toString(),
						eT_region.getText().toString(),
						""};
				contact.setLocationHome(answer);
				DatabaseHelper.getInstance(getActivity()).updateContact(contact);
				callback.reloadContactFragment(contact);
			}
		});
		// Später die Adresse eingeben
		builder.setNeutralButton(R.string.dialog_skip, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});

		return builder.create();
	}

}
