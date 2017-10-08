package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncCheckConv extends AsyncTask<String, Integer, String> {
    private static String URL = "http://ptdcloud.com/checkconversation/";
    int userID;
    int friendID;
    String conversationID;

    public AsyncCheckConv(int userID, int friendID) {
        this.userID = userID;
        this.friendID = friendID;
    }

    @Override
    protected String doInBackground(String... params) {

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
            conversationID = object.getString("conversationID");
            Log.d(TAG, "doInBackground: " + conversationID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conversationID;
    }

}
