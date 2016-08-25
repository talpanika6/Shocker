package com.example.tal.shocker.Push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.example.tal.shocker.R;
import com.example.tal.shocker.view.DispatchActivity;
import com.example.tal.shocker.view.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Handle push notifications
 */
public class CustomReceiver extends ParsePushBroadcastReceiver
{



    @Override
    public void onPushReceive(Context context, Intent intent) {

//        super.onReceive(context, intent);

         final String NOTIFICATION_STRING="NOTIFICATION_ID";
         final int NOTIFICATION_ID=1;
        String notificationTitle=null;
        String notificationText=null;


        try {


            Bundle extra = intent.getExtras();

            String message= extra.getString("com.parse.Data");
            try{
            JSONObject obj =new JSONObject(message);
                notificationTitle=obj.getString("Title");
                notificationText=obj.getString("Text");

        } catch (JSONException e) {
           Toast.makeText(context,"Error parsing JSON",Toast.LENGTH_LONG).show();
        }




            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra(NOTIFICATION_STRING, NOTIFICATION_ID);
            PendingIntent pendingIntent =  PendingIntent.getBroadcast(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            //create inbox style notification

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            String[] events = {notificationText,"View your shock photo"};

            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle(notificationTitle);

            // Moves events into the expanded layout
            for (String event :events){
                inboxStyle.addLine(event);
            }



            // Create custom notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.android_icon3)
                    .setLargeIcon(getLargeIcon(context,intent))
                    .setContentText(notificationText)
                    .setContentTitle(notificationTitle)
                    .setContentIntent(pendingIntent)
                    .setFullScreenIntent(pendingIntent, true)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_wallpaper_black_18dp, "VIEW", pendingIntent)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(inboxStyle);


            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification);

        }
        catch (Exception e) {
            Toast.makeText(context, e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_shocker_48dp);
    }



}
