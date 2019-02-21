package com.example.pracprac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class saveSolarInfo extends AppCompatActivity {
    EditText latEditText,lonEditText,areaEditText,maxPowerEditText,effEditText,panelCountEditText;
    Button calcEnergyButton;
    String UId;
    solarClass data;

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
        UId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        calcEnergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeInfo();
            }
        });
    }
    private void storeInfo()
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
            return;
        }
        else {
                solarClass obj=new solarClass(lon,lat,area,eff,numOfPanel,maxPower);

                FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                        .child("solar")
                    .child(UId)
                    .setValue(obj);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(UId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        data = dataSnapshot.getValue(solarClass.class);
                        Log.d("value",data.getArea());
                        latEditText.setText(data.getLat());
                        lonEditText.setText(data.getLon());
                        areaEditText.setText(data.getArea());
                        maxPowerEditText.setText(data.getRatedVoltage());
                        effEditText.setText(data.getMaxEfficieny());
                        panelCountEditText.setText(data.getPanelCount());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(saveSolarInfo.this,"Error Connecting Database",Toast.LENGTH_SHORT);
                    }



                });


    }
}
