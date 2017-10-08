package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import info.trongdat.whisperapp.models.entities.User;

import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncMemOfCon extends AsyncTask<String, Integer, ArrayList<User>> {
    private static final String URL = "http://ptdcloud.com/getmember/";
    ArrayList<User> users;
    String id;

    public AsyncMemOfCon(String id) {
        this.id = id;
    }

    @Override
    protected ArrayList<User> doInBackground(String... params) {

        users = new ArrayList<>();
        String data = "";
        try {
            URL url = new URL(URL + id);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data += line;
            }
            bufferedReader.close();
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
//                User user2 = new AsyncUser(object.getInt("userID")).execute().get();
                Log.d(TAG, "doInBackground: mem of conv "+object.getInt("userID"));
                User user = new User(object.getInt("userID"), "0199999", "aaa", "aaaaaaaaa", "aaaaaaa", "aaaaaaaaa", "12/12/12", "aaaaaaaa", "aaaa", "122.222,123.4");
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
