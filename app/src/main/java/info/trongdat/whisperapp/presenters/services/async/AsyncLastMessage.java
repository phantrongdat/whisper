package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import info.trongdat.whisperapp.models.entities.Message;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncLastMessage extends AsyncTask<String, Integer, Message> {
    private static final String URL = "http://ptdcloud.com/getlastmessage/";
    String id;

    public AsyncLastMessage(String id) {
        this.id = id;
    }

    @Override
    protected Message doInBackground(String... params) {
        Message message = new Message();
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

            JSONObject object = new JSONObject(data);
            message.setConversationID(object.getString("conversationID"));
            message.setUserID(object.getInt("userID"));
            message.setText(object.getString("text"));
            message.setData(object.getString("data"));
            message.setTime(object.getString("time"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

}
