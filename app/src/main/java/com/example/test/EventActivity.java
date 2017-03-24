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

public class EventActivity extends AppCompatActivity{

    ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // show back button in ActionBar
        // load listView
        final List<Event> eventList = DataSupport.findAll(Event.class);
        List<String> dataList = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listView_event);
        for (int i = 0; i < eventList.size(); i++) {
            dataList.add(eventList.get(i).getName());
        }
        EventAdapter adapter = new EventAdapter(this, dataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EventActivity.this, EventNotesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("event", eventList.get(position).getName());
                startActivity(intent);
            }
        });

        // loading the button of adding new event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_event);
        final EditText editText_dialog = new EditText(EventActivity.this); // for getting the input in OK.OnClick()
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                new AlertDialog.Builder(EventActivity.this, R.style.AlertDialogDefault)
                        .setTitle("please input event name:")   //set title
                        .setView(editText_dialog)   // add a editText
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener(){
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
                                        Toast.makeText(getApplicationContext(), "Add successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(EventActivity.this, EventActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    Toast.makeText(EventActivity.this, "Something wrong with dialog!", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(EventActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
