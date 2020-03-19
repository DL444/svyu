package mg.studio.android.survey;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.Charset;

class JsonClient {

    private JsonClient(Context appContext) {
        requestQueue = Volley.newRequestQueue(appContext);
    }

    public static JsonClient getInstance(Context appContext) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new JsonClient(appContext.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void getJson(String httpUrl, Response.Listener<String> continueWith, Response.ErrorListener onError) {
        StringRequest request = new StringRequest(Request.Method.GET, httpUrl, continueWith, onError);
        requestQueue.add(request);
    }

    public void postJson(String httpUrl, final String json, Response.Listener<String> continueWith, Response.ErrorListener onError) {

        StringRequest request = new StringRequest(Request.Method.POST, httpUrl, continueWith, onError) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() {
                return json.getBytes(Charset.forName("utf-8"));
            }
        };
        requestQueue.add(request);
    }

    private RequestQueue requestQueue;

    private static volatile JsonClient instance;
    private static final Object lock = new Object();
}
