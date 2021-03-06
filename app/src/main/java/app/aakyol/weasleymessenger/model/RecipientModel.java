package app.aakyol.weasleymessenger.model;

/**
 * Created by aakyo on 31/03/2018.
 */

public class RecipientModel {

    private int dbID;
    private String alias;
    private String name;
    private boolean enabled;
    private String phoneNumber;
    private String messageToBeSent;
    private Double distance;
    private double latitude;
    private double longitude;

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return this.alias;
    }

}
