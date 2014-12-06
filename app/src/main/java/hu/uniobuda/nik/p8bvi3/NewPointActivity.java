package hu.uniobuda.nik.p8bvi3;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        Intent i = this.getIntent();
        final MapPoint m = (MapPoint) i.getParcelableExtra("selecteditem");
        if(m!=null){
            nameText.setText(m.getPointname());
            AddressText.setText(m.getPointaddress());

            findViewById(R.id.save_point_button).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    LatLng ltg = AddressToGPS(AddressText.getText().toString());
                    MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                    m.setPointname(nameText.getText().toString());
                    m.setPointaddress(AddressText.getText().toString());
                    db.updatePoint(m);
                    Intent data = new Intent();
                    data.putExtra("editedpoint", m);
                    setResult(4);
                    finish();
                }        });


        }else {
            Bundle b = getIntent().getExtras();
            if(b!=null){
                final double lat = b.getDouble("latKey");
                final double lon = b.getDouble("lonKey");
                final LatLng latlong = new LatLng(lat,lon);
                AddressText.setText(GPStoAddress(latlong));


                findViewById(R.id.save_point_button).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                        int id = db.GetMaxID();
                        MapPoint m = new MapPoint(id, nameText.getText().toString(), GPStoAddress(latlong), lat, lon);
                        Intent data = new Intent();
                        data.putExtra("key", m);
                        setResult(3);
                        db.addPoint(m);
                        finish();
                    }
                });

            }else {


                findViewById(R.id.save_point_button).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        LatLng ltg = AddressToGPS(AddressText.getText().toString());
                        MySQLiteHelper db = new MySQLiteHelper(getApplicationContext());
                        int id = db.GetMaxID();
                        MapPoint m = new MapPoint(id, nameText.getText().toString(), AddressText.getText().toString(), ltg.latitude, ltg.longitude);
                        Intent data = new Intent();
                        data.putExtra("key", m);
                        setResult(Activity.RESULT_OK, data);
                        db.addPoint(m);
                        finish();
                    }
                });
            }
        }
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

    public String GPStoAddress(LatLng ltg)
    {
        String strCompleteAddress="";
            Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(ltg.latitude, ltg.longitude, 1);

                if (addresses.size() > 0)
                {
                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                        strCompleteAddress+= addresses.get(0).getAddressLine(i) + "\n";
                }
            }
            catch (IOException e) {
                Log.i("MyLocTAG => ", "this is the exception part");
                e.printStackTrace();
            }
        return strCompleteAddress;
    }
}


