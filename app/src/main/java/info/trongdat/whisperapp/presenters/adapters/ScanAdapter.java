package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Timeline;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.utils.Internet;
import info.trongdat.whisperapp.views.conversation.ConversationDetail;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 5/6/2017.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<User> list;
    private String AND;
    private User friend;
    private Timeline newPost;
    Socket socket;
    UserPresenter userPresenter;

    public ScanAdapter(Context context, ArrayList<User> objects) {
        this.context = context;
        res = context.getResources();
        AND = res.getString(R.string.AND);
        this.list = objects;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_friend_3, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 0) {
            final User row = list.get(position);
            Log.d(TAG, "onBindViewHolder: aaaaaaaa" + row.getUserID());
            View view = holder.view;

            try {

                Picasso.with(context).load(row.getAvatar())
                        .into(holder.imgAvatar);
                holder.txtName.setText(row.getFullName());

                String[] loc = row.getLastLocation().split(",");
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(Double.parseDouble(loc[0]));
                location.setLongitude(Double.parseDouble(loc[1]));
                String distance = new Internet(context).distanceTo(location);
                holder.txtDistance.setText(distance);

            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        list.remove(position);
                        notifyDataSetChanged();

                        userPresenter = new UserPresenter(context);
                        socket = getIOSocket(context);
                        socket.connect();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("userID", userPresenter.getSessionID());
                        jsonObject.put("friendID", row.getUserID());
                        Log.d(TAG, "onClick: " + jsonObject);
                        socket.emit("reqfriendadd", jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ConversationDetail.class);
                    intent.putExtra("friendID", row.getUserID());
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName, txtDistance;
        public CircleImageView imgAvatar;
        private ImageButton btnAddFriend;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtDistance = (TextView) itemView.findViewById(R.id.txtDistance);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            btnAddFriend = (ImageButton) itemView.findViewById(R.id.btnAddFriend);
        }
    }
}

