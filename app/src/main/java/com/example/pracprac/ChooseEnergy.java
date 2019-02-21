package com.example.pracprac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseEnergy extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth mAuth;
    Button solar,wind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_energy);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();
        solar=findViewById(R.id.solarButton);
        wind=findViewById(R.id.windButton);
       solar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(ChooseEnergy.this,saveSolarInfo.class));
                    }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //This is initializing Tool Bar
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.select_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
                case R.id.solarConfig:
                startActivity(new Intent(ChooseEnergy.this, saveSolarInfo.class));
                break;

                case R.id.logOutMenu:
                finish();
                mAuth.signOut();
                startActivity(new Intent(ChooseEnergy.this,SignIn.class));
                break;

                case R.id.helPMenu:
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation animation=AnimationUtils.loadAnimation(ChooseEnergy.this,R.anim.fadein);
        solar.startAnimation(animation);
        wind.startAnimation(animation);
    }
}
