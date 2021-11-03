package com.example.myongsubway;

import android.app.Activity;
import android.widget.Toast;

public class BackPressHandler {
    private Activity mainactivity;
    private long backPressedTime = 0;

    public BackPressHandler(Activity _activity) {
        mainactivity = _activity;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis()> backPressedTime + 1800){
            backPressedTime =System.currentTimeMillis();
            Toast.makeText(mainactivity, "종료하려면 뒤로가기 버튼을 한번 더 누르세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(System.currentTimeMillis() <= backPressedTime + 1800){
            mainactivity.finish();
        }
    }
}
