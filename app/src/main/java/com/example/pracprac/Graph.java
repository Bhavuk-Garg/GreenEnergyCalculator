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

import java.util.ArrayList;


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

    static int choice=2;
    String latitude,longitude;
    private TextView mEmptyStateTextView;

    RequestQueue requestQueue;
    StringRequest stringRequest;
    String Area,noofpanels, efficiency,maxpower;
    public LineChart mchart;


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        DatabaseReference ref = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("users");

        switch (choice)
        {
            case 1:
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
                                                        for (int i = 0; i < array.length(); i += 2) {
                                                            JSONObject item = array.getJSONObject(i);
                                                            String dni = item.getString("dni");
                                                            String time = item.getString("period_end");
                                                            long  h= (Integer.valueOf(dni)*Integer.valueOf(Area)*Integer.valueOf(noofpanels)*Integer.valueOf(efficiency))/1000*36;
                                                            Log.d("energy",String.valueOf(h));
                                                            //yValues.add(new Entry(i/2,h));
                                                            //timeaxis.add(time);

                                                        }
                                                        yValues.add(new Entry(0,60));
                                                        yValues.add(new Entry(1,70));
                                                        yValues.add(new Entry(2,60));


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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "Unable to fetch information", Toast.LENGTH_LONG).show();
                            }
                        });
                stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //getting the whole json object from the response
                                    JSONObject obj = new JSONObject(response);

                                    JSONArray array = obj.getJSONArray("forecasts");


                                    for (int i = 0; i < array.length(); i += 2) {
                                        JSONObject item = array.getJSONObject(i);
                                        String dni = item.getString("dni");
                                        String time = item.getString("period_end");

                                        //solar_hour_data.add(new solar_daily_data_class(time, "dni : "+dni));

                                    }
                                   // mAdapter.addAll(solar_hour_data);


                                   // Log.d("length", String.valueOf(solar_hour_data.size()));

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

                break;

            case 3:
                break;
            case 4:
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

}

