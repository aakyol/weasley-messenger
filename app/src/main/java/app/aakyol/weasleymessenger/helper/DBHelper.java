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

import androidx.appcompat.app.AppCompatActivity;
import app.aakyol.weasleymessenger.model.RecipientModel;
import app.aakyol.weasleymessenger.resource.AppResources;

@Singleton
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WeasleyMessenger.db";

    @Inject
    public DBHelper(@Named("dbContext") Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static class DBEntry implements BaseColumns {
        public static final String RECIPIENT_TABLE_NAME = "recipients";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS = "alias";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_NAME = "name";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED = "enabled";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE = "phone";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE = "message";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE = "distance";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE = "latitude";
        public static final String RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE = "longitude";

        public static final String SERVICE_TABLE_NAME = "service_settings";
        public static final String SERVICE_COLUMN_NAME_FASTEST_INTERVAL = "fastest_interval";
        public static final String SERVICE_COLUMN_NAME_ACCURACY = "accuracy";
        public static final String SERVICE_COLUMN_NAME_MANUAL_SHUTDOWN = "manual_shutdown";
        public static final String SERVICE_COLUMN_NAME_BOOT_STARTUP = "boot_startup";
    }

    private static final String SQL_CREATE_RECIPIENT_ENTRIES =
            "CREATE TABLE " + DBEntry.RECIPIENT_TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS + " TEXT UNIQUE," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE + " TEXT," +
                    DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE + " TEXT)";

    private static final String SQL_CREATE_SETTINGS_ENTRIES =
            "CREATE TABLE " + DBEntry.SERVICE_TABLE_NAME + " (" +
                    DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBEntry.SERVICE_COLUMN_NAME_FASTEST_INTERVAL + " TEXT UNIQUE," +
                    DBEntry.SERVICE_COLUMN_NAME_ACCURACY + " TEXT," +
                    DBEntry.SERVICE_COLUMN_NAME_MANUAL_SHUTDOWN + " TEXT," +
                    DBEntry.SERVICE_COLUMN_NAME_BOOT_STARTUP + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RECIPIENT_ENTRIES);
        db.execSQL(SQL_CREATE_SETTINGS_ENTRIES);
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
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        Cursor cursor = db.query(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                columns,
                "1",
                null,
                null,
                null,
                null,
                null
        );

        List<RecipientModel> recipients = new ArrayList<>();
        while (cursor.moveToNext()) {
            RecipientModel recipient = new RecipientModel();
            recipient.setDbID(cursor.getInt(0));
            recipient.setAlias(cursor.getString(1));
            recipient.setName(cursor.getString(2));
            recipient.setEnabled(Boolean.parseBoolean(cursor.getString(3)));
            recipient.setPhoneNumber(cursor.getString(4));
            recipient.setMessageToBeSent(cursor.getString(5));
            recipient.setDistance(cursor.getDouble(6));
            recipient.setLatitude(cursor.getDouble(7));
            recipient.setLongitude(cursor.getDouble(8));
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
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE,
                DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE
        };

        String selection = DBHelper.DBEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(rowId)};

        Cursor cursor = db.query(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        RecipientModel recipient = new RecipientModel();
        while (cursor.moveToNext()) {
            recipient.setAlias(cursor.getString(0));
            recipient.setName(cursor.getString(1));
            recipient.setEnabled(Boolean.parseBoolean(cursor.getString(2)));
            recipient.setPhoneNumber(cursor.getString(3));
            recipient.setMessageToBeSent(cursor.getString(4));
            recipient.setDistance(cursor.getDouble(5));
            recipient.setLatitude(cursor.getDouble(6));
            recipient.setLongitude(cursor.getDouble(7));
        }
        cursor.close();
        db.close();

        return recipient;
    }

    public long addRecipient(final String alias, final String name, final Boolean enabled, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                null,
                provideValueObject(alias, name, enabled.toString(), phoneNo, message, distance, latitude, longitude)
        );
        db.close();
        return result;
    }

    public int updateRecipient(final int rowId, final String alias, final String name, final Boolean enabled, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.update(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                provideValueObject(alias, name, enabled.toString(), phoneNo, message, distance, latitude, longitude),
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(rowId)}
        );
        db.close();

        return result;
    }

    public int deleteRecipient(final int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(rowId)}
        );
        db.close();
        return result;
    }

    public int deleteAllRecipients() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                DBHelper.DBEntry.RECIPIENT_TABLE_NAME,
                "1",
                new String[]{}
        );
        db.close();
        return result;
    }

    public long updateRecipientEnabled(final int rowId, final Boolean enabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED, enabled.toString());
        int result = db.update(
                DBEntry.RECIPIENT_TABLE_NAME,
                values,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(rowId)}
        );
        db.close();
        return result;
    }

    private ContentValues provideValueObject(final String alias, final String name, final String enabled, final String phoneNo, final String message, final String distance, final String latitude, final String longitude) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ALIAS, alias);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_NAME, name);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_ENABLED, enabled);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_PHONE, phoneNo);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_MESSAGE, message);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_DISTANCE, distance);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LATITUDE, latitude);
        values.put(DBHelper.DBEntry.RECIPIENT_COLUMN_NAME_RECPIPENT_LONGITUDE, longitude);
        return values;
    }

    public boolean getServiceSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {
                DBHelper.DBEntry._ID,
                DBHelper.DBEntry.SERVICE_COLUMN_NAME_FASTEST_INTERVAL,
                DBHelper.DBEntry.SERVICE_COLUMN_NAME_ACCURACY,
                DBHelper.DBEntry.SERVICE_COLUMN_NAME_MANUAL_SHUTDOWN,
                DBHelper.DBEntry.SERVICE_COLUMN_NAME_BOOT_STARTUP
        };

        Cursor cursor = db.query(
                DBHelper.DBEntry.SERVICE_TABLE_NAME,
                columns,
                "1",
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToNext()) {
            AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_FASTEST_INTERVAL = Long.valueOf(cursor.getString(1));
            AppResources.serviceSettings.WEASLEY_SERVICE_LOCATION_ACCURACY = cursor.getString(2);
            AppResources.serviceSettings.WEASLEY_SERVICE_IF_MANUALLY_STOPPED = Boolean.parseBoolean(cursor.getString(3));
            AppResources.serviceSettings.WEASLEY_SERVICE_ON_BOOT_STARTUP = Boolean.parseBoolean(cursor.getString(4));
            cursor.close();
            db.close();
            return true;
        } else {
            return false;
        }
    }

    public long addServiceSettings(final Long interval, final String accuracy, final Boolean isManuallyStopped, final Boolean onBootStartup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.SERVICE_COLUMN_NAME_FASTEST_INTERVAL, interval);
        values.put(DBEntry.SERVICE_COLUMN_NAME_ACCURACY, accuracy);
        values.put(DBEntry.SERVICE_COLUMN_NAME_MANUAL_SHUTDOWN, isManuallyStopped.toString());
        values.put(DBEntry.SERVICE_COLUMN_NAME_BOOT_STARTUP, onBootStartup.toString());
        long result = db.insert(
                DBEntry.SERVICE_TABLE_NAME,
                null,
                values
        );
        db.close();
        return result;
    }

    public long updateServiceIntervalSettings(final Long interval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.SERVICE_COLUMN_NAME_FASTEST_INTERVAL, interval);
        int result = db.update(
                DBHelper.DBEntry.SERVICE_TABLE_NAME,
                values,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(1)}
        );
        db.close();
        return result;
    }

    public long updateServiceAccuracySettings(final String accuracy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.SERVICE_COLUMN_NAME_ACCURACY, accuracy);
        int result = db.update(
                DBHelper.DBEntry.SERVICE_TABLE_NAME,
                values,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(1)}
        );
        db.close();
        return result;
    }

    public long updateServiceIsManuallyStoppedSettings(final Boolean isManuallyStopped) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.SERVICE_COLUMN_NAME_MANUAL_SHUTDOWN, isManuallyStopped.toString());
        int result = db.update(
                DBHelper.DBEntry.SERVICE_TABLE_NAME,
                values,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(1)}
        );
        db.close();
        return result;
    }

    public long updateOnBootStartupSettings(final Boolean onBootStartup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBEntry.SERVICE_COLUMN_NAME_BOOT_STARTUP, onBootStartup.toString());
        int result = db.update(
                DBHelper.DBEntry.SERVICE_TABLE_NAME,
                values,
                DBHelper.DBEntry._ID + " = ?",
                new String[]{String.valueOf(1)}
        );
        db.close();
        return result;
    }
}
