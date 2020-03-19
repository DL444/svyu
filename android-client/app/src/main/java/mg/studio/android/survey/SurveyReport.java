package mg.studio.android.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class SurveyReport {
    public SurveyReport(String surveyId, ResponseEntity[] responses, long timestamp,
                        double latitude, double longitude, String imei) {
        id = surveyId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imei = imei;
        this.responses = responses.clone();
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImei() {
        return imei;
    }

    public ResponseEntity[] getResponses() {
        return responses;
    }

    public String getJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("len", responses.length);
        obj.put("longitude", longitude);
        obj.put("latitude", latitude);
        obj.put("time", timestamp);
        obj.put("imei", imei);
        JSONArray responseArray = new JSONArray();
        for (ResponseEntity r : responses) {
            responseArray.put(ResponseJsonObjectFactory.getResponseJsonObject(r));
        }
        obj.put("answers", responseArray);
        return obj.toString();
    }

    private String id;
    private long timestamp;
    private double latitude;
    private double longitude;
    private String imei;
    private ResponseEntity[] responses;
}
