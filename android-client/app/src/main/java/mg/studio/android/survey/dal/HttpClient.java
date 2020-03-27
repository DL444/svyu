package mg.studio.android.survey.dal;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.Charset;

/**
 * Singleton class of the abstraction of an HTTP client.
 */
public final class HttpClient {

    /**
     * Gets the singleton instance of HttpClient class.
     * @param appContext The application context to create an HTTP client in.
     * @return The singleton instance.
     */
    public static HttpClient getInstance(Context appContext) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HttpClient(appContext.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Sends an HTTP request with GET method to a remote endpoint.
     * @param httpUrl The URI of the remote endpoint.
     * @param continueWith The callback executed when the request is successful.
     * @param onError The callback executed when there is an error.
     */
    public void get(String httpUrl, Response.Listener<String> continueWith, Response.ErrorListener onError) {
        StringRequest request = new StringRequest(Request.Method.GET, httpUrl, continueWith, onError);
        requestQueue.add(request);
    }

    /**
     * Sends an HTTP request with POST method to a remote endpoint.
     * @param httpUrl The URI of the remote endpoint.
     * @param body The byte array containing the content to be sent in the request body.
     * @param contentType The value of the Content-Type header.
     * @param continueWith The callback executed when the request is successful.
     * @param onError The callback executed when there is an error.
     */
    public void post(String httpUrl, final byte[] body, final String contentType, Response.Listener<String> continueWith, Response.ErrorListener onError) {

        StringRequest request = new StringRequest(Request.Method.POST, httpUrl, continueWith, onError) {
            @Override
            public String getBodyContentType() {
                return contentType;
            }

            @Override
            public byte[] getBody() {
                return body;
            }
        };
        requestQueue.add(request);
    }

    /**
     * Sends an HTTP request with POST method and a JSON string in body to a remote endpoint.
     * @param httpUrl The URI of the remote endpoint.
     * @param json The JSON string to send.
     * @param continueWith The callback executed when the request is successful.
     * @param onError The callback executed when there is an error.
     */
    public void postJson(String httpUrl, final String json, Response.Listener<String> continueWith, Response.ErrorListener onError) {
        post(httpUrl, json.getBytes(Charset.forName("utf-8")), "application/json", continueWith, onError);
    }

    private HttpClient(Context appContext) {
        requestQueue = Volley.newRequestQueue(appContext);
    }

    private RequestQueue requestQueue;

    private static volatile HttpClient instance;
    private static final Object lock = new Object();
}
