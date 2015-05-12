package de.lucasschlemm.socretary;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

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
        db.execSQL(DatabaseContract.PersonEntry.DROP);
        db.execSQL(DatabaseContract.EncounterEntry.DROP);
        db.execSQL(DatabaseContract.PersonEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
    }


    public void createTables(SQLiteDatabase db){
        Log.d(LOG_CALLER, "Called createTables");
        Log.d(LOG_CALLER, DatabaseContract.PersonEntry.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.EncounterEntry.CREATE);
        db.execSQL(DatabaseContract.PersonEntry.CREATE);
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
            values.put(DatabaseContract.PersonEntry._ID, contact.getId());
        }

        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER, contact.getNumber());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY, contact.getFrequency());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONSTREET, contact.getLocationHome()[0]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONPOSTAL, contact.getLocationHome()[1]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCITY, contact.getLocationHome()[2]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCOUNTRY, contact.getLocationHome()[3]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONREGION, contact.getLocationHome()[4]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOOD, contact.getLocationHome()[5]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_CREATEDON, Utils.getCurrentTime());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_DELETED, 0);
        if (contact.getPicture() != null){
            values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE, Utils.blobify(contact.getPicture())); // TODO new
        }

        long id = db.insertWithOnConflict(DatabaseContract.PersonEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

        String where = DatabaseContract.PersonEntry._ID + "=?";
        String[] whereArgs = { contact.getId() };

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY, contact.getFrequency());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONSTREET, contact.getLocationHome()[0]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONPOSTAL, contact.getLocationHome()[1]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCITY, contact.getLocationHome()[2]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCOUNTRY, contact.getLocationHome()[3]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONREGION, contact.getLocationHome()[4]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOOD, contact.getLocationHome()[5]);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER, contact.getNumber());
        if (contact.getPicture() != null){
            values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE, Utils.blobify(contact.getPicture()));  // TODO new
        }

        int updated = db.update(DatabaseContract.PersonEntry.TABLE_NAME, values, where, whereArgs);

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

        String[] projection = DatabaseContract.PersonEntry.PROJECTIONFULL;
        String selection = DatabaseContract.PersonEntry.COLUMN_NAME_DELETED + " = ?";
        String[] selectionArgs = { String.valueOf(0) };
        String groupBy = null;
        String having = null;
        String sortOrder = DatabaseContract.PersonEntry.COLUMN_NAME_NAME + " DESC";

        Cursor c;
        c = db.query(
                DatabaseContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );
        Log.d(LOG_CALLER, c.getCount() + " entries found for ContactList");

        c.moveToFirst();
        while (!c.isAfterLast()){
            String[] locationHome = new String[6];
            Contact temp = new Contact();

            temp.setId(c.getLong(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry._ID)) + "");
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NAME)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY)));
            temp.setLastContact(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LASTCONTACT)));
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOOD));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE)) == null ){
                Log.d(LOG_CALLER, "getContactList - image null for contact" + temp.getName());
            } else {
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE)))); // TODO new
            }
            temp.setLocationHome(locationHome);

            returnContacts.add(temp);
            c.moveToNext();
        }
        c.close();
        Log.d(LOG_CALLER, "All rows queried. Finished");
        Intent intent = new Intent();
        intent.setAction("ajdsklasj");
        return returnContacts;
    }

    /**
     *
     * @param id long id of the Contact to be retrieved
     * @return Contact object of the person with the id, null if no Contact was found
     */
    public Contact getContact(long id){
        Log.d(LOG_CALLER, "Called getContactById with id " + id);

        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = DatabaseContract.PersonEntry.PROJECTIONFULL;
        String selection = DatabaseContract.PersonEntry._ID + " = ?" ;
        String[] selectionArgs = { String.valueOf(id) };
        String groupBy = null;
        String having = null;
        String sortOrder = null;

        Cursor c;
        c = db.query(
                DatabaseContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );

        Log.d("DatabaseQuery:", c.getCount() + " entries found");
        if (c.getCount() > 0){
            c.moveToFirst();
            Contact temp = new Contact();

            String[] locationHome = {"","","","","",""};
            temp.setName(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NAME)));
            temp.setBirthday(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY)));
            temp.setNumber(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER)));
            temp.setFrequency(c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY)));
            if (c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE)) == null ){
                Log.d(LOG_CALLER, "getContactList - image null for contact" + temp.getName());
            } else {
                temp.setPicture(Utils.bitmapify(c.getBlob(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE)))); // TODO new
            }
            temp.setId(id + "");
            locationHome[0] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONSTREET));
            locationHome[1] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONPOSTAL));
            locationHome[2] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCITY));
            locationHome[3] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONCOUNTRY));
            locationHome[4] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONREGION));
            locationHome[5] = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOOD));
            temp.setLocationHome(locationHome);
            c.close();
            return temp;
        } else {
            Log.e(LOG_CALLER, "No results for query Contact with id " + id);
            return null;
        }
    }

    /**
     * returns the encounters for a certain Contact as an ArrayList(Encounter)
     * @param personId of the Contact
     * @return ArrayList(Encounter)
     */
    public ArrayList<Encounter> getContactEncounterList(long personId){ // TODO order of returnarraylist
        Log.d(LOG_CALLER, "Called getContactEncounterList for Contact with id " + personId);
        ArrayList<Encounter> encounters = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = DatabaseContract.EncounterEntry.PROJECTIONFULL;
        String selection = DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID + " = ? AND " + DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED + " = ?" ;
        String[] selectionArgs = { String.valueOf(personId), String.valueOf(0) };
        String groupBy = null;
        String having = null;
        String sortOrder = DatabaseContract.EncounterEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor c;
        c = db.query(
                DatabaseContract.EncounterEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                sortOrder
        );

        if (c.getCount() > 0){
            Log.d(LOG_CALLER, c.getCount() + " entries found");
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()){
                Encounter temp = new Encounter();
                temp.setEncounterId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry._ID)));
                temp.setDescription(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_DESCRIPTION)));
                temp.setDirection(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_DIRECTION)));
                temp.setMeans(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_MEANS)));
                temp.setPersonId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID)));
                encounters.add(index, temp);
                index++;
                c.moveToNext();
            }
            c.close();
        } else {
            Log.e(LOG_CALLER, "No entries found");
        }

        return encounters;
    }




    /**
     * set the DELETED flag to true
     * @param person_id id of the person to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteContact(long person_id){ // TODO test
        Log.d(LOG_CALLER, "Deletion of Contact with id " + person_id);
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_DELETED, true);

        String selection = DatabaseContract.PersonEntry._ID + " LIKE ?";
        String[] selectionArgs = { person_id + "" };

        int count = db.update(
                DatabaseContract.PersonEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return (count != 0);
    }




    /**
     * set the DELETED flag to true, can be reversed
     * @param encounterId id of the encounter to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteEncounter(long encounterId){ // TODO test
        Log.d(LOG_CALLER, "Deletion of encounter with id " + encounterId);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED, true);

        String selection = DatabaseContract.EncounterEntry._ID + " LIKE ?";
        String[] selectionArgs = { encounterId + "" };

        int count = db.update(
                DatabaseContract.EncounterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return (count != 0);
    }

    /**
     * empties the database. This action is NOT REVERSIBLE
     */
    public void emptyTables(){
        SQLiteDatabase db = this.getWritableDatabase();

        int deletedPersons = db.delete(DatabaseContract.PersonEntry.TABLE_NAME, null, null);
        int deletedEncounters = db.delete(DatabaseContract.EncounterEntry.TABLE_NAME, null, null);
        Log.d(LOG_CALLER, "Deleted " + deletedPersons + " Contacts with a total of  " + deletedEncounters + " deleted Encounters");
    }

    private void t(String text){
        Toast.makeText(this.mContext, text, Toast.LENGTH_LONG).show();
    }
}