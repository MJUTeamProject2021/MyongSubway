package com.example.myongsubway;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;
    private String channelId = "alarm_channel";
    private String channelName = "alarm_name";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("test", "onReceive is called");
        mContext = context;

        // intent 로 넘겨받은 데이터들을 담고있는 Bundle 객체
        Bundle extras = intent.getExtras();
        String station = "알림";
        String doorDirection = "쪽";

        if (extras != null) {
            station = extras.getString("station");
            doorDirection = extras.getString("doorDirection");
        }

        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("test", "this device's version is more than Oreo");

            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 2000, 1000, 3000});
            AudioAttributes audioAttributes = new AudioAttributes.Builder().
                            setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).
                            setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, channelId);

        Intent getOffAlarmIntent = new Intent(mContext, ShortestPathActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestId = (int) System.currentTimeMillis();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntentWithParentStack(getOffAlarmIntent);
        PendingIntent getOffAlarmPendingIntent = stackBuilder.getPendingIntent(requestId, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentTitle(station + "역 하차 알림").
                            setContentText("잠시 후 도착 (내리는문 " + doorDirection + ")").
                            setDefaults(Notification.DEFAULT_ALL).
                            setPriority(NotificationCompat.PRIORITY_HIGH).
                            setAutoCancel(true).
                            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                            setSmallIcon(R.mipmap.img_appico4white_foreground).
                            setContentIntent(getOffAlarmPendingIntent);

        notificationManager.notify(requestId, notificationBuilder.build());

        // 알림 소리를 울린다.
        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(mContext.getApplicationContext(), notification);
        ringtone.play();*/

        // Notification 을 띄우고 알람을 제거하기 위해 ShortestPathActivity 의 finishAlarm() 메소드를 호출한다.
        if (ShortestPathActivity.ShortestPathContext != null) {
            ((ShortestPathActivity) ShortestPathActivity.ShortestPathContext).finishAlarm();
        }
    }
}
