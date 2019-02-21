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

public class saveSolarInfo extends AppCompatActivity {

    static String LATITUDE="lat" ,LONGITUDE="lon",AREA_PANEL="area",
            EFFICIENCY="eff",MAX_POWER="max",NUMBER_OF_PANEL="numofpanel";
    EditText latEditText,lonEditText,areaEditText,maxPowerEditText,effEditText,panelCountEditText;
    Button calcEnergyButton;
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
        calcEnergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeInfo();
            }
        });
    }
    private void storeInfo()
    {
        SharedPreferences pref=this.getSharedPreferences("com.example.pracprac", Context.MODE_PRIVATE);

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

            pref.edit().putString(LATITUDE,lat).apply();
            pref.edit().putString(LONGITUDE,lon).apply();
            pref.edit().putString(AREA_PANEL,area).apply();
            pref.edit().putString(MAX_POWER,maxPower).apply();
            pref.edit().putString(EFFICIENCY,eff).apply();
            pref.edit().putString(NUMBER_OF_PANEL,numOfPanel).apply();
            Toast.makeText(saveSolarInfo.this,"Data is Updated",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref=this.getSharedPreferences("com.example.pracprac", Context.MODE_PRIVATE);
        if(pref.getString(LATITUDE,"")!="")
        {
            latEditText.setText(pref.getString(LATITUDE,""));
            lonEditText.setText(pref.getString(LONGITUDE,""));
            areaEditText.setText(pref.getString(AREA_PANEL,""));
            maxPowerEditText.setText(pref.getString(MAX_POWER,""));
            effEditText.setText(pref.getString(EFFICIENCY,""));
            panelCountEditText.setText(pref.getString(NUMBER_OF_PANEL,""));
        }
    }
}
