package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;
    private List<Note> noteList;

    ImageView imageView_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Defines how many times app is started by SharedPreferences
        SharedPreferences setting = getSharedPreferences("com.example.test", 0);
        Boolean user_first = setting.getBoolean("FIRST",true);
        if(user_first){// if it is first time starting app, add a event named "my first event"
            Event event = new Event(getResources().getString(R.string.default_event));
            event.save();
            SharedPreferences.Editor editor = setting.edit();
            editor.putBoolean("FIRST", false);
            editor.putString("username", "your name");
            editor.commit();
        }

        // loading toolbar of header
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // loading the button of adding new note
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("comefrom", "main");
                startActivity(intent);
            }
        });

        // Loading the sidebar menu content
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Loading the sidebar
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // loading all the notes
        if(LitePal.getDatabase()!=null) {
            noteList = DataSupport.order("time desc").find(Note.class);
            if (noteList != null) {
                ListView listView = (ListView)findViewById(R.id.listView_notes);
                NoteAdapter adapter = new NoteAdapter(MainActivity.this, noteList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Note note = noteList.get(position);
                        Intent intent = new Intent(MainActivity.this, ContentActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("comefrom", "main");
                        intent.putExtra("event",note.getEvent());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("time",note.getTime());
                        startActivity(intent);
                    }
                });
            }
        }

        // load user information
        if(!setting.getString("username", "your name").equals("your name")){
            TextView textView_username = (TextView)navigationView.getHeaderView(0).findViewById(R.id.textView_username);
            textView_username.setText(setting.getString("username", "your name"));
        }
        imageView_user = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView_user);
        if(setting.getBoolean("avatar", false)){
            try {
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MayNoteDay/avatar.png");
                imageView_user.setImageURI(uri);
            }catch (Exception e){
                Toast.makeText(MainActivity.this, "Something wrong with your avatar!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = setting.edit();
                editor.putBoolean("avatar", false);
                editor.commit();
                e.printStackTrace();
            }
        }
        if(imageView_user != null)
        imageView_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_event) {
            // Handle the event action
            Intent intent = new Intent(MainActivity.this, EventActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_about) {
            // Handle the event action
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
