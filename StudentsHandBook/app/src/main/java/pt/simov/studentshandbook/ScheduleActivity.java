package pt.simov.studentshandbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.ScheduleEvent;
import pt.simov.entities.User;
import pt.simov.firebase.FirebaseConfig;
import pt.simov.helpers.Manager;

public class ScheduleActivity extends BaseActivity {
    private ListView listView;
    private ArrayList<ScheduleEvent> eventList;
    private GenericAdapter adapter;
    private Boolean emptyList;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        initNaviationView(1);
        listView = (ListView) findViewById(R.id.listView);
        emptyList=false;
        try {
            Manager.getInstance().CheckNetworkConnection(getApplicationContext());

            User u = Manager.getInstance().getUser();
            if(u == null){
                emptyList=true;
                eventList = new ArrayList<>();
            }else{
                eventList = u.getEventList();
            }
            if(emptyList){
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {

                      @Override
                      public void run() {
                          User user = Manager.getInstance().getUser();
                          if(user!=null && !user.getEventList().isEmpty()){
                              runOnUiThread(new Runnable(){

                                  @Override
                                  public void run(){
                                      changeList();
                                  }
                              });
                          }
                      }
                  },
                        0, 3000);   // 1000 Millisecond  = 1 second
            }
        }catch (Throwable e){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("Schedule", 0);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ScheduleEvent>>(){}.getType();
            String json = pref.getString("events", "");
            ArrayList<ScheduleEvent> evs = gson.fromJson(json, type);
            if(evs==null){
                Intent intent = new Intent(ScheduleActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }else{
                eventList = evs;
            }
        }
        setUpEventList();
    }

    protected void changeList(){
        eventList = Manager.getInstance().getUser().getEventList();
        timer.cancel();
        setUpEventList();
    }

    protected void setUpEventList(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Schedule", 0);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(eventList);
        editor.putString("events", json);
        editor.commit();
        adapter = new GenericAdapter<ScheduleEvent>(this, R.layout.schedule_event_item,eventList) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rowView;
                final Holder holder=new Holder();
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.
                        LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(rowLayout, null);
                ScheduleEvent event = (ScheduleEvent) getItem(position);
                holder.time = rowView.findViewById(R.id.eventTime);
                holder.time.setText(event.getTime()+" - "+event.getEndtime());
                holder.name = rowView.findViewById(R.id.eventName);
                holder.name.setText(event.getName());

                holder.location = rowView.findViewById(R.id.eventLocation);
                holder.location.setText(event.getLocation());

                holder.layout = rowView.findViewById(R.id.eventBackground);
                rowView.setBackgroundResource(R.drawable.round_rect_shape);
                holder.background = rowView.getBackground();
                holder.background.mutate().setAlpha(100);
                switch(event.getType()){
                    case "PL":
                        holder.background.setColorFilter(Color.RED,PorterDuff.Mode.MULTIPLY);
                        break;
                    case "T":
                            holder.background.setColorFilter(Color.YELLOW,PorterDuff.Mode.MULTIPLY);
                        break;
                    case "OT":
                            holder.background.setColorFilter(Color.CYAN,PorterDuff.Mode.MULTIPLY);
                        break;
                }
                return rowView;
            }
            class Holder{
                TextView name,time,location;
                LinearLayout layout;
                Drawable background;
            }
        };
        listView.setAdapter(adapter);
    }

}


