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

public class save_wind_info extends AppCompatActivity {
    EditText latEditText,lonEditText,diameterEditText,maxPowerEditText,mech_effEditText,rotorCountEditText,gene_effEditText;
    Button calcEnergyButton;
    String UId;
    windClass data;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_wind_info);
        latEditText=findViewById(R.id.latitudeEdittext);
        lonEditText=findViewById(R.id.longitudeEdittext);
        diameterEditText=findViewById(R.id.rdEdittext);
        maxPowerEditText=findViewById(R.id.powerEdittext);
        mech_effEditText=findViewById(R.id.mechanicalEdittext);
        calcEnergyButton=findViewById(R.id.calculate_button);
        rotorCountEditText=findViewById(R.id.routerNoEdittext);
        gene_effEditText=findViewById(R.id.generatorEdittext);
        toolbar=findViewById(R.id.savewindtoolbar);
        setSupportActionBar(toolbar);
        UId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        calcEnergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storeInfo())
                {
                    startActivity(new Intent(save_wind_info.this, wind_activity.class));
                }

            }
        });


    }
    private boolean storeInfo()
    {
        String lat,lon,dia,maxPower,m_eff,g_eff,numOfPanel;
        lat=latEditText.getText().toString().trim();
        lon=lonEditText.getText().toString().trim();
        dia=diameterEditText.getText().toString().trim();
        maxPower=maxPowerEditText.getText().toString().trim();
        m_eff=mech_effEditText.getText().toString().trim();
        g_eff=gene_effEditText.getText().toString().trim();
        numOfPanel=rotorCountEditText.getText().toString().trim();
        if(lat.isEmpty() || lon.isEmpty()|| dia.isEmpty() || maxPower.isEmpty() || m_eff.isEmpty()|| numOfPanel.isEmpty()|| g_eff.isEmpty())
        {
            Log.d("button Clicked","calculate energy");
            Toast.makeText(save_wind_info.this,"All Fields are mandatory ",Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            windClass obj=new windClass(lon,lat,dia,maxPower,m_eff,g_eff,numOfPanel);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child("wind")
                    .child(UId)
                    .setValue(obj);
            return true;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users")
                .child("wind").child(UId);

        ValueEventListener valueEvnetListener = new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                windClass obj=dataSnapshot.getValue(windClass.class);
                if(obj==null)
                {

                    return;
                }
                latEditText.setText(obj.getLat());
                lonEditText.setText(obj.getLon());
                diameterEditText.setText(obj.getDia());
                mech_effEditText.setText(obj.getMechMaxEfficieny());
                gene_effEditText.setText(obj.getGeneMaxEfficieny());
                rotorCountEditText.setText(obj.getrotorCount());
                maxPowerEditText.setText(obj.getRatedVoltage());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEvnetListener);

    }
}
