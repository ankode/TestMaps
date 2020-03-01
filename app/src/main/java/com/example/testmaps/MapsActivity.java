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

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(origin).title("Home").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.addMarker(new MarkerOptions().position(destination).title("Kormangla").icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
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
        getDirections(mMap, origin, destination, api_key, Float.valueOf("20.0"), R.color.colorPrimary);
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
        String url = "https://maps.googleapis.com/maps/api/directions/json?"+ originString + "&" + destinationString+ "&alternatives=true&units=metric&key=" + api_key;
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

    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        //        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", sourceLatitude, sourceLongitude, "Home Sweet Home", destinationLatitude, destinationLongitude, "Where the party is at");
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        intent.setPackage("com.google.android.apps.maps");
//        startActivity(intent);
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create(); //Read Update
        alertDialog.setTitle("Details");
        alertDialog.setMessage("Let's roll");

        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Start Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                String originString = "origin=" + origin.latitude + "," + origin.longitude;
//                String destinationString = "destination=" + destination.latitude + "," + destination.longitude;
                // here you can add functions
//                Uri gmmIntentUri = Uri.parse("google.navigation:q="+originString+","+destinationString + "&mode=d");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", origin.latitude, origin.longitude, "Home Sweet Home", destination.latitude, destination.longitude, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        alertDialog.show();

    }
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
            //Toast.makeText(showPreviousOrder.this, s, Toast.LENGTH_LONG).show();
            Log.d("OutputData",s);
