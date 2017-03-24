package com.example.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ren.qinc.edit.PerformEdit;

/**
 * Created by Kayla on 2017/3/17.
 */

public class ContentActivity extends AppCompatActivity {
    private Button button_event;
    private EditText editText_content;
    private PerformEdit performEdit;
    private String strTime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button in ActionBar
        button_event = (Button) findViewById(R.id.button_event);
        editText_content = (EditText)findViewById(R.id.editText_content);
        editText_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_content.setHint(null);
            }
        });
        performEdit = new PerformEdit(editText_content);
        Intent intent = getIntent();
        if(intent.getStringExtra("time") != null) {
            if (!intent.getStringExtra("time").equals("")) {
                strTime = intent.getStringExtra("time");
                performEdit.setDefaultText(intent.getStringExtra("content"));
                button_event.setText(intent.getStringExtra("event"));
            } else {
                if(intent.getStringExtra("event") != null)
                    if (!intent.getStringExtra("event").equals(""))
                        button_event.setText(intent.getStringExtra("event"));
                    else button_event.setText(R.string.default_event);
                else button_event.setText(R.string.default_event);
                if(intent.getStringExtra("content") != null)
                    if (!intent.getStringExtra("content").equals(""))
                        performEdit.setDefaultText(intent.getStringExtra("content"));
                    else performEdit.setDefaultText("");
                else performEdit.setDefaultText("");
            }
        } else {
            if(getIntent().getStringExtra("comefrom").equals("eventnotes"))
                button_event.setText(getIntent().getStringExtra("event"));
            else
                button_event.setText(R.string.default_event);
            performEdit.setDefaultText("");
        }
        button_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, SelectEventActivity.class);
                intent.putExtra("comefrom", getIntent().getStringExtra("comefrom"));
                intent.putExtra("event",button_event.getText().toString());
                intent.putExtra("content", editText_content.getText().toString());
                intent.putExtra("time", strTime);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_content, menu);
        try {
            //If not empty, reflecting setOptionalIconsVisible way to get the menu
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            //Violence access the method
            method.setAccessible(true);
            //Call this method to display the icon
            method.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            //if user click back button, go back to MainActivity
            case android.R.id.home:
                performEdit.clearHistory();//clear the store of edit-operations
                if(getIntent().getStringExtra("comefrom").equals("main")) {
                    Intent intent = new Intent(ContentActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(ContentActivity.this, EventNotesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("event", button_event.getText());
                    startActivity(intent);
                }
                break;
            //if user click check button, save note to database, and then go back to MainActivity
            case R.id.action_check:
                if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED))
                    Toast.makeText(getApplicationContext(), "The current system doesn't have the SD card directory!", Toast.LENGTH_SHORT).show();
                else if (editText_content.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "empty note can't be save!", Toast.LENGTH_SHORT).show();
                } else if (strTime.equals("")) {
                    if (saveNewNote()) {
                        performEdit.clearHistory();//clear the store of edit-operations
                        if(getIntent().getStringExtra("comefrom").equals("main")) {
                            Intent intent = new Intent(ContentActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(ContentActivity.this, EventNotesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("event", button_event.getText());
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "can't save the old note!", Toast.LENGTH_SHORT).show();
                    }
                } else if (saveOldNote()) {
                    performEdit.clearHistory();//clear the store of edit-operations
                    if(getIntent().getStringExtra("comefrom").equals("main")) {
                        Intent intent = new Intent(ContentActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(ContentActivity.this, EventNotesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("event", button_event.getText());
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "can't save the new note!", Toast.LENGTH_SHORT).show();
                }

                break;
            //if user click undo button, undo edittext_content
            case R.id.action_undo:
                performEdit.undo();
                break;
            //if user click redo button, undo edittext_content
            case R.id.action_redo:
                performEdit.redo();
                break;
            // if user click delete button, alert a dialog and if user insist, delete this note
            case R.id.action_delete:
                new AlertDialog.Builder(ContentActivity.this)
                        .setTitle("Are you sure to delete this note?")   //set title
                        .setNegativeButton("cancel", null)  // if user click cancel button, do nothing
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){  //if user click OK button
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!strTime.equals("")) {
                                    if (DataSupport.deleteAll(Note.class, "time like ?", strTime) > 0) {  // delete this note in database
                                        Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                                }
                                if(getIntent().getStringExtra("comefrom").equals("main")) {
                                    Intent intent = new Intent(ContentActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(ContentActivity.this, EventNotesActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("event", button_event.getText());
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();  // show the dialog
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean saveNewNote(){
        try {
            String strEvent = button_event.getText().toString();
            String strContent = editText_content.getText().toString();
            Note note = new Note();
            note.setEvent(strEvent);
            note.setContent(strContent);
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String strTime = sdf.format(now);
            note.setTime(strTime);
            LitePal.getDatabase();
            note.save();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveOldNote(){
        try {
            String strEvent = button_event.getText().toString();
            String strContent = editText_content.getText().toString();
            Note note = new Note();
            note.setEvent(strEvent);
            note.setContent(strContent);
            LitePal.getDatabase();
            note.updateAll("time = ?", strTime);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
