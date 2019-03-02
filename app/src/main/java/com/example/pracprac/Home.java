package com.example.pracprac;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.SNIHostName;

public class Home extends AppCompatActivity {

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user=FirebaseAuth.getInstance().getCurrentUser();
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                if( user!=null)
                {
                    if(user.isEmailVerified())
                        startActivity(new Intent(Home.this,ChooseEnergy.class));
                    else
                        startActivity(new Intent(Home.this,SignIn.class));
                }
                else {
                    finish();
                    startActivity(new Intent(Home.this, SignIn.class));
                }
            }
        };
        handler.postDelayed(runnable,1400);
    }
}
