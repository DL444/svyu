package mg.studio.android.survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ResponseJsonObjectFactory {
    public static JSONObject getResponseJsonObject(ResponseEntity entity) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("type", entity.getType());
        switch (entity.getType()) {
            case "single":
                obj.put("answer", Integer.valueOf(entity.getResponse()));
                break;
            case "multiple":
                String[] opts = entity.getResponse().split(" ");
                JSONArray optArray = new JSONArray();
                for (String o : opts) {
                    optArray.put(Integer.valueOf(o));
                }
                obj.put("answer", optArray);
                break;
            case "text":
                obj.put("answer", entity.getResponse());
                break;
        }
        return obj;
    }
}
