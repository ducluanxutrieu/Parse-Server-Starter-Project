package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<String> user;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        setTitle(R.string.user_list);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        user = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, user);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                GetUserList();
            }
        });
        thread.start();


/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), UserFeedActivity.class);
                intent.putExtra("username", user.get(i));
                startActivity(intent);
            }
        });*/



    }

    private void GetUserList(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addAscendingOrder("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object : objects){
                        user.add(object.getString("username"));
                    }
                    listView.setAdapter(adapter);
                }else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share){
            GetPhoto();
        }else if (item.getItemId() == R.id.logout){
            ParseUser.logOut();
            finish();
            /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        assert data != null;
        final Uri uri = data.getData();

        if (requestCode == 1 && resultCode == RESULT_OK){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    UploadImage(uri);
                }
            });

            thread.start();


        }
    }

    private void UploadImage(Uri data){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data);
            //imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] byteArray = stream.toByteArray();
            ParseFile file = new ParseFile("image.jpeg", byteArray);

            ParseObject object = new ParseObject("Image");
            object.put("image", file);
            object.put("username",  ParseUser.getCurrentUser().getUsername());
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        Toast.makeText(getApplicationContext(), "Upload image successful!", Toast.LENGTH_LONG).show();
                    }else {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Upload image false!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            Log.i("statuss", "successful! ");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
