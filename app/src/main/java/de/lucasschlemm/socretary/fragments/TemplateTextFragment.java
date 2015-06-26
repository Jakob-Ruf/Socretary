package de.lucasschlemm.socretary.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.lucasschlemm.socretary.R;
import de.lucasschlemm.socretary.classes.AutomatedMessage;
import de.lucasschlemm.socretary.classes.Contact;
import de.lucasschlemm.socretary.database.DatabaseHelper;

/**
 * Framgent zur Anzeige der SMS Vorlagen. Zusätzlich können hier weitere SMS Vorlagen erstellt werden. Auch ein Entfernen von bestehenden Vorlagen ist hier drin möglich.
 * Created by lucas.schlemm on 28.05.2015.
 */
public class TemplateTextFragment extends Fragment
{
	private static ListView lv_textTemplates;

	private static TemplateTextFragment instance;

	private DatabaseHelper dbHelper;

	public static Fragment getInstance()
	{
		if (instance == null)
		{
			instance = new TemplateTextFragment();
		}
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		dbHelper = DatabaseHelper.getInstance(getActivity());
		return inflater.inflate((R.layout.fragment_text_template_list), container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

		ImageButton btn_addTextTemplate = (ImageButton) view.findViewById(R.id.btn_addTextTemplate);
		btn_addTextTemplate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getTextInput().show();
			}
		});

		ImageButton btn_remTextTemplate = (ImageButton) view.findViewById(R.id.btn_remTextTemplate);
		btn_remTextTemplate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getCurrentTemplates().show();
			}
		});

		lv_textTemplates = (ListView) view.findViewById(R.id.lvTextTemplateList);
		buildListView();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
	}

	/**
	 * Abruf der aktuellen SMS Vorlagen und Neuaufbau der Liste
	 */
	private void buildListView()
	{
		ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getTemplates());
		lv_textTemplates.setAdapter(listAdapter);
	}

	private String[] getTemplates()
	{
		ArrayList<AutomatedMessage> templateTextArrayList = dbHelper.getAutoTextList();
		String[]                    templateTextArray     = new String[templateTextArrayList.size()];

		for (AutomatedMessage message : templateTextArrayList)
		{
			templateTextArray[templateTextArrayList.indexOf(message)] = message.getText();
		}

		return templateTextArray;
	}

	private AlertDialog.Builder getCurrentTemplates()
	{
		final ArrayList<Integer> selectedTemplates = new ArrayList<>();

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
				removeTemplates(getTemplates(), selectedTemplates);
				buildListView();
			}
		});

		return alert;
	}

	private void removeTemplates(String[] templates, ArrayList<Integer> selectedTemplates)
	{
		ArrayList<AutomatedMessage> templateTextArrayList = dbHelper.getAutoTextList();

		String[] templatesToRemove = new String[selectedTemplates.size()];

		// Auslesen der zu löschenden Strings
		for (Integer selectedTemplate : selectedTemplates)
		{
			templatesToRemove[selectedTemplates.indexOf(selectedTemplate)] = templates[selectedTemplate];
		}

		// Zuordnen der jeweiligen IDs
		for (AutomatedMessage automatedMessage : templateTextArrayList)
		{
			for (String aTemplatesToRemove : templatesToRemove)
			{
				if (automatedMessage.getText().equals(aTemplatesToRemove))
				{
					ArrayList<Contact> contacts = dbHelper.getContactList();
					deleteIDs(String.valueOf(automatedMessage.getId()), contacts);
					dbHelper.deleteAutoText(automatedMessage.getId());
				}
			}
		}

		buildListView();
	}

	/**
	 * Methode zum Erstellen eines neuen TextInputDialogs
	 *
	 * @return TextInputDialog
	 */
	private AlertDialog.Builder getTextInput()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		// Text anpassen
		alert.setTitle(getString(R.string.TemplateSMS));
		alert.setMessage(getString(R.string.TemplateTextInputText));

		// Eingabefeld hinzufügen
		final EditText input = new EditText(getActivity());
		input.setHint(getString(R.string.TemplateTextInputHint));
		alert.setView(input);

		// Okay-Button mit Funktion einfügen
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (!input.getText().toString().trim().equals(""))
				{
					addText(input.getText().toString());
				}
				else
				{
					Toast.makeText(getActivity(), getString(R.string.ErrorEmptyInput), Toast.LENGTH_LONG).show();
				}
			}
		});

		return alert;
	}

	/**
	 * Funktion welche eine neue SMS Vorlage in die Datenbank speichert und dann die ListView neu aufbaut.
	 *
	 * @param string Text, welcher als neue Vorlage dient.
	 */
	private void addText(String string)
	{
		dbHelper.addAutoText(string);
		buildListView();
	}

	private void deleteIDs(String id, ArrayList<Contact> contacts)
	{
		for (Contact contact : contacts)
		{
			String[] tempIDs = contact.getPossibleTextArray();
			if (tempIDs != null)
			{
				String templateIDS = "";
				for (int i = 0; i < contact.getPossibleTextArray().length; i++)
				{
					if (!contact.getPossibleTextArray()[i].equals(id))
					{
						if (templateIDS.equals(""))
						{
							templateIDS += tempIDs[i];
						}
						else
						{
							templateIDS += "," + tempIDs[i];
						}
					}
				}
				contact.setPossibleAutoTextArray(templateIDS);
				dbHelper.updateContact(contact);
			}
		}
	}
}
