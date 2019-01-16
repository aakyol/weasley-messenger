package app.aakyol.weasleymessenger.model;

import android.location.Location;

import com.google.android.gms.location.LocationResult;

/**
 * Created by aakyo on 31/03/2018.
 */

public class RecipientModel {

    private int dbID;
    private String aliasName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String messageToBeSent;
    private double latitude;
    private double longitude;

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMessageToBeSent() {
        return messageToBeSent;
    }

    public void setMessageToBeSent(String messageToBeSent) {
        this.messageToBeSent = messageToBeSent;
    }

    @Override
    public String toString() {
        return this.aliasName;
    }

}
