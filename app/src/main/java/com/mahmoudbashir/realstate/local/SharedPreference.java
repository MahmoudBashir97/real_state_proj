package com.mahmoudbashir.realstate.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    Context context;
    private static final String SHARED_PREF_USER = "REALSTATE_APP";

    private static SharedPreference sharedPrefranceManager;

    private SharedPreference(Context context) {
        this.context = context;
    }

    public synchronized static SharedPreference getInastance(Context context) {
        if (sharedPrefranceManager == null) {
            sharedPrefranceManager = new SharedPreference(context);
        }
        return sharedPrefranceManager;
    }

    public void save_InfoData(String full_name,String email,String userType,String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //editor.clear();
        editor.putString("f_name", full_name);
        editor.putString("email", email);
        editor.putString("userType", userType);
        editor.putString("userId", userId);
        editor.putBoolean("userLogged", true);
        editor.apply();
    }


    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("userLogged", false);
    }

    public boolean getAlarmStatus() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("alarm_status", true);
    }


    public String getFullName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("f_name", "");
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", "");
    }

    public String getUserType() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userType", "User");
    }
    public String getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }


    public void clearUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLogged", false);
        editor.clear();
        editor.apply();
        editor.commit();
    }

}