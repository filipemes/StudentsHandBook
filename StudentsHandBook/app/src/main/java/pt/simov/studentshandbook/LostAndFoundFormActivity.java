package pt.simov.studentshandbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.simov.helpers.Manager;

public class LostAndFoundFormActivity extends AppCompatActivity {

    private Bitmap imageReport;
    private byte[] imageToSend;
    private static final int REQUEST_CODE = 123;
    private Button captureImageButton;
    private TextView captureImageText;
    private RadioGroup radioLostOrFoundGroup;
    private RadioButton radioButton;
    private ImageView imageCapture;
    private Button submitReportButton;
    private EditText description;
    private EditText location;
    private EditText subject;
    private EditText observation;
    private Manager service=Manager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostandfound_activity_createform);
        backPage();
        captureImageButton = (Button) findViewById(R.id.captureImageButton);
        imageCapture = (ImageView) findViewById(R.id.image_capture);
        captureImageText = (TextView) findViewById(R.id.image_capture_text);
        createRadioGroup();
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
        location=(EditText) findViewById(R.id.lostAndFoundLocationText);
        description=(EditText) findViewById(R.id.lostAndFoundDescriptionText);
        subject=(EditText) findViewById(R.id.lostAndFoundSubjectText);
        observation=(EditText) findViewById(R.id.lost_and_foundsObservationText);
        submitReportButton=(Button) findViewById(R.id.lostAndFoundsButtonSubmit);
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location.getText().length()>0 &&
                        description.getText().length()>0 &&
                        subject.getText().length()>0 &&
                        observation.getText().length()>0 &&
                        imageReport!=null){
                    imageToSend=convertToBase64(imageReport);
                    service.getServiceFacade().getServiceLostAndFoundInterface().postLostAndFound(
                            location.getText().toString(),
                            subject.getText().toString(),
                            radioButton != null ? radioButton.getText().toString() : "Achado",
                            description.getText().toString(),
                            observation.getText().toString(), imageToSend);
                    try {
                        new FirebaseSendMessage().execute(radioButton!= null ? radioButton.getText().toString() : "Achado");
                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }
                    Toast.makeText(getApplicationContext(),"Reported with success", Toast.LENGTH_SHORT).show();
                }else if(location.getText().length()>0 &&
                        description.getText().length()>0 &&
                        subject.getText().length()>0 &&
                        observation.getText().length()>0 &&
                        imageReport==null){
                    service.getServiceFacade().getServiceLostAndFoundInterface().postLostAndFound(
                            location.getText().toString(),
                            subject.getText().toString(),
                            radioButton != null ? radioButton.getText().toString() : "Perdido",
                            description.getText().toString(),
                            observation.getText().toString());
                    try {
                        new FirebaseSendMessage().execute(radioButton!= null ? radioButton.getText().toString() : "Perdido");
                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }
                    Toast.makeText(getApplicationContext(),"Reported with success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please fill the all fields", Toast.LENGTH_SHORT).show();
                }
                initField();
                Intent homepage = new Intent(LostAndFoundFormActivity.this, LostAndFoundActivity.class);
                startActivity(homepage);

            }
        });

    }

    public void initField(){
        location.setText("");
        description.setText("");
        if(radioButton != null){
            radioButton.setText("");
        }
        subject.setText("");
        observation.setText("");
        imageCapture.setImageBitmap(null);
    }

    public byte[] convertToBase64(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void backPage(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.ic_back);

            toolbar.setTitle("Form");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(LostAndFoundFormActivity.this);
                }
            });
            toolbar.inflateMenu(R.menu.menu_main);
        }
    }

    private void createRadioGroup() {
        radioLostOrFoundGroup = (RadioGroup) findViewById(R.id.radioGroup);

        if (radioLostOrFoundGroup != null) {
            radioLostOrFoundGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.radioLost) {
                        imageCapture.setVisibility(View.GONE);
                        captureImageButton.setVisibility(View.GONE);
                        captureImageText.setVisibility(View.GONE);
                        int selectedId = radioLostOrFoundGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                    }
                    if (checkedId == R.id.radioFound) {
                        imageCapture.setVisibility(View.VISIBLE);
                        captureImageButton.setVisibility(View.VISIBLE);
                        captureImageText.setVisibility(View.VISIBLE);
                        int selectedId = radioLostOrFoundGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                    }
                }
            });
        }

    }

    public void onLaunchCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name).setMessage("Selecione a fonte da Imagem:");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                takeAPicture();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void takeAPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageCapture.setImageBitmap(imageBitmap);
            imageReport=imageBitmap;
        }
    }

}

class FirebaseSendMessage extends AsyncTask<String, Integer, Double> {
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String AUTH_KEY = "AAAAIY7hYjw:APA91bH4o0Iw2FgYSQF-pcqxMczGpq_3uEMg9I_oFMqNhMZ5VPM6L-lQ-uPyFQV24kMjqYNMTudmq_8UhQ6TQzegC-7E_GQRf_MJBTMqYhJnEtgiY3Xr1SzXm0KvbBrltx3GS_8FRXa_";

    private Exception exception;

    protected Double doInBackground(String... params) {
        try {
            sendRequest(params);
        } catch (Exception e) {
            this.exception = e;
        }
        return null;
    }


    public void sendRequest(String... params) {
        try {
            String urlString = "https://fcm.googleapis.com/fcm/send";
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "key=" + AUTH_KEY);

            String postJsonData = "";

            if(params != null && params[0].equals("Achado")){
                FirebaseMessaging.getInstance().subscribeToTopic("LOST");
                postJsonData = "{\"to\": \"/topics/FOUND\", \"notification\": {\"title\": \"Produto Achado\", \"text:\" : \" Foi encontrado um produto!\"}}";
            }

            if(params != null && params[0].equals("Perdido")){
                FirebaseMessaging.getInstance().subscribeToTopic("FOUND");
                postJsonData = "{\"to\": \"/topics/LOST\", \"notification\": {\"title\": \"Produto Perdido\", \"text:\" : \" Foi perdido um produto! \"}}";
            }

            con.connect(); // Note the connect() here
            OutputStream os = con.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(postJsonData);
            osw.flush();
            osw.close();

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("succeeded");
            }

        } catch (IOException e) {
            Log.d("exception thrown: ", e.toString());
        }
    }
}
