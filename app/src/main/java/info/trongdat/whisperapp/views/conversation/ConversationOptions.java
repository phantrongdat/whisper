package info.trongdat.whisperapp.views.conversation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.adapters.AutoCompleteAdapter;
import info.trongdat.whisperapp.presenters.adapters.MemberAdapter;
import info.trongdat.whisperapp.presenters.services.async.AsyncChips;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvIName;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvImage;
import info.trongdat.whisperapp.presenters.services.async.AsyncConversation;
import info.trongdat.whisperapp.presenters.services.async.AsyncLastMessage;
import info.trongdat.whisperapp.presenters.services.async.AsyncMemOfCon;
import info.trongdat.whisperapp.presenters.services.async.AsyncUser;
import info.trongdat.whisperapp.presenters.services.async.AsyncUsers;
import info.trongdat.whisperapp.views.libs.circularavatar.CircularImageView;
import info.trongdat.whisperapp.views.libs.materialchips.ChipsInput;
import info.trongdat.whisperapp.views.libs.materialchips.model.Chip;
import info.trongdat.whisperapp.views.libs.materialchips.model.ChipInterface;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.getBitmapFromURL;

public class ConversationOptions extends AppCompatActivity {
    //    AutoCompleteTextView edtAddMember;
    String conversationID;
    RecyclerView lstMember;
    TextView txtName, txtLastMessage;
    CircularImageView imgAvatar;
    ArrayList<User> users, members;
    MemberAdapter memAdapter;

    Socket socket;
    User user;
    UserPresenter userPresenter;
    ChipsInput mChipsInput;
    ImageButton btnAddMember;

    private List<Chip> mContactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_setting);

        init();
    }

    private void init() {
        try {

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );
            userPresenter = new UserPresenter(this);
            user = userPresenter.getSession();
            socket = getIOSocket(this);
            try {
                mContactList = new AsyncChips().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            conversationID = getIntent().getExtras().getString("conversationID");
            Log.d("datttt", "init: " + conversationID);

            btnAddMember = (ImageButton) findViewById(R.id.btnAddMember);
            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Chip chip : (List<Chip>) mChipsInput.getSelectedChipList()) {
                        try {
                            if (!checkMem((Integer) chip.getId())) {
                                JSONObject object = new JSONObject();
                                object.put("conversationID", conversationID);
                                object.put("userID", chip.getId());
                                object.put("lastUpdate", new Date());

                                socket.emit("req_add_member", object);
                                members.add(new AsyncUser((Integer) chip.getId()).execute().get());
                                memAdapter.notifyDataSetChanged();
                            }
                            mChipsInput.removeChip(chip);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            lstMember = (RecyclerView) findViewById(R.id.lstMember);
            members = new AsyncMemOfCon(conversationID).execute().get();
            memAdapter = new MemberAdapter(this, conversationID, members);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            lstMember.setLayoutManager(linearLayoutManager);
            lstMember.setAdapter(memAdapter);


            users = new AsyncUsers().execute().get();
            //initilaze this array with your data
            AutoCompleteAdapter adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, users);

            mChipsInput = (ChipsInput) findViewById(R.id.chips_input);

            mChipsInput.setFilterableList(mContactList);
            // chips listener
            mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
                @Override
                public void onChipAdded(ChipInterface chip, int newSize) {
                    Log.e("Dattt", "chip added, " + newSize);
                }

                @Override
                public void onChipRemoved(ChipInterface chip, int newSize) {
                    Log.e("Dattt", "chip removed, " + newSize);
                }

                @Override
                public void onTextChanged(CharSequence text) {
                    Log.e("Dattt", "text changed: " + text.toString());
                }
            });
//            edtAddMember.setAdapter(adapter);
//
//            edtAddMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    try {
//                        edtAddMember.setText(null);
//                        Toast.makeText(ConversationOptions.this, "" + position, Toast.LENGTH_SHORT).show();
////                        JSONObject object = new JSONObject();
////                        object.put("conversationID", conversationID);
////                        object.put("userID", users.get(position).getUserID());
////                        object.put("lastUpdate", new Date());
////
////                        socket.emit("req_add_member", object);
////                        members.add(users.get(position));
////                        memAdapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            txtName = (TextView) findViewById(R.id.txtName);
            txtLastMessage = (TextView) findViewById(R.id.txtLastMessage);
            imgAvatar = (CircularImageView) findViewById(R.id.imgAvatar);
            Conversation conversation = new AsyncConversation(conversationID).execute().get();
            Message message = new AsyncLastMessage(conversationID).execute().get();

            String title = conversation.getTitle();
            if (title.equals("") || title.equals("null") || title == null)
                title = new AsyncConvIName(user.getUserID(), conversationID).execute().get();
            txtName.setText(title);
            txtLastMessage.setText(message.getText());
            final ArrayList<Bitmap> mBmps = new ArrayList<Bitmap>();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String[] imgs = new AsyncConvImage(user.getUserID(), conversationID).execute().get().split(";");

                        for (int i = 0; i < imgs.length; i++) {
                            String image = imgs[i];
                            mBmps.add(getBitmapFromURL(image));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imgAvatar.setImageBitmaps(mBmps);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkMem(int id) {
        for (User user : members) {
            if (user.getUserID() == id) return true;
        }
        return false;
    }

}
