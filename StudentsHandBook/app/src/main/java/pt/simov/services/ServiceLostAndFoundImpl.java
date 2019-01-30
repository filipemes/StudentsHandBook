package pt.simov.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.simov.entities.LostAndFound;
import pt.simov.entities.LostOrFound;
import pt.simov.helpers.Manager;
import pt.simov.interfaces.ServiceLostAndFoundInterface;

public class ServiceLostAndFoundImpl implements ServiceLostAndFoundInterface {

    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private HashMap<String, LostAndFound> lostAndFoundsList;

    public ServiceLostAndFoundImpl() {
        lostAndFoundsList = new HashMap<>();
    }

    @Override
    public void postLostAndFound(String location, String subject, String lostOrFound, String description, String observation, byte[] image) {

        final LostAndFound f = new LostAndFound();
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        f.setDateNow(currentDateandTime);
        f.setUser(Manager.getInstance().getUser());
        f.setLocation(location);
        f.setSubject(subject);
        if (lostOrFound.equals("Achado")) {
            f.setLostOrFound(LostOrFound.FOUND.name());
        }
        if (lostOrFound.equals("Perdido")) {
            f.setLostOrFound(LostOrFound.LOST.name());
        }
        f.setObservation(observation);
        f.setStatus("Not Solved");
        f.setDescription(description);
        //Get Storage Ref
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //Get storageRef from rep
        StorageReference storageRef = storage.getReferenceFromUrl("gs://studenthandbook-585d8.appspot.com");
        //GenerateName
        final StorageReference imageRefToUpload = storageRef.child(System.currentTimeMillis() + ".jpg");
        //Create metadata for image
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        //Put data and metada
        UploadTask uploadTask = imageRefToUpload.putBytes(image, metadata);
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
                        String key = database.getReference("lostAndFounds").push().getKey();
                        f.setUid(key);
                        f.setImageLink(uri.toString());
                        mDatabase.child(key).setValue(f);
                    }
                });
            }
        });
    }

    @Override
    public void postLostAndFound(String location, String subject, String lostOrFound, String description, String observation) {
        final LostAndFound f = new LostAndFound();
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        f.setDateNow(currentDateandTime);
        f.setUser(Manager.getInstance().getUser());
        f.setLocation(location);
        f.setSubject(subject);
        if (lostOrFound.equals("Achado")) {
            f.setLostOrFound(LostOrFound.FOUND.name());
        }
        if (lostOrFound.equals("Perdido")) {
            f.setLostOrFound(LostOrFound.LOST.name());
        }
        f.setObservation(observation);
        f.setStatus("Not Solved");
        f.setDescription(description);
        database = FirebaseDatabase.getInstance();
        String key = database.getReference("lostAndFounds").push().getKey();
        f.setUid(key);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("lostAndFounds").child(key).setValue(f);
    }


    @Override
    public ArrayList<LostAndFound> getAllLost() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ArrayList<LostAndFound> lostArrayList = new ArrayList<>();
        for (Map.Entry<String, LostAndFound> entry : this.lostAndFoundsList.entrySet()) {
            LostAndFound value = entry.getValue();
            if (value.getLostOrFound().equals(LostOrFound.LOST.name()) && value.getStatus().equals("Not Solved")) {
                lostArrayList.add(value);
            }
        }

        Collections.sort(lostArrayList, new Comparator<LostAndFound>() {
            @Override
            public int compare(LostAndFound o1, LostAndFound o2) {
                Date x = null;
                Date y = null;
                try {
                    x = formatter.parse(o1.getDateNow());
                    y = formatter.parse(o2.getDateNow());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return y.compareTo(x);
            }
        });

        return lostArrayList;
    }

    @Override
    public ArrayList<LostAndFound> getAllFound() {

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ArrayList<LostAndFound> foundArrayList = new ArrayList<>();
        for (Map.Entry<String, LostAndFound> entry : this.lostAndFoundsList.entrySet()) {
            LostAndFound value = entry.getValue();
            if (value.getLostOrFound().equals(LostOrFound.FOUND.name()) && value.getStatus().equals("Not Solved")) {
                foundArrayList.add(value);
            }
        }

        Collections.sort(foundArrayList, new Comparator<LostAndFound>() {
            @Override
            public int compare(LostAndFound o1, LostAndFound o2) {
                Date x = null;
                Date y = null;
                try {
                    x = formatter.parse(o1.getDateNow());
                    y = formatter.parse(o2.getDateNow());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return y.compareTo(x);
            }
        });

        return foundArrayList;
    }

    public void addLostAndFoundList(LostAndFound lostAndFound) {
        this.lostAndFoundsList.put(lostAndFound.getUid(), lostAndFound);
    }


    @Override
    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, String localArquivo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://studenthandbook-585d8.appspot.com");
        StorageReference imagesRef = storageRef.child(localArquivo);

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                // Do what you want
            }
        });
    }


    @Override
    public void registerEventListenerLostAndFound() {
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("lostAndFounds");
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LostAndFound lostAndFound = dataSnapshot.getValue(LostAndFound.class);
                addLostAndFoundList(lostAndFound);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LostAndFound lostAndFound = dataSnapshot.getValue(LostAndFound.class);
                addLostAndFoundList(lostAndFound);
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

    @Override
    public void setStatusByLostAndFound(LostAndFound f) {

        DatabaseReference leadersRef = FirebaseDatabase.getInstance().getReference("lostAndFounds");
        Query query = leadersRef.orderByChild("uid").equalTo(f.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    child.getRef().child("status").setValue("Solved");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
