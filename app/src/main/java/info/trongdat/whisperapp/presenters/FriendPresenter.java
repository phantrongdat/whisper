package info.trongdat.whisperapp.presenters;

import android.content.Context;

import java.util.ArrayList;

import info.trongdat.whisperapp.models.FriendModel;
import info.trongdat.whisperapp.models.entities.Contact;
import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Alone on 5/7/2017.
 */

public class FriendPresenter {
    Context context;
    FriendModel friendModel;

    public FriendPresenter(Context context) {
        this.context = context;
        friendModel = new FriendModel(context);
    }

    public ArrayList<Contact> getContacts() {
        return friendModel.getContacts();
    }


    public ArrayList<User> getSuggestion() {
        return friendModel.getSuggestion();
    }
}
