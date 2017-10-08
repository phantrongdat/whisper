package info.trongdat.whisperapp.presenters.adapters;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.views.libs.TimelineRow;

import static info.trongdat.whisperapp.utils.Utils.getRandomColor;


public abstract class TimelineViewAdapter extends RecyclerView.Adapter<TimelineViewAdapter.ViewHolder> {

    private Context context;
    private Resources res;
    private List<TimelineRow> RowDataList;
    private String AND;


    public TimelineViewAdapter(Context context, int resource, ArrayList<TimelineRow> objects, boolean orderTheList) {
        this.context = context;
        res = context.getResources();
        AND = res.getString(R.string.AND);
        if (orderTheList)
            this.RowDataList = rearrangeByDate(objects);
        else
            this.RowDataList = objects;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ctimeline_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (RowDataList.size() > 0) {
            TimelineRow row = RowDataList.get(position);

            View view = holder.view;

            final float scale = context.getResources().getDisplayMetrics().density;


            if (position == 0 && position == RowDataList.size() - 1) {
//            holder.rowUpperLine.setVisibility(View.INVISIBLE);
//            holder.rowLowerLine.setVisibility(View.INVISIBLE);
            } else if (position == 0) {
                int pixels = (int) (row.getBellowLineSize() * scale + 0.5f);

//            holder.rowUpperLine.setVisibility(View.INVISIBLE);
                holder.rowUpperLine.setBackgroundColor(getRandomColor());
                holder.rowUpperLine.getLayoutParams().width = pixels;

                holder.rowLowerLine.setBackgroundColor(row.getBellowLineColor());
                holder.rowLowerLine.getLayoutParams().width = pixels;
            } else if (position == RowDataList.size() - 1) {
                int pixels = (int) (RowDataList.get(position - 1).getBellowLineSize() * scale + 0.5f);

                holder.rowLowerLine.setVisibility(View.INVISIBLE);
                holder.rowUpperLine.setBackgroundColor(RowDataList.get(position - 1).getBellowLineColor());
                holder.rowUpperLine.getLayoutParams().width = pixels;
            } else {
                int pixels = (int) (row.getBellowLineSize() * scale + 0.5f);
                int pixels2 = (int) (RowDataList.get(position - 1).getBellowLineSize() * scale + 0.5f);

                holder.rowLowerLine.setBackgroundColor(row.getBellowLineColor());
                holder.rowUpperLine.setBackgroundColor(RowDataList.get(position - 1).getBellowLineColor());
                holder.rowLowerLine.getLayoutParams().width = pixels;
                holder.rowUpperLine.getLayoutParams().width = pixels2;
            }


            holder.rowDate.setText(getPastTime(row.getDate()));
//        if (row.getTitle() == null)
//            holder.rowTitle.setVisibility(View.GONE);
//        else
//            holder.rowTitle.setText(row.getTitle());
            if (row.getDescription() == null)
                holder.rowDescription.setVisibility(View.GONE);
            else
                holder.rowDescription.setText(row.getDescription());


            if (row.getImage() != null) {
                Picasso.with(context).load(row.getImage())
                        .error(R.drawable.anhdaidien)
                        .placeholder(R.drawable.anhdaidien)
                        .into(holder.rowImage);
//                holder.rowImage.setImageBitmap(row.getImage());
            }
//            if (row.getImagePost() != null && !row.getImagePost().equals("null")) {
            Picasso.with(context).load(row.getImagePost())
                    .into(holder.rowImageData);

//            }

            int pixels = (int) (row.getImageSize() * scale + 0.5f);
            holder.rowImage.getLayoutParams().width = pixels;
            holder.rowImage.getLayoutParams().height = pixels;

            View backgroundView = view.findViewById(R.id.crowBackground);
            if (row.getBackgroundColor() == -1)
                backgroundView.setBackground(null);
            else {
                if (row.getBackgroundSize() == -1) {
                    backgroundView.getLayoutParams().width = pixels;
                    backgroundView.getLayoutParams().height = pixels;
                } else {
                    int BackgroundPixels = (int) (row.getBackgroundSize() * scale + 0.5f);
                    backgroundView.getLayoutParams().width = BackgroundPixels;
                    backgroundView.getLayoutParams().height = BackgroundPixels;
                }
                GradientDrawable background = (GradientDrawable) backgroundView.getBackground();
                if (background != null) {
                    background.setColor(row.getBackgroundColor());
                }
            }

            Log.d("dattttt", "onBindViewHolder: " + row.getTitle());
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) holder.rowImage.getLayoutParams();
            marginParams.setMargins(0, (int) (pixels / 2) * -1, 0, (pixels / 2) * -1);


        }

        if (position == RowDataList.size() - 1)
            holder.line.setVisibility(View.GONE);
        if ((position >= getItemCount() - 1))
            loadMore();
    }

    public abstract void loadMore();

    private ArrayList<TimelineRow> rearrangeByDate(ArrayList<TimelineRow> objects) {
        if (objects.size() > 0) {
            if (objects.get(0) == null || objects.get(0).getDate() == null) return objects;
            int size = objects.size();
            for (int i = 0; i < size - 1; i++) {
                for (int j = i + 1; j < size; j++) {
                    if (objects.get(i).getDate().compareTo(objects.get(j).getDate()) <= 0)
                        Collections.swap(objects, i, j);
                }
            }
        }
        return objects;
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

    @Override
    public int getItemCount() {
        return RowDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView rowDate;
        //        public TextView rowTitle;
        public TextView rowDescription,line;
        public CircleImageView rowImage;
        ImageView rowImageData;
        public View rowUpperLine;
        public View rowLowerLine;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;

            rowDate = (TextView) view.findViewById(R.id.crowDate);
            rowImageData = (ImageView) view.findViewById(R.id.crowImage);
//            rowTitle = (TextView) view.findViewById(R.id.crowTitle);
            rowDescription = (TextView) view.findViewById(R.id.crowDesc);
            rowImage = (CircleImageView) view.findViewById(R.id.crowImg);
            rowUpperLine = (View) view.findViewById(R.id.crowUpperLine);
            rowLowerLine = (View) view.findViewById(R.id.crowLowerLine);

            line = (TextView) itemView.findViewById(R.id.line);
        }
    }

}

