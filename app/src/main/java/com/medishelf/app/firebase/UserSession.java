package com.medishelf.app.firebase;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "MediShelfSession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public UserSession(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String email, String userType) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, "customer");
    }

    public boolean isAdmin() {
        return "admin".equals(getUserType());
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}