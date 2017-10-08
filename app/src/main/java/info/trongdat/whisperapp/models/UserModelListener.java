package info.trongdat.whisperapp.models;

/**
 * Created by Admin on 4/8/2017.
 */

public interface UserModelListener {
    void sessionSuccess(String name);

    void sessionFail();

    public void attemptSuccess();

    public void attemptFail(String err);
}
