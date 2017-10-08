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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Conversation;
import info.trongdat.whisperapp.models.entities.Friend;
import info.trongdat.whisperapp.models.entities.Timeline;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncCheckConv;
import info.trongdat.whisperapp.presenters.services.async.AsyncConversation;
import info.trongdat.whisperapp.presenters.services.async.AsyncNewPost;
import info.trongdat.whisperapp.presenters.services.async.AsyncUser;
import info.trongdat.whisperapp.views.conversation.ConversationDetail;
import info.trongdat.whisperapp.views.libs.CustomPopWindow;

import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/6/2017.
 */

public abstract class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<Friend> list;
    private String AND;
    private User friend;
    private Timeline newPost;

    User user;
    UserPresenter userPresenter;

    public FriendAdapter(Context context, ArrayList<Friend> objects) {
        this.context = context;
        res = context.getResources();
        AND = res.getString(R.string.AND);
        this.list = objects;
        userPresenter = new UserPresenter(context);
        user = userPresenter.getSession();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_friend_1, parent, false);
        return new ViewHolder(v);
    }

    int pos = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 0) {
            final Friend row = list.get(position);
            Log.d(TAG, "onBindViewHolder: " + row.getFriendID());
            final View view = holder.view;

            try {
                friend = new AsyncUser(row.getFriendID()).execute().get();
                newPost = new AsyncNewPost(row.getFriendID()).execute().get();
                Picasso.with(context).load(friend.getAvatar())
                        .into(holder.imgAvatar);
                holder.txtName.setText(friend.getFullName());
                holder.txtNewPost.setText(newPost.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = position;
//                    Intent intent = new Intent(context, ConversationDetail.class);
//                    intent.putExtra("friend", friend);
//                    context.startActivity(intent);
                    View contentView = LayoutInflater.from(context).inflate(R.layout.pop_menu_friends, null);
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
                            .showAsDropDown(holder.btnInfo, 0, 20);
                }
            });
            if ((position >= getItemCount() - 1))
                loadMore();
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
                    case R.id.menu1:
                        try {
                            String convID = new AsyncCheckConv(user.getUserID(), list.get(pos).getUserID()).execute().get();
                            Conversation conversation = new AsyncConversation(convID).execute().get();
                            Intent intent = new Intent(context, ConversationDetail.class);
                            intent.putExtra("conversation", conversation);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.menu2:
                        showContent = "View profile";
                        Toast.makeText(context, showContent, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu3:
                        showContent = "Unfollow";
                        Toast.makeText(context, showContent, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
        contentView.findViewById(R.id.menu3).setOnClickListener(listener);
    }

    public abstract void loadMore();

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName, txtNewPost;
        public CircleImageView imgAvatar;
        public ImageButton btnInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtNewPost = (TextView) itemView.findViewById(R.id.txtNewPost);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            btnInfo = (ImageButton) itemView.findViewById(R.id.btnInfo);
        }
    }
}

