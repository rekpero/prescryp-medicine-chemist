package com.prescyber.prescryp.chemists.SessionManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.prescyber.prescryp.chemists.SigninActivity;

import java.util.HashMap;

public class UserSessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREFER_NAME = "AndroidPref";
    private static final String IS_MOB_LOGIN = "IsMobLoggedIn";
    public static final String KEY_NAME = "Name";
    public static final String KEY_MOB = "MobileNumber";
    public static final String KEY_PASS = "Password";

    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createUserLoginSession(String name, String mobnum, String password){
        editor.putBoolean(IS_MOB_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_MOB, mobnum);
        editor.putString(KEY_PASS, password);
        editor.commit();
    }

    public void checkLogin(){
        if (!this.isLoggedIn()){
            Intent i = new Intent(_context, SigninActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOB, pref.getString(KEY_MOB, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, SigninActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_MOB_LOGIN, false);
    }

}
