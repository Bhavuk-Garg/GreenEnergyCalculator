package com.example.pracprac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FirstTimeSolarinfo extends AppCompatActivity {

    static String LATITUDE="lat" ,LONGITUDE="lon",AREA_PANEL="area",
            EFFICIENCY="eff",MAX_POWER="max",NUMBER_OF_PANEL="numofpanel";
    EditText latEditText,lonEditText,areaEditText,maxPowerEditText,effEditText,panelCountEditText;
    Button calcEnergyButton;
    String UId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solarinfo);
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
            Toast.makeText(FirstTimeSolarinfo.this,"All Fields are mandatory ",Toast.LENGTH_LONG).show();
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


}
