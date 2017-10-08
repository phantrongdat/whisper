package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Timeline;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncNewPost;
import info.trongdat.whisperapp.views.conversation.ConversationDetail;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/6/2017.
 */

public abstract class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<User> list;
    private String AND;
    private User friend;
    private Timeline newPost;
    Socket socket;
    UserPresenter userPresenter;

    public SuggestionAdapter(Context context, ArrayList<User> objects) {
        this.context = context;
        res = context.getResources();
        AND = res.getString(R.string.AND);
        this.list = objects;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_friend_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 0) {
            final User row = list.get(position);
            Log.d(TAG, "onBindViewHolder: aaaaaaaa" + row.getUserID());
            View view = holder.view;

            try {
//                friend = new AsyncUser(row.getFriendID()).execute().get();
                newPost = new AsyncNewPost(row.getUserID()).execute().get();
                Picasso.with(context).load(row.getAvatar())
                        .into(holder.imgAvatar);
                holder.txtName.setText(row.getFullName());
                holder.txtNewPost.setText(newPost.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemClick(v, position);
                }
            });
//                    try {
//                        list.remove(position);
//                        notifyDataSetChanged();
//
//                        userPresenter = new UserPresenter(context);
//                        socket = getIOSocket();
//                        socket.connect();
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("userID", userPresenter.getSessionID());
//                        jsonObject.put("friendID", row.getUserID());
//                        Log.d(TAG, "onClick: " + jsonObject);
//                        socket.emit("reqfriendadd", jsonObject);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ConversationDetail.class);
                    intent.putExtra("friend", friend);
                    context.startActivity(intent);
                }
            });

        }
    }

    public abstract void itemClick(View v, int position);

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName, txtNewPost;
        public CircleImageView imgAvatar;
        private ImageButton btnAddFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtNewPost = (TextView) itemView.findViewById(R.id.txtNewPost);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            btnAddFriend = (ImageButton) itemView.findViewById(R.id.btnAddFriend);
        }
    }
}

