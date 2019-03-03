package com.example.pracprac;

import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class saveSolarInfo extends AppCompatActivity {
    EditText latEditText,lonEditText,areaEditText,maxPowerEditText,effEditText,panelCountEditText,cityEditText;
    Button calcEnergyButton;
    String UId;
    solarClass data;
    Toolbar toolbar;
    String latitude;
    String longitude;

    public void findLocation(View view)
    {
        String city=cityEditText.getText().toString();
        if(city.isEmpty())
            Toast.makeText(saveSolarInfo.this,"ENTER CITY NAME",Toast.LENGTH_SHORT).show();
        else {
            DownloadTask task = new DownloadTask();
            task.execute("https://api.opencagedata.com/geocode/v1/json?q=" + city + "&key=b5c64abf8d664de09d47e1c92b0a5d05");
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection =null;


            try {
                url = new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader =new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return  result;
            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject=new JSONObject((s));
                String results=jsonObject.getString("results");
                JSONArray resSub1=new JSONArray(results);
                JSONObject bounds=resSub1.getJSONObject(0);
                String boundsString=bounds.getString("bounds");
                Log.i("bounds",boundsString);
                Pattern p=Pattern.compile( "\"lat\":(.*?),\"lng\":" );
                Matcher m=p.matcher(boundsString);
                while(m.find())
                {
                    latitude=m.group(1);
                }
                p=Pattern.compile( "\"lng\":(.*?)," );
                m=p.matcher(boundsString);
                while(m.find())
                {
                    longitude=m.group(1);
                }
                Log.i(latitude,longitude);
                int length=longitude.length();
                longitude = longitude.substring(0, length-1);
                Log.i(latitude,longitude);
                latEditText.setText(latitude);
                lonEditText.setText(longitude);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(saveSolarInfo.this,"ENTER VALID CITY NAME",Toast.LENGTH_SHORT).show();
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_solar_info);
        cityEditText=findViewById(R.id.entercityEdittext);
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
                    finish();
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
