package info.trongdat.whisperapp.models.entities;

/**
 * Created by Alone on 3/13/2017.
 */

public class Friend {
    int userID, friendID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getFriendID() {
        return friendID;
    }

    public void setFriendID(int friendID) {
        this.friendID = friendID;
    }
}
