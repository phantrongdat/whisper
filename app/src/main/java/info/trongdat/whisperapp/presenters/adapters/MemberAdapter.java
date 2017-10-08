package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncUser;
import info.trongdat.whisperapp.views.libs.CustomPopWindow;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Constants.TAG;
import static info.trongdat.whisperapp.utils.Internet.getIOSocket;

/**
 * Created by Alone on 5/6/2017.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<User> list;
    Socket socket;
    User user;
    UserPresenter userPresenter;
    String conversationID;

    public MemberAdapter(Context context, String conversationID, ArrayList<User> objects) {
        this.context = context;
        this.conversationID = conversationID;
        res = context.getResources();
        this.list = objects;
        userPresenter = new UserPresenter(context);
        user = userPresenter.getSession();
        socket = getIOSocket(context);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_member_1, parent, false);
        return new ViewHolder(v);
    }

    int pos = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 0) {
            final int uid = list.get(position).getUserID();
            Log.d(TAG, "onBindViewHolder: mem adapter uid: " + uid);
            final View view = holder.view;

            try {
                User row = new AsyncUser(uid).execute().get();
//                mem = new AsyncUser(row.getUserID()).execute().get();
                Picasso.with(context).load(row.getAvatar())
                        .into(holder.imgAvatar);
                holder.txtName.setText(row.getFullName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = position;
//                    Intent intent = new Intent(context, ConversationDetail.class);
//                    intent.putExtra("friend", friend);
//                    context.startActivity(intent);
                    View contentView = LayoutInflater.from(context).inflate(R.layout.pop_menu_member, null);
                    handleLogic(contentView);
                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(context)
                            .setView(contentView)
                            .enableBackgroundDark(true)
                            .setAnimationStyle(R.style.CustomPopWindowStyle)
                            .setBgDarkAlpha(0.7f)
                            .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    Log.e("TAG", "onDismiss");
                                }
                            })
                            .create()
                            .showAsDropDown(holder.btnMore, 0, 20);
                }
            });
        }
    }

    CustomPopWindow mCustomPopWindow;

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                switch (v.getId()) {
                    case R.id.menu2:
                        showContent = "View profile";
                        Toast.makeText(context, showContent, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu3:
                        try {
                            JSONObject object = new JSONObject();
                            object.put("conversationID", conversationID);
                            object.put("userID", list.get(pos).getUserID());

                            socket.emit("req_remove_member", object);
                            list.remove(pos);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        showContent = "Remove from group";
//                        Toast.makeText(context, showContent, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName;
        public CircleImageView imgAvatar;
        public ImageButton btnMore;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMore);
        }
    }
}

