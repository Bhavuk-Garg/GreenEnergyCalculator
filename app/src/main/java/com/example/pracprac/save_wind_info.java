package com.example.pracprac;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

public class save_wind_info extends AppCompatActivity {
    EditText latEditText,lonEditText,diameterEditText,maxPowerEditText,mech_effEditText,rotorCountEditText,gene_effEditText,cityEditText;
    Button calcEnergyButton;
    String UId;
    windClass data;
    Toolbar toolbar;
    String latitude;
    String longitude;

    public void findLocation(View view)
    {
        String city=cityEditText.getText().toString();
        if(city.isEmpty())
            Toast.makeText(save_wind_info.this,"ENTER CITY NAME",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(save_wind_info.this,"ENTER VALID CITY NAME",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_wind_info);
        cityEditText=findViewById(R.id.entercitywindEdittext);
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
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {}
        //we are connected to a network

        else{
            latEditText.setInputType(InputType.TYPE_NULL);
            Toast.makeText(save_wind_info.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();

        }
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
