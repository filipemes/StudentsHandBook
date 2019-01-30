package pt.simov.studentshandbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().isEmpty())
            showNotfication(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        else
            showNotfication(remoteMessage.getData());
    }

    public void showNotfication(Map<String,String> data) {

        String text = data.get("body").toString();
        String title = data.get("title").toString();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_ID = "pt.simov.studentshandbook";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_ID,
                    "Notification",NotificationManager.IMPORTANCE_DEFAULT);


            notificationChannel.setDescription("SIMOV Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_ID);
        notificationBuilder.setAutoCancel(true).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    public void showNotfication(String title, String body) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_ID = "pt.simov.studentshandbook";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_ID,
                    "Notification",NotificationManager.IMPORTANCE_DEFAULT);


            notificationChannel.setDescription("SIMOV Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_ID);
        notificationBuilder.setAutoCancel(true).
                setDefaults(Notification.DEFAULT_ALL).
                setWhen(System.currentTimeMillis()).
                setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body).setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("Token", s);
    }
}