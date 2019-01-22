package app.aakyol.weasleymessenger.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import app.aakyol.weasleymessenger.model.RecipientModel;

@Singleton
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RecipientReader.db";

    @Inject
    public DBHelper(@Named("dbContext") Context context) {
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

    public List<RecipientModel> getAllRecipients() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                DBHelper.DBEntry._ID,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        Cursor cursor = db.query(
                DBHelper.DBEntry.TABLE_NAME,
                columns,
                "1",
                null,
                null,
                null,
                null,
                null
        );

        List<RecipientModel> recipients = new ArrayList<>();
        while(cursor.moveToNext()) {
            RecipientModel recipient = new RecipientModel();
            recipient.setDbID(cursor.getInt(0));
            recipient.setAlias(cursor.getString(1));
            recipient.setPhoneNumber(cursor.getString(2));
            recipient.setMessageToBeSent(cursor.getString(3));
            recipient.setLatitude(cursor.getDouble(4));
            recipient.setLongitude(cursor.getDouble(5));
            recipients.add(recipient);
        }
        cursor.close();
        db.close();

        return recipients;
    }

    public RecipientModel getAllRecipientsById(final int rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        String selection = DBHelper.DBEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        Cursor cursor = db.query(
                DBHelper.DBEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        RecipientModel recipient = new RecipientModel();
        while(cursor.moveToNext()) {
            recipient.setAlias(cursor.getString(0));
            recipient.setPhoneNumber(cursor.getString(1));
            recipient.setMessageToBeSent(cursor.getString(2));
            recipient.setLatitude(cursor.getDouble(3));
            recipient.setLongitude(cursor.getDouble(4));
        }
        cursor.close();
        db.close();

        return recipient;
    }

    public long addRecipient(final String alias, final String phoneNo, final String message, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(
                DBHelper.DBEntry.TABLE_NAME,
                null,
                provideValueObject(alias, phoneNo, message, latitude, longitude)
        );
        db.close();
        return result;
    }

    public int updateRecipient(final int rowId, final String alias, final String phoneNo, final String message, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(
                DBHelper.DBEntry.TABLE_NAME,
                provideValueObject(alias, phoneNo, message, latitude, longitude),
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(rowId)}
        );
        db.close();

        return result;
    }

    public int deleteRecipient(final int rowId) {
        SQLiteDatabase db =  this.getWritableDatabase();
        int result = db.delete(
                DBHelper.DBEntry.TABLE_NAME,
                DBHelper.DBEntry._ID + " = ?",
                new String[] {String.valueOf(rowId)}
        );
        db.close();
        return result;
    }

    private ContentValues provideValueObject(final String alias, final String phoneNo, final String message, final String latitude, final String longitude) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_NAME, alias);
        values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_PHONE, phoneNo);
        values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_MESSAGE, message);
        values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LATITUDE, latitude);
        values.put(DBHelper.DBEntry.COLUMN_NAME_RECPIPENT_LONGITUDE, longitude);
        return values;
    }

}
