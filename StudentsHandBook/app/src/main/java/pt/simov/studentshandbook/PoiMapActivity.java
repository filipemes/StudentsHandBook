package pt.simov.studentshandbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.POI;
import pt.simov.helpers.Manager;
import pt.simov.services.ServiceFacade;

public class PoiMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private ListView listView;
    private GoogleMap mMap;
    private ArrayList<POI> poiList;
    private POI poi;
    private GenericAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_complete_item);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpServiceFacade();
        Toast.makeText(getApplicationContext(),poi.getName(),Toast.LENGTH_LONG).show();
        setUpPoi();
    }

    public void setUpServiceFacade(){
        poi =Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoi(getIntent().getStringExtra("poi"));
    }

    public void setUpPoi(){
        TextView poiName = findViewById(R.id.poiName);
        poiName.setText(poi.getName());

        TextView poiAddress = findViewById(R.id.poiAddress);
        poiAddress.setText(poi.getAddress());

        ImageButton poiDirections = findViewById(R.id.poiDirectionsBtn);
        poiDirections.setContentDescription(poi.getUid());
        poiDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POI poi = Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoi((String)view.getContentDescription());
                Map<String,Double> coords = poi.getCoords();
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+coords.get("Latitude")+","+coords.get("Longitude"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        ImageButton poiCall = findViewById(R.id.poiCallBtn);
        if(poi.getCellPhone()!=null){
            poiCall.setContentDescription(poi.getUid());
            poiCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    POI poi = Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoi((String)view.getContentDescription());
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", poi.getCellPhone(), null)));
                }
            });
        }else{
            ((ViewGroup) poiCall.getParent()).removeAllViewsInLayout();
        }
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
        setUpMap();

    }

    public void setUpMap(){
        // Add a marker in Sydney and move the camera
        LatLng poiLocation = new LatLng(poi.getCoords().get("Latitude"), poi.getCoords().get("Longitude"));
        mMap.addMarker(new MarkerOptions().position(poiLocation).title(poi.getName()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(poiLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(poiLocation, 16));
    }
}
