package de.lucasschlemm.socretary;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.Random;

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

	@Override
	public String toString()
	{
		return "ID: " + this.getId() + " - Name: " + this.getName() + " - Birthday: " + this.getBirthday() + " - Frequency: " + this.getFrequency();
	}

	public Bitmap getPicture()
	{
		return picture;
	}

	public void setPicture(Bitmap picture)
	{
		this.picture = picture;
	}

	private Bitmap picture;

	// Konstruktor um ein Beispiel Kontakt zu bekommen
	public Contact()
	{
		this.setId("");
		this.setName("");
		this.setNumber("");
		this.setBirthday("");
		
		//TODO entfernen des Random-Generators
		Random r = new Random();
		this.setFrequency(String.valueOf(r.nextInt(10) +5));

		this.setLastContact("");
		this.setLocationHome(new String[]{
				"",
				"",
				"",
				"",
				"",
				""});
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
		this.locationHome = locationHome;
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
}
