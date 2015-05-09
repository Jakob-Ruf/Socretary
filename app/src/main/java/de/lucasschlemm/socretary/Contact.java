package de.lucasschlemm.socretary;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by lucas.schlemm on 04.03.2015.
 */
public class Contact
{
	private String id;
	private String name;
	private String number;
	private String birthday;
	private String frequency;
	private String lastContact;

	private String[] locationHome;

	private String addrStreet;
	private String addrPostal;
	private String addrCity;
	private String addrCountry;
	private String addrRegion;
	private String addrHood;


	private Bitmap picture;

	// Konstruktor um ein Beispiel Kontakt zu bekommen
	public Contact()
	{
		this.setId("10");
		this.setName("Test Kontakt");
		this.setNumber("+49 176 20806284");
		this.setBirthday("02-04-1989");
		this.setFrequency("4");
		this.setLastContact("07-05-2015");
		this.setLocationHome(new String[]{
				"Brückenstraße 47",
				"69120",
				"Heidelberg",
				"Deutschland",
				"Baden-Württemberg",
				"Neuenheim"});
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getBirthday()
	{
		return birthday;
	}

	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}

	public String getFrequency()
	{
		return frequency;
	}

	public void setFrequency(String frequency)
	{
		this.frequency = frequency;
	}

	public String getLastContact()
	{
		return lastContact;
	}

	public void setLastContact(String lastContact)
	{
		this.lastContact = lastContact;
	}

	public String[] getLocationHome()
	{
		return this.locationHome;
	}

	public void setLocationHome(String[] locationHome)
	{
		this.addrStreet     = locationHome[0];
		this.addrPostal     = locationHome[1];
		this.addrCity       = locationHome[2];
		this.addrCountry    = locationHome[3];
		this.addrRegion     = locationHome[4];
		this.addrHood       = locationHome[5];
		this.locationHome   = locationHome;
	}

	public String getLocationHomeComplete()
	{
		String tempAddr = "";
		for (int i = 0; i <= 5; i++)
		{
			switch (i)
			{
				case 0:
					tempAddr += this.locationHome[i];
					break;
				default:
					tempAddr += ", " + this.locationHome[i];
					break;
			}
		}
		return tempAddr;
	}

	public void setLocationHomeComplete(String locationHome)
	{
		//TODO @Lucas: Methode später wieder löschen
		Log.e("Contact.java", "LocationHome muss noch angepasst werden Jakob.. :)");
	}
}
