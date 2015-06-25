package de.lucasschlemm.socretary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_CALLER, "onCreate-Methode der Datenbank aufgerufen");
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_CALLER, "Upgrade der Datenbank von Version " + oldVersion + " auf Version " + newVersion);
        dropTables(db);
        createTables(db);
    }

    private void dropTables(SQLiteDatabase db){
        db.execSQL(DatabaseContract.ContactEntry.DROP);
        db.execSQL(DatabaseContract.EncounterEntry.DROP);
//        db.execSQL(DatabaseContract.MessageForContact.DROP);
        db.execSQL(DatabaseContract.AutomatedMessage.DROP);
    }

    private void createTables(SQLiteDatabase db){
        Log.d(LOG_CALLER, "Called createTables");
        Log.d(LOG_CALLER, DatabaseContract.ContactEntry.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.EncounterEntry.CREATE);
		Log.d("DatabaseHelper", DatabaseContract.AutomatedMessage.CREATE);
        db.execSQL(DatabaseContract.ContactEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
		db.execSQL(DatabaseContract.AutomatedMessage.CREATE);
    }


    /**
     *
     * @param contact Contact object to insert into database
     * @return id of the inserted Contact
     */
    public long insertContact(Contact contact){
        Log.d(LOG_CALLER, "insertContact: neuer Kontakt mit Namen: " + contact.getName());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (contact.getFrequency().length() == 0){
            contact.setFrequency("4"); // TODO aus SharedPrefs laden
        }
        if (contact.getId().length() > 0){
            values.put(DatabaseContract.ContactEntry._ID, contact.getId());
        }

        values.put(DatabaseContract.ContactEntry.COLUMNS.NAME, contact.getName());
        values.put(DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.ContactEntry.COLUMNS.NUMBER, Utils.normalizeNumber(contact.getNumber()));
        values.put(DatabaseContract.ContactEntry.COLUMNS.FREQUENCY, contact.getFrequency());
        if (contact.getLocationHome().length == 0) {
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONSTREET,"");
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONPOSTAL, "");
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCITY, "");
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCOUNTRY, "");
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONREGION, "");
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOOD, "");
        } else {
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONSTREET, contact.getLocationHome()[0]);
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONPOSTAL, contact.getLocationHome()[1]);
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCITY, contact.getLocationHome()[2]);
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCOUNTRY, contact.getLocationHome()[3]);
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONREGION, contact.getLocationHome()[4]);
            values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOOD, contact.getLocationHome()[5]);
        }
        values.put(DatabaseContract.ContactEntry.COLUMNS.CREATEDON, Utils.getCurrentTime());
        values.put(DatabaseContract.ContactEntry.COLUMNS.LASTCONTACT, contact.getLastContact());
        values.put(DatabaseContract.ContactEntry.COLUMNS.DELETED, 0);
        if (contact.getPicture() != null){
            values.put(DatabaseContract.ContactEntry.COLUMNS.IMAGE, Utils.blobify(contact.getPicture()));
        }

        long id = db.insert(DatabaseContract.ContactEntry.TABLE_NAME, null, values);
        if (id != -1){
            return id;
        } else {
            Log.e(LOG_CALLER, "Kontakt konnte nicht gespeichert werden. Existiert er bereits?");
            return -1;
        }
    }

    public long insertEncounterAutomated(Encounter encounter){
        return insertEncounterGeneral(encounter, 1);
    }

    public long insertEncounterManual(Encounter encounter){
        return insertEncounterGeneral(encounter, 0);
    }

    private long insertEncounterGeneral(Encounter encounter, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        JodaTimeAndroid.init(mContext);
        long contactId = Long.parseLong(encounter.getPersonId());

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.EncounterEntry._ID, Long.parseLong(encounter.getTimestamp()));
        values.put(DatabaseContract.EncounterEntry.COLUMNS.CONTACTID, encounter.getPersonId());
        values.put(DatabaseContract.EncounterEntry.COLUMNS.DESCRIPTION, encounter.getDescription());
        values.put(DatabaseContract.EncounterEntry.COLUMNS.DIRECTION, encounter.getDirection());
        values.put(DatabaseContract.EncounterEntry.COLUMNS.TYPE, encounter.getMeans());
        values.put(DatabaseContract.EncounterEntry.COLUMNS.DELETED, 0);
        values.put(DatabaseContract.EncounterEntry.COLUMNS.LENGTH, encounter.getLength());
        values.put(DatabaseContract.EncounterEntry.COLUMNS.AUTOMATED, type);

        long id;
        if (type == 1){
            id = db.insertWithOnConflict(DatabaseContract.EncounterEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE );
        } else {
            id = rollingInsertion(values, 0);
        }

        // insert the new time into the Contact entry
        // find out the most current timestamp first
        long newTimestamp = getTimestampOfLatestEncounterForId(Long.parseLong(encounter.getPersonId()));
        String selection = DatabaseContract.ContactEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(contactId) };
        ContentValues values1 = new ContentValues();
        values1.put(DatabaseContract.ContactEntry.COLUMNS.LASTCONTACT, newTimestamp);
        int result = db.update(
                DatabaseContract.ContactEntry.TABLE_NAME,
                values1,
                selection,
                selectionArgs
        );
        if (result > 0){
            Log.e(LOG_CALLER, "Letztes Treffen: " + newTimestamp);
        } else {
            Log.d(LOG_CALLER, "Keine Aenderung wurde durchgefuehrt");
        }


        return id;
    }

	/**
	 * method for choosing a semi random timestamp (and thus id) for the manual Encounter
	 * if the user chooses to add several Encounters for the same time (e.g. 04/04/2015 12:00)
	 * @param values values to insert
	 * @param tryCount value that increments with every recursion to prevent clogging the thread
	 * @return id of the inserted Encounter
	 */
	private long rollingInsertion(ContentValues values, int tryCount){
		if (tryCount > 4){
			Log.d(LOG_CALLER, "Limit reached. Not inserting");
			return -1;
		}
		SQLiteDatabase db = this.getWritableDatabase();
		String suffix = String.format("%03d", new Random().nextInt(1000));
		String prefix = values.get(DatabaseContract.EncounterEntry._ID).toString().substring(0, 10);
		String idString = prefix + suffix;
		long newId = 1234567890123l;
		try {
			Log.d(LOG_CALLER, idString);
			newId = Long.parseLong(idString);
		} catch (Exception e){
			e.printStackTrace();
			Log.e(LOG_CALLER, "Numberformatexception for " + newId);
		}
		try {
			values.put(DatabaseContract.EncounterEntry._ID, newId);
			return db.insertWithOnConflict(DatabaseContract.EncounterEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
		} catch (SQLiteException e){
			Log.d(LOG_CALLER, "insertion failed. ID does already exist. Generating new suffix and trying again");
			tryCount++;
			return rollingInsertion(values, tryCount);
		}
	}

    /**
     * method for updating the Contact
     * @param contact Contact object with the new values to be updated in the database
     * @return true if update succeeded, false if not
     */
    public boolean updateContact(Contact contact){
        Log.d(LOG_CALLER, "updateContact");
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = DatabaseContract.ContactEntry._ID + "=?";
        String[] selectionArgs = { contact.getId() };

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.ContactEntry.COLUMNS.FREQUENCY, contact.getFrequency());
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONSTREET, contact.getLocationHome()[0]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONPOSTAL, contact.getLocationHome()[1]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCITY, contact.getLocationHome()[2]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCOUNTRY, contact.getLocationHome()[3]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONREGION, contact.getLocationHome()[4]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOOD, contact.getLocationHome()[5]);
        values.put(DatabaseContract.ContactEntry.COLUMNS.NAME, contact.getName());
        values.put(DatabaseContract.ContactEntry.COLUMNS.NUMBER, Utils.normalizeNumber(contact.getNumber()));
        values.put(DatabaseContract.ContactEntry.COLUMNS.LASTCONTACT, contact.getLastContact());
		values.put(DatabaseContract.ContactEntry.COLUMNS.NUMBERNORMALIZED, Utils.normalizeNumber(contact.getNumber()));
		values.put(DatabaseContract.ContactEntry.COLUMNS.POSSIBLEAUTOTEXTARRAY, Utils.stringifyMessageArray(contact.getPossibleTextArray()));
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOMEY, contact.getLocationHomeLat());
        values.put(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOMEX, contact.getLocationHomeLong());
        if (contact.getPicture() != null){
            values.put(DatabaseContract.ContactEntry.COLUMNS.IMAGE, Utils.blobify(contact.getPicture()));
        }
        int updated = db.update(DatabaseContract.ContactEntry.TABLE_NAME, values, selection, selectionArgs);

        return (updated != 0);
    }

    public Contact getContactNameImageById(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {
                DatabaseContract.ContactEntry._ID,
                DatabaseContract.ContactEntry.COLUMNS.NAME,
                DatabaseContract.ContactEntry.COLUMNS.IMAGE
        };
        String selection = DatabaseContract.ContactEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        String sortOrder = null;

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
        c.moveToFirst();
        Contact returnContact = new Contact();
        if (!c.isAfterLast()){
            returnContact.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NAME)));
            returnContact.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.IMAGE))));
        }
        return returnContact;
    }

    /**
     * method for getting all the Contacts in the database
     * @return an ArrayList of Contacts in the DB
     */
    public ArrayList<Contact> getContactList(){
		Log.v("DatabaseHelper", "getContactList: " + "called");
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contact> returnContacts = new ArrayList<>();

        String[] projection = DatabaseContract.ContactEntry.PROJECTIONFULL;
        String selection = DatabaseContract.ContactEntry.COLUMNS.DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        String sortOrder = DatabaseContract.ContactEntry.COLUMNS.NAME + " ASC";

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
        Log.v(LOG_CALLER, c.getCount() + " Kontakte wurden in der Datenbank gefunden");

        c.moveToFirst();
        while (!c.isAfterLast()){
            String[] locationHome = new String[6];
            Contact temp = new Contact();

            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NAME)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NUMBER)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.FREQUENCY)));
            temp.setLastContact(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LASTCONTACT)));
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOOD));
            temp.setLocationHomeLat(c.getDouble(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOMEY)));
            temp.setLocationHomeLong(c.getDouble(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOMEX)));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.IMAGE)) != null ){
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.IMAGE))));
            }
			temp.setPossibleAutoTextArray(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.POSSIBLEAUTOTEXTARRAY)));
            temp.setLocationHome(locationHome);

            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
		Log.v("DatabaseHelper", "getContactList: " + "beendet");
		return returnContacts;
    }


    /**
     * method for getting all the Contacts in the database
     * @return an ArrayList of Contacts in the DB
     */
    public ArrayList<Contact> getContactListNameNumberId(){
        Log.v("DatabaseHelper", "getContactList: " + "called");
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contact> returnContacts = new ArrayList<>();

        String[] projection = {
                DatabaseContract.ContactEntry._ID,
                DatabaseContract.ContactEntry.COLUMNS.NAME,
                DatabaseContract.ContactEntry.COLUMNS.NUMBER
        };
        String selection = DatabaseContract.ContactEntry.COLUMNS.DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        String sortOrder = DatabaseContract.ContactEntry.COLUMNS.NAME + " ASC";

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
        Log.v(LOG_CALLER, c.getCount() + " Kontakte wurden in der Datenbank gefunden");

        c.moveToFirst();
        while (!c.isAfterLast()){
            String[] locationHome = new String[6];
            Contact temp = new Contact();

            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NAME)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NUMBER)));

            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
        Log.v("DatabaseHelper", "getContactList: " + "beendet");
        return returnContacts;
    }

    /**
     * method that returns the contacts having birthday
     * @return ArrayList(Contact)
     */
    public ArrayList<Contact> getContactListBirthday(){
        Log.d(LOG_CALLER, "getContactListBirthday");
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contact> returnContacts = new ArrayList<>();

        // set up the search string for the current date
        JodaTimeAndroid.init(mContext);
        DateTime dateTime = new DateTime(new Date());
        DecimalFormat df = new DecimalFormat("00");
        String searchString = "%%%%-" + df.format(dateTime.monthOfYear().get()) + "-" + df.format(dateTime.dayOfMonth().get());

        String[] projection = {
                DatabaseContract.ContactEntry._ID,
                DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY,
                DatabaseContract.ContactEntry.COLUMNS.NAME
        };
        String selection = DatabaseContract.ContactEntry.COLUMNS.DELETED + " = ? AND " + DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY + " LIKE ?";
        String[] selectionArgs = { String.valueOf(0), searchString };
        String sortOrder = DatabaseContract.ContactEntry.COLUMNS.NAME + " ASC";

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
        Log.d(LOG_CALLER, c.getCount() + " Kontakte gefunden, die heute Geburtstag haben");

        c.moveToFirst();
        while (!c.isAfterLast()){
            Contact temp = new Contact();
            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NAME)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY)));
            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
        Log.d(LOG_CALLER, "GetContactList: Alle Zeilen abgefragt. Fertig.");
        return returnContacts;
    }

    public long addAutoText(String text){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AutomatedMessage.COLUMNS.CONTENT, text);
		values.put(DatabaseContract.AutomatedMessage.COLUMNS.SENTAMOUNT, 0);
        return db.insert(DatabaseContract.AutomatedMessage.TABLE_NAME, null, values);
    }

    public int deleteAutoText(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = DatabaseContract.AutomatedMessage._ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(DatabaseContract.AutomatedMessage.TABLE_NAME, selection, selectionArgs);
    }

    public ArrayList<AutomatedMessage> getAutoTextList(){
        ArrayList<AutomatedMessage> automatedMessages = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                DatabaseContract.AutomatedMessage._ID,
                DatabaseContract.AutomatedMessage.COLUMNS.CONTENT
        };

        Cursor c = db.query(DatabaseContract.AutomatedMessage.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
                );

        c.moveToFirst();
		while (!c.isAfterLast()){
            AutomatedMessage temp = new AutomatedMessage();
            temp.setText(c.getString(c.getColumnIndexOrThrow(DatabaseContract.AutomatedMessage.COLUMNS.CONTENT)));
            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.AutomatedMessage._ID)));
            automatedMessages.add(temp);
			c.moveToNext();
        }
		c.close();
        return automatedMessages;
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
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NAME)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.BIRTHDAY)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.NUMBER)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.FREQUENCY)));
            temp.setLastContact(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LASTCONTACT)));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.IMAGE)) == null ){
                Log.d(LOG_CALLER, "getContactList: Bild ist null bei " + temp.getName());
            } else {
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.IMAGE))));
            }
            temp.setId(id + "");
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactEntry.COLUMNS.LOCATIONHOOD));
            temp.setLocationHome(locationHome);
            c.close();
            return temp;
        } else {
            Log.e(LOG_CALLER, "GetContact: Keine Ergebnisse bei der Suche nach " + id);
            return null;
        }
    }

    /**
     * returns the encounters for a certain Contact as an ArrayList(Encounter)
     * @param personId of the Contact
     * @return ArrayList(Encounter)
     */
    public ArrayList<Encounter> getEncounterListForContact(long personId){
        String selection = DatabaseContract.EncounterEntry.COLUMNS.CONTACTID + " = ? AND " + DatabaseContract.EncounterEntry.COLUMNS.DELETED + " = ?" ;
        String[] selectionArgs = { String.valueOf(personId), String.valueOf(0) };
        return getEncounterList(selection, selectionArgs);
    }

    /**
     * Method to get all the Enocunters from Database
     * @return Arraylist(Encounter) all Encounters in the DB
     */
    public ArrayList<Encounter> getEncounterListFull(){
        String selection = DatabaseContract.EncounterEntry.COLUMNS.DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        return getEncounterList(selection, selectionArgs);
    }

    private long getTimestampOfLatestEncounterForId(long personid){
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = DatabaseContract.EncounterEntry.COLUMNS.CONTACTID + " = ?";
        String[] selectionArgs = { String.valueOf(personid) };
        String[] projection = {
                DatabaseContract.EncounterEntry._ID,
                DatabaseContract.EncounterEntry.COLUMNS.CONTACTID
        };
        String sortOrder = DatabaseContract.EncounterEntry._ID + " DESC";
        String limit = "1";

        Cursor c = db.query(
				DatabaseContract.EncounterEntry.TABLE_NAME,
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder,
				limit
		);
        if (c.getCount() > 0){
            Log.d(LOG_CALLER, "GetEncounterList: " + c.getCount() + " Einträge gefunden");
            c.moveToFirst();
            long timestamp = Long.parseLong(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry._ID)));
            c.close();
            return timestamp;
        } else {
            Log.e(LOG_CALLER, "GetEncounterList: Keine Einträge gefunden");
            return 0l;
        }
    }


    /**
     * set the DELETED flag to true
     * @param person_id id of the person to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteContact(long person_id){
        Log.d(LOG_CALLER, "Loeschung des Kontakts mit der ID " + person_id);
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
    public boolean deleteEncounter(long encounterId){
        Log.d(LOG_CALLER, "Löschen des Treffens mit der ID " + encounterId);
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
    private ArrayList<Encounter> getEncounterList(String selection, String[] selectionArgs){
        ArrayList<Encounter> encounters = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = DatabaseContract.EncounterEntry.PROJECTIONFULL;
        String sortOrder = DatabaseContract.EncounterEntry._ID + " DESC";

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
            Log.d(LOG_CALLER, "GetEncounterList: " + c.getCount() + " Einträge gefunden");
            c.moveToFirst();
            while (!c.isAfterLast()){
                Encounter tempEncounter = new Encounter();
                tempEncounter.setEncounterId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry._ID)));
                tempEncounter.setDescription(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMNS.DESCRIPTION)));
                tempEncounter.setDirection(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMNS.DIRECTION)));
                tempEncounter.setMeans(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMNS.TYPE)));
                tempEncounter.setPersonId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMNS.CONTACTID)));
                tempEncounter.setTimestamp(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry._ID)));
                tempEncounter.setLength(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMNS.LENGTH)));
                encounters.add(tempEncounter);

                c.moveToNext();
            }
            c.close();
        } else {
            Log.e(LOG_CALLER, "GetEncounterList: Keine Einträge gefunden");
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
        Log.d(LOG_CALLER, "EmptyTables: " + deletedPersons + " Kontakte und " + deletedEncounters + " Treffen wurden gelöscht");
    }

    /**
	 *
	 *
	 *
	 *
     * This is the section for the AsyncTasks to execute the performance heavy tasks in a background thread
	 *
	 *
	 *
	 *
     */


    public AsyncGetContactList getAsyncGetContactList(){
        return new AsyncGetContactList();
    }

    class AsyncGetContactList extends AsyncTask<Integer, Integer, ArrayList<Contact>> {
        public AsyncGetContactList(){}
        @Override
        protected ArrayList<Contact> doInBackground(Integer... integers) {
            return getContactList();
        }

		/**
		 * dies könnte ein Beispiel dafür sein, wie das UI nach einem AsyncTask aktualisiert wird
		 * kann natürlich auch in einer Extra-Klasse durchgeführt werden
		 * in den Fragments wird das ganze dann aufgerufen, indem die Klasse instanziiert wird
		 * anschließend wird die execute-Methode aufgerufen
		 * @param contacts ArrayList of the contacts to be inserted into the UI
		 */
		@Override
		protected void onPostExecute(ArrayList<Contact> contacts) {
			Activity activity = ApplicationContext.getActivity();
			TextView tv = (TextView) activity.findViewById(R.id.tV_con_dialogName);
//			tv.setText(contacts.get(0).getLocationTime());
		}
	}



    public AsyncContactListBirthday getAsyncContactListBirthday(){
        return new AsyncContactListBirthday();
    }

    class AsyncContactListBirthday extends AsyncTask<Integer, Integer, ArrayList<Contact>> {
        @Override
        protected ArrayList<Contact> doInBackground(Integer... integers) {
            return getContactListBirthday();
        }

		@Override
		protected void onPostExecute(ArrayList<Contact> contacts) {
			super.onPostExecute(contacts);
		}
	}

    public AsyncEncounterListForContact getAsyncEncounterListForContact(){
        return new AsyncEncounterListForContact();
    }

    class AsyncEncounterListForContact extends AsyncTask<Integer, Integer, ArrayList<Encounter>> {
        @Override
        protected ArrayList<Encounter> doInBackground(Integer... integers) {
            return getEncounterListForContact(integers[0]);
        }

		@Override
		protected void onPostExecute(ArrayList<Encounter> encounters) {

		}
	}

	public AsyncTextTemplateList getAsyncTextTemplateList(){
		return new AsyncTextTemplateList();
	}

	class AsyncTextTemplateList extends AsyncTask<Integer, Integer, ArrayList<AutomatedMessage>>{
		@Override
		protected ArrayList<AutomatedMessage> doInBackground(Integer... integers) {
			return getAutoTextList();
		}

		@Override
		protected void onPostExecute(ArrayList<AutomatedMessage> automatedMessages) {

		}
	}
}