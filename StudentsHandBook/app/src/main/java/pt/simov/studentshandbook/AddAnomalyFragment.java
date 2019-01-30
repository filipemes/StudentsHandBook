package pt.simov.studentshandbook;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import pt.simov.helpers.Manager;

public class AddAnomalyFragment extends Fragment {

    private final int REQUEST_CODE =1242;
    private ImageView imageCapture;
    private Button captureImageButton;
    private Button submitReportButton;
    private Bitmap imageReport;
    private EditText description;
    private EditText room;
    private Manager service=Manager.getInstance();
    private  byte[] imageToSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_anomaly, container, false);
        imageCapture=(ImageView)view.findViewById(R.id.image_capture);
        room=(EditText) view.findViewById(R.id.anomalyRoomText);
        description=(EditText) view.findViewById(R.id.anomalyDescriptionText);
        submitReportButton=(Button)view.findViewById(R.id.anomalyButtonSubmit);
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(room.getText().length()>0 && description.getText().length()>0 && imageReport!=null){
                    imageToSend=convertToBase64(imageReport);
                    service.getServiceFacade().getServiceAnomaliesInterface().postAnomaly(description.getText().toString(),service.getUser().getFirstName()+" "+service.getUser().getLastName(),service.getUser().getUid(),room.getText().toString(),imageToSend);
                    initField();
                    sendEmail();
                    Toast.makeText(getActivity(),"Reported with success", Toast.LENGTH_SHORT).show();
                }else if(room.getText().length()>0 && description.getText().length()>0 && imageReport==null){
                    service.getServiceFacade().getServiceAnomaliesInterface().postAnomaly(description.getText().toString(),service.getUser().getFirstName()+" "+service.getUser().getLastName(),service.getUser().getUid(),room.getText().toString());
                    initField();
                    sendEmail();
                    Toast.makeText(getActivity(),"Reported with success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Please fill the all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        captureImageButton=(Button)view.findViewById(R.id.captureImageButton);
        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
        return view;
    }

    public void initField(){
        room.setText("");
        description .setText("");
        imageCapture.setImageBitmap(null);
    }
    public byte[] convertToBase64(Bitmap image){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE);
        }
    }

    public void sendEmail(){
        String to = "ateneuleiria@gmail.com";
        String subject = "Notificação de Anomalia";
        String message = "Ao enviar este email, a anomalia detectada por si será analisada pelo departamento responsável. Obrigado";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageCapture.setImageBitmap(imageBitmap);
            imageReport=imageBitmap;
        }
    }
}
