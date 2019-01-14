package app.aakyol.weasleymessenger.model;

import android.location.Location;

/**
 * Created by aakyo on 31/03/2018.
 */

public class RecipientModel {

    private String aliasName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String messageToBeSent;
    private String locationForRecipient;

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

    public String getLocationForRecipient() {
        return locationForRecipient;
    }

    public void setLocationForRecipient(String locationForRecipient) {
        this.locationForRecipient = locationForRecipient;
    }

    public String getMessageToBeSent() {
        return messageToBeSent;
    }

    public void setMessageToBeSent(String messageToBeSent) {
        this.messageToBeSent = messageToBeSent;
    }

}
