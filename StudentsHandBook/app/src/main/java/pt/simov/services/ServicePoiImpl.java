package pt.simov.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.Anomaly;
import pt.simov.entities.POI;
import pt.simov.interfaces.ServicePoiInterface;

public class ServicePoiImpl implements ServicePoiInterface {

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private HashMap<String,POI> poiList;

    public ServicePoiImpl(){
     poiList = new HashMap<String,POI>();
     registerListenerPOI();
    }

    @Override
    public void postPoi(POI poi) {
        database = FirebaseDatabase.getInstance();
        String key = database.getReference("pois").push().getKey();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("pois").child(key).setValue(poi);
    }

    @Override
    public POI getPoi(String uid) {
        for (String key:this.poiList.keySet()) {
            if(this.poiList.get(key).getUid().compareTo(uid)==0){
                return this.poiList.get(key);
            }
        }
        return null;
    }

    @Override
    public ArrayList<POI> getPoiList() {
        ArrayList<POI>pois=new ArrayList<>();
        for (String key:poiList.keySet()) {
            pois.add(poiList.get(key));
        }
        return pois;
    }

    public void addPoiToList(POI poi){
        this.poiList.put(poi.getUid(),poi);
    }

    @Override
    public void registerListenerPOI() {
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("pois");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                POI poi = dataSnapshot.getValue(POI.class);
                addPoiToList(poi);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                POI poi = dataSnapshot.getValue(POI.class);
                addPoiToList(poi);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
