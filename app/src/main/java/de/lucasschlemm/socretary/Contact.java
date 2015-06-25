package de.lucasschlemm.socretary;

import android.graphics.Bitmap;

public class Contact
{
	private String id;
	private String name;
	private String number;
	private String birthday;
	private String frequency;
	private String lastContact;

	private String[] locationHome;
	private double   locationHomeLat;
	private double   locationHomeLong;

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
		this.setFrequency("");
		this.setLastContact("");
		this.setLocationHome(new String[]{
				"",
				"",
				"",
				"",
				"",
				""});
		this.setPossibleAutoTextArray("");
		this.setLocationHomeLat(0);
		this.setLocationHomeLong(0);
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

	public String[] getPossibleTextArray()
	{
		String[] tempArray = null;
		if (possibleAutoTextArray == null)
		{
			tempArray = null;
		}
		else if (possibleAutoTextArray.equals(""))
		{
			tempArray = null;
		}
		else if (possibleAutoTextArray.isEmpty())
		{
			tempArray = null;
		}
		else
		{
			tempArray = possibleAutoTextArray.split(",");
		}
		return tempArray;
	}

	public void setPossibleAutoTextArray(String possibleAutoTextArray)
	{
		this.possibleAutoTextArray = possibleAutoTextArray;
	}

	public double getLocationHomeLat()
	{
		return locationHomeLat;
	}

	public void setLocationHomeLat(double lat)
	{
		this.locationHomeLat = lat;
	}

	public double getLocationHomeLong()
	{
		return locationHomeLong;
	}

	public void setLocationHomeLong(double lng)
	{
		this.locationHomeLong = lng;
	}

	public String getPossibleAutoTextArray()
	{
		return possibleAutoTextArray;
	}
}
