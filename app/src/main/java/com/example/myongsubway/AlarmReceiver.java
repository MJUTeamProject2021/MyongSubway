package com.example.myongsubway;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;
    private String channelId = "alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("test", "onReceive is called");

        mContext = context;

        // intent 로 넘겨받은 데이터들을 담고있는 Bundle 객체
        Bundle extras = intent.getExtras();
        String station = "알림";
        String doorDirection = "쪽";
        int requestId = 1;

        if (extras != null) {
            station = extras.getString("station");
            doorDirection = extras.getString("doorDirection");
            requestId = extras.getInt("requestId");
        }

        Intent getOffAlarmIntent = new Intent(mContext, ShortestPathActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addNextIntentWithParentStack(getOffAlarmIntent);
        PendingIntent getOffAlarmPendingIntent = stackBuilder.getPendingIntent(requestId, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, channelId).
                setSmallIcon(R.mipmap.img_appico4white_foreground).
                setDefaults(Notification.DEFAULT_ALL).
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                setAutoCancel(true).
                setPriority(NotificationCompat.PRIORITY_HIGH).
                setContentTitle(station + "역 하차 알림").
                setContentText("잠시 후 도착 (내리는문 " + doorDirection + ")").
                setContentIntent(getOffAlarmPendingIntent);

        final NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        int id = (int) System.currentTimeMillis();

        notificationManager.notify(id, notificationBuilder.build());

        // Notification 을 띄우고 알람을 제거하기 위해 ShortestPathActivity 의 finishAlarm() 메소드를 호출한다.
        if ((ShortestPathActivity) ShortestPathActivity.ShortestPathContext != null) {
            ((ShortestPathActivity) ShortestPathActivity.ShortestPathContext).finishAlarm();
        }
    }
}
