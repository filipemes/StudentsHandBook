package pt.simov.studentshandbook;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class AnomaliesActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anomalies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initNaviationView(-1);
        loadFragment(1);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.optionmenu_anomalies, menu);
         return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.anomaliesOptionMenuList:
                loadFragment(1);
                break;
            case R.id.anomaliesOptionMenuReport:
                loadFragment(2);
                break;
            case R.id.anomaliesOptionMenuListByUser:
                loadFragment(3);
                break;
        }
        return true;
    }

    public void loadFragment(int button) {
        switch (button) {
            case 1:
                selectFragment(new ListAnomaliesFragment());
                break;
            case 2:
                selectFragment(new AddAnomalyFragment());
                break;
            case 3:
                Bundle bundle = new Bundle();
                bundle.putString("filter", "FilterByUser" );
                ListAnomaliesFragment f=new ListAnomaliesFragment();
                f.setArguments(bundle);
                selectFragment(f);
                break;
        }
    }

    public void selectFragment(Fragment fragment) {
        if (fragment == null)
            return;
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.rootLayout, fragment);
                ft.commit();
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                loadFragment(1);
                break;
            case 1:
                loadFragment(2);
                break;
            case 2:
                loadFragment(3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
