package com.example.semproject;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_ID = "user_id";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_ROLE = "user_role";

    private static User currentUser;

    // 🔐 Save user data to memory + SharedPreferences
    public static void saveUser(Context context, User user) {
        currentUser = user;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    // 👤 Retrieve user session (from memory or storage)
    public static User getCurrentUser(Context context) {
        if (currentUser != null) return currentUser;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int id = prefs.getInt(KEY_ID, -1);
        String name = prefs.getString(KEY_NAME, null);
        String email = prefs.getString(KEY_EMAIL, null);
        String password = prefs.getString(KEY_PASSWORD, null);
        String role = prefs.getString(KEY_ROLE, null);

        if (id != -1 && name != null && email != null && password != null && role != null) {
            currentUser = new User(id, name, email, password, role);
            return currentUser;
        }

        return null;
    }

    // 🔓 Check if user is logged in
    public static boolean isLoggedIn(Context context) {
        return getCurrentUser(context) != null;
    }

    // 🔄 Get current user role
    public static String getUserRole(Context context) {
        User user = getCurrentUser(context);
        return user != null ? user.getRole() : null;
    }
    public static void clearSession(Context context) {
        currentUser = null;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
