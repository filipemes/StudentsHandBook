package pt.simov.interfaces;

import android.graphics.Bitmap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;


import pt.simov.entities.LostAndFound;
import pt.simov.entities.User;


public interface ServiceLostAndFoundInterface {

    public void postLostAndFound(String location, String subject, String lostOrFound, String description, String observation, byte [] image);

    public void postLostAndFound(String location, String subject, String lostOrFound, String description, String observation);

    public ArrayList<LostAndFound> getAllLost();

    public ArrayList<LostAndFound> getAllFound();

    public void registerEventListenerLostAndFound();

    public void setStatusByLostAndFound(final LostAndFound f);

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap, String localPhoto);


}
