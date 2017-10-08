package info.trongdat.whisperapp.presenters.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.whisperapp.R;
import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Alone on 5/23/2017.
 */

public class AutoCompleteAdapter extends ArrayAdapter<User> implements Filterable {

    private ArrayList<User> fullList;
    private ArrayList<User> mOriginalValues;
    private ArrayFilter mFilter;
    Context context;

    public AutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {

        super(context, resource, textViewResourceId, objects);
        fullList = (ArrayList<User>) objects;
        mOriginalValues = new ArrayList<User>(fullList);
        this.context = context;
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public User getItem(int position) {
        return fullList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_item_user_1, parent, false);
        TextView txtName = (TextView) rowView.findViewById(R.id.txtName);
        TextView txtInfo = (TextView) rowView.findViewById(R.id.txtInfo);
        CircleImageView imgAvatar = (CircleImageView) rowView.findViewById(R.id.imgAvatar);
        User row = fullList.get(position);
        txtName.setText(row.getFullName());
        txtInfo.setText(row.getAddress());
        Picasso.with(context).load(row.getAvatar())
                .into(imgAvatar);
//        rowView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: "+position);
//                itemClick(v, position);
//            }
//        });
        return rowView;
    }

//    public abstract void itemClick(View v, int position);

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<User>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<User> list = new ArrayList<User>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<User> values = mOriginalValues;
                int count = values.size();

                ArrayList<User> newValues = new ArrayList<User>(count);

                for (int i = 0; i < count; i++) {
                    User item = values.get(i);
                    if (item.getFullName().toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (results.values != null) {
                fullList = (ArrayList<User>) results.values;
            } else {
                fullList = new ArrayList<User>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}