package com.example.pracprac;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class saveSolarInfo extends AppCompatActivity {
    EditText latEditText,lonEditText,areaEditText,maxPowerEditText,effEditText,panelCountEditText;
    Button calcEnergyButton;
    String UId;
    solarClass data;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_solar_info);
        latEditText=findViewById(R.id.latEditText);
        lonEditText=findViewById(R.id.lonEditText);
        areaEditText=findViewById(R.id.editTextArea);
        maxPowerEditText=findViewById(R.id.maxPowerEditText);
        effEditText=findViewById(R.id.effEditText);
        calcEnergyButton=findViewById(R.id.calculateButton);
        panelCountEditText=findViewById(R.id.panelCountEditText);
        toolbar=findViewById(R.id.savesolartoolbar);
        setSupportActionBar(toolbar);
        UId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        calcEnergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeInfo())
                {
                    startActivity(new Intent(saveSolarInfo.this, SolarActivity.class));
                }

            }
        });


    }
    private boolean storeInfo()
    {
        String lat,lon,area,maxPower,eff,numOfPanel;
        lat=latEditText.getText().toString().trim();
        lon=lonEditText.getText().toString().trim();
        area=areaEditText.getText().toString().trim();
        maxPower=maxPowerEditText.getText().toString().trim();
        eff=effEditText.getText().toString().trim();
        numOfPanel=panelCountEditText.getText().toString().trim();
        if(lat.isEmpty() || lon.isEmpty()|| area.isEmpty() || maxPower.isEmpty() || eff.isEmpty()|| numOfPanel.isEmpty())
        {
            Log.d("button Clicked","calculate energy");
            Toast.makeText(saveSolarInfo.this,"All Fields are mandatory ",Toast.LENGTH_LONG).show();
            return false;
        }
        else {
                solarClass obj=new solarClass(lon,lat,area,maxPower,eff,numOfPanel);

                FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                        .child("solar")
                    .child(UId)
                    .setValue(obj);
                return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users")
                .child("solar").child(UId);

        ValueEventListener valueEvnetListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                solarClass obj=dataSnapshot.getValue(solarClass.class);
                if(obj==null)
                {
                    
                    return;
                }
                latEditText.setText(obj.getLat());
                lonEditText.setText(obj.getLon());
                areaEditText.setText(obj.getArea());
                effEditText.setText(obj.getMaxEfficieny());
                panelCountEditText.setText(obj.getPanelCount());
                maxPowerEditText.setText(obj.getRatedVoltage());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEvnetListener);

    }
}
