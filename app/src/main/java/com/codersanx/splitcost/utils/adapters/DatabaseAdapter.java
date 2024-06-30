package com.codersanx.splitcost.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.codersanx.splitcost.R;

import java.util.List;

public class DatabaseAdapter extends ArrayAdapter<String> {
    private final int resourceLayout;
    private final Context mContext;

    public DatabaseAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(resourceLayout, null);
        }

        String item = getItem(position);

        if (item != null) {
            TextView textView = view.findViewById(R.id.textViewTitle);
            if (textView != null) {
                textView.setText(item);
            }
        }

        return view;
    }
}
