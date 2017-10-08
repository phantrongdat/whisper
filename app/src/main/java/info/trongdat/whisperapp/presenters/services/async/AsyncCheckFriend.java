package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static android.R.attr.phoneNumber;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncCheckFriend extends AsyncTask<String, Integer, Boolean> {
    private static String URL = "http://ptdcloud.com/isfriend/";
    boolean result = false;
    int userID, friendID;

    public AsyncCheckFriend(int userID, int friendID) {
        this.userID = userID;
        this.friendID = friendID;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String data = "";
        try {
            URL url = new URL(URL + userID + "/" + friendID);
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
            result = Boolean.parseBoolean(object.getString("result"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("datttttt", "onPostExecute: " + phoneNumber + " " + result);
        return result;
    }

}
