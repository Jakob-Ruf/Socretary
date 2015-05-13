package de.lucasschlemm.socretary;

/**
 * Created by lucas.schlemm on 13.05.2015.
 * Interface zur Kommunikation innerhalb der App
 */
public interface FragmentListener
{
	// Dialog anzeigen
	void onDialogNeeded(String type);

	// Frequency-Dialog Antwort
	void onFrequencyDialogPressed(String[] answer);

	// Birthday-Dialog Antwort
	void onBirthdayDialogPressed(String[] answer);

	// Address-Dialog Antwort
	void onAddressDialogPressed(String[] answer);

	// Kontakt-Dialog anzeigen
	void onContactDialogNeeded(Contact contact);

	// Navigationsleiste
	void onNavSelected(int position);
}
