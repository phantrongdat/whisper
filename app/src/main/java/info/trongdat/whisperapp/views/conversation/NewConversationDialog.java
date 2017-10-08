package info.trongdat.whisperapp.views.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.jeffrey.library.BottomTopDialogFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncCheckConv;
import info.trongdat.whisperapp.presenters.services.async.AsyncChips;
import info.trongdat.whisperapp.presenters.services.async.AsyncConversation;
import info.trongdat.whisperapp.presenters.services.async.AsyncUser;
import info.trongdat.whisperapp.views.libs.materialchips.ChipsInput;
import info.trongdat.whisperapp.views.libs.materialchips.model.Chip;
import info.trongdat.whisperapp.views.libs.materialchips.model.ChipInterface;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 5/17/2017.
 */

public class NewConversationDialog extends BottomTopDialogFragment {

    View belowView;
    View view;
    @InjectView(R.id.chips_input)
    ChipsInput mChipsInput;
    @InjectView(R.id.edtConversationTitle)
    EditText edtTitle;
    @InjectView(R.id.edtMessage)
    EmojiconEditText edtMessage;
    @InjectView(R.id.btnAddAction)
    ImageButton btnAddAction;
    @InjectView(R.id.btnAddEmojicon)
    ImageButton btnAddEmojicon;
    @InjectView(R.id.btnSendMessage)
    ImageButton btnSend;
    @InjectView(R.id.emojicons)
    FrameLayout emojicons;
    private List<Chip> mContactList = new ArrayList<>();
    Socket socket;

    User user;
    UserPresenter userPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindContent(ViewGroup viewGroup) {
        view = viewGroup;
        ButterKnife.inject(this, viewGroup);
        socket = getIOSocket(getActivity());
        userPresenter = new UserPresenter(getActivity());
        user = userPresenter.getSession();
        try {
            mContactList = new AsyncChips().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        mChipsInput.setFilterableList(mContactList);
        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip added, " + newSize);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip removed, " + newSize);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e(TAG, "text changed: " + text.toString());
            }
        });

        socket.on("res_new_conversation", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject object = new JSONObject(args[0].toString());
                                String conversationID = object.getString("conversationID");

                                JSONObject member = new JSONObject();
                                member.put("conversationID", conversationID);
                                member.put("userID", user.getUserID());
                                member.put("lastUpdate", new Date());
                                socket.emit("req_add_member", member);

                                for (Chip chip : (List<Chip>) mChipsInput.getSelectedChipList()) {
                                    JSONObject memberf = new JSONObject();
                                    memberf.put("conversationID", conversationID);
                                    memberf.put("userID", (Integer) chip.getId());
                                    memberf.put("lastUpdate", new Date());
                                    socket.emit("req_add_member", memberf);
                                }

                                Conversation conversation = new AsyncConversation(conversationID).execute().get();
                                Intent intent = new Intent(getActivity(), ConversationDetail.class);
                                intent.putExtra("conversation", conversation);
                                getActivity().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.btnSendMessage, R.id.btnAddAction, R.id.btnAddEmojicon, R.id.edtMessage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddEmojicon:
                InputMethodManager inputManager =
                        (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                emojicons.setVisibility(View.VISIBLE);
                break;
            case R.id.edtMessage:
                emojicons.setVisibility(View.GONE);
                break;
            case R.id.btnSendMessage:
                try {
                    String text = edtMessage.getText().toString();
                    ArrayList<User> userIDs = new ArrayList<>();
                    for (Chip chip : (List<Chip>) mChipsInput.getSelectedChipList()) {
                        User friend = new AsyncUser((Integer) chip.getId()).execute().get();
                        userIDs.add(friend);
                    }
                    if (!text.equals("") && text != null) {
                        edtMessage.setText(null);
                        if (userIDs.size() == 1) {
                            String convID = new AsyncCheckConv(user.getUserID(), userIDs.get(0).getUserID()).execute().get();
                            if (!convID.equals("null")) {
                                Log.d(TAG, "onClick: aaaaa " + convID);
                                final Message message = new Message();
                                message.setConversationID(convID);
                                message.setUserID(user.getUserID());
                                message.setTime(new Date().toString());
                                message.setData("null");
                                message.setText(text);

                                JSONObject object = new JSONObject();
                                object.put("conversationID", convID);
                                object.put("userID", user.getUserID());
                                object.put("text", text.replace("'", "/'"));
                                object.put("data", "null");
                                object.put("time", new Date().toString());
                                socket.emit("req_new_message", object);

                                Log.d("datt", "onClick: message: " + object);

                                Conversation conversation = new AsyncConversation(convID).execute().get();
                                Intent intent = new Intent(getActivity(), ConversationDetail.class);
                                intent.putExtra("conversation", conversation);
                                getActivity().startActivity(intent);
                            } else {
                                String title = edtTitle.getText().toString();
                                if (title.equals("") || title == null) {
                                    title = "";
                                }
                                JSONObject object = new JSONObject();
                                object.put("title", title);
                                object.put("userID", user.getUserID());
                                object.put("text", text.replace("'", "/'"));
                                object.put("data", "null");
                                object.put("time", new Date().toString());
                                socket.emit("req_new_conversation", object);

                                Log.d("datt", "onClick: message: " + object);

                            }
                        } else {
                            String title = edtTitle.getText().toString();
                            if (title.equals("") || title == null) {
                                title = "";
                            }
                            JSONObject object = new JSONObject();
                            object.put("title", title);
                            object.put("userID", user.getUserID());
                            object.put("text", text.replace("'", "/'"));
                            object.put("data", "null");
                            object.put("time", new Date().toString());
                            socket.emit("req_new_conversation", object);

                            Log.d("datt", "onClick: message: " + object);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.layout_new_conversation;
    }

    @Override
    public int getGravity() {
        return Gravity.TOP;
    }


    @Override
    protected void contentShow() {
        bottomTopView.showBelow(belowView);
    }

    public void show(FragmentManager manager, String tag, View belowView) {
        this.belowView = belowView;
        show(manager, tag);
    }
}