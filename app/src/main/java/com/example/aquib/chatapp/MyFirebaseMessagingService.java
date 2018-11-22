package com.example.aquib.chatapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message = remoteMessage.getNotification().getBody();

        String notification_click = remoteMessage.getNotification().getClickAction();

        String notification_data = remoteMessage.getData().get("from_userId");

        Intent intent = new Intent(notification_click);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Log.d("onMessageReceived: ",notification_data);

        intent.putExtra("user_id",notification_data);

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this,
                        0,
                        intent,
                        0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this ,"default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification_title)
                .setContentText(notification_message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        int notificationId = (int) System.currentTimeMillis();

        NotificationManager mNotify = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotify.notify(notificationId,mBuilder.build());


    }
}
