package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncConvIName extends AsyncTask<String, Integer, String> {
    private static String URL = "http://ptdcloud.com/getconvname/";
    String title = "";
    int userID;
    String conversationID;

    public AsyncConvIName(int userID, String conversationID) {
        this.userID = userID;
        this.conversationID = conversationID;
    }

    @Override
    protected String doInBackground(String... params) {

        String data = "";
        try {
            URL url = new URL(URL + conversationID + "/" + userID);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data += line;
            }
            bufferedReader.close();

            JSONObject object = new JSONObject(data);
            title = object.getString("title");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("datttttt", "onPostExecute: "  + " " + title);
        return title;
    }

}
