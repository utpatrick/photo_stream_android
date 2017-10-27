package com.ee382v.sparrow.ee382v_sparrow_mini_phase3;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kyle on 10/20/17.
 */

public class GridViewAdapter extends ArrayAdapter<GridItem> {

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    private Context mContext;
    private ArrayList<GridItem> items = new ArrayList<>();
    private int resource;

    public GridViewAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<GridItem> items) {
        super(context, resource, items);
        this.mContext = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            view = inflater.inflate(resource,parent,false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) view.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) view.findViewById(R.id.grid_item_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        GridItem item = items.get(position);
        holder.titleTextView.setText(item.getTitle());
        if (item.getExtra() != null) holder.titleTextView.setTag(item.getExtra());
        holder.imageView.setTag(item.getLink());
        String link = item.getLink();
        if (link == null || link.length() < 1 || link.startsWith("/static/images/")) {
            Picasso.with(mContext).load(R.drawable.default_image).into(holder.imageView);
        } else {
            Picasso.with(mContext).load(item.getLink()).resize(100, 100).into(holder.imageView);
        }
        return view;
    }
}
