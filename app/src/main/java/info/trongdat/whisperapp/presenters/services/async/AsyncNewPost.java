package info.trongdat.whisperapp.presenters.services.async;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import info.trongdat.whisperapp.models.entities.Timeline;

/**
 * Created by Alone on 5/7/2017.
 */

public class AsyncNewPost extends AsyncTask<String, Integer, Timeline> {
    private static final String URL = "http://ptdcloud.com/getnewpost/";
    int id;

    public AsyncNewPost(int id) {
        this.id = id;
    }

    @Override
    protected Timeline doInBackground(String... params) {
        Timeline timeline = new Timeline();
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
            timeline.setTimelineID(object.getInt("timelineID"));
            timeline.setText(object.getString("text"));
            timeline.setData(object.getString("data"));
            timeline.setDate(object.getString("time"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeline;
    }

    @Override
    protected void onPostExecute(Timeline s) {
        super.onPostExecute(s);
        Log.d("datttttt", "onPostExecute: " + s);
    }

}
