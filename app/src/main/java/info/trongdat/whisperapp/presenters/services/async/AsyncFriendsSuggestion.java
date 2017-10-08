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

public class AsyncFriendsSuggestion extends AsyncTask<String, Integer, String> {
    private static String URL = "http://ptdcloud.com/checksuggestion/";
    int id;
    String result = "notfound";
    String phoneNumber;


    public AsyncFriendsSuggestion(int id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected String doInBackground(String... params) {

        String data = "";
        try {
            URL url = new URL(URL + id + "/" + phoneNumber);
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
            result = object.getString("result");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("datttttt", "onPostExecute: " + phoneNumber + " " + result);
        return result;
    }

}
