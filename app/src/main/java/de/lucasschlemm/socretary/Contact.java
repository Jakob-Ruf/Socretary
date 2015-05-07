package de.lucasschlemm.socretary;

import android.graphics.Bitmap;

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
    private String locationHome;

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
        this.setLocationHome("Brückenstraße 47-69120-Heidelberg");
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

    public String getLocationHome()
    {
        return locationHome;
    }

    public void setLocationHome(String locationHome)
    {
        this.locationHome = locationHome;
    }
}
