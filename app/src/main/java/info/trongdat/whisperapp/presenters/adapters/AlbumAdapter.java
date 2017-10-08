package info.trongdat.whisperapp.presenters.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.Timeline;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Timeline> list = new ArrayList<>();
    private LayoutInflater mInflater;

    public AlbumAdapter(Context context, ArrayList<Timeline> list) {
        this.mContext = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_album_item_1, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Timeline current = list.get(position);
        holder.textView.setText(current.getText());
        Picasso.with(mContext).load(current.getData()).placeholder(R.drawable.green_camera_icon).
                into(holder.imageView);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(mContext);
                View view = ((AppCompatActivity) mContext).getLayoutInflater().inflate(R.layout.layout_view_image, null);
                dialog.setContentView(view);
                ImageView imageView = (ImageView) view.findViewById(R.id.imgImage);
                TextView textView = (TextView) view.findViewById(R.id.txtText);

                Picasso.with(mContext).load(current.getData()).placeholder(R.drawable.green_camera_icon).
                        into(imageView);
                textView.setText(current.getText());
                dialog.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Timeline image, int position) {
        list.add(position, image);
        notifyItemInserted(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.imgAlbum);
            textView = (TextView) itemView.findViewById(R.id.txtText);

        }


    }
}

