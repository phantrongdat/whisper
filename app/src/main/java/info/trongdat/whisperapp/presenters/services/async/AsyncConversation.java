package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import info.trongdat.whisperapp.models.entities.Conversation;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncConversation extends AsyncTask<String, Integer, Conversation> {
    private static final String URL = "http://ptdcloud.com/getconversation/";
    String id;

    public AsyncConversation(String id) {
        this.id = id;
    }

    @Override
    protected Conversation doInBackground(String... params) {
        Conversation conversation = new Conversation();
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
            conversation.setConversationID(object.getString("conversationID"));
            conversation.setTitle(object.getString("title"));
//            conversation.setImage(object.getString("image"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conversation;
    }

}
