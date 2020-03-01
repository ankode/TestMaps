package com.example.testmaps;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private LatLng origin = new LatLng(19.103265, 72.874282);
    private LatLng destination = new LatLng(19.052173, 72.825570);
    private String api_key = String.valueOf(R.string.google_maps_key);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap = googleMap;

        /*getDirections() arguments :
                1. GoogleMap
                2. Origin LatLng
                3. Destination LatLng
                4. Google Maps API Key String
                5. Polyline width Float
                6. Polyline color Color
         */
        getDirections(mMap, origin, destination, api_key, Float.valueOf("4.0"), R.color.colorPrimary);
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
    }
    private void getDirections(GoogleMap mMap, LatLng origin, LatLng destination, String api_key, Float inWidth, int inColor)
    {
        //Move camera to origin
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15));


        //Forming an URL string which will return JSON as a result.
        String originString = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationString = "destination=" + destination.latitude + "," + destination.longitude;

        //IF THIS GENERATES ERROR, HARD CODE API KEY INTO URL.
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+ originString + "&" + destinationString + "&key=" + api_key;
        Log.d("StringUrl",url);



        //Run the URL formed in above step and wait for result.
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String downloadUrl(String url) throws IOException
    {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try
        {
            URL actualURL = new URL(url);
            urlConnection = (HttpURLConnection)actualURL.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();
        }
        catch (Exception e)
        {
            Log.d("EXCEPTION DOWNLADING", e.toString());
        }
        finally {
            inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
//        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Home Sweet Home", destinationLatitude, destinationLongitude, "Where the party is at");
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        intent.setPackage("com.google.android.apps.maps");
//        startActivity(intent);
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+origin+","+destination + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = "";

            try {
                data = downloadUrl(strings[0]);
            } catch (Exception e) {
                Log.d("ASYNC TASK", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(showPreviousOrder.this, s, Toast.LENGTH_LONG).show();
            Log.d("OutputData",s);

            int totalDistance = 0;
            int totalTravelTime = 0;

            try {
                JSONObject parentMain = new JSONObject(s);
                JSONArray legs = parentMain.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

                for (int i = 0; i < legs.length(); i++) {
                    JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
                    JSONObject distance = legs.getJSONObject(i).getJSONObject("distance");
                    JSONObject duration = legs.getJSONObject(i).getJSONObject("duration");

                    totalDistance += Integer.parseInt(distance.getString("value"));
                    totalTravelTime += Integer.parseInt(duration.getString("value"));

                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject polyline = steps.getJSONObject(j).getJSONObject("polyline");
                        List<LatLng> markers = PolyUtil.decode(polyline.getString("points"));

                        mMap.addPolyline(new PolylineOptions().addAll(markers).width(Float.valueOf("4.0")).color(Color.GREEN));
                    }
                }

            } catch (JSONException e) {
                Toast.makeText(MapsActivity.this, "WELL WE MESSED UP!", Toast.LENGTH_LONG).show();
            }
            toastData(totalDistance, totalTravelTime);
        }

        //Simply displays a toast message containing total distance and total time required.
        public void toastData(int totalDistance, int totalTravelTime) {
            int km = 0, m = 0;
            String displayDistance = "";

            if (totalDistance < 1000) {
                displayDistance = "0." + String.valueOf(totalDistance) + " km";
            } else {
                while (totalDistance >= 1000) {
                    km++;
                    totalDistance -= 1000;
                }
                m = totalDistance;
                displayDistance = String.valueOf(km) + "." + String.valueOf(m) + " km";
            }

            int min = 0, sec = 0;
            String displayTravelTime = "";
            if (totalDistance < 60)
                displayTravelTime = "1 minute";
            else {
                while (totalTravelTime >= 60) {
                    min++;
                    totalTravelTime -= 60;
                }
                sec = totalTravelTime;
                displayTravelTime = String.valueOf(min) + ":" + String.valueOf(sec) + " minutes";
            }

            Toast.makeText(MapsActivity.this, "DISTANCE : " + displayDistance + "\nTIME REQUIRED : " + displayTravelTime, Toast.LENGTH_LONG).show();
        }
    }
}
