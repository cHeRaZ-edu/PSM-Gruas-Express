package com.psm.edu.psm_gruas_express.sharedprefences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.psm.edu.psm_gruas_express.R;

import static android.content.Context.MODE_PRIVATE;

public class SharedUtil {
    public static final String FILE_USER = "user_pref";
    public static final String USER_NICKNAME = "user_name";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_PROVIDER = "user_provider";
    public static final String BACKGROUND_COLOR_LOGIN = "background_color_login";

    public static String getUserNickname(Context context) {
       SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
       return prefs.getString(USER_NICKNAME,"");
    }

    public static void setUserNickname(Context context, String nickname) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        prefs.edit().putString(USER_NICKNAME, nickname).apply();
    }

    public static String getUserPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        return prefs.getString(USER_PASSWORD,"");
    }

    public static void setUserPassword(Context context, String password) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        prefs.edit().putString(USER_PASSWORD, password).apply();
    }

    public static String getUserProvider(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        return prefs.getString(USER_PROVIDER,"");
    }

    public static void setUserProvider(Context context, String provider) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        prefs.edit().putString(USER_PROVIDER,provider).apply();
    }

    public static void setBackgroundColorLogin(Context context,String color) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        prefs.edit().putString(BACKGROUND_COLOR_LOGIN,color).apply();
    }

    public static int getBackgroundColorLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FILE_USER, MODE_PRIVATE);
        String color = prefs.getString(BACKGROUND_COLOR_LOGIN,"");
        if(color.equals(""))
            return context.getResources().getColor(R.color.background_color);

        return Color.parseColor(color);
    }

}
