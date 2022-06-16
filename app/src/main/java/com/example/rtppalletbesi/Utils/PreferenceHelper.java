package com.example.rtppalletbesi.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    SharedPreferences sharedPreferences;
    public PreferenceHelper(Context ctx) {
        if(sharedPreferences == null){
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        }
    }

    public void setGroup(String group){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("group", group);
        editor.apply();
    }
    public String getGroup() {
        String values;
        values = sharedPreferences.getString("group", "PACK A");
        return values;
    }
}
