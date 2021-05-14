package com.ashtech.sahayak_updated.Databases;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashMap;


public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    //Sesion Name
    public static final String SESSION_USERSESSION="userLoginSession";
    public static final String SESSION_REMEMBERME="rememberMe";
    public static final String SESSION_LOCATION="locationSession";
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONENUMBER = "phoneNum";
    public static final String KEY_EMAIL = "email";
    //LOCATION VARIABLE
    public static final String KEY_LATITUDE="NA";
    public static final String KEY_LONGITUDE="NA";
    // Remember Me variables
    private static final String IS_REMEMBERME="Is RememberMe";
    public static final String KEY_SESSIONPHONENUMBER="phoneNumber";
    public static final String KEY_SESSIONPASSWORD="password";

    /* User
    Login SESSION
     */

    public SessionManager(Context _context,String sessionName) {
        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }


    public void createLoginSession(String fullName, String userName, String password, String phoneNum, String email) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_PHONENUMBER, phoneNum);
        editor.putString(KEY_EMAIL, email);

        editor.commit();

    }

    public HashMap<String, String> getUserDetailfromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_PHONENUMBER, usersSession.getString(KEY_PHONENUMBER, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));

        return userData;


    }

    public boolean checkLogin() {
        if (usersSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();

    }

    /*
    RememberME
    Session functions
     */

    public void createRememberMeSession(String password, String phoneNum) {
        editor.putBoolean(IS_REMEMBERME, true);
        editor.putString(KEY_SESSIONPASSWORD, password);
        editor.putString(KEY_SESSIONPHONENUMBER, phoneNum);


        editor.commit();

    }

    public HashMap<String, String> getRememberMeDetailfromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();


        userData.put(KEY_SESSIONPHONENUMBER, usersSession.getString(KEY_SESSIONPHONENUMBER, null));
        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD, null));


        return userData;


    }

    public boolean checkRememberMe() {
        if (usersSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }


// Location Session
public void createLocationSession(String latitude, String longitude) {

    editor.putString(KEY_LATITUDE, latitude );
    editor.putString(KEY_USERNAME, longitude);
    editor.commit();

}

    public HashMap<String, String> getLocationDetailfromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();


        userData.put(KEY_LATITUDE, usersSession.getString(KEY_LATITUDE, null));
        userData.put(KEY_LONGITUDE, usersSession.getString(KEY_LONGITUDE, null));


        return userData;


    }


}
