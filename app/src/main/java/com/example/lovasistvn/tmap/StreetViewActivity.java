package com.example.lovasistvn.tmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;


public class StreetViewActivity extends Activity {

    StreetViewPanorama m_StreetView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        createStreetView();


        Bundle b = getIntent().getExtras();
        double lat = b.getDouble("latKey");
        double lon = b.getDouble("lonKey");

        LatLng latlong = new LatLng(lat,lon);

        m_StreetView.setPosition(latlong);
        //Toast.makeText(this,String.valueOf(lat),Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_street_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createStreetView() {
        m_StreetView = ((StreetViewPanoramaFragment)getFragmentManager()
                .findFragmentById(R.id.StreetViewMap)).getStreetViewPanorama();

    }
}
