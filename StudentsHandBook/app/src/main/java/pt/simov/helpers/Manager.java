package pt.simov.helpers;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;

import pt.simov.entities.Anomaly;
import pt.simov.entities.LostAndFound;
import pt.simov.entities.POI;
import pt.simov.entities.User;
import pt.simov.services.ServiceFacade;


public class Manager {

    public static Manager INSTANCE;
    private ServiceFacade serviceFacade;

    Manager(){
        this.serviceFacade = new ServiceFacade();
    }

    public static Manager getInstance(){
        if(INSTANCE==null){
            INSTANCE=new Manager();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public ServiceFacade getServiceFacade(){
        return this.serviceFacade;
    }

    public void registerEventUserAccount(String uid) {
        this.serviceFacade.getServiceUserInterface().registerEventUser(uid);
    }

    public ArrayList<Anomaly> getAllAnomalies(){
        return this.serviceFacade.getServiceAnomaliesInterface().getAnomaliesList();
    }

    public void registerEventLostAndFound(){
        this.serviceFacade.getServiceLostAndFoundInterface().registerEventListenerLostAndFound();
    }

    public void registerEventAnomalies(){
        this.serviceFacade.getServiceAnomaliesInterface().registerEventListenerAnomalies();
    }
    public void registerEventPois(){
        this.serviceFacade.getServicePoiInterface().registerListenerPOI();
    }
    public ArrayList<POI> getPOIList() {
        return this.serviceFacade.getServicePoiInterface().getPoiList();
    }

    public ArrayList<LostAndFound> getLostList() {
        return this.serviceFacade.getServiceLostAndFoundInterface().getAllLost();
    }

    public ArrayList<LostAndFound> getFoundList(){
       return this.serviceFacade.getServiceLostAndFoundInterface().getAllFound();
    }
    public void registerEventUser(String uuid){
        this.serviceFacade.getServiceUserInterface().registerEventUser(uuid);
    }
    public User getUser(){
        return this.serviceFacade.getServiceUserInterface().getUser();
    }

    public static void CheckNetworkConnection(Context context) throws Throwable {

        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMan.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            Log.i(context.getClass().getName(), "Network connection");
        } else {

            Log.i(context.getClass().getName(), "No network avaliable.");
            throw new Throwable();
        }
    }

}
