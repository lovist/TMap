package com.example.lovasistvn.tmap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Element;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class PointsActivity extends Activity implements AbsListView.MultiChoiceModeListener {

    private String[] pointname =null;
    private String[] pointaddress =null;
    private String[] pointlatitude =null;
    private String[] pointlongitude =null;
    ListView listView=null;
    Context contex=null;
    MyListAdapter adapter=null;
    //private List<MapPoint> DbMapPoints=new ArrayList<MapPoint>();
    List<MapPoint> pointsList;

    EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        contex=this;
        listView = (ListView) findViewById(R.id.listview);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        //DatabaseHandler db = new DatabaseHandler(this);
        //List<MapPoint> points = db.getAllPoints();
        Intent i = this.getIntent();
        pointsList=null;
        pointsList = i.getParcelableArrayListExtra("points");

        //List<MapPoint> points = getIntent().getParcelableArrayListExtra("pointslist");

        adapter=new MyListAdapter(contex, pointsList);
        listView.setAdapter(adapter);
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
    }
    @Override
    public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
        SparseBooleanArray selected;
        MapPoint selecteditem;
        switch (arg1.getItemId()) {
            case R.id.delete:
                selected = adapter.getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        selecteditem = adapter.getItem(selected.keyAt(i));
                        adapter.remove(selecteditem);

                        MySQLiteHelper db = new MySQLiteHelper(this);
                        db.deletePoint(selecteditem);

                    }
                }
                // Close CAB
                setResult(3);
                arg0.finish();
                return true;
            case R.id.action_edit_point:
                selected = adapter.getSelectedIds();
                if(selected.size()==1){
                    Intent i = new Intent(this, NewPointActivity.class);
                    selecteditem = adapter.getItem(selected.keyAt(0));
                    i.putExtra("selecteditem", selecteditem);
                    startActivityForResult(i, 4);
                }else{
                    Toast.makeText(getApplicationContext(), "Nem egy pont van kijelölve!", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }
    @Override
    public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
        arg0.getMenuInflater().inflate(R.menu.menu_points, arg1);
        return true;

    }
    @Override
    public void onDestroyActionMode(ActionMode arg0) {
        adapter.removeSelection();
    }
    @Override
    public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
        return false;
    }
    @Override
    public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2, boolean arg3) {

        final int checkedCount = listView.getCheckedItemCount();
        arg0.setTitle(checkedCount + " Selected");
        adapter.toggleSelection(arg1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_new_point) {
            Intent i = new Intent(this, NewPointActivity.class);
            //startActivity(i);
            startActivityForResult(i, 2);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 2 && resultCode == Activity.RESULT_OK){
            MapPoint m = (MapPoint) data.getParcelableExtra("key");
            pointsList.add(m);
            adapter=new MyListAdapter(contex, pointsList);
            listView.setAdapter(adapter);
            listView.setMultiChoiceModeListener(this);
            listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
            Toast.makeText(getApplicationContext(), "Új pont hozzáadva..."+m.getPointlatitude()+" "+m.getPointlongitude(), Toast.LENGTH_SHORT).show();

            setResult(3);
        }

        if(requestCode == 4 && resultCode == 4){
            MySQLiteHelper db = new MySQLiteHelper(this);
            Log.d("getAllPoints: ", "Getting ..");
            pointsList = db.getAllPoints();

            adapter=new MyListAdapter(contex, pointsList);
            listView.setAdapter(adapter);
            listView.setMultiChoiceModeListener(this);
            listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
            Toast.makeText(getApplicationContext(), "Pont módosítva...", Toast.LENGTH_SHORT).show();
            setResult(3);
        }
    }



}
