package info.trongdat.whisperapp.presenters.services.async;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import info.trongdat.whisperapp.views.libs.materialchips.model.Chip;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncChips extends AsyncTask<String, Integer, ArrayList<Chip>> {
    private static final String URL = "http://ptdcloud.com/getusers/";
    ArrayList<Chip> users;

    @Override
    protected ArrayList<Chip> doInBackground(String... params) {

        users=new ArrayList<>();
        String data = "";
        try {
            URL url = new URL(URL);
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
                Chip user = new Chip();
                user.setId(object.getInt("userID"));
                user.setInfo(object.getString("address"));
                user.setLabel(object.getString("fullName"));
                user.setAvatarUri(Uri.parse(object.getString("avatar")));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
