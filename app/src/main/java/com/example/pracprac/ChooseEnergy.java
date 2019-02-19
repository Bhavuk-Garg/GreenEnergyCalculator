package com.example.pracprac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class ChooseEnergy extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_energy);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth=FirebaseAuth.getInstance();
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
}
