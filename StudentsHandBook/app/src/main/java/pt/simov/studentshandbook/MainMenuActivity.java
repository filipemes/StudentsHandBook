package pt.simov.studentshandbook;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.simov.firebase.FirebaseConfig;
import pt.simov.helpers.Manager;

public class MainMenuActivity extends BaseActivity {

    private LinearLayout nfcButton;
    private LinearLayout scheduleButton;
    private LinearLayout lostAndFoundButton;
    private LinearLayout anomaliesButton;
    private LinearLayout accountButton;
    private LinearLayout pointsOfInterest;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Manager.getInstance().CheckNetworkConnection(getApplicationContext());

            firebaseAuth = FirebaseConfig.getInstanceFirebaseAuth();
            firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                setContentView(R.layout.activity_main_menu);
                Manager.getInstance().registerEventUser(firebaseUser.getUid());
                initNaviationView(0);
                nfcButton = (LinearLayout) findViewById(R.id.nfc_button);
                scheduleButton = (LinearLayout) findViewById(R.id.schedule_button);
                lostAndFoundButton = (LinearLayout) findViewById(R.id.lost_and_found_button);
                anomaliesButton = (LinearLayout) findViewById(R.id.anomalies_button);
                accountButton = (LinearLayout) findViewById(R.id.account_button);
                pointsOfInterest = (LinearLayout) findViewById(R.id.points_of_interest_button);


                nfcButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, NfcActivity.class);
                        startActivity(intent);
                    }
                });

                scheduleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, ScheduleActivity.class);
                        startActivity(intent);
                    }
                });

                lostAndFoundButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, LostAndFoundActivity.class);
                        startActivity(intent);
                    }
                });

                anomaliesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, AnomaliesActivity.class);
                        startActivity(intent);
                    }
                });

                accountButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainMenuActivity.this, AccountActivity.class);
                        startActivity(intent);
                    }
                });

                pointsOfInterest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), PoiActivity.class);
                        startActivity(intent);
                    }
                });
                Manager.getInstance().registerEventUserAccount(firebaseUser.getUid());
                Manager.getInstance().registerEventAnomalies();
                Manager.getInstance().registerEventPois();
                Manager.getInstance().registerEventLostAndFound();
            }

        }catch (Throwable e){
            Toast.makeText(MainMenuActivity.this, "Internet Access Required", Toast.LENGTH_LONG).show();
        }

    }
}