//            s= '{"geocoded_waypoints":[{"geocoder_status":"OK","place_id":"ChIJl2PH6EHI5zsRHn_HJOzBRXs","types":["route"]},{"geocoder_status":"OK","place_id":"ChIJiaptGUHJ5zsRZ3rYB77PlxU","types":["doctor","establishment","health","point_of_interest"]}],"routes":[{"bounds":{"northeast":{"lat":19.1032383,"lng":72.8766907},"southwest":{"lat":19.0451225,"lng":72.8258144}},"copyrights":"Mapdata\u00a92020","legs":[{"distance":{"text":"12.8km","value":12763},"duration":{"text":"23mins","value":1396},"end_address":"100AValentineRest,HillRd,nearMehboobStudio,StSebastianColony,Ranwar,BandraWest,Mumbai,Maharashtra400050,India","end_location":{"lat":19.0522206,"lng":72.8258646},"start_address":"IAProjectRd,Navpada,GreaterIndraNagar,AndheriEast,Mumbai,Maharashtra400059,India","start_location":{"lat":19.1032383,"lng":72.8742737},"steps":[{"distance":{"text":"0.3km","value":253},"duration":{"text":"1min","value":72},"end_location":{"lat":19.1031298,"lng":72.8766825},"html_instructions":"Head\u003cb\u003eeast\u003c/b\u003eon\u003cb\u003eAirportRd\u003c/b\u003e/\u003cwbr/\u003e\u003cb\u003eIAProjectRd\u003c/b\u003etoward\u003cb\u003eAirportRd\u003c/b\u003e\u003cdivstyle=\"font-size:0.9em\"\u003eContinuetofollowIAProjectRd\u003c/div\u003e\u003cdivstyle=\"font-size:0.9em\"\u003ePassbyAmbassadorHotel(ontheleft)\u003c/div\u003e","polyline":{"points":"gbrsBegx{L?e@@YBqBBkA@kADyB?KBQ"},"start_location":{"lat":19.1032383,"lng":72.8742737},"travel_mode":"DRIVING"},{"distance":{"text":"69m","value":69},"duration":{"text":"1min","value":15},"end_location":{"lat":19.1030468,"lng":72.876119},"html_instructions":"Sharp\u003cb\u003eright\u003c/b\u003eatAirportHealthOrganization,Mumbaitostayon\u003cb\u003eIAProjectRd\u003c/b\u003e","maneuver":"turn-sharp-right","polyline":{"points":"qarsBgvx{LNA?pB"},"start_location":{"lat":19.1031298,"lng":72.8766825},"travel_mode":"DRIVING"},{"distance":{"text":"0.2km","value":196},"duration":{"text":"1min","value":36},"end_location":{"lat":19.1012848,"lng":72.8761834},"html_instructions":"Turn\u003cb\u003eleft\u003c/b\u003e\u003cdivstyle=\"font-size:0.9em\"\u003ePassbySaharPoliceStation(ontheleft)\u003c/div\u003e","maneuver":"turn-left","polyline":{"points":"aarsBwrx{Ld@Cp@?tAAd@A|BCN?"},"start_location":{"lat":19.1030468,"lng":72.876119},"travel_mode":"DRIVING"},{"distance":{"text":"0.5km","value":465},"duration":{"text":"2mins","value":92},"end_location":{"lat":19.1018271,"lng":72.8725451},"html_instructions":"Turn\u003cb\u003eright\u003c/b\u003eatthe1stcrossstreet\u003cdivstyle=\"font-size:0.9em\"\u003ePassbyBankofBarodaATM(ontheleft)\u003c/div\u003e","maneuver":"turn-right","polyline":{"points":"_vqsBcsx{LNTDHDHBH@J@t@?fA?T?JCHCLAJAL@hAApA@H@H@HBJ@J@H?JAJATA`@AR?N@J?LFp@?D?DADABCDEBCBE@}B@"},"start_location":{"lat":19.1012848,"lng":72.8761834},"travel_mode":"DRIVING"},{"distance":{"text":"0.4km","value":426},"duration":{"text":"1min","value":50},"end_location":{"lat":19.1029473,"lng":72.8693512},"html_instructions":"Keep\u003cb\u003eleft\u003c/b\u003e","maneuver":"keep-left","polyline":{"points":"myqsBm|w{LUF[?W@[@SBMBSFEBUJC@EDCDABEPCT?LCz@At@Af@A`A?X?h@Dz@@t@@X@x@AFCDIV"},"start_location":{"lat":19.1018271,"lng":72.8725451},"travel_mode":"DRIVING"},{"distance":{"text":"1.7km","value":1675},"duration":{"text":"2mins","value":140},"end_location":{"lat":19.1001665,"lng":72.8545694},"html_instructions":"Mergeonto\u003cb\u003eSaharAirportRd\u003c/b\u003e\u003cdivstyle=\"font-size:0.9em\"\u003ePassbyNaushadRestaurant(ontheright)\u003c/div\u003e","maneuver":"merge","polyline":{"points":"m`rsBmhw{LHpDDhB?zA@x@DnB?Z@n@DpBDhC@~ABdADp@HhAJ`AHt@Lv@TpA`@dCLbALx@Hv@HrADhA@dA?R?|A?VAr@?BEfAAXAXM|BIzAAp@@HB`@Hj@FRDRLXFNZf@`@d@d@^j@Xf@TzBr@@@VT"},"start_location":{"lat":19.1029473,"lng":72.8693512},"travel_mode":"DRIVING"},{"distance":{"text":"0.1km","value":127},"duration":{"text":"1min","value":21},"end_location":{"lat":19.0991497,"lng":72.8540131},"html_instructions":"Mergeonto\u003cb\u003eWesternExpressHwy\u003c/b\u003e","maneuver":"merge","polyline":{"points":"aoqsBalt{Lb@TbA`@ZLXLl@Z"},"start_location":{"lat":19.1001665,"lng":72.8545694},"travel_mode":"DRIVING"},{"distance":{"text":"4.6km","value":4614},"duration":{"text":"6mins","value":372},"end_location":{"lat":19.0638099,"lng":72.8466821},"html_instructions":"Keep\u003cb\u003eright\u003c/b\u003etostayon\u003cb\u003eWesternExpressHwy\u003c/b\u003e","maneuver":"keep-right","polyline":{"points":"uhqsBqht{L~@\\|HrB|A^B@@?ZHB?NDNDj@NjAV@?LB@@D@HBz@VHD`A`@vBdAx@j@`@^\\Z`@`@h@r@\\h@\\l@Zn@d@bATv@Px@PbAHn@Fh@BVP|AX~Cf@jEBRLx@P|@Pl@^dAXd@b@f@\\^n@f@f@\\l@^^N@?^NPDZFh@Hl@Hh@@p@?h@Cj@Ej@IVERGPGZM^ORMXOTSb@]vCgCb@_@j@e@BAVU`@U`@Ul@[ZMXG\\INEh@I^GD@F?HATAlAOjQwBx@Kb@Eb@AL?jIJ|G@hIHbC@p@@r@B`A?fOBxB?jRH\\@pMFP@^?jEB"},"start_location":{"lat":19.0991497,"lng":72.8540131},"travel_mode":"DRIVING"},{"distance":{"text":"3.3km","value":3315},"duration":{"text":"4mins","value":241},"end_location":{"lat":19.0453002,"lng":72.82856760000001},"html_instructions":"Keep\u003cb\u003eright\u003c/b\u003etostayon\u003cb\u003eWesternExpressHwy\u003c/b\u003e","maneuver":"keep-right","polyline":{"points":"ykjsBwzr{L~MJ~@?~@@rJ@lF?lF@bEC~@Bj@Dx@Hf@H`@Ht@XrAf@ZP^T^TXXRPRVVZb@v@HN@@?@HLZx@\\tA`ArDHXrB`Hx@xBtApDT|@BFnAvEL`@Pr@BH@L@FBJ@J@`@@L@rAAdBEdA[nJE`A?z@Br@Bd@@BPdAL^Th@R\\^h@TThCbCvDnDdBhBvCnCp@n@DDBBz@x@vApA"},"start_location":{"lat":19.0638099,"lng":72.8466821},"travel_mode":"DRIVING"},{"distance":{"text":"0.4km","value":370},"duration":{"text":"1min","value":58},"end_location":{"lat":19.0470149,"lng":72.8280261},"html_instructions":"Taketheexittoward\u003cb\u003eKCMarg\u003c/b\u003e","maneuver":"ramp-left","polyline":{"points":"cxfsBqio{LNADA@A@A@C@A@A@A?A@C?CGq@?CC[AMCKIKGIIIKIGCICAAMAKAI@I@I@OFIDGFEDCBCBEFIJSVCBABMRa@d@WZo@t@Y^EHAF?N"},"start_location":{"lat":19.0453002,"lng":72.82856760000001},"travel_mode":"DRIVING"},{"distance":{"text":"0.6km","value":594},"duration":{"text":"1min","value":89},"end_location":{"lat":19.0508244,"lng":72.82976669999999},"html_instructions":"Continueonto\u003cb\u003eKCMarg\u003c/b\u003e\u003cdivstyle=\"font-size:0.9em\"\u003ePassbyCasualty(ontheleftin600&nbsp;m)\u003c/div\u003e","polyline":{"points":"ybgsBefo{LuCxDQBM?KAMAMCUEeAYi@SSIk@W_@Sw@a@[[W]U]U[Q_@iA}BQ_@c@w@K["},"start_location":{"lat":19.0470149,"lng":72.8280261},"travel_mode":"DRIVING"},{"distance":{"text":"0.1km","value":100},"duration":{"text":"1min","value":26},"end_location":{"lat":19.0516018,"lng":72.8292945},"html_instructions":"Turn\u003cb\u003eleft\u003c/b\u003etoward\u003cb\u003eGeneralArunkumarVaidyaMarg\u003c/b\u003e","maneuver":"turn-left","polyline":{"points":"szgsBaqo{LcAh@wAt@"},"start_location":{"lat":19.0508244,"lng":72.82976669999999},"travel_mode":"DRIVING"},{"distance":{"text":"0.1km","value":105},"duration":{"text":"1min","value":25},"end_location":{"lat":19.0513027,"lng":72.82834769999999},"html_instructions":"Turn\u003cb\u003eleft\u003c/b\u003eafterLilavatiHospitalAndResearchCentre(ontheleft)","maneuver":"turn-left","polyline":{"points":"o_hsBano{LJ`@Jb@Rx@H\\D\\"},"start_location":{"lat":19.0516018,"lng":72.8292945},"travel_mode":"DRIVING"},{"distance":{"text":"57m","value":57},"duration":{"text":"1min","value":15},"end_location":{"lat":19.0512072,"lng":72.8278114},"html_instructions":"Continueonto\u003cb\u003eChapelRd\u003c/b\u003e","polyline":{"points":"s}gsBeho{LPvA?H?H"},"start_location":{"lat":19.0513027,"lng":72.82834769999999},"travel_mode":"DRIVING"},{"distance":{"text":"0.2km","value":196},"duration":{"text":"1min","value":58},"end_location":{"lat":19.0528318,"lng":72.8273097},"html_instructions":"Turn\u003cb\u003eright\u003c/b\u003eonto\u003cb\u003eStVeronicaRd\u003c/b\u003e","maneuver":"turn-right","polyline":{"points":"a}gsBydo{Lg@@M?O?Q?O@Y@]BWFWFQDMDQDWH]TCBCDAFAD"},"start_location":{"lat":19.0512072,"lng":72.8278114},"travel_mode":"DRIVING"},{"distance":{"text":"0.2km","value":201},"duration":{"text":"1min","value":86},"end_location":{"lat":19.0522206,"lng":72.8258646},"html_instructions":"Continuestraightonto\u003cb\u003eStRoqueRd\u003c/b\u003e\u003cdivstyle=\"font-size:0.9em\"\u003eDestinationwillbeontheright\u003c/div\u003e","maneuver":"straight","polyline":{"points":"eghsBuao{L?@A@?@?Bb@pEBr@?D@DBH@@B@B@@?F?TCb@E"},"start_location":{"lat":19.0528318,"lng":72.8273097},"travel_mode":"DRIVING"}],"traffic_speed_entry":[],"via_waypoint":[]}],"overview_polyline":{"points":"gbrsBegx{LH}FFeEB]NA?pBd@CfCAbDEN?NTJRDT@|B?`@GVCX?zCHh@BTAVEzAHpAAJEHIFcCBUF[?s@Ba@FYJc@XGTCb@EpBChB?bAFpBBrAELIVHpDDdEHtFP`LNzBTvBrArIVpBN|C@nEIxCOvCKlCDj@P~@Rl@b@v@`@d@d@^rAn@|Bt@VTb@T~An@fAh@~@\\|HrB`B`@p@NhCl@dBh@xDfBzAjA~@|@fA|Ax@|Ad@bATv@b@|BPxAvA`NPlAb@jB^dAXd@`AfAvAdAl@^^N`@Nl@LvARzA@tAIbAOd@Oz@]l@]x@q@jFoEx@k@nAq@t@Ul@OhAQL@xTkC|AQp@AjIJ|G@lMJdBDhQBdVHnNH|UP`VBpLAjBH`BRvAb@nBx@~@j@l@j@j@r@n@jAd@fA~AhG|BzHnCjHvB~HXrAFx@B`BGjDa@pLBnBDh@PdAL^h@fAt@~@`IrHdBhBvCnCv@t@~@|@vApANAFCFIBGKuAEYQUUSQGOCU?SBYLUTc@j@u@`AgApA_@h@AVuCxDQBYA[E{A_@}@]kAk@w@a@[[m@{@g@{@kCqF{C~AVdA\\vAVtB?Ru@@kABu@Ji@L_@JWH]TGHCLAHb@pEBr@@JDJFBbAI"},"summary":"WesternExpressHwy","warnings":[],"waypoint_order":[]}],"status":"OK"}';
//            Log.d("OutputDataNew",s);

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

