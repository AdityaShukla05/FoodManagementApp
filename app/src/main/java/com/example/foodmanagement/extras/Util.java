package com.example.foodmanagement.extras;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.foodmanagement.services.NotificationServiceses;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class Util {
    public static  void showViews(List<View> views){
        for(View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views){
        for(View view : views){
            view.setVisibility(View.GONE);
        }
    }
    public static boolean moreThanJellyBean() {
        return Build.VERSION.SDK_INT > 15;
    }
    public static void setBackground(View view, Drawable drawable) {
        if (moreThanJellyBean()) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void schedulenoti(Context context){
        AlarmManager manager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationServiceses.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //todo change 5000 ie 5 seconds to 30 min
        manager1.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000,5000,pendingIntent);

    }
}
