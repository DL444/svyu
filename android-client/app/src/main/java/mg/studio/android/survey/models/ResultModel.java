package mg.studio.android.survey.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a survey result model.
 */
public final class ResultModel implements Serializable {

    /**
     * Gets the ID of the survey the result is for.
     * @return The ID of the survey.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the survey the result is for.
     * @param id The ID to set to.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the number of responses in the result.
     * @return The number of responses in the result.
     */
    public int getLength() {
        return responses.size();
    }

    /**
     * Gets the latitude at which the result is obtained.
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude at which the result is obtained.
     * @param latitude The latitude to set to.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude at which the result is obtained.
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude at which the result is obtained.
     * @param longitude The longitude to set to.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the time at which the result is obtained.
     * @return The time, in milliseconds, in Unix time, in UTC.
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the time at which the result is obtained.
     * @param time The time to set to, in milliseconds, in Unix time, in UTC.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets the IMEI of the device on which the survey is obtained.
     * @return The IMEI of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI of the device on which the survey is obtained.
     * @param imei The IMEI to set to.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the ArrayList object containing the responses in the result.
     * @return The ArrayList object containing the responses in the result.
     */
    public ArrayList<IResponse> responses() {
        return responses;
    }

    private String id = "";
    private double latitude;
    private double longitude;
    private long time;
    private String imei = "";
    private ArrayList<IResponse> responses = new ArrayList<>();
}