//                    totalDistance += Integer.parseInt(distance.getString("value"));
//                    totalTravelTime += Integer.parseInt(duration.getString("value"));

                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject polyline = steps.getJSONObject(j).getJSONObject("polyline");
                        List<LatLng> markers = decodePoly(polyline.getString("points"));
                        Log.d("polyline",polyline.toString() );
                        mMap.addPolyline(new PolylineOptions().clickable(true).addAll(markers).width(Float.valueOf("20.0")).color(Color.RED));
                    }

                }
//            legs = parentMain.getJSONArray("routes").getJSONObject(2).getJSONArray("legs");
//
//                for (int i = 0; i < legs.length(); i++) {
//                    JSONArray steps = legs.getJSONObject(i).getJSONArray("steps");
//                    JSONObject distance = legs.getJSONObject(i).getJSONObject("distance");
//                    JSONObject duration = legs.getJSONObject(i).getJSONObject("duration");
//
//                    totalDistance += Integer.parseInt(distance.getString("value"));
//                    totalTravelTime += Integer.parseInt(duration.getString("value"));
//
//                    for (int j = 0; j < steps.length(); j++) {
//                        JSONObject polyline = steps.getJSONObject(j).getJSONObject("polyline");
//                        List<LatLng> markers = decodePoly(polyline.getString("points"));
//                        Log.d("polyline",polyline.toString() );
//                        mMap.addPolyline(new PolylineOptions().clickable(true).addAll(markers).width(Float.valueOf("30.0")).color(Color.YELLOW));
//                    }
//
//                }

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
