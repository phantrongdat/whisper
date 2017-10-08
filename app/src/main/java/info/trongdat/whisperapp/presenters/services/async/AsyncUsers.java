package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncUsers extends AsyncTask<String, Integer, ArrayList<User>> {
    private static final String URL = "http://ptdcloud.com/getusers/";
    ArrayList<User> users;

    @Override
    protected ArrayList<User> doInBackground(String... params) {

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
                User user = new User();
                user.setUserID(object.getInt("userID"));
                user.setPhoneNumber(object.getString("phoneNumber"));
                user.setPassword(object.getString("password"));
                user.setFullName(object.getString("fullName"));
                user.setAvatar(object.getString("avatar"));
                user.setAddress(object.getString("address"));
                user.setBirthDay(object.getString("birthDay"));
                user.setIntro(object.getString("intro"));
                user.setEmail(object.getString("email"));
                user.setLastLocation(object.getString("lastLocation"));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
