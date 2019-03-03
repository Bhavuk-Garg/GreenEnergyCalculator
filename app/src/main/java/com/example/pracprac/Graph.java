package com.example.pracprac;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.mikephil.charting.data.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class Graph extends Fragment {

    public Graph() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }
    String todayString="";
    Calendar c;
    long dnisum=0;
    SimpleDateFormat formatter;

    String latitude,longitude;
    private TextView mEmptyStateTextView;

    RequestQueue requestQueue;
    StringRequest stringRequest;
    String Area,noofpanels, efficiency,maxpower;
    public LineChart mchart;
    String rotorCount,MechMaxEff,GeneMaxEff,diameter,ratedVoltage;



    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        DatabaseReference ref = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("users");

        switch (Table.choice)
        {
            case 1:
                Log.d("bhavuk graph","hourly");
                //solar_hourly
                mchart = (LineChart) view.findViewById(R.id.llinechart);
                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(true);
                YAxis leftAxis = mchart.getAxisLeft();
                //leftAxis.setAxisMaximum(80f);
                //leftAxis.setAxisMinimum(-55f);
                leftAxis.enableGridDashedLine(10f,10f,0);
                leftAxis.setDrawLimitLinesBehindData(true);

                mchart.getAxisRight().setEnabled(false);
                ref .child("solar").child(FirebaseAuth.getInstance().getUid().toString()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                solarClass obj = dataSnapshot.getValue(solarClass.class);
                                if(obj!=null){
                                    latitude = obj.getLon().toString().trim();
                                    longitude = obj.getLat().toString().trim();
                                    Area=obj.getArea();
                                    noofpanels=obj.panelCount;
                                    efficiency=obj.maxEfficieny;
                                    maxpower=obj.ratedVoltage;
                                    String url="";

                                    //Log.d("current time ", todayString);
                                    url = "https://api.solcast.com.au/radiation/forecasts?longitude="+longitude+"&latitude="+latitude+"&api_key=dsQiZXOrsq3npvYi6XMs-s1RLAumDaVQ&format=json";
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        //getting the whole json object from the response
                                                        JSONObject obj = new JSONObject(response);

                                                        JSONArray array = obj.getJSONArray("forecasts");

                                                        ArrayList<Entry> yValues = new ArrayList<>();
                                                        ArrayList<String> timeaxis  = new ArrayList<>();
                                                        for (int i = 0; i <=48; i += 2) {
                                                            JSONObject item = array.getJSONObject(i);
                                                            String dni = item.getString("dni");
                                                            String time = getNewDate(item.getString("period_end"));


                                                            float  h= (Integer.valueOf(dni)*Integer.valueOf(Area)*Integer.valueOf(noofpanels)*Integer.valueOf(efficiency))/1000*36;
                                                            Log.d("energy",String.valueOf(h));
                                                            yValues.add(new Entry(i/2,h));
                                                            timeaxis.add(time);

                                                        }

                                                        //timeaxis.add("34");


                                                        LineDataSet set1 = new LineDataSet(yValues,"Time");
                                                        set1.setFillAlpha(101);
                                                        set1.setColor(Color.RED);
                                                        set1.setLineWidth(3f);
                                                        set1.setValueTextSize(10f);
                                                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                                        dataSets.add(set1);

                                                        LineData data = new LineData(dataSets);

                                                        mchart.setData(data);
                                                        XAxis xaxis = mchart.getXAxis();
                                                        xaxis.setValueFormatter(new MyAxisValueFormatter(timeaxis));
                                                        xaxis.setGranularity(1);
                                                        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);







                                                        //we have the array named hero inside the object
                                                        //so here we are getting that json array

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //displaying the error in toast if occurrs
                                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //creating a request queue
                                    requestQueue = Volley.newRequestQueue(getActivity());

                                    //adding the string request to request queue
                                    requestQueue.add(stringRequest);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Unable to fetch information", Toast.LENGTH_LONG).show();
                            }
                        });

                break;
            case 2:
                Log.d("bhavuk graph","daily");
                mchart = (LineChart) view.findViewById(R.id.llinechart);
                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(true);
                 leftAxis = mchart.getAxisLeft();
                //leftAxis.setAxisMaximum(80f);
                //leftAxis.setAxisMinimum(-55f);
                leftAxis.enableGridDashedLine(10f,10f,0);
                leftAxis.setDrawLimitLinesBehindData(true);

                mchart.getAxisRight().setEnabled(false);
                String url="";
                url = "https://api.solcast.com.au/radiation/forecasts?longitude=35.165&latitude=74.211&api_key=dsQiZXOrsq3npvYi6XMs-s1RLAumDaVQ&format=json";
                ref .child("solar").child(FirebaseAuth.getInstance().getUid().toString()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                solarClass obj = dataSnapshot.getValue(solarClass.class);
                                if(obj!=null){
                                    latitude = obj.getLon().toString().trim();
                                    longitude = obj.getLat().toString().trim();
                                    Area=obj.getArea();
                                    noofpanels=obj.panelCount;
                                    efficiency=obj.maxEfficieny;
                                    maxpower=obj.ratedVoltage;

                                    c= Calendar.getInstance();
                                    c.add(Calendar.DATE, 1);
                                    Date todayDate = c.getTime();
                                    formatter = new SimpleDateFormat("dd-MM-yyyy");
                                    todayString = formatter.format(todayDate);

                                    String url="";

                                    //Log.d("current time ", todayString);
                                    url = "https://api.solcast.com.au/radiation/forecasts?longitude="+longitude+"&latitude="+latitude+"&api_key=dsQiZXOrsq3npvYi6XMs-s1RLAumDaVQ&format=json";
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        //getting the whole json object from the response
                                                        JSONObject obj = new JSONObject(response);

                                                        JSONArray array = obj.getJSONArray("forecasts");

                                                        ArrayList<Entry> yValues = new ArrayList<>();
                                                        ArrayList<String> timeaxis  = new ArrayList<>();
                                                        int count=48,j=0;
                                                        for (int i = 0; i <array.length(); i += 1) {
                                                            JSONObject item = array.getJSONObject(i);
                                                            String dni = item.getString("dni");
                                                            String time = getNewDatedaily(item.getString("period_end"));
                                                            Log.d("bhavuk","daily");

                                                            if(!todayString.equals(time))
                                                                continue;
                                                            else{
                                                                dnisum= Integer.valueOf(dni)+dnisum;
                                                                count--;
                                                                if(count==0)
                                                                {

                                                                    float h= (dnisum*Integer.valueOf(Area)*Integer.valueOf(noofpanels)*Integer.valueOf(efficiency))/1000*18;
                                                                    Log.d("energy",String.valueOf(h));
                                                                    yValues.add(new Entry(j,h));
                                                                    timeaxis.add(time);

                                                                    c.add(Calendar.DATE, 1);
                                                                    Date todayDate = c.getTime();
                                                                    todayString = formatter.format(todayDate);
                                                                    count=48;
                                                                    dnisum=0;
                                                                    j++;

                                                                }
                                                            }


                                                        }

                                                        //timeaxis.add("34");


                                                        LineDataSet set1 = new LineDataSet(yValues,"Time");
                                                        set1.setFillAlpha(101);
                                                        set1.setColor(Color.RED);
                                                        set1.setLineWidth(3f);
                                                        set1.setValueTextSize(10f);
                                                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                                        dataSets.add(set1);

                                                        LineData data = new LineData(dataSets);

                                                        mchart.setData(data);
                                                        XAxis xaxis = mchart.getXAxis();
                                                        xaxis.setValueFormatter(new MyAxisValueFormatter(timeaxis));
                                                        xaxis.setGranularity(1);
                                                        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);







                                                        //we have the array named hero inside the object
                                                        //so here we are getting that json array

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                            }

                                            ,
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //displaying the error in toast if occurrs
                                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //creating a request queue
                                    requestQueue = Volley.newRequestQueue(getActivity());

                                    //adding the string request to request queue
                                    requestQueue.add(stringRequest);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Unable to fetch information", Toast.LENGTH_LONG).show();
                            }
                        });

                break;

            case 3:

                Log.d("bhavuk","in case 3");
                mchart = (LineChart) view.findViewById(R.id.llinechart);
                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(true);
                leftAxis = mchart.getAxisLeft();
                //leftAxis.setAxisMaximum(80f);
                //leftAxis.setAxisMinimum(-55f);
                leftAxis.enableGridDashedLine(10f,10f,0);
                leftAxis.setDrawLimitLinesBehindData(true);

                mchart.getAxisRight().setEnabled(false);

                ref .child("wind").child(FirebaseAuth.getInstance().getUid().toString()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                windClass obj = dataSnapshot.getValue(windClass.class);
                                if(obj!=null){
                                    latitude = obj.getLon().toString().trim();
                                    longitude = obj.getLat().toString().trim();
                                    diameter=obj.getDia();
                                    MechMaxEff=obj.getMechMaxEfficieny();
                                    GeneMaxEff=obj.getGeneMaxEfficieny();
                                    rotorCount=obj.getrotorCount();
                                    ratedVoltage=obj.getRatedVoltage();


                                    String url="";

                                    //Log.d("current time ", todayString);
                                    url = "https://api.darksky.net/forecast/7d1ff8ef8796fbcb790c6d9a424391c7/"+latitude+","+longitude+"?exclude=currently,minutely,daily,alerts,flags";
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        //getting the whole json object from the response
                                                        JSONObject obj = new JSONObject(response);

                                                        JSONObject objt = obj.getJSONObject("hourly");
                                                        JSONArray array = objt.getJSONArray("data");

                                                        ArrayList<Entry> yValues = new ArrayList<>();
                                                        ArrayList<String> timeaxis  = new ArrayList<>();
                                                        float count=48,j=0;
                                                        for (int i = 0; i <=24; i += 1) {
                                                            JSONObject item = array.getJSONObject(i);
                                                            String windSpeed = item.getString("windSpeed");
                                                            windSpeed=String.valueOf(Double.valueOf(windSpeed)*1609.34/3600);
                                                            Double windsp =Double.valueOf(windSpeed);
                                                            Double temp= ((Double.valueOf(item.getString("temperature")) ));
                                                            temp= (temp-32)*5/9+273.15;
                                                            //Log.d("temp",temp);
                                                            Double pressure = Double.valueOf(item.getString("pressure"))*100;
                                                            String time = item.getString("time");
                                                            long time2= Long.valueOf(time);
                                                            java.util.Date formatteddate= new java.util.Date(time2*1000);



                                                            //Date dataobject= new Date(time);
                                                            //String formatteddate= formatDate(dataobject);
                                                            //Log.d("formatdate",formatteddate);

                                                            float  h= (float)(Double.valueOf(MechMaxEff)*Double.valueOf(GeneMaxEff)*Double.valueOf(rotorCount)*Double.valueOf(ratedVoltage)*3.14*Double.valueOf(diameter)*Double.valueOf(diameter)/4);

                                                            //Log.d("energy",String.valueOf(h));
                                                            h*=0.5*windsp*windsp*windsp*pressure/(287.05*temp)*24*3600/1000000;
                                                            Log.d("bhavuk","daily");

                                                                    yValues.add(new Entry(j,h));
                                                                    String formatdate =formatteddate.toString().substring(4,10);
                                                                    formatdate= formatdate+"\n"+formatteddate.toString().substring(11,16);
                                                                    Log.d("formatedate=",formatdate);
                                                                    timeaxis.add(formatdate);

                                                                    j++;

                                                        }

                                                        //timeaxis.add("34");


                                                        LineDataSet set1 = new LineDataSet(yValues,"Time");
                                                        set1.setFillAlpha(101);
                                                        set1.setColor(Color.RED);
                                                        set1.setLineWidth(3f);
                                                        set1.setValueTextSize(10f);
                                                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                                        dataSets.add(set1);

                                                        LineData data = new LineData(dataSets);

                                                        mchart.setData(data);
                                                        XAxis xaxis = mchart.getXAxis();
                                                        xaxis.setValueFormatter(new MyAxisValueFormatter(timeaxis));
                                                        xaxis.setGranularity(1);
                                                        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);







                                                        //we have the array named hero inside the object
                                                        //so here we are getting that json array

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }

                                            ,
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //displaying the error in toast if occurrs
                                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //creating a request queue
                                    requestQueue = Volley.newRequestQueue(getActivity());

                                    //adding the string request to request queue
                                    requestQueue.add(stringRequest);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Unable to fetch information", Toast.LENGTH_LONG).show();
                            }
                        });
                break;
            case 4:
                mchart = (LineChart) view.findViewById(R.id.llinechart);
                mchart.setDragEnabled(true);
                mchart.setScaleEnabled(true);
                leftAxis = mchart.getAxisLeft();
                //leftAxis.setAxisMaximum(80f);
                //leftAxis.setAxisMinimum(-55f);
                leftAxis.enableGridDashedLine(10f,10f,0);
                leftAxis.setDrawLimitLinesBehindData(true);

                mchart.getAxisRight().setEnabled(false);

                ref .child("wind").child(FirebaseAuth.getInstance().getUid().toString()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                windClass obj = dataSnapshot.getValue(windClass.class);
                                if(obj!=null){
                                    latitude = obj.getLon().toString().trim();
                                    longitude = obj.getLat().toString().trim();
                                    diameter=obj.getDia();
                                    MechMaxEff=obj.getMechMaxEfficieny();
                                    GeneMaxEff=obj.getGeneMaxEfficieny();
                                    rotorCount=obj.getrotorCount();
                                    ratedVoltage=obj.getRatedVoltage();


                                    String url="";

                                    //Log.d("current time ", todayString);
                                    url = "https://api.darksky.net/forecast/7d1ff8ef8796fbcb790c6d9a424391c7/"+latitude+","+longitude+"?exclude=currently,minutely,hourly,alerts,flags";
                                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        //getting the whole json object from the response
                                                        JSONObject obj = new JSONObject(response);

                                                        JSONObject objt = obj.getJSONObject("daily");
                                                        JSONArray array = objt.getJSONArray("data");

                                                        ArrayList<Entry> yValues = new ArrayList<>();
                                                        ArrayList<String> timeaxis  = new ArrayList<>();
                                                        float count=48,j=0;
                                                        for (int i = 0; i <array.length(); i += 1) {
                                                            JSONObject item = array.getJSONObject(i);
                                                            String windSpeed = item.getString("windSpeed");
                                                            windSpeed=String.valueOf(Double.valueOf(windSpeed)*1609.34/3600);
                                                            Double windsp =Double.valueOf(windSpeed);
                                                            Double temp= ((Double.valueOf(item.getString("temperatureMin")) +Double.valueOf(item.getString("temperatureMax")))/2);
                                                            temp= (temp-32)*5/9+273.15;
                                                            //temp= (temp-32)*5/9+273.15;
                                                            //Log.d("temp",temp);
                                                            Double pressure = Double.valueOf(item.getString("pressure"))*100;
                                                            String time = item.getString("time");
                                                            long time2= Long.valueOf(time);
                                                            java.util.Date formatteddate= new java.util.Date(time2*1000);



                                                            //Date dataobject= new Date(time);
                                                            //String formatteddate= formatDate(dataobject);
                                                            //Log.d("formatdate",formatteddate);

                                                            float  h= (float)(Double.valueOf(MechMaxEff)*Double.valueOf(GeneMaxEff)*Double.valueOf(rotorCount)*Double.valueOf(ratedVoltage)*3.14*Double.valueOf(diameter)*Double.valueOf(diameter)/4);

                                                            //Log.d("energy",String.valueOf(h));
                                                            h*=0.5*windsp*windsp*windsp*pressure/(287.05*temp)*24*3600/1000000;
                                                            Log.d("bhavuk","daily");
                                                            String check = "\n";

                                                            yValues.add(new Entry(j,h));
                                                            String formatdate =formatteddate.toString().substring(4,10);
                                                            formatdate= formatdate+check+formatteddate.toString().substring(11,16);
                                                            Log.d("formatedate=",formatdate);
                                                            timeaxis.add(formatdate);

                                                            j++;

                                                        }

                                                        //timeaxis.add("34");


                                                        LineDataSet set1 = new LineDataSet(yValues,"Time");
                                                        set1.setFillAlpha(101);
                                                        set1.setColor(Color.RED);
                                                        set1.setLineWidth(3f);
                                                        set1.setValueTextSize(10f);
                                                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                                        dataSets.add(set1);

                                                        LineData data = new LineData(dataSets);

                                                        mchart.setData(data);
                                                        XAxis xaxis = mchart.getXAxis();
                                                        xaxis.setValueFormatter(new MyAxisValueFormatter(timeaxis));
                                                        xaxis.setGranularity(1);
                                                        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);



                                                        //we have the array named hero inside the object
                                                        //so here we are getting that json array

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }

                                            ,
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    //displaying the error in toast if occurrs
                                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    //creating a request queue
                                    requestQueue = Volley.newRequestQueue(getActivity());

                                    //adding the string request to request queue
                                    requestQueue.add(stringRequest);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Unable to fetch information", Toast.LENGTH_LONG).show();
                            }
                        });
                break;

            default:
                break;
        }
    }

    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private ArrayList<String> mvalues;
        public MyAxisValueFormatter(ArrayList<String> values){
            this.mvalues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mvalues.get((int)value);
        }
    }
    public String getNewDate(String getOldDate){

        if (getOldDate == null){
            return "";
        }



        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000000'");


        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(getOldDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy - hh:mm a");

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }

    public String getNewDatedaily(String getOldDate){

        if (getOldDate == null){
            return "";
        }



        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000000'");


        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse(getOldDate);
            SimpleDateFormat newFormatter = new SimpleDateFormat("dd-MM-yyyy");

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dueDateAsNormal;
    }

}

