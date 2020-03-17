package mg.studio.android.survey;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    public void postJson(String httpUrl, String param){

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);

            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
//            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Charset", "UTF-8");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(param.getBytes());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RequestQueue requestQueue;

    private static volatile JsonClient instance;
    private static final Object lock = new Object();
}
