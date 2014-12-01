package com.example.lovasistvn.tmap;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapLongClickListener, OnInfoWindowClickListener {
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
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    LatLng actLatLong;
    final int RQS_GooglePlayServices = 1;
    TextView tvLocInfo;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        //tvLocInfo = (TextView)findViewById(R.id.locinfo);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setOnMapLongClickListener(this);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        //DrawCircle(500);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), "isGooglePlayServicesAvailable SUCCESS", Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
        actLatLong = null;
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
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
            Intent masikActivity = new Intent(this, PointsActivity.class);
            startActivity(masikActivity);
        }

        if (id==R.id.action_check_updates){
            DrawCircle(50000);
        }

        if (id == R.id.action_streetview) {
            if (actLatLong != null) {

                Intent masikActivity = new Intent(this, StreetViewActivity.class);

                Intent intent = new Intent(MapsActivity.this, StreetViewActivity.class);
                Bundle b = new Bundle();
                b.putDouble("latKey", actLatLong.latitude);
                b.putDouble("lonKey", actLatLong.longitude);
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(this, "Nincs pont kiválasztva!!!", Toast.LENGTH_SHORT).show();
            }

            //startActivity(masikActivity);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //tvLocInfo.setText("New marker added@" + point.toString());
        Marker newMarker = mMap.addMarker(new MarkerOptions().position(point).snippet(point.toString()));
        newMarker.setTitle(newMarker.getId());

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

    public void geoLocate(View v) throws IOException {
        hideSoftKeyboard(v);
        EditText et = (EditText) findViewById(R.id.editText1);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 10);
        Address add = list.get(0);
        String locality = add.getLocality();
        Toast.makeText(this, location, Toast.LENGTH_LONG).show();
        double lat = add.getLatitude();
        double lon = add.getLongitude();
        gotoLocation(lat, lon, 21);     //zoom level 2-21

    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void gotoLocation(double lat, double lon, float zoom) {
        LatLng ll = new LatLng(lat, lon);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    public void DrawCircle(int meter) {
        LatLng point = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        CircleOptions circleOptions = new CircleOptions()
                .center(point)   //set center
                .radius(meter)   //set radius in meters
                .fillColor(Color.TRANSPARENT)  //default
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        Circle myCircle = mMap.addCircle(circleOptions);
    }
}





