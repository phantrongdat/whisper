package info.trongdat.whisperapp.presenters;

/**
 * Created by Admin on 4/8/2017.
 */

public interface UserPresenterListener {
    void sessionSuccess(String name);

    void sessionFail();

    void attemptSuccess();

    void attemptFail(String err);
}
