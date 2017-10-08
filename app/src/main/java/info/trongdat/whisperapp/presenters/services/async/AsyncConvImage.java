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

public class AsyncConvImage extends AsyncTask<String, Integer, String> {
    private static String URL = "http://ptdcloud.com/getconvimage/";
    String image = "";
    int userID;
    String conversationID;

    public AsyncConvImage(int userID, String conversationID) {
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
            image = object.getString("image");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("datttttt", "onPostExecute: " + phoneNumber + " " + image);
        return image;
    }

}
