package de.lucasschlemm.socretary;

/**
 * Created by lucas.schlemm on 13.05.2015.
 */
public interface FragmentListener
{
	// Dialog anzeigen
	public void onDialogNeeded(String type);

	// Frequency-Dialog Antwort
	public void onFrequencyDialogPressed(String[] answer);

	// Birthday-Dialog Antwort
	public void onBirthdayDialogPressed(String[] answer);

	// Navigationsleiste
	public void onNavSelected(int position);
}
