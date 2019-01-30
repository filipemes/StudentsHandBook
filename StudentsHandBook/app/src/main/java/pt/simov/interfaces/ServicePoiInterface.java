package pt.simov.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.POI;

public interface ServicePoiInterface {

    public void postPoi(POI poi);

    public POI getPoi(String uid);

    public  ArrayList<POI> getPoiList();

    public void registerListenerPOI();
}
