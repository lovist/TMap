package com.example.lovasistvn.tmap;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lovas Istvan on 2014.11.23..
 */
public class MyListAdapter extends ArrayAdapter<MapPoint> {

    Context context;
    LayoutInflater inflater;
    List<MapPoint> list;
    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<MapPoint> arraylist;

    public MyListAdapter(Context context, List<MapPoint> list) {
        super(context, 0, list);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.point_name);
            holder.address = (TextView) convertView.findViewById(R.id.point_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(list.get(position).getPointname());
        holder.address.setText(list.get(position).getPointaddress());
        return convertView;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
    private class ViewHolder {
        TextView name;
        TextView address;
    }
}

