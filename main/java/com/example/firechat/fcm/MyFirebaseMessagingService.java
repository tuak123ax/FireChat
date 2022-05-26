package com.example.firechat.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firechat.Home;
import com.example.firechat.Notification;
import com.example.firechat.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        RemoteMessage.Notification notification=message.getNotification();
        if(notification==null)
        {
            return;
        }
        String title=notification.getTitle();
        String content=notification.getBody();
        sendNotification(title,content);
    }

    private void sendNotification(String title, String content) {
        Intent intent=new Intent(this, Home.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, Notification.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);
        android.app.Notification notification=builder.build();
        NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager!=null)
        {
            manager.notify(1,notification);
        }
    }
}
