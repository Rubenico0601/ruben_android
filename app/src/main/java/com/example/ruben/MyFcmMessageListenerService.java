package com.example.ruben;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.pushnotification.NotificationInfo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;
import java.time.Instant;

public class MyFcmMessageListenerService extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage message) {
        try {
            if (message.getData().size() > 0) {
                Bundle extras = new Bundle();
                for (Map.Entry<String, String> entry : message.getData().entrySet()) {
                    extras.putString(entry.getKey(), entry.getValue());
                }
                NotificationInfo info = CleverTapAPI.getNotificationInfo(extras);


                if (info.fromCleverTap) {
                    if (extras.containsKey("Pt")) {

                        String Pt_title = extras.getString("Pt");
                        String Pm_message = extras.getString("Pm");
                        String S_time = extras.getString("P_time");
                        String End_title = extras.getString("Pt_end");
                        String End_msg = extras.getString("Pm_end");
                        long time = Long.parseLong(S_time);

                        Log.v("From Neha App", "Bundle Received: " +
                                "Pt_title: " + Pt_title +
                                ", Pm_message: " + Pm_message +
                                ", S_time: " + time +
                                ", End_title: " + End_title +
                                ", End_msg: " + End_msg);


                        int PROGRESS_MAX = 100;
                        int PROGRESS_CURRENT = 0;
                        int d_time;
                        RemoteViews collapsed = new RemoteViews(getPackageName(), R.layout.custom_layout_collapsed);
                        collapsed.setProgressBar(R.id.progressBar, PROGRESS_MAX, PROGRESS_CURRENT, false);
                        collapsed.setTextViewText(R.id.textView, Pm_message);
                        // collapsed.setImageViewResource(R.id.movable_icon,R.drawable.small_icon);

                        RemoteViews expanded = new RemoteViews(getPackageName(), R.layout.custom_layout_expanded);
                        expanded.setProgressBar(R.id.progressBar2, PROGRESS_MAX, PROGRESS_CURRENT, false);
                        expanded.setImageViewResource(R.id.imageView2, R.drawable.notificationpicture);
                        expanded.setTextViewText(R.id.textView2, Pm_message);

                        if (Pt_title != null && Pm_message != null && S_time != null && End_title != null && End_msg != null) {
                            String CHANNEL_ID = "TAM-RO";

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                            builder.setContentTitle(Pm_message)
                                    .setSmallIcon(R.drawable.small_icon)
                                    .setCustomContentView(collapsed)
                                    .setCustomBigContentView(expanded)
                                    .setOnlyAlertOnce(true)
                                    .setPriority(NotificationCompat.PRIORITY_LOW);

                            CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);
                            // Issue the initial notification with zero progress.

//                            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }

                            Notification notification = builder.build();
                            notificationManager.notify(2, notification);

                            if (checkTime(S_time)) {
                                d_time = DurTime(time);
                            } else
                                d_time = DelayTime(time);

//

                            for (int i = 1; i <= 50; i++) {   //100% progress bar
                                PROGRESS_CURRENT += 2;
                                collapsed.setProgressBar(R.id.progressBar, PROGRESS_MAX, PROGRESS_CURRENT, false);
                                expanded.setProgressBar(R.id.progressBar2, PROGRESS_MAX, PROGRESS_CURRENT, false);
                                // collapsed.setFloat(R.id.movable_icon, "setTranslationX",PROGRESS_CURRENT);
                                notificationManager.notify(2, notification);

                                Thread.sleep(d_time); // delay
                            }

                            collapsed.setTextViewText(R.id.textView,End_msg);
                            expanded.setTextViewText(R.id.textView2, End_msg);
                            builder.setContentTitle(End_msg)
                                    .setSmallIcon(R.drawable.small_icon)
                                    .setCustomContentView(collapsed)
                                    .setCustomBigContentView(expanded)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);
                            notificationManager.notify(2, builder.build());


                        } else if (Pt_title != null && Pm_message != null) {


                            Notification notification = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                notification = new Notification.Builder(this)
                                        .setSmallIcon(R.drawable.small_icon)
                                        .setContentTitle(Pt_title)
                                        .setContentText(Pm_message)
                                        .setChannelId("TAM-RO")
                                        .build();
                            }
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(1, notification);
                            Log.v("From Ruben's App", "Bundle Received: " +
                                    "Pt_title: " + Pt_title +
                                    ", Pm_message: " + Pm_message);
                            CleverTapAPI.getDefaultInstance(this).pushNotificationViewedEvent(extras);// to track push impression.
                        }
                    } else {
                        CleverTapAPI.createNotification(getApplicationContext(), extras);
                    }
                }

            }

        } catch (Throwable t) {
            Log.v("From Ruben's App", "Error parsing FCM message", t);
        }
    }


    public int DelayTime(long time) {
        Instant instant = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            instant = Instant.now();
        }
        long epochSeconds = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            epochSeconds = instant.getEpochSecond();
        }
        int delay = (int) (time - epochSeconds) * 10;

        return delay;
    }

    public int DurTime(long time) {
        int delay = (int) time * 10;
        return delay;
    }

    public boolean checkTime(String time) {
        if (time.length() < 10 && time.length() > 0) {
            return true;
        } else
            return false;
    }

}