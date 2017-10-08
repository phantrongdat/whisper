package info.trongdat.whisperapp.models.entities;

import java.io.Serializable;

/**
 * Created by Alone on 3/13/2017.
 */

public class Conversation implements Serializable{
    String conversationID;
    String title;

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
