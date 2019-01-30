package pt.simov.services;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import pt.simov.entities.ScheduleEvent;
import pt.simov.entities.User;
import pt.simov.firebase.FirebaseConfig;
import pt.simov.helpers.Manager;
import pt.simov.interfaces.ServiceUserInterface;
import pt.simov.studentshandbook.ScheduleActivity;

public class ServiceUserImpl implements ServiceUserInterface {

    private DatabaseReference databaseReference;
    private User user;

    public ServiceUserImpl() {
        databaseReference = FirebaseConfig.getInstanceDatabaseReference();
    }

    @Override
    public void getEvents() {
        try {
            new getToken().execute("https://www.googleapis.com/calendar/v3/calendars/primary/events").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postUser(String firstName, String lastName, String email, String number, String uid, String course, String courseclass) {
        User user = new User(firstName, lastName, email, number, uid, course, courseclass);
        databaseReference.child("users").child(user.getUid()).setValue(user);
    }

    @Override
    public void changeCourse(String uid, String course) {
        databaseReference.child("users").child(uid).child("course").setValue(course);
    }

    @Override
    public void changeClass(String uid, String courseClass) {
        databaseReference.child("users").child(uid).child("courseclass").setValue(courseClass);
    }

    public User getUser() {
        return this.user;
    }

    @Override
    public void registerEventUser(String uid) {
        databaseReference.child("users").orderByChild("uid")
                .equalTo(uid)
                .limitToFirst(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                         user = dataSnapshot.getValue(User.class);
                         getEvents();
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                         user = dataSnapshot.getValue(User.class);
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

    protected void parseData(JSONObject data){
        String className = user.getCourse()+"-"+user.getCourseclass();
        Calendar today = Calendar.getInstance();
        JSONArray events = new JSONArray();
        try {
            events = data.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Calendar evDate;
            user.clearEventList();
            for(int j=0;j<events.length();j++){
                JSONObject obj = (JSONObject) events.get(j);
                ScheduleEvent ev = new ScheduleEvent();
                String[] classes;
                String a = obj.getString("summary");
                classes = obj.getString("description").split(";");
                if(!Arrays.asList(classes).contains(className)){
                    continue;
                }
                evDate = Calendar.getInstance();
                String[] dateParts= obj.getJSONObject("start").getString("dateTime").split("-");
                String[] dateEndParts= obj.getJSONObject("end").getString("dateTime").split("-");
                evDate.set(Calendar.YEAR,Integer.parseInt(dateParts[0]));
                evDate.set(Calendar.MONTH,Integer.parseInt(dateParts[1])-1);
                evDate.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateParts[2].split("T")[0]));
                if( (today.get(Calendar.YEAR)==evDate.get(Calendar.YEAR) && today.get(Calendar.MONTH)==evDate.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH)==evDate.get(Calendar.DAY_OF_MONTH))){
                    ev.setName(obj.getString("summary"));
                    ev.setDesc(obj.getString("description"));
                    ev.setLocation(obj.getString("location"));
                    ev.setTime(dateParts[2].split("T")[1].split(":00Z")[0]);
                    ev.setEndtime(dateEndParts[2].split("T")[1].split(":00Z")[0]);
                    ev.setType(ev.getName().split(" ")[1]);
                    user.addEvent(ev);
                }else{
                    continue;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void getEvents(JSONObject token){
        try {
            new getData().execute(token.getString("access_token")).get();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class getToken extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... urls) {
            URL url = null;
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                String urlString = "https://www.googleapis.com/oauth2/v4/token?refresh_token=1/Ynw-Run_nG8ERbpM6wOjJd0VxaTKujM9HVrArBEF4Tlgk1kAYCYaeXZXjreuyF8Q&client_id=144131056188-f467hcufokh82h4ab0rpqmak812ciuef.apps.googleusercontent.com&client_secret=B7pKuOXcHy2SQsuGbLpTPmVe&grant_type=refresh_token";
                url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                BufferedReader br;
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println(":::Done ERROR MAL "+e.toString());
            } catch (IOException e) {
                System.out.println(":::Done ERROR IO "+e.toString());
                e.printStackTrace();
            }
            return new JSONObject();
        }
        protected void onPostExecute(JSONObject result) {
            getEvents(result);
        }
    }

    private class getData extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... token) {
            URL url = null;
            String line;
            StringBuilder sb = new StringBuilder();
            String calendarId="";
            String course = Manager.getInstance().getUser().getCourse();
            try {
                switch(course){
                    case "SGM":
                        calendarId = "7n5gaeuca2vavvre29ejcpktp8@group.calendar.google.com";
                        break;
                    case "ES":
                        calendarId = "eu2kcjllrj9s3s3ogavthkc9nk@group.calendar.google.com";
                        break;
                    case "SC":
                        calendarId = "b5k62sde72ga74pvl1gvsosg14@group.calendar.google.com";
                        break;
                    case "SIC":
                        calendarId = "03fagb1m8neq63u5drr03resgo@group.calendar.google.com";
                        break;
                }
                String urlString = "https://www.googleapis.com/calendar/v3/calendars/"+calendarId+"/events?singleEvents=true&orderBy=startTime";
                url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer "+token[0]);
                conn.connect();
                BufferedReader br;
                if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(sb.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println(":::Done ERROR MAL "+e.toString());
            } catch (IOException e) {
                System.out.println(":::Done ERROR IO "+e.toString());
                e.printStackTrace();
            }
            return new JSONObject();
        }
        protected void onPostExecute(JSONObject result) {
            parseData(result);
        }
    }

}
