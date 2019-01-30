package pt.simov.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import pt.simov.entities.Anomaly;

public interface ServiceAnomaliesInterface {

    public void postAnomaly(String description, String publisher,String publisherUid,String location);

    public void postAnomaly(String description, String publisher,String publisherUid,String location,byte[] image);

    public ArrayList<Anomaly> getAnomaliesList();

    public void registerEventListenerAnomalies();

    public Anomaly getAnomaly(String uuidAnomaly);

    public ArrayList<Anomaly> getAnomaliesByUser();
}
