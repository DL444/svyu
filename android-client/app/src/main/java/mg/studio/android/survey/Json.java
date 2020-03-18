package mg.studio.android.survey;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.util.Log;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class Json {
    public String getJson(String httpUrl){
        HttpURLConnection connection = null;
        String result = "";
        byte[] buffer = new byte[1024];
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                result = new String(bos.toByteArray());
                is.close();
            }
            if (connection.getResponseCode() == 404){
                Log.wtf("getResult", "can not find ID");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();
        return result;
    }

    public void postJson(String httpUrl, JSONObject param){

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Charset", "UTF-8");
            os = connection.getOutputStream();
            os.write(param.toString().getBytes());
            if (connection.getResponseCode() == 400)
                Log.wtf("response","400");
            if (connection.getResponseCode() == 200)
                Log.wtf("response","200");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
