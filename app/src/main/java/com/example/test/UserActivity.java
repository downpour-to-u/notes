package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
/**
 * Created by Kayla on 2017/3/23.
 */

public class UserActivity extends AppCompatActivity{

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static Uri tempUri;

    private ImageView imageView_change;
    private EditText editText_change_name;

    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button in ActionBar

        imageView_change = (ImageView)findViewById(R.id.imageView_change);
        if(getSharedPreferences("com.example.test", 0).getBoolean("avatar", false)){
            try {
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MayNoteDay/avatar.png");
                imageView_change.setImageURI(uri);
            }catch (Exception e){
                Toast.makeText(UserActivity.this, "Something wrong with your avatar!", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = getSharedPreferences("com.example.test", 0).edit();
                editor.putBoolean("avatar", false);
                editor.commit();
                e.printStackTrace();
            }
        }
        imageView_change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            }
        });

        editText_change_name = (EditText)findViewById(R.id.editText_change_name);
        editText_change_name.setText(getSharedPreferences("com.example.test", 0).getString("username", "your name"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(UserActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_check:
                String strName = editText_change_name.getText().toString();
                if(!strName.equals("")) {
                    SharedPreferences.Editor editor = getSharedPreferences("com.example.test", 0).edit();
                    editor.putString("username", strName);
                    editor.commit();
                    Intent intent2 = new Intent(UserActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                }else{
                    Toast.makeText(UserActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // If the return code can be used
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // Start cutting processing of images
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // Select the crop just get the pictures displayed in the interface
                    }
                    break;
            }
        }
    }

    /**
     * Crop the picture Method
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // Sets the clipping
        intent.putExtra("crop", "true");
        // aspectX aspectY is the ratio of width to height
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY is crop the picture width to height
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * save the cropped image data
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = com.example.test.Utils.toRoundBitmap(photo, tempUri); // This time the pictures have already been processed into a circle
            imageView_change.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    /**
     * save the image into external storage
     */
    private void uploadPic(Bitmap bitmap) {
        // Uploaded to the server
        // Here you can convert Bitmap file, and then get the URL of the file, do file upload /
        // Notice that the image has been obtained here is a circular image
        // Bitmap is not doing a round handle, but it has been cropped

        String imagePath = com.example.test.Utils.savePhoto(bitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+"/MayNoteDay/", "avatar");
        if(imagePath != null){
            // Upload with imagePath
            SharedPreferences.Editor editor = getSharedPreferences("com.example.test", 0).edit();
            editor.putBoolean("avatar", true);
            editor.commit();
        }
    }
}
