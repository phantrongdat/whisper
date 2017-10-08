package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Message;
import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.async.AsyncUser;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static info.trongdat.whisperapp.R.string.AND;
import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/6/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<Message> list;
    User user;
    UserPresenter userPresenter;
    boolean showTime = false;
    public final int NORMAL_MESSAGE = 0;
    public final int MY_MESSAGE = 1;
    int viewType = NORMAL_MESSAGE;
    int lastPosition = 0;

    public MessageAdapter(Context context, ArrayList<Message> objects) {
        this.context = context;
        res = context.getResources();
        this.list = objects;

        userPresenter = new UserPresenter(context);
        user = userPresenter.getSession();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getUserID() == user.getUserID()) {
            return MY_MESSAGE;
        } else {
            return NORMAL_MESSAGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.viewType = viewType;
        View m1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_message_1, parent, false);
        View m2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row_message_2, parent, false);
        if (viewType == NORMAL_MESSAGE) return new ViewHolder(m1);
        else return new ViewHolder(m2);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list.size() >= 0) {
            Message row = list.get(position);

            Log.d(TAG, "onBindViewHolder: " + row.getConversationID());
            View view = holder.view;
            try {
//                if (viewType == MY_MESSAGE) {
//                    holder.txtName.setVisibility(View.INVISIBLE);
//                    holder.imgAvatar.setVisibility(View.GONE);
//                }
                User userMessage = new AsyncUser(row.getUserID()).execute().get();
                Picasso.with(context).load(userMessage.getAvatar())
                        .into(holder.imgAvatar);
                holder.txtName.setText(userMessage.getFullName());

                holder.txtText.setText(row.getText());
                holder.txtTime.setReferenceTime(new Date(row.getTime()).getTime());
//                holder.txtTime.setText(getPastTime(row.getTime()));
                Picasso.with(context).load(row.getData())
                        .into(holder.imgData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            final ViewHolder finalHolder = holder;
            if (position == list.size() - 1)
                finalHolder.txtTime.setVisibility(View.VISIBLE);
            holder.txtText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalHolder.txtTime.getVisibility() == View.VISIBLE) {

                        finalHolder.txtTime.setVisibility(View.GONE);
//                        finalHolder.txtTime.animate()
//                                .translationY(-finalHolder.txtTime.getHeight())
//                                .alpha(0.0f)
//                                .setDuration(300)
//                                .setListener(new AnimatorListenerAdapter() {
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        finalHolder.txtTime.setVisibility(View.GONE);
//                                    }
//                                });
                    } else {
                        finalHolder.txtTime.setVisibility(View.VISIBLE);
//                        finalHolder.txtTime.animate()
//                                .translationY(0)
//                                .alpha(0.0f)
//                                .setDuration(300);
                    }
//                    showTime = !showTime;
                }
            });
//            Animation animation = AnimationUtils.loadAnimation(context,
//                    (position > lastPosition) ? R.anim.up_from_bottom
//                            : R.anim.down_from_top);
//            holder.itemView.startAnimation(animation);
//            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtName;
        RelativeTimeTextView txtTime;
        EmojiconTextView txtText;
        public CircleImageView imgAvatar;
        public ImageView imgData;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtText = (EmojiconTextView) itemView.findViewById(R.id.txtText);
            txtTime = (RelativeTimeTextView) itemView.findViewById(R.id.txtTime);
            imgData = (ImageView) itemView.findViewById(R.id.imgData);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
        }
    }


    public String getPastTime(String date) {
        return getPastTime(new Date(date));
    }

    public String getPastTime(Date date) {

        if (date == null) return "";
        StringBuilder dateText = new StringBuilder();
        Date today = new Date();
        long diff = (today.getTime() - date.getTime()) / 1000;

        long years = diff / (60 * 60 * 24 * 30 * 12);
        long months = (diff / (60 * 60 * 24 * 30)) % 12;
        long days = (diff / (60 * 60 * 24)) % 30;
        long hours = (diff / (60 * 60)) % 24;
        long minutes = (diff / 60) % 60;
        long seconds = diff % 60;

        if (years > 0) {
            appendPastTime(dateText, years, R.plurals.years, months, R.plurals.months);
        } else if (months > 0) {
            appendPastTime(dateText, months, R.plurals.months, days, R.plurals.days);
        } else if (days > 0) {
            appendPastTime(dateText, days, R.plurals.days, hours, R.plurals.hours);
        } else if (hours > 0) {
            appendPastTime(dateText, hours, R.plurals.hours, minutes, R.plurals.minutes);
        } else if (minutes > 0) {
            appendPastTime(dateText, minutes, R.plurals.minutes, seconds, R.plurals.seconds);
        } else if (seconds >= 0) {
            dateText.append(res.getQuantityString(R.plurals.seconds, (int) seconds, (int) seconds));
        }

        return dateText.toString();
    }

    private void appendPastTime(StringBuilder s,
                                long timespan, int nameId,
                                long timespanNext, int nameNextId) {

        s.append(res.getQuantityString(nameId, (int) timespan, timespan));
        if (timespanNext > 0) {
            s.append(' ').append(AND).append(' ');
            s.append(res.getQuantityString(nameNextId, (int) timespanNext, timespanNext));
        }
    }
}

