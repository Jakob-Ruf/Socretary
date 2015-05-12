package de.lucasschlemm.socretary;

import android.provider.BaseColumns;

public class DatabaseContract {

    public DatabaseContract(){}

    public static final String SEP_COMMA = ", ";
    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_INT = " INTEGER";
    public static final String TYPE_PRIMARYKEY = " INTEGER PRIMARY KEY";
    public static final String TYPE_BLOB = " BLOB";
    public static final String TYPE_TIMESTAMP = " INTEGER";
    public static final String TYPE_BOOL = " INTEGER";
    public static final String TYPE_ID = " INTEGER";
    public static final String CREATE_START = "CREATE TABLE IF NOT EXISTS ";
    public static final String DROP_START = "DROP TABLE ";


    /**
     * Klasse für den Eintrag einer Person
     */
    public static abstract class PersonEntry implements BaseColumns {
        public static final String TABLE_NAME = "person";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_FREQUENCY = "limit1";
        public static final String COLUMN_NAME_CREATEDON = "created_on";
        public static final String COLUMN_NAME_NOTIFICATION = "notification";
        public static final String COLUMN_NAME_DELETED = "deleted";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_BIRTHDAY = "birthday";
        public static final String COLUMN_NAME_LOCATIONSTREET = "addrStreet";
        public static final String COLUMN_NAME_LOCATIONPOSTAL = "addrPostal";
        public static final String COLUMN_NAME_LOCATIONCITY = "addrCity";
        public static final String COLUMN_NAME_LOCATIONCOUNTRY = "addrCountry";
        public static final String COLUMN_NAME_LOCATIONREGION = "addrRegion";
        public static final String COLUMN_NAME_LOCATIONHOOD = "addrHood";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_LOCATIONX = "locationX"; // TODO
        public static final String COLUMN_NAME_LOCATIONY = "locationY"; // TODO
        public static final String COLUMN_NAME_LOCATIONTIME = "locationTime"; // TODO

        public static final String COLUMN_NAME_LASTCONTACT = "last_contact";

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" +
                _ID + TYPE_PRIMARYKEY + SEP_COMMA +
                COLUMN_NAME_NAME + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_NUMBER + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_FREQUENCY + TYPE_INT + SEP_COMMA +
                COLUMN_NAME_BIRTHDAY + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_NOTIFICATION + TYPE_BOOL + SEP_COMMA +
                COLUMN_NAME_IMAGE + TYPE_BLOB + SEP_COMMA +
                COLUMN_NAME_CREATEDON + TYPE_TIMESTAMP + SEP_COMMA +
                COLUMN_NAME_DELETED + TYPE_BOOL + SEP_COMMA +
                COLUMN_NAME_LOCATIONSTREET + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LOCATIONPOSTAL + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LOCATIONCITY + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LOCATIONCOUNTRY + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LOCATIONREGION + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LOCATIONHOOD + TYPE_TEXT + SEP_COMMA +
                COLUMN_NAME_LASTCONTACT + TYPE_TEXT +
                ")";
        public static final String DROP = DROP_START + TABLE_NAME;

        public static final String[] PROJECTIONFULL = {
                _ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_BIRTHDAY,
                COLUMN_NAME_NUMBER,
                COLUMN_NAME_FREQUENCY,
                COLUMN_NAME_LOCATIONSTREET,
                COLUMN_NAME_LOCATIONPOSTAL,
                COLUMN_NAME_LOCATIONCITY,
                COLUMN_NAME_LOCATIONCOUNTRY,
                COLUMN_NAME_LOCATIONREGION,
                COLUMN_NAME_LOCATIONHOOD,
                COLUMN_NAME_LASTCONTACT,
                COLUMN_NAME_IMAGE
        };
    }


    /**
     * Klasse für den Eintrag einer Personen-Kategorie
     */
    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "category";

        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DELETED = "deleted";

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" + COLUMN_NAME_NAME + TYPE_PRIMARYKEY + SEP_COMMA + COLUMN_NAME_DESCRIPTION + TYPE_TEXT + SEP_COMMA + COLUMN_NAME_DELETED + TYPE_BOOL + ")";
        public static final String DROP = DROP_START + TABLE_NAME;
    }

    /**
     * Klasse für den Eintrag eines Treffens. Stellt Konstanten für die Art des Treffens sowie für die Richtung an
     */
    public static abstract class EncounterEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact";
        public static final int DIRECTION_COINCIDENCE = 0;
        public static final int DIRECTION_INBOUND = 1;
        public static final int DIRECTION_OUTBOUND = 2;
        public static final int DIRECTION_MUTUAL = 3;
        public static final int MEANS_PERSONAL = 0;
        public static final int MEANS_PHONE = 1;
        public static final int MEANS_MESSENGER = 2;
        public static final int MEANS_MAIL = 3;
        public static final int MEANS_SOCIALNETWORK = 4;

        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_DIRECTION = "direction";
        public static final String COLUMN_NAME_DELETED = "deleted";
        public static final String COLUMN_NAME_MEANS = "means";
        public static final String COLUMN_NAME_PERSONID = "person_id";

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" + _ID + TYPE_PRIMARYKEY + SEP_COMMA + COLUMN_NAME_PERSONID + TYPE_INT + SEP_COMMA + COLUMN_NAME_DESCRIPTION + TYPE_TEXT + SEP_COMMA + COLUMN_NAME_DIRECTION + TYPE_INT + SEP_COMMA + COLUMN_NAME_TIMESTAMP + TYPE_TIMESTAMP + SEP_COMMA + COLUMN_NAME_DELETED + TYPE_BOOL + SEP_COMMA + COLUMN_NAME_MEANS + TYPE_INT + ")";
        public static final String DROP = DROP_START + TABLE_NAME;

        public static final String[] PROJECTIONFULL = {
                _ID,
                COLUMN_NAME_PERSONID,
                COLUMN_NAME_DESCRIPTION,
                COLUMN_NAME_DIRECTION,
                COLUMN_NAME_MEANS,
                COLUMN_NAME_TIMESTAMP
        };
    }


    /**
     * Klasse für die Verknüpfung von Person und Kategorie
     */
    public static abstract class Person_in_category implements BaseColumns {
        public static final String TABLE_NAME = "person_in_category";

        public static final String COLUMN_NAME_PERSONID = "id_person";
        public static final String COLUMN_NAME_CATEGORYNAME = "name_category";

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" + COLUMN_NAME_PERSONID + TYPE_ID + SEP_COMMA + COLUMN_NAME_CATEGORYNAME + TYPE_TEXT + SEP_COMMA + " PRIMARY KEY (" + COLUMN_NAME_CATEGORYNAME + SEP_COMMA + COLUMN_NAME_PERSONID + "))";
        public static final String DROP = DROP_START + TABLE_NAME;
    }

}
