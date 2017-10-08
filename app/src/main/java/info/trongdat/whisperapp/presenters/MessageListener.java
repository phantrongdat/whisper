package info.trongdat.whisperapp.presenters;

/**
 * Created by Alone on 5/12/2017.
 */

public interface MessageListener {
    void init();
    void listener();
    void newConversation();
    void newMessage();

}
