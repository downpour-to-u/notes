package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Kayla on 2017/3/22.
 */

public class SelectEventAdapter extends BaseAdapter{

    private String selectedEvent;
    private List<String> dataList = new ArrayList<String>();//data collections

    public SelectEventAdapter(Context context, List<String> objects, String selected){
        super();
        selectedEvent = selected;
        dataList = objects;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String strEvent = getItem(position);//get current event name
        View view;
        if (strEvent.equals(selectedEvent)) {// when this event is selected one
            view = LayoutInflater.from(getContext()).inflate(R.layout.select_event_item_selected, parent, false);
        } else {// when this event is not selected one
            view = LayoutInflater.from(getContext()).inflate(R.layout.select_event_item, parent, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(strEvent);
        return view;
    }
}
