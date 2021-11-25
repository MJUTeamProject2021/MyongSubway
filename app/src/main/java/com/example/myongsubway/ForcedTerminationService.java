package com.example.myongsubway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ForcedTerminationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("test","onTaskRemoved - 강제 종료 " + rootIntent);

        ((CustomAppGraph)getApplicationContext()).destroyAlarm();

        stopSelf();
    }
}
