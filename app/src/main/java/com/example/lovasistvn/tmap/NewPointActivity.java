package com.example.lovasistvn.tmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class NewPointActivity extends Activity {

    Button sButton;
    EditText nameText;
    EditText AddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_point);
        nameText = (EditText) findViewById(R.id.new_point_name);
        AddressText = (EditText) findViewById(R.id.new_point_address);

        findViewById(R.id.save_point_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng ltg = AddressToGPS(AddressText.getText().toString());
                MapPoint m = new MapPoint(nameText.getText().toString(), AddressText.getText().toString(),ltg.latitude,ltg.longitude);
                Intent data = new Intent();
                data.putExtra("key", m);
                setResult(Activity.RESULT_OK, data);

                MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                db.addPoint(m);
                finish();
            }        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_point, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public LatLng AddressToGPS(String address) {
        Geocoder gc = new Geocoder(this, Locale.ENGLISH);
        List<Address> addresses;
        LatLng ltg= new LatLng(0,0);
        try {
            addresses = gc.getFromLocationName(address, 5);
            if (addresses.size() > 0) {
                ltg = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        };
        return ltg;
    }
}


