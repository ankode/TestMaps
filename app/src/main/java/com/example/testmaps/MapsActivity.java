package com.example.testmaps;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener,
        GoogleMap.OnPolygonClickListener {

    private LatLng origin = new LatLng(12.991761, 77.7049027);
    private LatLng destination = new LatLng(12.9327701, 77.6142604);
    private String api_key;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        api_key=getResources().getString(R.string.google_maps_key);
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
        mMap.addMarker(new MarkerOptions().position(origin).title("Home").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.addMarker(new MarkerOptions().position(destination).title("Kormangla").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        /*getDirections() arguments :
                1. GoogleMap
                2. Origin LatLng
                3. Destination LatLng
                4. Google Maps API Key String
                5. Polyline width Float
                6. Polyline color Color
         */
        getDirections(mMap, origin, destination, api_key, Float.valueOf("20.0"), R.color.colorPrimary);
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
    }
    private void getDirections(GoogleMap mMap, LatLng origin, LatLng destination, String api_key, Float inWidth, int inColor)
    {
        //Set desired zoom level to 15
        int zoomLevel = 15;
        //Move camera to origin
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, zoomLevel));


        //Forming an URL string which will return JSON as a result.
        String originString = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationString = "destination=" + destination.latitude + "," + destination.longitude;

        //IF THIS GENERATES ERROR, HARD CODE API KEY INTO URL.
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+ originString + "&" + destinationString+ "&alternatives=true&units=metric&key=" + api_key;
        Log.d("StringUrl",url);



        //Run the URL formed in above step and wait for result.
        //         DownloadTask downloadTask = new DownloadTask();
        //    downloadTask.execute(url);
            new DownloadTask().execute(url); // HACK ALERT: if code crashes, uncomment above, and comment this.
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

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create(); //Read Update
        alertDialog.setTitle("Details");
        alertDialog.setMessage("Starting Navigation");

        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Start Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", origin.latitude, origin.longitude, "Home Sweet Home", destination.latitude, destination.longitude, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        alertDialog.show();

    }
     // Util function to decode polystring from MAP api to a LatLang object to draw line
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
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
                        List<LatLng> markers = decodePoly(polyline.getString("points"));
                        Log.d("polyline",polyline.toString() );
                        mMap.addPolyline(new PolylineOptions().clickable(true).addAll(markers).width(Float.valueOf("20.0")).color(Color.GREEN));
                    }

                }
            legs = parentMain.getJSONArray("routes").getJSONObject(1).getJSONArray("legs");

                for (int i = 0; i < legs.length(); i++) {
                    JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
                    JSONObject distance = legs.getJSONObject(i).getJSONObject("distance");
                    JSONObject duration = legs.getJSONObject(i).getJSONObject("duration");

                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject polyline = steps.getJSONObject(j).getJSONObject("polyline");
                        List<LatLng> markers = decodePoly(polyline.getString("points"));
                        Log.d("polyline",polyline.toString() );
                        mMap.addPolyline(new PolylineOptions().clickable(true).addAll(markers).width(Float.valueOf("20.0")).color(Color.RED));
                    }

                }

            } catch (JSONException e) {
                Toast.makeText(MapsActivity.this, "Couldn't draw polyline on map", Toast.LENGTH_LONG).show();
            }
            toastData(totalDistance, totalTravelTime);
        }

        //Simply displays a toast message containing total distance and total time required.
        public void toastData(int totalDistance, int totalTravelTime) {
            int km = 0, m = 0;
            String displayTotalDistance = "";
            int ONE_KM = 1000; 
            /*
             * Get string to display from total distance.
             */
            if (totalDistance < ONE_KM) {
                displayTotalDistance = "0." + String.valueOf(displayTotalDistance) + " km";
            } else {
                while (totalDistance >= ONE_KM) {
                    km++;
                    totalDistance -= ONE_KM;
                }
                m = totalDistance;
                displayTotalDistance = String.valueOf(km) + "." + String.valueOf(m) + " km";
            }

            int min = 0, sec = 0;
            String displayTravelTime = "";
            int  ONE_MIN = 60;
            
            /*
             * Get string to display from total time.
             */
            if (totalTravelTime < ONE_MIN)
                // Print travel time in seconds if total travel time is less than one minute
                displayTravelTime = String.valueOf(totalTravelTime) + " seconds";
            else {
                while (totalTravelTime >= ONE_MIN) {
                    min++;
                    totalTravelTime -= ONE_MIN;
                }
                sec = totalTravelTime;
                displayTravelTime = String.valueOf(min) + ":" + String.valueOf(sec) + " minutes";
            }

            Toast.makeText(MapsActivity.this, "DISTANCE : " + displayTotalDistance + "\nTRAVEL TIME : " + displayTravelTime, Toast.LENGTH_LONG).show();
        }
    }
}
