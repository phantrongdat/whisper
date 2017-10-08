package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncInfo extends AsyncTask<String, Integer, User> {
    private static final String URL = "http://ptdcloud.com/getinfo/";
    String phoneNumber;

    public AsyncInfo(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected User doInBackground(String... params) {
        User user = new User();
        String data = "";
        try {
            URL url = new URL(URL + phoneNumber);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    protected void onPostExecute(User s) {
        super.onPostExecute(s);
        Log.d("datttttt", "onPostExecute: ccccccccccccccccccccccccccc" + s);
    }

}
