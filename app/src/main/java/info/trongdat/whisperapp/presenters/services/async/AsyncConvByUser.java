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

import info.trongdat.whisperapp.models.entities.Conversation;

import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncConvByUser extends AsyncTask<String, Integer, ArrayList<Conversation>> {
    private static final String URL = "http://ptdcloud.com/getconversationid/";
    int id;

    public AsyncConvByUser(int id) {
        this.id = id;
    }

    @Override
    protected ArrayList<Conversation> doInBackground(String... params) {
        ArrayList<Conversation> conversations = new ArrayList<Conversation>();
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
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String conversationID = object.getString("conversationID");
                Conversation conversation = new AsyncConversation(conversationID).execute().get();
                conversations.add(conversation);
                Log.d(TAG, "doInBackground: aaaaaaa " + conversationID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conversations;
    }

}
