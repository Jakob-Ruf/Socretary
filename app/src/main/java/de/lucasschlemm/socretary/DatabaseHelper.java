package de.lucasschlemm.socretary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String LOG_CALLER = "DatabaseHelper";
    SQLiteDatabase db;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Socretary.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(LOG_CALLER, "Called the constructor");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(LOG_CALLER, "Called onCreate");
        db = this.getWritableDatabase();
        createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.CategoryEntry.DROP);
        db.execSQL(DatabaseContract.PersonEntry.DROP);
        db.execSQL(DatabaseContract.EncounterEntry.DROP);
        db.execSQL(DatabaseContract.Person_in_category.DROP);
        db.execSQL(DatabaseContract.CategoryEntry.CREATE);
        db.execSQL(DatabaseContract.PersonEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
        db.execSQL(DatabaseContract.Person_in_category.CREATE);

    }

    public void createTables(){
        Log.d(LOG_CALLER, "Called createTables");
        Log.d(LOG_CALLER, DatabaseContract.PersonEntry.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.Person_in_category.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.EncounterEntry.CREATE);
        Log.d(LOG_CALLER, DatabaseContract.CategoryEntry.CREATE);
        db.execSQL(DatabaseContract.CategoryEntry.CREATE);
        db.execSQL(DatabaseContract.PersonEntry.CREATE);
        db.execSQL(DatabaseContract.EncounterEntry.CREATE);
        db.execSQL(DatabaseContract.Person_in_category.CREATE);
    }




    public long insertPerson(String name, String number, String birthday, int frequency, boolean notification){
        Log.d(LOG_CALLER, "Called insertPerson for person " + name + " with frequency " + frequency);
        ContentValues values = new ContentValues();
//        values.put(DatabaseContract.PersonEntry._ID, id);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER, number);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY, birthday);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY, frequency);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NOTIFICATION, notification);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_CREATEDON, Utils.getCurrentTime());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_DELETED, false);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE, "");
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGETHUMB, "");

        return db.insert(DatabaseContract.PersonEntry.TABLE_NAME, null, values);
    }

    public long insertPerson(Contact contact, boolean notification){
        Log.d(LOG_CALLER, "Called insertPerson with contact object with name " + contact.getName());
        ContentValues values = new ContentValues();
//        values.put(DatabaseContract.PersonEntry._ID, contact.getId());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NAME, contact.getName());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY, contact.getBirthday());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER, contact.getNumber());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY, contact.getFrequency());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_NOTIFICATION, notification);
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOME, contact.getLocationHome());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_CREATEDON, Utils.getCurrentTime());
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE, "");
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_IMAGETHUMB, "");
        values.put(DatabaseContract.PersonEntry.COLUMN_NAME_DELETED, false);
        return db.insert(DatabaseContract.PersonEntry.TABLE_NAME, null, values);
    }



    /**
     * Insert a new Encounter for a certain Person
     * @param db SQLiteDatabase as in DatabaseHelper.getWriteableDatabase()
     * @param description Description of the encounter
     * @param timestamp Time when the encounter happened
     * @param direction Direction of the encounter as specified in DatabaseContract.Encounter Direction constants
     * @param person_id ID of the person the encounter happened with
     * @param means Means of communication the encounter happened with as specified in DatabaseContract.Encounter Means constants
     * @return ID of the encounter entry as a long
     */
    public long insertEncounter(String description, long timestamp, int direction, long person_id, int means){
//        Insert a new Encounter into the EncounterEntry table
        Log.d(LOG_CALLER, "Called insertEncounter for person " + person_id + " with description " + description);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DIRECTION, direction);
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_TIMESTAMP, Utils.getCurrentTime());
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED, false);
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_MEANS, means);
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_PERSONID, person_id);

        return db.insert(DatabaseContract.EncounterEntry.TABLE_NAME, null, values);
    }


    /**
     *
     * @param db SQLiteDatabase as in DatabaseHelper.getWriteableDatabase()
     * @param name Name of the category
     * @param description Description of the category
     * @return ID of the category as a long
     */
    public long insertCategory(String name, String description) {
        Log.d(LOG_CALLER, "Called insertCategory with name " + name + " and description " + description);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_DESCRIPTION, description);
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_DELETED, false);

        return db.insert(DatabaseContract.CategoryEntry.TABLE_NAME, null, values);
    }

    public void getPersonEntries(){
        Log.d(LOG_CALLER, "Called getPersonEntries");
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                DatabaseContract.PersonEntry.COLUMN_NAME_NAME,
                DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY,
                DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY,
                DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOME,
                DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE,
                DatabaseContract.PersonEntry.COLUMN_NAME_IMAGETHUMB
        };


        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = DatabaseContract.PersonEntry.COLUMN_NAME_NAME + " DESC";

        Cursor c;
        c = db.query(
                DatabaseContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Log.d("DatabaseQuery:", c.getCount()+" Einträge wurden gefunden");
        c.moveToFirst();
        while (!c.isAfterLast()){
            String fullName = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NAME));
            String birthday = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY));
            String number = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER));
            String locationHome = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOME));
            String frequency = c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY));

            Log.d(LOG_CALLER,  fullName + " - " + birthday + " - " + number + " - " + frequency + " - " + locationHome );
            c.moveToNext();
        }
        c.close();
        Log.d(LOG_CALLER, "All rows queried. Finished");

    }

    public Contact getPerson(long id){
        Contact contact = new Contact();

        Log.d(LOG_CALLER, "Called getPerson");
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                DatabaseContract.PersonEntry.COLUMN_NAME_NAME,
                DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY,
                DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER,
                DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY,
                DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOME,
                DatabaseContract.PersonEntry.COLUMN_NAME_IMAGE,
                DatabaseContract.PersonEntry.COLUMN_NAME_IMAGETHUMB
        };

        String selection = DatabaseContract.PersonEntry._ID + " LIKE ?" ;
        String[] selectionArgs = { id+"" };
        String sortOrder = DatabaseContract.PersonEntry.COLUMN_NAME_NAME + " DESC";

        Cursor c;
        c = db.query(
                DatabaseContract.PersonEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        Log.d("DatabaseQuery:", c.getCount()+" Einträge wurden gefunden");

        c.moveToFirst();
        String fullName = "bla " + c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NAME));
        String birthday = "bla" + c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_BIRTHDAY));
        String number = "bla" + c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_NUMBER));
        String locationHome = "bla" + c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_LOCATIONHOME));
        String frequency = "bla" + c.getString(c.getColumnIndexOrThrow(DatabaseContract.PersonEntry.COLUMN_NAME_FREQUENCY));

        Log.d(LOG_CALLER, fullName + " - " + birthday + " - " + number + " - " + frequency + " - " + locationHome);
        c.close();
        contact.setBirthday(birthday);
        contact.setFrequency(frequency);
        contact.setId(id + "");
        contact.setLocationHome(locationHome);
        contact.setNumber(number);
        contact.setName(fullName);
        return contact;
    }

    /**
     * add a person to a category
     * @param person_id id of the person
     * @param category_name name of the category
     * @return true if operation succeeded
     */
    public boolean addPersonToCategory(long person_id, String category_name){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Person_in_category.COLUMN_NAME_PERSONID, person_id);
        values.put(DatabaseContract.Person_in_category.COLUMN_NAME_CATEGORYNAME, category_name);

        return (db.insert(DatabaseContract.Person_in_category.TABLE_NAME, null, values) != -1);
    }

    /**
     * set the DELETED flag to true
     * @param person_id id of the person to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deletePerson(long person_id){
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
     * set the DELETED flag to true
     * @param contact_id id of the encounter to delete
     * @return true if operation succeeded, false if no row affected
     */
    public boolean deleteEncounter(long contact_id){
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.EncounterEntry.COLUMN_NAME_DELETED, true);

        String selection = DatabaseContract.EncounterEntry._ID + " LIKE ?";
        String[] selectionArgs = { contact_id + "" };

        int count = db.update(
                DatabaseContract.EncounterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        return (count != 0);
    }

    /**
     * set the DELETED flag to true
     * @param category_name The name of the category as identifier
     * @return true if the operation succeeded, false if no row affected
     */
    public boolean deleteCategory(String category_name){
        SQLiteDatabase db;
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.CategoryEntry.COLUMN_NAME_DELETED, true);

        String selection = DatabaseContract.CategoryEntry.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = { category_name };

        int count = db.update(
                DatabaseContract.CategoryEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        return (count != 0);
    }
}