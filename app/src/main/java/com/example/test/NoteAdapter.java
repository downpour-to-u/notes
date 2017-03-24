package com.example.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static org.litepal.LitePalApplication.getContext;

/**
 * Created by Kayla on 2017/3/18.
 */

public class NoteAdapter extends BaseAdapter {
    private Context mContext;
    private static final int TYPE_COUNT = 2;//Total number of item type
    private static final int TYPE_NOTE_NORMAL = 0;//normal note type
    private static final int TYPE_NOTE_MONTH = 1;//just month type
    private List<Note> dataList = new ArrayList<Note>();//data collections

    public NoteAdapter(Context mContext,
                                    List<Note> dataList) {
        super();
        this.dataList = dataList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    @Override
    public Note getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        if(position == 0){
            return TYPE_NOTE_MONTH;
        }else {
            String strMonth = dataList.get(position).getTime().substring(0, 6);
            String strMonth_preNote = dataList.get(position - 1).getTime().substring(0, 6);
            if (strMonth.equals(strMonth_preNote)) {
                return TYPE_NOTE_NORMAL;// normal note type
            } else {
                return TYPE_NOTE_MONTH;// first note of the month
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);//get current Note object
        View view;
        int CURRENT_TYPE = getItemViewType(position);
        if (CURRENT_TYPE == TYPE_NOTE_NORMAL) {// when type is normal note
            view = LayoutInflater.from(getContext()).inflate(R.layout.note_item, parent, false);
            TextView textView_event = (TextView) view.findViewById(R.id.textView_event);
            TextView textView_content1 = (TextView) view.findViewById(R.id.textView_content1);
            TextView textView_content2 = (TextView) view.findViewById(R.id.textView_content2);
            TextView textView_time = (TextView) view.findViewById(R.id.textView_time);
            textView_event.setText(note.getEvent());
            String strContent = note.getContent();
            if (strContent.contains("\n")) { // if the note have multiple lines, output line one and line two, and the textStyle of line1 is bold
                textView_content1.setText(strContent.substring(0, strContent.indexOf("\n")));
                textView_content2.setText(strContent.substring(strContent.indexOf("\n") + 1, strContent.length()));
            } else { //else just output line1
                textView_content1.setText(strContent);
            }
            SimpleDateFormat sdf_in = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf_out = new SimpleDateFormat("dd, HH:mm");
            Date dtTime = new Date(System.currentTimeMillis());
            try {
                dtTime = sdf_in.parse(note.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (note.getTime() != null)
                textView_time.setText(sdf_out.format(dtTime));
        }
        else // when type is first note of the month
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.note_item_month, parent, false);
            TextView textView_month = (TextView) view.findViewById(R.id.textView_month);
            TextView textView_event = (TextView) view.findViewById(R.id.textView_event_month);
            TextView textView_content1 = (TextView) view.findViewById(R.id.textView_content1_month);
            TextView textView_content2 = (TextView) view.findViewById(R.id.textView_content2_month);
            TextView textView_time = (TextView) view.findViewById(R.id.textView_time_month);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            Date date = new Date();
            try {
                date = sdf.parse(note.getTime().substring(0, 6));
            }catch (ParseException e){
                e.printStackTrace();
            }
            sdf = new SimpleDateFormat("MMM", Locale.US);
            String strMonth = sdf.format(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar calendar_compare = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
            if(calendar.get(Calendar.YEAR) != calendar_compare.get(Calendar.YEAR)){
                textView_month.setText(strMonth + ", " + calendar.get(Calendar.YEAR));
            }else{
                textView_month.setText(strMonth);
            }
            textView_event.setText(note.getEvent());
            String strContent = note.getContent();
            if (strContent.contains("\n")) { // if the note have multiple lines, output line one and line two, and the textStyle of line1 is bold
                textView_content1.setText(strContent.substring(0, strContent.indexOf("\n")));
                textView_content2.setText(strContent.substring(strContent.indexOf("\n") + 1, strContent.length()));
            } else { //else just output line1
                textView_content1.setText(strContent);
            }
            SimpleDateFormat sdf_in = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sdf_out = new SimpleDateFormat("dd, HH:mm");
            Date dtTime = new Date(System.currentTimeMillis());
            try {
                dtTime = sdf_in.parse(note.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (note.getTime() != null)
                textView_time.setText(sdf_out.format(dtTime));
        }
        return view;
    }
}
