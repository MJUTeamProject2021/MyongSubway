package com.example.myongsubway;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class BackPressHandler {
    private Activity activity;
    private long backPressedTime = 0;

    public BackPressHandler(Activity _activity) {
        activity = _activity;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis()> backPressedTime + 1800){
            backPressedTime =System.currentTimeMillis();
            Toast.makeText(activity, "종료하려면 뒤로가기 버튼을 한번 더 누르세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(System.currentTimeMillis() <= backPressedTime + 1800){

            activity.finishAndRemoveTask();
            
            /*ActivityCompat.finishAffinity(mainactivity);
            System.exit(0);*/
        }
    }
}
