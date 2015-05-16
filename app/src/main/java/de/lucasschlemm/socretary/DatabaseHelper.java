package de.lucasschlemm.socretary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_CALLER = "DatabaseHelper";

    private static DatabaseHelper mInstance = null;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Socretary.db";

    private Context mContext;


    public static DatabaseHelper getInstance(Context context){
        if (mInstance == null){
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        Log.d(LOG_CALLER, "Called the constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_CALLER, "Called onCreate");
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ContactEntry.DROP);
        db.execSQL(DatabaseContract.EncounterEntry.DROP);
        db.execSQL(DatabaseContract.ContactEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
    }


    private void createTables(SQLiteDatabase db){
        Log.d(LOG_CALLER, "Called createTables");
        Log.d(LOG_CALLER, DatabaseContract.ContactEntry.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.EncounterEntry.CREATE);
        db.execSQL(DatabaseContract.ContactEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
    }











    /**
     *
     * @param contact Contact object to insert into database
     * @return id of the inserted Contact
     */
    public long insertContact(Contact contact){
        Log.d(LOG_CALLER, "Called insertContact with contact object with name " + contact.getName());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (contact.getFrequency().length() == 0){
            contact.setFrequency("4"); // TODO aus SharedPrefs laden
        }
        if (contact.getId().length() > 0){
            values.put(DatabaseContract.ContactEntry._ID, contact.getId());
        }

        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NUMBER, contact.getNumber());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_FREQUENCY, contact.getFrequency());
        Log.d(LOG_CALLER, contact.getLocationHome().toString());
        if (contact.getLocationHome().length == 0) {
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONSTREET,"");
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONPOSTAL, "");
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCITY, "");
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCOUNTRY, "");
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONREGION, "");
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONHOOD, "");
        } else {
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONSTREET, contact.getLocationHome()[0]);
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONPOSTAL, contact.getLocationHome()[1]);
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCITY, contact.getLocationHome()[2]);
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCOUNTRY, contact.getLocationHome()[3]);
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONREGION, contact.getLocationHome()[4]);
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONHOOD, contact.getLocationHome()[5]);
        }
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_CREATEDON, Utils.getCurrentTime());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LASTCONTACT, contact.getLastContact());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_DELETED, 0);
        if (contact.getPicture() != null){
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE, Utils.blobify(contact.getPicture()));
        }

        long id = db.insertWithOnConflict(DatabaseContract.ContactEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (id != -1){
            return id;
        } else {
            Log.e(LOG_CALLER, "Insertion failed. Does the Contact already exist?");
            return -1;
        }
    }

    /**
     *
     * @param encounter Enconuter object to be inserted
     * @return id of the encounter
     */
    public long insertEncounter(Encounter encounter){
        Log.d(LOG_CALLER, "Called insertEncounter with Encounter object with description " + encounter.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID, encounter.getPersonId());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DESCRIPTION, encounter.getDescription());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DIRECTION, encounter.getDirection());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_MEANS, encounter.getMeans());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_TIMESTAMP, encounter.getTimestamp());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED, 0);
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_LENGTH, encounter.getLength());

        long id = db.insert(DatabaseContract.EncounterEntry.TABLE_NAME, null, values);
        if (id != -1){
            return id;
        } else {
            Log.e(LOG_CALLER, "Error while inserting encounter");
            return 0;
        }
    }

    /**
     *
     * @param contact Contact object with the new values to be updated in the database
     * @return boolean if update succeeded
     */
    public boolean updateContact(Contact contact){ // TODO test
        Log.d(LOG_CALLER, "Called updateContact with Contact object");
        SQLiteDatabase db = this.getWritableDatabase();

        String where = DatabaseContract.ContactEntry._ID + "=?";
        String[] whereArgs = { contact.getId() };

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_FREQUENCY, contact.getFrequency());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONSTREET, contact.getLocationHome()[0]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONPOSTAL, contact.getLocationHome()[1]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCITY, contact.getLocationHome()[2]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCOUNTRY, contact.getLocationHome()[3]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONREGION, contact.getLocationHome()[4]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONHOOD, contact.getLocationHome()[5]);
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_NUMBER, contact.getNumber());
        values.put(DatabaseContract.ContactEntry.COLUMN_NAME_LASTCONTACT, contact.getLastContact());
        if (contact.getPicture() != null){
            values.put(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE, Utils.blobify(contact.getPicture()));
        }

        int updated = db.update(DatabaseContract.ContactEntry.TABLE_NAME, values, where, whereArgs);

        return (updated != 0);
    }

    /**
     *
     * @return an ArrayList of Contacts in the DB
     */
    public ArrayList<Contact> getContactList(){ // TODO order of return ArrayList
        Log.d(LOG_CALLER, "Called getContactList");
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contact> returnContacts = new ArrayList<>();

        String[] projection = DatabaseContract.ContactEntry.PROJECTIONFULL;
        String selection = DatabaseContract.ContactEntry.COLUMN_NAME_DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        String sortOrder = DatabaseContract.ContactEntry.COLUMN_NAME_NAME + " ASC";

        Cursor c;
        c = db.query(
                DatabaseContract.ContactEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Log.d(LOG_CALLER, c.getCount() + " entries found for ContactList");

        c.moveToFirst();
        while (!c.isAfterLast()){
            String[] locationHome = new String[6];
            Contact temp = new Contact();

            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NAME)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NUMBER)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_FREQUENCY)));
            temp.setLastContact(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LASTCONTACT)));
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONHOOD));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE)) != null ){
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE))));
            }
            temp.setLocationHome(locationHome);

            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
        Log.d(LOG_CALLER, "GetContactList: All rows queried. Finished");
        return returnContacts;
    }

    /**
     * method that returns the contacts having birthday
     * @return ArrayList(Contact)
     */
    public ArrayList<Contact> getContactListBirthday(){
        Log.d(LOG_CALLER, "Called getContactListBirthday");
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contact> returnContacts = new ArrayList<>();

        // set up the search string for the current date
        JodaTimeAndroid.init(mContext);
        DateTime dateTime = new DateTime(new Date());
        DecimalFormat df = new DecimalFormat("00");
        String searchString = "%%%%-" + df.format(dateTime.monthOfYear().get()) + "-" + df.format(dateTime.dayOfMonth().get());

        String[] projection = {
                DatabaseContract.ContactEntry._ID,
                DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY,
                DatabaseContract.ContactEntry.COLUMN_NAME_NAME
        };
        String selection = DatabaseContract.ContactEntry.COLUMN_NAME_DELETED + " = ? AND " + DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY + " LIKE ?";
        String[] selectionArgs = { String.valueOf(0), searchString };
        String sortOrder = DatabaseContract.ContactEntry.COLUMN_NAME_NAME + " ASC";

        Cursor c;
        c = db.query(
                DatabaseContract.ContactEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Log.d(LOG_CALLER, c.getCount() + " entries found for ContactList");

        c.moveToFirst();
        while (!c.isAfterLast()){
            Contact temp = new Contact();
            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NAME)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY)));
            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
        Log.d(LOG_CALLER, "GetContactList: All rows queried. Finished");
        return returnContacts;
    }

    /**
     *
     * @param id long id of the Contact to be retrieved
     * @return Contact object of the person with the id, null if no Contact was found
     */
    public Contact getContact(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = DatabaseContract.ContactEntry.PROJECTIONFULL;
        String selection = DatabaseContract.ContactEntry._ID + " = ?" ;
        String[] selectionArgs = { String.valueOf(id) };

        Cursor c;
        c = db.query(
                DatabaseContract.ContactEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.getCount() > 0){
            c.moveToFirst();
            Contact temp = new Contact();

            String[] locationHome = {"","","","","",""};
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NAME)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_BIRTHDAY)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_NUMBER)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_FREQUENCY)));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE)) == null ){
                Log.d(LOG_CALLER, "getContactList - image null for contact" + temp.getName());
            } else {
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_IMAGE))));
            }
            temp.setId(id + "");
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMN_NAME_LOCATIONHOOD));
            temp.setLocationHome(locationHome);
            c.close();
            return temp;
        } else {
            Log.e(LOG_CALLER, "GetContact: No results for query with id " + id);
            return null;
        }
    }

    /**
     * returns the encounters for a certain Contact as an ArrayList(Encounter)
     * @param personId of the Contact
     * @return ArrayList(Encounter)
     */
    public ArrayList<Encounter> getEncounterListForContact(long personId){
        String selection = DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID + " = ? AND " + DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED + " = ?" ;
        String[] selectionArgs = { String.valueOf(personId), String.valueOf(0) };
        return getEncounterList(selection, selectionArgs);
    }

    /**
     * Method to get all the Enocunters from Database
     * @return Arraylist(Encounter) all Encounters in the DB
     */
    public ArrayList<Encounter> getEncounterListFull(){
        String selection = DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        return getEncounterList(selection, selectionArgs);
    }


    /**
     * set the DELETED flag to true
     * @param person_id id of the person to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteContact(long person_id){ // TODO test
        Log.d(LOG_CALLER, "Deletion of Contact with id " + person_id);
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = DatabaseContract.ContactEntry._ID + " = ?";
        String[] selectionArgs = { person_id + "" };

        int success = db.delete(
                DatabaseContract.ContactEntry.TABLE_NAME,
                selection,
                selectionArgs
        );

        return (success != 0);
    }

    /**
     * set the DELETED flag to true, can be reversed
     * @param encounterId id of the encounter to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteEncounter(long encounterId){ // TODO test
        Log.d(LOG_CALLER, "Deletion of encounter with id " + encounterId);
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = DatabaseContract.EncounterEntry._ID + " LIKE ?";
        String[] selectionArgs = { encounterId + "" };

        int count = db.delete(
                DatabaseContract.EncounterEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
        return (count != 0);
    }




    /**
     * Private method to make the other ones smaller
     * @param selection String of the selection
     * @param selectionArgs values for the selection
     * @return ArrayList(Encounter) list of the encounters matching the criteria
     */
    private ArrayList<Encounter> getEncounterList(String selection, String[] selectionArgs){ // TODO order of returnarraylist
        ArrayList<Encounter> encounters = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = DatabaseContract.EncounterEntry.PROJECTIONFULL;
        String groupBy = null;
        String having = null;
        String sortOrder = DatabaseContract.EncounterEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor c = db.query(
                DatabaseContract.EncounterEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        if (c.getCount() > 0){
            Log.d(LOG_CALLER, "GetEncounterList: " + c.getCount() + " entries found");
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()){
                Encounter tempEncounter = new Encounter();
                tempEncounter.setEncounterId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry._ID)));
                tempEncounter.setDescription(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_DESCRIPTION)));
                tempEncounter.setDirection(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_DIRECTION)));
                tempEncounter.setMeans(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_MEANS)));
                tempEncounter.setPersonId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID)));
                tempEncounter.setTimestamp(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_TIMESTAMP)));
                tempEncounter.setLength(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_LENGTH)));
                encounters.add(index, tempEncounter);
                index++;

                c.moveToNext();
            }
            c.close();
        } else {
            Log.e(LOG_CALLER, "GetEncounterList: No entries found");
        }
        return encounters;
    }

    /**
     * empties the database. This action is NOT REVERSIBLE
     */
    private void emptyTables(){
        SQLiteDatabase db = this.getWritableDatabase();

        int deletedPersons = db.delete(DatabaseContract.ContactEntry.TABLE_NAME, null, null);
        int deletedEncounters = db.delete(DatabaseContract.EncounterEntry.TABLE_NAME, null, null);
        Log.d(LOG_CALLER, "EmptyTables: Deleted " + deletedPersons + " Contacts with a total of  " + deletedEncounters + " deleted Encounters");
    }
}