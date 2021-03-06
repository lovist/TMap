package hu.uniobuda.nik.p8bvi3;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener {
    LatLng actLatLong;
    MapPoint actMapPoint;

    class MyInfoWindowAdapter implements InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            actLatLong = marker.getPosition();
            actMapPoint = new MapPoint(0,marker.getTitle(), marker.getSnippet(),actLatLong.latitude, actLatLong.longitude);
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }


    final int RQS_GooglePlayServices = 1;
    private GoogleMap map; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> markerPoints;
    ArrayList<MapPoint> dbMapPoints;
    Circle RangeCircle;     //drawing range circle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //tvLocInfo = (TextView)findViewById(R.id.locinfo);

        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        //map.setTrafficEnabled(true);
        map.setOnMapLongClickListener(this);
        map.setInfoWindowAdapter(new MyInfoWindowAdapter());
        map.setOnInfoWindowClickListener(this);
        map.getUiSettings().setAllGesturesEnabled(true);

        //DrawCircle(500);
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        // Setting onclick event listener for the map

        MySQLiteHelper db = new MySQLiteHelper(this);
        Log.d("getAllPoints: ", "Getting ..");
        dbMapPoints = db.getAllPoints();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS) {
            //Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
        actLatLong = null;
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    private void setUpMap() {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_points) {
            Intent i = new Intent(this, PointsActivity.class);
            i.putParcelableArrayListExtra("points", dbMapPoints);
            startActivityForResult(i, 1);
        }

        if (id == R.id.action_add_new_selected_point) {
                if (actLatLong != null) {

                    Intent intent = new Intent(MapsActivity.this, NewPointActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("latKey", actLatLong.latitude);
                    b.putDouble("lonKey", actLatLong.longitude);
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivityForResult(intent,1);
                    actLatLong = null;
                    //finish();
                } else {
                    Toast.makeText(this, getString(R.string.no_selected_point), Toast.LENGTH_SHORT).show();
                }
        }

        if (id == R.id.action_streetview) {
            if (isOnline()) {
                if (actLatLong != null) {

                    Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("latKey", actLatLong.latitude);
                    b.putDouble("lonKey", actLatLong.longitude);
                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                    actLatLong = null;
                    //finish();
                } else {
                    Toast.makeText(this, getString(R.string.no_selected_point), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.action_drawing_driving_route) {
            if(isOnline()) {
                if (actLatLong != null) {
                    DrawingDrivingRoute(actLatLong);
                    Toast.makeText(this, getString(R.string.directions_in_progress), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.no_selected_point), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        }

        if(id==R.id.action_select_range){
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle(getString(R.string.nearby_points));
            alert.setMessage(getString(R.string.search_distance));

            LinearLayout linear=new LinearLayout(this);

            linear.setOrientation(LinearLayout.VERTICAL);
            final TextView text=new TextView(this);
            text.setText("0 km");
            text.setPadding(10, 10, 10, 10);

            final SeekBar seek=new SeekBar(this);
            seek.setMax(25000);

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                    // TODO Auto-generated method stub
                    text.setText((seek.getProgress()/1000)+" km");
                }
            });

            linear.addView(seek);
            linear.addView(text);

            alert.setView(linear);

            AlertDialog.Builder ok = alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Toast.makeText(getApplicationContext(), "OK Pressed",Toast.LENGTH_LONG).show();
                   CalculateNearbyPoints(seek.getProgress());


                }
            });
            alert.setNegativeButton("Mégse",new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                    //Toast.makeText(getApplicationContext(), "Cancel Pressed",Toast.LENGTH_LONG).show();

                }
            });
            alert.show();
        }


        return super.onOptionsItemSelected(item);
    }

    //pontok keresése és megjelenítése a megadott távolságon belül
    public void CalculateNearbyPoints(int range){
        map.clear();
        if (RangeCircle != null) RangeCircle.remove();
        DrawCircle(range);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(map.getMyLocation().getLatitude(),
                map.getMyLocation().getLongitude()), 9.0f));

        int i;
        Location ltg= new Location("");
        Location myloc = map.getMyLocation();
        LatLng mypos = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
        Marker marker;
        for (i = 0; i < dbMapPoints.size(); i++) {
            Log.d("Index:", String.valueOf(i));
            ltg.setLatitude(dbMapPoints.get(i).getPointlatitude());
            ltg.setLongitude(dbMapPoints.get(i).getPointlongitude());
            float dist = myloc.distanceTo(ltg);
            if (dist<=range){
                map.addMarker(new MarkerOptions()
                                .position(new LatLng(
                                        ltg.getLatitude(),
                                        ltg.getLongitude()))
                                .title(dbMapPoints.get(i).getPointname())
                                .snippet(dbMapPoints.get(i).getPointaddress())
                                .draggable(false)
                );

            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {


        Marker newpoint = map.addMarker(new MarkerOptions()
                .position(point)
                .snippet(point.latitude+", "+ point.longitude));
        newpoint.showInfoWindow();
    }


    //útvonal rajzolás
    public void DrawingDrivingRoute(LatLng point) {

        Location myLocation = map.getMyLocation();

        double latitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();
        LatLng myposition = new LatLng(latitude, longitude);
        markerPoints.clear();
        map.clear();


        // Adding new item to the ArrayList
        markerPoints.add(myposition);
        markerPoints.add(point);


        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (markerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        //map.addMarker(options);

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {

        //Toast.makeText(getBaseContext(),"Info Window clicked@" + marker.getId(),Toast.LENGTH_SHORT).show();
        /*
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Delete...");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want delete this?");
        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event
                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                marker.remove();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
        */
    }

    //címből koordináta
    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        EditText et = (EditText) findViewById(R.id.editText1);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this, Locale.ENGLISH);
        List<Address> list = gc.getFromLocationName(location, 20);
        if (list.isEmpty()) {
        } else {
            Address add = list.get(0);
            String locality = add.getLocality();
            Toast.makeText(this, location, Toast.LENGTH_LONG).show();
            double lat = add.getLatitude();
            double lon = add.getLongitude();
            gotoLocation(lat, lon, 21);     //zoom level 2-21
        }
    }


    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void gotoLocation(double lat, double lon, float zoom) {
        LatLng ll = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        map.moveCamera(update);
    }
    //keresési sugár rajzolása
    public void DrawCircle(int meter) {
        LatLng point = new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude());
        CircleOptions circleOptions = new CircleOptions()
                .center(point)   //set center
                .radius(meter)   //set radius in meters
                .fillColor(Color.TRANSPARENT)  //default
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        RangeCircle = map.addCircle(circleOptions);

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            //tvDistanceDuration.setText("Distance:"+distance + ", Duration:"+duration);
            //Toast.makeText(getApplicationContext(), "Distance:" + distance + ", Duration:" + duration, Toast.LENGTH_SHORT).show();
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);

            Marker destination = map.addMarker(new MarkerOptions()
                    .position(actLatLong)
                    .title(actMapPoint.getPointname())
                    .snippet(getString(R.string.pointaddress)+actMapPoint.getPointaddress()+"\r\n"+getString(R.string.travel_time)+ duration+"\r\n"+getString(R.string.travel_distance)+distance));
            destination.showInfoWindow();


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 3) {
            MySQLiteHelper db = new MySQLiteHelper(this);
            dbMapPoints = db.getAllPoints();
            Toast.makeText(getApplicationContext(), getString(R.string.new_point_added_message), Toast.LENGTH_SHORT).show();
        }
    }

    //Internet is available?
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}





