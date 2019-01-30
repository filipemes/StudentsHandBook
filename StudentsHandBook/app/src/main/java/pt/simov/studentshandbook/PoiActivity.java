package pt.simov.studentshandbook;

import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.POI;
import pt.simov.helpers.Manager;
import pt.simov.services.ServiceFacade;

public class PoiActivity extends BaseActivity  {
    private ListView listView;
    private GenericAdapter adapter;
    private ArrayList<POI> poiList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_list);
        initNaviationView(-1);
        poiList=new ArrayList<POI>();
        setUpPoiList();
        //testApiRequest();
    }

    public void getPois(){
        poiList.add(new POI(
                "ISEP",
                "Rua Dr. António Bernardino de Almeida, 431, 4200-072 Porto"));
        poiList.add(new POI(
                "Campus S.João",
                "R. Dr. Plácido da Costa, 4200-450 Porto"));
        poiList.add(new POI(
                "Casa",
                "R. do Salgueiral de Cima 565",
                40.875167,
                -8.596918));
    }

    public void setPoisDB(){
        for (Iterator<POI> i = poiList.iterator(); i.hasNext();) {
            POI poi = i.next();
            if(poi.getCoords().equals(new HashMap<String, Double>())){
                Geocoder geocoder = new Geocoder(getApplicationContext());
                List<Address> addresses;
                System.out.println(poi.getName());
                try {
                    addresses = geocoder.getFromLocationName(poi.getAddress(), 1);
                    if(addresses.size() > 0) {
                        HashMap<String, Double> coords = new HashMap<String, Double>();
                        coords.put("Latitude",addresses.get(0).getLatitude());
                        coords.put("Longitude",addresses.get(0).getLongitude());
                        poi.setCoords(coords);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Manager.getInstance().getServiceFacade().getServicePoiInterface().postPoi(poi);
            Toast.makeText(this,poi.getName()+" added",Toast.LENGTH_LONG).show();
        }
    }

    public void setUpPoiList(){
        //getPois();
        //setPoisDB();
        listView = (ListView) findViewById(R.id.listView);
        poiList= Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoiList();
        for(POI p:poiList){
            if(p.getImageLink()!=null&&p.getImageLink().length()>0) {
                System.out.println(p.getImageLink());
            }else{
                System.out.println("***********nao tem*********");
            }
        }
        adapter = new GenericAdapter<POI>(this, R.layout.poi_simple_item,poiList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rowView;
                final Holder holder=new Holder();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(rowLayout, null);
                POI poi = (POI)getItem(position);
                /*First Row*/
                holder.name = (TextView) rowView.findViewById(R.id.poiName);
                holder.name.setText(poi.getName());
                /*Second Row*/
                holder.poiDetails = (ImageButton) rowView.findViewById(R.id.poiDetailsBtn);
                holder.poiDetails.setContentDescription(poi.getUid());
                holder.poiDirections = (ImageButton) rowView.findViewById(R.id.poiDirectionsBtn);
                holder.poiDirections.setContentDescription(poi.getUid());
                holder.poiDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        POI poi = Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoi(holder.poiDetails.getContentDescription().toString());
                        Intent i = new Intent(getApplicationContext(),PoiMapActivity.class);
                        i.putExtra("poi",poi.getUid());
                        startActivity(i);
                    }
                });
                holder.poiDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        POI poi = Manager.getInstance().getServiceFacade().getServicePoiInterface().getPoi((String)view.getContentDescription());
                        Map<String,Double> coords = poi.getCoords();
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+coords.get("Latitude")+","+coords.get("Longitude"));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        //Toast.makeText(getApplicationContext(),"Direções do POI",Toast.LENGTH_LONG).show();

                    }
                });
                holder.image = (ImageView) rowView.findViewById(R.id.poiImage);
                if(poi.getImageLink()!=null && !poi.getImageLink().equals("")){
                    Glide.with(rowView).load(poi.getImageLink()).into(holder.image);
                }
                return rowView;
            }
            class Holder{
                TextView name;
                ImageView image;
                ImageButton poiDetails,poiDirections;
            }
        };
        listView.setAdapter(adapter);
        //Manager.getInstance().getServiceFacade().getServicePoiInterface().registerListenerPOI();
    }

    public void testApiRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://studenthandbook-585d8.firebaseapp.com/nomes";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            }
        );
        requestQueue.add(objectRequest);
    }
}
