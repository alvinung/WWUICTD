package com.example.kasingj.smokecessation2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class login extends AppCompatActivity {

    EditText USERNAME, PASSWORD;
    String username, password;
    Context ctx = this;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userService = new UserService(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // authenticates users: if successful, go to dashboard
    public void goToDashboard (View view) {
        UserEntity entity;
        USERNAME = (EditText) findViewById(R.id.userNameInput);
        PASSWORD = (EditText) findViewById(R.id.passwordInput);
        username = USERNAME.getText().toString();
        password = PASSWORD.getText().toString();

        DatabaseOperations db = new DatabaseOperations(ctx);
        //Cursor cr = db.getUserAuth(db);
        UserEntity userEntity;
        //if not authorized return userAuth id
        userEntity = userService.getUserIfAuthorized(username,password);

        if(userEntity != null){

            //try to add user again
            ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if(  networkInfo != null && networkInfo.isConnected()){
                HttpServices services = new HttpServices(getApplicationContext());
                if(userEntity.getServerId() ==-1){
                    services.addUserToServer(userEntity);
                }else{
                    services.getBuddies(userEntity);
                }
            }

            SharedPreferences preference = getSharedPreferences(getApplicationContext().getPackageName() , 1 );
            SharedPreferences.Editor editor = preference.edit();
            editor.putInt(MainActivity.CURRENT_USER_ID ,userEntity.getID());
            editor.commit();

            Toast.makeText(getBaseContext(), "Login Success", Toast.LENGTH_LONG).show();
            Intent intent = new Intent (this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
            USERNAME.setText("");
            PASSWORD.setText("");
        }

    }

    // go to sign up page
    public void goToSignUp (View view) {
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
