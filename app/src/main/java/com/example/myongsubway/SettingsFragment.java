package com.example.myongsubway;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;



public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences prefs;//설정 저장
    ListPreference ringtonePreference;//벨소리

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
                
            }

           //((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };
}