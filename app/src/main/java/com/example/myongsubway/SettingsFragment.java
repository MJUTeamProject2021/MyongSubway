package com.example.myongsubway;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;



public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences prefs;//설정 저장
    Preference ringtonePreference;//벨소리

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String s) {

        setPreferencesFromResource(R.xml.fragment_settings, s);
        ringtonePreference = (ListPreference)findPreference("ringtone_list");//벨소리 설정
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());//sharedPreference

        if(!prefs.getString("ringtone_list", "").equals("")) {//불러옴
            ringtonePreference.setSummary(prefs.getString("ringtone_list", "기본"));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);//설정 변경 리스너
    }//OnCreatePreferences

    SharedPreferences.OnSharedPreferenceChangeListener prefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {//설정 변경 리스너
            if(s.equals("ringtone_list")) {
                ringtonePreference.setSummary(prefs.getString("ringtone_list", "기본"));//키에서 찾아서 리턴
                Toast myToast = Toast.makeText(getActivity(),prefs.getString("ringtone_list", "기본"), Toast.LENGTH_SHORT);
                myToast.show();
            }
            else if(prefs.getBoolean("ring", true)){//.벨소리 확인 코드 삭제 가능
                if(prefs.getString("ringtone_list","").equals("기본")){
                    MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.clock);// ** listPreference에서 알림 파일 연결 어떻게?
                    player.start();
                }else if(prefs.getString("ringtone_list","").equals("카톡")){
                    MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.kakao);// ** listPreference에서 알림 파일 연결 어떻게?
                    player.start();
                }else if(prefs.getString("ringtone_list","").equals("카톡카톡")){
                    MediaPlayer player = MediaPlayer.create(getActivity(), R.raw.kakaokakao);// ** listPreference에서 알림 파일 연결 어떻게?
                    player.start();
                }
            }
            else if(prefs.getBoolean("sneeze",true)){//진동확인 코드 삭제 가능
                Vibrator vib = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                Toast myToast = Toast.makeText(getActivity(),"진동", Toast.LENGTH_SHORT);
                myToast.show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vib.vibrate(1000);
                }
            }
           //((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };
}