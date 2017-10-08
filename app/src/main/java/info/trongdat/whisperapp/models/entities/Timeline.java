package info.trongdat.whisperapp.models.entities;

import java.io.Serializable;

/**
 * Created by Alone on 3/13/2017.
 */

public class Timeline implements Serializable {
    int timelineID;
    String text, data, date;

    public Timeline() {
    }

    public Timeline(int timelineID, String text, String data, String date) {
        this.timelineID = timelineID;
        this.text = text;
        this.data = data;
        this.date = date;
    }

    public int getTimelineID() {
        return timelineID;
    }

    public void setTimelineID(int timelineID) {
        this.timelineID = timelineID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
