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

	// Navigation zur Kontaktansicht
	void onContactLongClick(Contact contact);

	// Tabsbefüllen im Kontaktfragment
	Contact getContactNeeded();

	// Kontakt aus der Datenbank entfernen
	void removeContact(Contact contact);

	// Encounter hinzufügen
	void addEncounter(String[] encounter);

	// Neuladen des ContactFragment, falls was geändert wurde
	void reloadContactFragment(Contact contact);
}
