package info.trongdat.whisperapp.views.bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.adapters.MessageAdapter;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Constants.ACTION_BOT_REPLY;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

public class AutoConversation extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    @InjectView(R.id.lstMessage)
    RecyclerView lstMessage;
    @InjectView(R.id.edtMessage)
    EmojiconEditText edtMessage;
    @InjectView(R.id.btnAddAction)
    ImageButton btnAddAction;
    @InjectView(R.id.btnAddEmojicon)
    ImageButton btnAddEmojicon;
    @InjectView(R.id.btnSendMessage)
    ImageButton btnSend;
    @InjectView(R.id.loadingView)
    SlackLoadingView loadingView;
    @InjectView(R.id.emojicons)
    FrameLayout emojicons;
    LinearLayoutManager linearLayoutManager;
    Socket socket;
    User user;
    UserPresenter userPresenter;
    ArrayList<Message> messages;
    MessageAdapter messageAdapter;
    BroadcastReceiver bcrNewMessage;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Emojiconize.activity(this).go();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        try {
//            setEmojiconFragment(false);
            getSupportActionBar().setTitle("Simsimi");
            userPresenter = new UserPresenter(this);
            user = userPresenter.getSession();

            try {
                socket = getIOSocket(this);
                JSONObject object = new JSONObject();
                object.put("fullName", user.getFullName());
                socket.emit("reqsimsimi", object);
                socket.on("reschatbot", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        responseProcess(args);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//            linearLayoutManager.setStackFromEnd(true);
//            linearLayoutManager.setSmoothScrollbarEnabled(false);

            lstMessage.setLayoutManager(linearLayoutManager);
            messages = new ArrayList<>();
            messageAdapter = new MessageAdapter(this, messages);
            lstMessage.setAdapter(messageAdapter);
            loadingView.start();
            bcrNewMessage = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    processMessage(context, intent);
                }
            };
//            registerReceiver(bcrNewMessage, new IntentFilter(ACTION_BOT_REPLY));

            edtMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    emojicons.setVisibility(View.GONE);
                    return false;
                }
            });
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
        setEmojiconFragment(false);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(edtMessage, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(edtMessage);
    }


    private void processMessage(Context context, Intent intent) {
        loadingView.setVisibility(View.GONE);
        try {
            if (intent.getAction() == ACTION_BOT_REPLY) {
                String text = intent.getExtras().getString("text");

                final Message message = new Message();
                message.setUserID(3);
                message.setTime(new Date().toString());
                message.setData("null");
                JSONObject msg = new JSONObject(text);
                String str = msg.getString("result");
                message.setText(str);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        messages.add(message);
                        messageAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(messages.size() - 1);
                        mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.receiver);
                        mediaPlayer.start();
//                    linearLayoutManager.scrollToPositionWithOffset(messages.size()-1,200);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btnSendMessage, R.id.btnAddAction, R.id.btnAddEmojicon, R.id.edtMessage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddEmojicon:
                InputMethodManager inputManager =
                        (InputMethodManager) this.
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                emojicons.setVisibility(View.VISIBLE);
                break;
            case R.id.edtMessage:
                emojicons.setVisibility(View.GONE);
                break;
            case R.id.btnSendMessage:
                try {
                    String text = edtMessage.getText().toString();

                    if (!text.equals("") && text != null) {
                        edtMessage.setText(null);
                        final Message message = new Message();
                        message.setUserID(user.getUserID());
                        message.setTime(new Date().toString());
                        message.setData("null");
                        message.setText(text);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                messages.add(message);
                                messageAdapter.notifyDataSetChanged();
                                linearLayoutManager.scrollToPosition(messages.size() - 1);
                                mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.send);
                                mediaPlayer.start();
                            }
                        });

                        JSONObject object = new JSONObject();
                        object.put("message", text);
                        socket.emit("reqchatbot", object);
                        Log.d("datt", "onClick: message: " + object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        emojicons.setVisibility(View.GONE);
    }

    private void responseProcess(final Object[] args) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadingView.setVisibility(View.GONE);
                        final Message message = new Message();
                        message.setUserID(3);
                        message.setTime(new Date().toString());
                        message.setData("null");
                        JSONObject msg = new JSONObject(args[0].toString());
                        String str = msg.getString("result");
                        message.setText(str);
                        messages.add(message);
                        messageAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(messages.size() - 1);
                        mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.receiver);
                        mediaPlayer.start();
//                    linearLayoutManager.scrollToPositionWithOffset(messages.size()-1,200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(bcrNewMessage);
    }

//    private void setEmojiconFragment(boolean useSystemDefault) {
////        getSupportFragmentManager()
////                .beginTransaction()
////                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
////                .commit();
//    }
//
////    @Override
//    public void onEmojiconClicked(Emojicon emojicon) {
//        EmojiconsFragment.input(edtMessage, emojicon);
//    }
//
////    @Override
//    public void onEmojiconBackspaceClicked(View v) {
//        EmojiconsFragment.backspace(edtMessage);
//    }
//
//    public void openEmojiconsActivity(View view) {
//        startActivity(new Intent(this, EmojiconsActivity.class));
//    }
}

