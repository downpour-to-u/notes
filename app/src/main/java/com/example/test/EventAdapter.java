package com.example.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Kayla on 2017/3/22.
 */

public class EventAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int resourceId;

    public EventAdapter(Context context, List<String> objects){
        super(context, R.layout.event_item, objects);
        mContext = context;
        resourceId = R.layout.event_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String event = getItem(position);//get current Event object
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView textView_event_name = (TextView)view.findViewById(R.id.textView_event_name);
        TextView textView_note_num = (TextView)view.findViewById(R.id.textView_note_num);
        Button button_edit = (Button) view.findViewById(R.id.button_edit);
        Button button_delete = (Button) view.findViewById(R.id.button_delete);
        textView_event_name.setText(event);
        textView_note_num.setText("The event has " + DataSupport.where("event like ?", event).count(Note.class) + " notes");
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText_dialog = new EditText(mContext); // for getting the input in OK.OnClick()
                editText_dialog.setText(event);
                try{
                new AlertDialog.Builder(mContext, R.style.AlertDialogDefault)
                        .setTitle("Edit event name:")   //set title
                        .setView(editText_dialog)   // add a editText
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })  // if user click cancel button, do nothing
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){  //if user click OK button
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strInput = editText_dialog.getText().toString();
                                if(strInput.equals("")){
                                    Toast.makeText(mContext, "It can't be empty!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Event evtEvent = new Event(strInput);
                                    if(evtEvent.updateAll("name like ?", event) > 0) {  // update event in database
                                        Note note = new Note();
                                        note.setEvent(strInput);
                                        note.updateAll("event like ?", event);
                                        Toast.makeText(mContext, "Edit successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mContext, EventActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(intent);
                                    }else{
                                        Toast.makeText(mContext, "Event of the same name already exists", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();  // show the dialog
                }catch (Exception e){
                    Toast.makeText(mContext, "Something wrong with dialog!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AlertDialog.Builder(mContext, R.style.AlertDialogDefault)
                            .setTitle("The notes belong this event will be delete too.\nAre you sure to delete it?")   //set title
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })  // if user click cancel button, do nothing
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {  //if user click OK button
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (DataSupport.deleteAll(Event.class, "name like ?", event) > 0) {  // delete this note in database
                                        DataSupport.deleteAll(Note.class, "event like ?", event);
                                        Toast.makeText(mContext, "Delete successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mContext, EventActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mContext.startActivity(intent);
                                    } else {
                                        Toast.makeText(mContext, "Delete failed", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .show();  // show the dialog
                }catch (Exception e){
                    Toast.makeText(mContext, "Something wrong with dialog!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
}
