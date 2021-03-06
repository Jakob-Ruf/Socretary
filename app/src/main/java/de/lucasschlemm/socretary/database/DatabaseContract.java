package de.lucasschlemm.socretary.database;

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
    public static final String CREATE_START = "CREATE TABLE IF NOT EXISTS ";
    public static final String DROP_START = "DROP TABLE ";


    /**
     * Klasse für den Eintrag einer Person
     */
    public static abstract class ContactEntry implements BaseColumns {
        public static final String TABLE_NAME = "contact";
        
        public static abstract class COLUMNS {
            public static final String NAME = "name";
            public static final String FREQUENCY = "frequency";
            public static final String CREATEDON = "created_on";
            public static final String DELETED = "deleted";
            public static final String NUMBER = "number";
            public static final String NUMBERNORMALIZED = "number_normalized";
            public static final String BIRTHDAY = "birthday";
            public static final String LOCATIONSTREET = "addr_Street";
            public static final String LOCATIONPOSTAL = "addr_Postal";
            public static final String LOCATIONCITY = "addr_City";
            public static final String LOCATIONREGION = "addr_Region";
            public static final String LOCATIONHOOD = "addr_Hood";
            public static final String LOCATIONCOUNTRY = "addr_Country";
            public static final String LOCATIONHOMEX = "addr_X";
            public static final String LOCATIONHOMEY = "addr_Y";
            public static final String IMAGE = "image";
            public static final String LASTCONTACT = "last_contact";
            public static final String LOCATIONX = "location_X";
            public static final String LOCATIONY = "location_Y";
            public static final String LOCATIONTIME = "location_Time";
            public static final String POSSIBLEAUTOTEXTARRAY = "possible_Auto_Text_Array";
        }

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" +
                _ID + TYPE_PRIMARYKEY + SEP_COMMA +
                COLUMNS.NAME + TYPE_TEXT + SEP_COMMA +
                COLUMNS.NUMBER + TYPE_TEXT + SEP_COMMA +
                COLUMNS.FREQUENCY + TYPE_INT + SEP_COMMA +
                COLUMNS.BIRTHDAY + TYPE_TEXT + SEP_COMMA +
                COLUMNS.IMAGE + TYPE_BLOB + SEP_COMMA +
                COLUMNS.CREATEDON + TYPE_TIMESTAMP + SEP_COMMA +
                COLUMNS.DELETED + TYPE_BOOL + SEP_COMMA +
                COLUMNS.LOCATIONSTREET + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONPOSTAL + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONCITY + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONCOUNTRY + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONREGION + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONHOOD + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONHOMEX + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONHOMEY + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LASTCONTACT + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONX + TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONY+ TYPE_TEXT + SEP_COMMA +
                COLUMNS.LOCATIONTIME + TYPE_TEXT + SEP_COMMA +
                COLUMNS.NUMBERNORMALIZED + TYPE_TEXT + SEP_COMMA +
                COLUMNS.POSSIBLEAUTOTEXTARRAY + TYPE_TEXT +
                ")";

        public static final String DROP = DROP_START + TABLE_NAME;

        public static final String[] PROJECTIONFULL = {
                _ID,
                COLUMNS.NAME,
                COLUMNS.NUMBER,
                COLUMNS.FREQUENCY,
                COLUMNS.BIRTHDAY,
                COLUMNS.IMAGE,
                COLUMNS.CREATEDON,
                COLUMNS.LOCATIONSTREET,
                COLUMNS.LOCATIONPOSTAL,
                COLUMNS.LOCATIONCITY,
                COLUMNS.LOCATIONCOUNTRY,
                COLUMNS.LOCATIONREGION,
                COLUMNS.LOCATIONHOOD,
                COLUMNS.LOCATIONHOMEX,
                COLUMNS.LOCATIONHOMEY,
                COLUMNS.LASTCONTACT,
                COLUMNS.LOCATIONX,
                COLUMNS.LOCATIONY,
                COLUMNS.LOCATIONTIME,
                COLUMNS.NUMBERNORMALIZED,
                COLUMNS.POSSIBLEAUTOTEXTARRAY
        };
    }

    /**
     * Klasse für den Eintrag eines Treffens. Stellt Konstanten für die Art des Treffens sowie für die Richtung an
     */
    public static abstract class EncounterEntry implements BaseColumns {
        public static final String TABLE_NAME = "encounter";
        public static final int DIRECTION_COINCIDENCE = 0;
        public static final int DIRECTION_INBOUND = 1;
        public static final int DIRECTION_OUTBOUND = 2;
        public static final int DIRECTION_MUTUAL = 3;
        public static final int MEANS_PERSONAL = 0;
        public static final int MEANS_PHONE = 1;
        public static final int MEANS_MESSENGER = 2;
        public static final int MEANS_MAIL = 3;
        public static final int MEANS_SOCIALNETWORK = 4;
        
        public static abstract class COLUMNS {
            public static final String DESCRIPTION = "description";
            public static final String DIRECTION = "direction";
            public static final String DELETED = "deleted";
            public static final String TYPE = "type";
            public static final String CONTACTID = "contact_id";
            public static final String LENGTH = "length";
            public static final String AUTOMATED = "automated";
        }
        

        public static final String CREATE = CREATE_START + TABLE_NAME + " (" +
				_ID + TYPE_PRIMARYKEY + SEP_COMMA +
				COLUMNS.CONTACTID + TYPE_INT + SEP_COMMA +
				COLUMNS.DESCRIPTION + TYPE_TEXT + SEP_COMMA +
				COLUMNS.DIRECTION + TYPE_INT + SEP_COMMA +
                COLUMNS.DELETED + TYPE_BOOL + SEP_COMMA +
                COLUMNS.TYPE + TYPE_INT + SEP_COMMA +
                COLUMNS.LENGTH + TYPE_TEXT + SEP_COMMA +
				COLUMNS.AUTOMATED + TYPE_TEXT +
                ")";
        public static final String DROP = DROP_START + TABLE_NAME;

        public static final String[] PROJECTIONFULL = {
                _ID,
                COLUMNS.CONTACTID,
                COLUMNS.DESCRIPTION,
                COLUMNS.DIRECTION,
                COLUMNS.TYPE,
                COLUMNS.LENGTH
        };
    }


    public static abstract class AutomatedMessage implements BaseColumns{
		public static final String TABLE_NAME = "message";
		
        public static abstract class COLUMNS{
            public static final String CONTENT = "content";
            public static final String SENTAMOUNT = "sent_amount";
        }

		public static final String CREATE = 
                CREATE_START + TABLE_NAME + " (" +
				_ID + TYPE_PRIMARYKEY + SEP_COMMA +
				COLUMNS.CONTENT + TYPE_TEXT + SEP_COMMA +
				COLUMNS.SENTAMOUNT + TYPE_INT +
				")";

		public static final String[] PROJECTION_FULL = {
				_ID,
				COLUMNS.CONTENT,
				COLUMNS.SENTAMOUNT
		};

		public static final String DROP = DROP_START + TABLE_NAME;
	}




//    /**
//     * Klasse für die Verknüpfung von Person und Kategorie
//     */
//    public static abstract class Person_in_category implements BaseColumns {
//        public static final String TABLE_NAME = "person_in_category";
//
//        public static final String COLUMN_NAME_PERSONID = "id_person";
//        public static final String COLUMN_NAME_CATEGORYNAME = "name_category";
//
//        public static final String CREATE = CREATE_START + TABLE_NAME + " (" + COLUMN_NAME_PERSONID + TYPE_ID + SEP_COMMA + COLUMN_NAME_CATEGORYNAME + TYPE_TEXT + SEP_COMMA + " PRIMARY KEY (" + COLUMN_NAME_CATEGORYNAME + SEP_COMMA + COLUMN_NAME_PERSONID + "))";
//        public static final String DROP = DROP_START + TABLE_NAME;
//    }

}
