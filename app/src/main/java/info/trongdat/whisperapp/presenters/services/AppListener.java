package info.trongdat.whisperapp.presenters.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.util.Timer;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.MessageListener;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvIName;
import info.trongdat.whisperapp.presenters.services.async.AsyncConversation;
import info.trongdat.whisperapp.views.conversation.ConversationDetail;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Admin on 4/9/2017.
 */

public class AppListener extends Service implements MessageListener {

    private static Timer timer;
    private static CropCircleTransformation cropCircleTransformation;
    private static SharedPreferences preferences;

    private Handler mHandler = new Handler();
    private static Context mContext;
    private UserPresenter userPresenter;
    private Socket socket;
    private User user;
    MediaPlayer mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cropCircleTransformation = new CropCircleTransformation(this);

        userPresenter = new UserPresenter(this);
        user = userPresenter.getSession();
        socket = getIOSocket(this);
//        socket.connect();
        listener();

        Log.d(TAG, "onCreate: oncreate service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: start command");
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
//                case ACTION_SET_TRACK: {
//
//                    break;
//                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void init() {

    }

    @Override
    public void listener() {
        try {
//            JSONObject object = new JSONObject();
//            object.put("fullName", user.getFullName());
//        socket.emit("reqsimsimi", object);
            socket.on("res_new_message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final Message message = new Message();
                                JSONObject msg = new JSONObject(args[0].toString());

                                Log.d(TAG, "run: " + msg.getInt("userID") + " " + user.getUserID());
                                if (msg.getInt("userID") != user.getUserID()) {
                                    message.setUserID(msg.getInt("userID"));
                                    message.setConversationID(msg.getString("conversationID"));
                                    message.setTime(msg.getString("time"));
                                    message.setData(msg.getString("data"));
                                    message.setText(msg.getString("text"));

                                    Conversation conversation = new AsyncConversation(message.getConversationID()).execute().get();

                                    mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.receiver);
                                    mediaPlayer.start();
                                    addNotification(message, conversation);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotification(Message message, Conversation conversation) {
        try {
            String title = conversation.getTitle();
            if (title.equals("") || title.equals("null") || title == null)
                title = new AsyncConvIName(user.getUserID(), conversation.getConversationID()).execute().get();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.app_icon)
                            .setContentTitle(title)
                            .setContentText(message.getText());


            Intent notificationIntent = new Intent(this, ConversationDetail.class);
            notificationIntent.putExtra("conversation", conversation);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            // Add as notification
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newConversation() {

    }

    @Override
    public void newMessage() {

    }


}
