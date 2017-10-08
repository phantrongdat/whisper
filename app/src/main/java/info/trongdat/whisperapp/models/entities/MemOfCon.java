package info.trongdat.whisperapp.models.entities;

import java.io.Serializable;

/**
 * Created by Alone on 3/13/2017.
 */

public class MemOfCon implements Serializable {
    int userID;
    String conversationID, lastUpdate;

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
