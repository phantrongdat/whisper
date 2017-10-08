package info.trongdat.whisperapp.views.conversation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.adapters.MessageAdapter;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvIName;
import info.trongdat.whisperapp.views.libs.httpconnect.AsyncResponse;
import info.trongdat.whisperapp.views.libs.httpconnect.PostResponseAsyncTask;
import info.trongdat.whisperapp.views.libs.slackloadingview.SlackLoadingView;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.bitmapResize;
import static info.trongdat.whisperapp.utils.Utils.getImageBase64;

public class ConversationDetail extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
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
    User friend;
    ArrayList<User> friends;
    Conversation conversation;
    String conversationID;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        try {

            userPresenter = new UserPresenter(this);
            user = userPresenter.getSession();

            friend = (User) getIntent().getSerializableExtra("friend");
            conversation = (Conversation) getIntent().getSerializableExtra("conversation");
            friends = (ArrayList<User>) getIntent().getSerializableExtra("friends");
            if (friend != null) {
                toolbarSetup(friend.getFullName(), "Active now");

            } else if (conversation != null) {
                toolbarSetup(conversation.getTitle(), "Active now");
                conversationID = conversation.getConversationID();
            } else {
                toolbarSetup("Conversation Details", "Active now");

            }
            socket = getIOSocket(this);

            JSONObject object = new JSONObject();
            object.put("conversationID", conversationID);
            socket.emit("req_get_message", object);
            socket.on("res_get_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    responseGetMessage(args);
                }
            });
            socket.on("res_new_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    responseNewMessage(args);
                }
            });
            linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            linearLayoutManager.setStackFromEnd(true);
//            linearLayoutManager.setSmoothScrollbarEnabled(false);

            lstMessage.setLayoutManager(linearLayoutManager);
            messages = new ArrayList<>();
            messageAdapter = new MessageAdapter(this, messages);
            lstMessage.setAdapter(messageAdapter);
            loadingView.start();
            edtMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    emojicons.setVisibility(View.GONE);
                    return false;
                }
            });
            setEmojiconFragment(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void responseGetMessage(final Object[] args) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadingView.setVisibility(View.GONE);
                        JSONArray jsonArray = new JSONArray(args[0].toString());
                        Log.d("dattt", "run: " + jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Message message = new Message();
                            JSONObject msg = jsonArray.getJSONObject(i);
                            Log.d("dattt", "run:msg " + msg);
                            message.setUserID(msg.getInt("userID"));
                            message.setConversationID(conversationID);
                            message.setTime(msg.getString("time"));
                            message.setData(msg.getString("data"));
                            message.setText(msg.getString("text"));
                            messages.add(message);
                            messageAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void responseNewMessage(final Object[] args) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    final Message message = new Message();
                    JSONObject msg = new JSONObject(args[0].toString());
                    if (msg.getInt("userID") != user.getUserID()) {
                        message.setUserID(msg.getInt("userID"));
                        message.setConversationID(conversationID);
                        message.setTime(msg.getString("time"));
                        message.setData(msg.getString("data"));
                        message.setText(msg.getString("text"));

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                messages.add(message);
                                messageAdapter.notifyDataSetChanged();
//                                linearLayoutManager.scrollToPosition(messages.size() - 1);
                                mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.receiver);
                                mediaPlayer.start();
                                linearLayoutManager.scrollToPositionWithOffset(messages.size() - 1, 200);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void toolbarSetup(String name, String sub) {
        try {
            ActionBar actionBar = getSupportActionBar();
            String title = conversation.getTitle();
            Log.d("datttt", "toolbarSetup: "+title);
            if (title.equals("") || title.equals("null") || title == null)
                title = new AsyncConvIName(user.getUserID(), conversation.getConversationID()).execute().get();
            actionBar.setTitle(title);
            Log.d("datttt", "toolbarSetup: "+title);
            actionBar.setSubtitle(sub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversation_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmCall:
                break;
            case R.id.itmVideoCall:
                break;
            case R.id.itmDetails:
                Intent intent = new Intent(ConversationDetail.this, ConversationOptions.class);
                intent.putExtra("conversationID", conversation.getConversationID());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
                        btnAddAction.setBackgroundResource(R.drawable.ic_add_a_photo_icon_24dp);

                        final Message message = new Message();
                        message.setConversationID(conversationID);
                        message.setUserID(user.getUserID());
                        message.setTime(new Date().toString());
                        message.setText(text);
                        final JSONObject object = new JSONObject();
                        object.put("conversationID", conversationID);
                        object.put("userID", user.getUserID());
                        object.put("text", text.replace("'", "/'"));
                        object.put("time", new Date().toString());
                        Log.d("datt", "onClick: message: " + object);

                        if (bitmap != null) {
                            HashMap dataPost = new HashMap();
                            dataPost.put("action", "upload-image");
                            dataPost.put("image", getImageBase64(bitmap));
                            PostResponseAsyncTask postResponseAsyncTask = new PostResponseAsyncTask(new AsyncResponse() {
                                @Override
                                public void processFinish(String var1) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(var1);
                                        Log.d("datttt", "processFinish: " + jsonObject.getString("image_url"));

                                        object.put("data", jsonObject.getString("image_url"));
                                        message.setData(jsonObject.getString("image_url"));
                                        socket.emit("req_new_message", object);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, dataPost);
                            postResponseAsyncTask.execute("http://trongdat.info/images/upload.php");
                        } else {
                            object.put("data", "null");
                            message.setData("null");
                            socket.emit("req_new_message", object);
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                messages.add(message);
                                messageAdapter.notifyDataSetChanged();
                                linearLayoutManager.scrollToPositionWithOffset(messages.size() - 1, 200);
                                mediaPlayer = MediaPlayer.create(getApplication().getApplicationContext(), R.raw.send);
                                mediaPlayer.start();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnAddAction:
                Intent pickIMG = new Intent(Intent.ACTION_PICK);
                pickIMG.setType("image/*");
                startActivityForResult(pickIMG, 6789);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6789 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(is);
            bitmap = bitmapResize(bitmap, 500, 500);
            btnAddAction.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        emojicons.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}