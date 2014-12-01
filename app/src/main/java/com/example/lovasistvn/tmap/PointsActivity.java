package com.example.lovasistvn.tmap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    private List<MapPoint> list=new ArrayList<MapPoint>();

    EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        contex=this;
        listView = (ListView) findViewById(R.id.listview);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        pointname = new String[] { "Sunil Gupta", "Abhishek Tripathi","Sandeep Pal", "Amit Verma" };
        pointaddress = new String[] { "sunil android", "Abhi cool","Sandy duffer", "Budhiya jokar"};
        pointlatitude = new String[] { "Sunil Gupta", "Abhishek Tripathi","Sandeep Pal", "Amit Verma" };
        pointlongitude = new String[] { "sunil android", "Abhi cool","Sandy duffer", "Budhiya jokar"};

        for(int index=0; index< pointname.length; index++){
            MapPoint details=new MapPoint(pointname[index], pointaddress[index],pointlatitude[index], pointlongitude[index]);
            list.add(details);
        }

        adapter=new MyListAdapter(contex, list);
        listView.setAdapter(adapter);
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
    }
    @Override
    public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
        switch (arg1.getItemId()) {
            case R.id.delete:
                SparseBooleanArray selected = adapter.getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        MapPoint selecteditem = adapter.getItem(selected.keyAt(i));
                        adapter.remove(selecteditem);
                    }
                }
                // Close CAB
                arg0.finish();
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



}
