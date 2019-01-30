package pt.simov.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import pt.simov.entities.Anomaly;
import pt.simov.entities.LostAndFound;
import pt.simov.firebase.FirebaseConfig;
import pt.simov.interfaces.ServiceAnomaliesInterface;

public class ServiceAnomaliesImpl implements ServiceAnomaliesInterface {

    private DatabaseReference databaseReference;
    private HashMap<String,Anomaly>anomalies=new HashMap<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public ServiceAnomaliesImpl(){
        firebaseAuth = FirebaseConfig.getInstanceFirebaseAuth();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference=FirebaseConfig.getInstanceDatabaseReference();
    }

    @Override
    public void postAnomaly(String description, String publisher, String publisherUid, String location) {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String key = databaseReference.getDatabase().getReference("anomalies").push().getKey();
        Anomaly anomaly=new Anomaly(description,publisher,publisherUid,"Not Solved",location,key,currentDateandTime);
        databaseReference.child("anomalies").child(key).setValue(anomaly);
    }

    @Override
    public void postAnomaly(String description, String publisher, String publisherUid, String location,byte[] image) {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String key = databaseReference.getDatabase().getReference("anomalies").push().getKey();
        final Anomaly anomaly=new Anomaly(description,publisher,publisherUid,"Not Solved",location,key,currentDateandTime,"");
        //Get Storage Ref
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Get storageRef from rep
        StorageReference storageRef = storage.getReferenceFromUrl("gs://studenthandbook-585d8.appspot.com");
        //GenerateName
        final StorageReference imageRefToUpload = storageRef.child(System.currentTimeMillis()+".jpg");
        //Create metadata for image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        //Put data and metada
        UploadTask uploadTask = imageRefToUpload.putBytes(image,metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRefToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        String key = databaseReference.getDatabase().getReference("anomalies").push().getKey();
                        anomaly.setImageURL(uri.toString());
                        databaseReference.child("anomalies").child(key).setValue(anomaly);
                    }
                });
            }
        });
    }

    @Override
    public ArrayList<Anomaly> getAnomaliesList() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ArrayList<Anomaly>anomaliesArray=new ArrayList<>();
        for (String key:this.anomalies.keySet()) {
                anomaliesArray.add(this.anomalies.get(key));
        }

        Collections.sort(anomaliesArray, new Comparator<Anomaly>() {
            @Override
            public int compare(Anomaly o1, Anomaly o2) {
                Date x = null;
                Date y = null;
                try {
                    x = formatter.parse(o1.getDate());
                    y = formatter.parse(o2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return y.compareTo(x);
            }
        });

        return anomaliesArray;
    }

    @Override
    public void registerEventListenerAnomalies() {
        databaseReference.getDatabase().getReference("anomalies").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Anomaly anomaly = dataSnapshot.getValue(Anomaly.class);
                addAnomalyToList(anomaly);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Anomaly anomaly = dataSnapshot.getValue(Anomaly.class);
                addAnomalyToList(anomaly);
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
    public void addAnomalyToList(Anomaly anomaly){
        this.anomalies.put(anomaly.getUid(),anomaly);
    }
    @Override
    public Anomaly getAnomaly(String uuidAnomaly) {
        if(anomalies.containsKey(uuidAnomaly)) return anomalies.get(uuidAnomaly);
        return null;
    }

    @Override
    public ArrayList<Anomaly> getAnomaliesByUser() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ArrayList<Anomaly>anomaliesArray=new ArrayList<>();
        String uuid= firebaseUser.getUid();
        for (String key:this.anomalies.keySet()) {
            if(uuid.compareTo(this.anomalies.get(key).getPublisherUid())==0)
                anomaliesArray.add(this.anomalies.get(key));
        }

        Collections.sort(anomaliesArray, new Comparator<Anomaly>() {
            @Override
            public int compare(Anomaly o1, Anomaly o2) {
                Date x = null;
                Date y = null;
                try {
                    x = formatter.parse(o1.getDate());
                    y = formatter.parse(o2.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return y.compareTo(x);
            }
        });

        return anomaliesArray;
    }
}
