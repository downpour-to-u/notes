package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Kayla on 2017/3/23.
 */

public class EventNotesActivity extends AppCompatActivity {

    List<Note> noteList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // show back button in ActionBar

        Intent intent = getIntent();
        final String event = intent.getStringExtra("event");
        // loading all the notes of this event
        if(LitePal.getDatabase()!=null) {
            noteList = DataSupport.where("event like ?", event).order("time desc").find(Note.class);
            if (noteList != null) {
                ListView listView = (ListView)findViewById(R.id.listView_event);
                NoteAdapter adapter = new NoteAdapter(EventNotesActivity.this, noteList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Note note = noteList.get(position);
                        Intent intent = new Intent(EventNotesActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("comefrom", "eventnotes");
                        intent.putExtra("event",note.getEvent());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("time",note.getTime());
                        startActivity(intent);
                    }
                });
            }
        }

        // loading the button of adding new event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_event);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventNotesActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("comefrom", "eventnotes");
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            //if user click back button, go back to MainActivity
            case android.R.id.home:
                Intent intent = new Intent(EventNotesActivity.this, EventActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
