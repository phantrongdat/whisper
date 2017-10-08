package info.trongdat.whisperapp.presenters;

import android.content.Context;

import info.trongdat.whisperapp.models.UserModel;
import info.trongdat.whisperapp.models.UserModelListener;
import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Admin on 4/8/2017.
 */

public class UserPresenter implements UserModelListener {
    UserPresenterListener callback;
    UserModel userModel;
    Context context;

    public UserPresenter(Context context) {
        this.context = context;
        userModel = new UserModel(context);
    }

    public boolean createSession(User user) {
        return userModel.createSession(user);
    }

    public int getSessionID() {
        return userModel.getSessionID();
    }

    public User getSession() {
        return userModel.getSession();
    }

    public String attemptSession() {
        return userModel.attemptSession();
    }

    public UserPresenter(Context context, UserPresenterListener callback) {
        this.callback = callback;
        this.context = context;
        userModel = new UserModel(context, this);
    }

    public boolean attemptLogin(String phoneNumber, String password) {

        return userModel.attemptLogin(phoneNumber, password);
    }

    public boolean attemptSignUp(String phoneNumber, String password, String rePassword, String fullName, String address,
                                 String birthDay, String email, String intro) {

        return userModel.attemptSignUp(phoneNumber, password, rePassword, fullName, address, birthDay, email, intro);
    }


    @Override
    public void sessionSuccess(String name) {
        callback.sessionSuccess(name);
    }

    @Override
    public void sessionFail() {
        callback.sessionFail();
    }

    @Override
    public void attemptSuccess() {
        callback.attemptSuccess();
    }

    @Override
    public void attemptFail(String err) {
        callback.attemptFail(err);
    }

}
