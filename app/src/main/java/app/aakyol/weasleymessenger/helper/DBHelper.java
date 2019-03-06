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
    public static final String DATABASE_NAME = "WeasleyMessenger.db";

    @Inject
    public DBHelper(@Named("dbContext") Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static class DBEntry implements BaseColumns {
        public static final String RECPIENT_TABLE_NAME = "recipients";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS = "alias";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_NAME = "name";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE = "phone";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE = "message";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE = "distance";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE = "latitude";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE = "longitude";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBEntry.RECPIENT_TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS + " TEXT UNIQUE," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE + " TEXT)";

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
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        Cursor cursor = db.query(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
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
            recipient.setName(cursor.getString(2));
            recipient.setPhoneNumber(cursor.getString(3));
            recipient.setMessageToBeSent(cursor.getString(4));
            recipient.setDistance(cursor.getDouble(5));
            recipient.setLatitude(cursor.getDouble(6));
            recipient.setLongitude(cursor.getDouble(7));
            recipients.add(recipient);
        }
        cursor.close();
        db.close();

        return recipients;
    }

    public RecipientModel getAllRecipientsById(final int rowId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        String selection = DBHelper.DBEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(rowId) };

        Cursor cursor = db.query(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
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
            recipient.setName(cursor.getString(1));
            recipient.setPhoneNumber(cursor.getString(2));
            recipient.setMessageToBeSent(cursor.getString(3));
            recipient.setDistance(cursor.getDouble(4));
            recipient.setLatitude(cursor.getDouble(5));
            recipient.setLongitude(cursor.getDouble(6));
        }
        cursor.close();
        db.close();

        return recipient;
    }

    public long addRecipient(final String alias, final String name, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
                null,
                provideValueObject(alias, name, phoneNo, message, distance, latitude, longitude)
        );
        db.close();
        return result;
    }

    public int updateRecipient(final int rowId, final String alias, final String name, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
                provideValueObject(alias, name, phoneNo, message, distance, latitude, longitude),
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(rowId)}
        );
        db.close();

        return result;
    }

    public int deleteRecipient(final int rowId) {
        SQLiteDatabase db =  this.getWritableDatabase();
        int result = db.delete(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
                DBHelper.DBEntry._ID + " = ?",
                new String[] {String.valueOf(rowId)}
        );
        db.close();
        return result;
    }

    public int deleteAllRecipients() {
        SQLiteDatabase db =  this.getWritableDatabase();
        int result = db.delete(
                DBHelper.DBEntry.RECPIENT_TABLE_NAME,
                "1",
                new String[] {}
        );
        db.close();
        return result;
    }

    private ContentValues provideValueObject(final String alias, final String name, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS, alias);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME, name);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE, phoneNo);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE, message);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE, distance);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE, latitude);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE, longitude);
        return values;
    }

}
