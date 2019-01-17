package app.aakyol.weasleymessenger.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecipientReader.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipients";
        public static final String COLUMN_NAME_RECPIPENT_NAME = "name";
        public static final String COLUMN_NAME_RECPIPENT_PHONE = "phone";
        public static final String COLUMN_NAME_RECPIPENT_MESSAGE = "message";
        public static final String COLUMN_NAME_RECPIPENT_LATITUDE = "latitude";
        public static final String COLUMN_NAME_RECPIPENT_LONGITUDE = "longitude";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBEntry.TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBEntry.COLUMN_NAME_RECPIPENT_NAME + " TEXT," +
                    DBEntry.COLUMN_NAME_RECPIPENT_PHONE + " TEXT," +
                    DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE + " TEXT," +
                    DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE + " TEXT," +
                    DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
