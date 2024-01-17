package com.example.firechat.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firechat.constants.Constant;
import com.example.firechat.home.Home;
import com.example.firechat.R;
import com.example.firechat.home.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        User user = new User();
        user.setUid(message.getData().get(Constant.KEY_USER_ID));
        user.setName(message.getData().get(Constant.KEY_NAME));
        user.setToken(message.getData().get(Constant.KEY_FCM_TOKEN));
        String notificationMessage = message.getData().get(Constant.KEY_MESSAGE);
        sendNotification(user.getName(), notificationMessage);
    }

    private void sendNotification(String title, String content) {
        Intent intent=new Intent(this, Home.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, Notification.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content));
        android.app.Notification notification=builder.build();
        NotificationManager manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager!=null)
        {
            manager.notify(1,notification);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        updateToken(token);
        super.onNewToken(token);
    }
    private void updateToken(String token)
    {
        SharedPreferences sharedPreferences = getSharedPreferences();
        sharedPreferences.edit().putString(Constant.KEY_FCM_TOKEN, token).apply();
    }
    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences("local_data", MODE_PRIVATE);
    }
}
