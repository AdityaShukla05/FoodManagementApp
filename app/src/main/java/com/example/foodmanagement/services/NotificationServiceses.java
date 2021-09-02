package com.example.foodmanagement.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.foodmanagement.MainActivity;
import com.example.foodmanagement.R;
import com.example.foodmanagement.beans.Drop;

import io.realm.Realm;
import io.realm.RealmResults;
import ir.zadak.zadaknotify.notification.ZadakNotification;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationServiceses extends IntentService {
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    private static final String TAG = "Adi";

    public NotificationServiceses() {
        super("NotificationServiceses");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<Drop> results = realm.where(Drop.class).equalTo("completed", false).findAll();
//            Log.e(TAG,"New");
//            long now = (long) (System.currentTimeMillis()+300000000);
            for (Drop current : results) {
////                long now = (long) (System.currentTimeMillis()*1.0000000000000000000000000000000000000000000000000000000000001);
//                long when = current.getWhen();
////                long added = current.getAdded();
////                long diff70 = (long) (0.1 * (when - added));
//           boolean b=  (now > when) ? true : false;
//                Toast.makeText(getApplicationContext(),current.getWhat()+" "+String.valueOf(now-when),Toast.LENGTH_LONG).show();
//                Log.e(TAG,current.getWhat()+" "+String.valueOf(b));


                if (NotificationNeeded(current.getWhat(), current.getWhen())) {
                    Log.e(TAG,"inside NNNNNNNN");
                    fireNotif(current);
                }
            }

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void fireNotif(Drop drop) {
        //todo notification handle
//        createNotif();
        String message = "Item(s) is about to expire";
//                +" "+getString(R.string.notifmsg);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notification";
            String description = "Include abc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);

        }

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Alert!");
        builder.setContentText(message);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.l5y___copy);
        builder.setLargeIcon(largeIcon);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

    }

    private void createNotif(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Personal Notification";
            String description = "Include abc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.WHITE);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }

    private boolean NotificationNeeded(String added, long when) {
        Log.e(TAG,"inside NN");
        long now = (long) (System.currentTimeMillis()+300000000);
        long last = System.currentTimeMillis();
        if (last > when) {
            Log.e(TAG,added+ "inside if return false");
            return false;
        } else {
            boolean ams = (now > when) ? true : false;
            Log.e(TAG,added+" inside else "+String.valueOf(ams));
            return ams;
        }
    }
}