package de.lucasschlemm.socretary;

import android.graphics.Bitmap;

import java.util.Random;

public class Contact
{
	private String id;
	private String name;
	private String number;
	private String birthday;
	private String frequency;
	private String lastContact;

	private String[] locationHome;

	private String locationX;
	private String locationY;
	private String locationTime;

	private String autoTextFrequency;
	private String lastAutoTextID;
	private String possibleAutoTextArray;

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
		this.setFrequency(String.valueOf(r.nextInt(10) + 5));

		this.setLastContact("");
		this.setLocationHome(new String[]{
				"",
				"",
				"",
				"",
				"",
				""});
		this.setAutoTextFrequency("0");
		this.setLastAutoTextID("");
		this.setPossibleAutoTextArray("");
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

	public String getLocationX()
	{
		return locationX;
	}

	public void setLocationX(String locationX)
	{
		this.locationX = locationX;
	}

	public String getLocationY()
	{
		return locationY;
	}

	public void setLocationY(String locationY)
	{
		this.locationY = locationY;
	}

	public String getLocationTime()
	{
		return locationTime;
	}

	public void setLocationTime(String locationTime)
	{
		this.locationTime = locationTime;
	}

	public String getAutoTextFrequency()
	{
		return autoTextFrequency;
	}

	public void setAutoTextFrequency(String autoTextFrequency)
	{
		this.autoTextFrequency = autoTextFrequency;
	}

	public String getLastAutoTextID()
	{
		return lastAutoTextID;
	}

	public void setLastAutoTextID(String lastAutoTextID)
	{
		this.lastAutoTextID = lastAutoTextID;
	}

	public String[] getPossibleTextArray()
	{
		String[] tempArray = possibleAutoTextArray.split(",");
		return tempArray;
	}

	public void setPossibleAutoTextArray(String possibleAutoTextArray)
	{
		this.possibleAutoTextArray = possibleAutoTextArray;
	}
}
