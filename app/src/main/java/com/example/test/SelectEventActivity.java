package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kayla on 2017/3/22.
 */

public class SelectEventActivity extends AppCompatActivity {

    List<String> arr_data;// all event name
    ListView listView;
    String event;
    String content;
    String time;
    String comefrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // show back button in ActionBar
        // get information of this note
        Intent intent = getIntent();
        event = intent.getStringExtra("event") + "";
        content = intent.getStringExtra("content") + "";
        time = intent.getStringExtra("time") + "";
        comefrom = intent.getStringExtra("comefrom");
        // load listView
        List<Event> eventList = DataSupport.findAll(Event.class);
        arr_data = new ArrayList<String>();
        listView = (ListView)findViewById(R.id.listView_event);
        for(int i = 0; i < eventList.size(); i++){
            arr_data.add(eventList.get(i).getName());
        }
        SelectEventAdapter adapter = new SelectEventAdapter(this, arr_data, event);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SelectEventActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("comefrom", comefrom);
                intent.putExtra("event", arr_data.get(position));
                intent.putExtra("content", content);
                intent.putExtra("time", time);
                startActivity(intent);
            }
        });

        // loading the button of adding new event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_event);
        final EditText editText_dialog = new EditText(SelectEventActivity.this); // for getting the input in OK.OnClick()
        editText_dialog.setBackgroundResource(android.R.color.background_light);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new AlertDialog.Builder(SelectEventActivity.this, R.style.AlertDialogDefault)
                            .setTitle("New Event")   //set title
                            .setView(editText_dialog)   // add a editText
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })  // if user click cancel button, do nothing
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {  //if user click OK button
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strInput = editText_dialog.getText().toString();
                                    if (strInput.equals("")) {
                                        Toast.makeText(getApplicationContext(), "It can't be empty!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Event evtEvent = new Event(strInput);
                                        if (evtEvent.save()) {  // save event in database
                                            // go back to content edit view
                                            Intent intent = new Intent(SelectEventActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.putExtra("event", strInput);
                                            intent.putExtra("comefrom", comefrom);
                                            intent.putExtra("content", content);
                                            intent.putExtra("time", time);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Event of the same name already exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            })
                            .show();  // show the dialog
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Something wrong with dialog!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            //if user click back button, go back to MainActivity
            case android.R.id.home:
                Intent intent = new Intent(SelectEventActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("comefrom", comefrom);
                intent.putExtra("event", event);
                intent.putExtra("content", content);
                intent.putExtra("time", time);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
