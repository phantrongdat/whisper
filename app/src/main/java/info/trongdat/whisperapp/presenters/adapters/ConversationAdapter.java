package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.MemOfCon;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvIName;
import info.trongdat.whisperapp.presenters.services.async.AsyncConvImage;
import info.trongdat.whisperapp.presenters.services.async.AsyncConversation;
import info.trongdat.whisperapp.presenters.services.async.AsyncLastMessage;
import info.trongdat.whisperapp.views.conversation.ConversationDetail;
import info.trongdat.whisperapp.views.libs.circularavatar.CircularImageView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static android.content.ContentValues.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;
import static info.trongdat.whisperapp.utils.Utils.getBitmapFromURL;

/**
 * Created by Alone on 5/6/2017.
 */

public abstract class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<MemOfCon> list;
    private String AND;
    Socket socket;

    User user;
    UserPresenter userPresenter;


    public ConversationAdapter(Context context, ArrayList<MemOfCon> objects) {
        this.context = context;
        res = context.getResources();
        AND = res.getString(R.string.AND);
        this.list = objects;
        rearrangeByDate();
        socket = getIOSocket(context);
        userPresenter = new UserPresenter(context);
        user = userPresenter.getSession();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_conversation_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 0) {
            final MemOfCon memOfCon = list.get(position);
            View view = holder.view;
            if (position == list.size() - 1)
                holder.line.setVisibility(View.GONE);
            if (!randomBool(position)) holder.txtOnline.setVisibility(View.GONE);
            else
                holder.txtOnline.setVisibility(View.VISIBLE);
            try {
                final Conversation conversation = new AsyncConversation(memOfCon.getConversationID()).execute().get();
                Message message = new AsyncLastMessage(conversation.getConversationID()).execute().get();
//                Picasso.with(context).load(conversation.getImage())
//                        .into(holder.imgAvatar);

                final ArrayList<Bitmap> mBmps = new ArrayList<Bitmap>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String[] imgs = new AsyncConvImage(user.getUserID(), memOfCon.getConversationID()).execute().get().split(";");

                            for (int i = 0; i < imgs.length; i++) {
                                String image = imgs[i];
                                mBmps.add(getBitmapFromURL(image));
                            }
                            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.imgAvatar.setImageBitmaps(mBmps);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                String title = conversation.getTitle();
                if (title.equals("") || title.equals("null") || title == null)
                    title = new AsyncConvIName(user.getUserID(), memOfCon.getConversationID()).execute().get();
                holder.txtName.setText(title);
                holder.txtLastMessage.setText(message.getText());
                holder.txtTime.setReferenceTime(new Date(message.getTime()).getTime());

                socket.on("res_new_message", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        try {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject object = new JSONObject(args[0].toString());
                                        String conversationID = object.getString("conversationID");
                                        int pos = getPosition(conversationID);
                                        Collections.swap(list, pos, 0);
                                        notifyItemMoved(pos, 0);
                                        if (conversationID.equals(conversation.getConversationID())) {
                                            holder.txtLastMessage.setText(object.getString("text"));
                                            holder.txtTime.setReferenceTime(new Date(object.getString("time")).getTime());
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
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ConversationDetail.class);
                        intent.putExtra("conversation", conversation);
                        context.startActivity(intent);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((position >= getItemCount() - 1))
                loadMore();
        }
    }

    public int getPosition(String conversationID) {
        int pos = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getConversationID().equals(conversationID))
                return i;
        }
        return pos;
    }

    public abstract void loadMore();

    public boolean randomBool(int position) {
        return (new Random().nextInt(69) + 1234567890) % (position + 9999) == 0;
    }

    public void rearrangeByDate() {
        try {
            if (list.size() > 0) {
                int size = list.size();
                for (int i = 0; i < size - 1; i++) {
                    for (int j = i + 1; j < size; j++) {
                        Date date = new Date(list.get(i).getLastUpdate()), date1 = new Date(list.get(j).getLastUpdate());
                        if (date.getTime() < date1.getTime()) {
                            Collections.swap(list, i, j);
                            notifyItemMoved(i, j);
                        }
                        Log.d(TAG, "rearrangeByDate: " + (date.getTime() < date1.getTime()));
                    }
                }
            } else Log.d(TAG, "rearrangeByDate: <0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName, txtLastMessage, txtOnline, line;
        RelativeTimeTextView txtTime;
        public CircularImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtOnline = (TextView) itemView.findViewById(R.id.txtOnline);
            txtLastMessage = (TextView) itemView.findViewById(R.id.txtLastMessage);
            txtTime = (RelativeTimeTextView) itemView.findViewById(R.id.txtTime);
            imgAvatar = (CircularImageView) itemView.findViewById(R.id.imgAvatar);
            line = (TextView) itemView.findViewById(R.id.line);
        }
    }
}

