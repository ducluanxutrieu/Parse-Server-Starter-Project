/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity implements View.OnKeyListener{
    TextInputEditText userEdit, passEdit;
    String user, pass;
    Button btnLogin, btnSignUp;
    ParseUser parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.instagram);

        userEdit = (TextInputEditText) findViewById(R.id.edUser);
        passEdit = (TextInputEditText) findViewById(R.id.edPass);
        parseUser = new ParseUser();
        if (SDK_INT > M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        passEdit.setOnKeyListener(this);

/*

//Cái này là để thực hiện và lấy giá trị từ database hoặc chỉnh sửa thêm sửa xóa
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
    query.getInBackground("aNs14uFZYn", new GetCallback<ParseObject>() {
      @Override
      public void done(ParseObject object, ParseException e) {
        if(e == null && object != null){
          object.put("ducluanxutrie", 22);
          object.saveInBackground();
          Log.i("username", object.getString("username"));
          Log.i("password", object.getInt("password") + "") ;
        }else {
          e.printStackTrace();
        }
      }
    });*/



        //Lấy toàn bộ bảng

/*    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Sports");

    query.whereEqualTo("stt", 1); //Tìm ra stt = 1
    query.setLimit(1); //set số phần tử tối đa để xuất hiện chứ không ảnh hưởng đến database
    query.findInBackground(new FindCallback<ParseObject>() {
      @Override
      public void done(List<ParseObject> objects, ParseException e) {
        if (e == null){
          if (objects.size() > 0){
            for (ParseObject object : objects){
              Log.i("stt", object.getInt("stt") + "");
              Log.i("stt", object.getString("sport"));
            }
          }
        }
      }
    });
    */

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }



    public void SignUpClicked(View view) {
        user = userEdit.getText().toString();
        pass = passEdit.getText().toString();
        if (!user.isEmpty() && !pass.isEmpty()) {
            parseUser.setUsername(user);
            parseUser.setPassword(pass);
            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "SignUp Successful!", Toast.LENGTH_LONG).show();
                        ShowUserList();
                    } else {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "A username or password are required!", Toast.LENGTH_LONG).show();
        }


    }

    public void LoginClicked(View view) {
        user = userEdit.getText().toString();
        pass = passEdit.getText().toString();

        if (!user.isEmpty() && !pass.isEmpty()) {
            ParseUser.logInInBackground(user, pass, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null && e == null){
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        ShowUserList();
                    }else {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "A username or password are required!", Toast.LENGTH_LONG).show();
        }

    }

    public void ShowUserList(){
        Intent intent = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            LoginClicked(view);
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Permission accept sucessful!", Toast.LENGTH_LONG).show();
            }
        }
    }




}