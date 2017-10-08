package info.trongdat.whisperapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import info.trongdat.whisperapp.models.entities.User;

import static android.content.Context.MODE_PRIVATE;
import static info.trongdat.whisperapp.utils.Constants.SESSION_ADDRESS;
import static info.trongdat.whisperapp.utils.Constants.SESSION_AVATAR;
import static info.trongdat.whisperapp.utils.Constants.SESSION_BIRTH_DAY;
import static info.trongdat.whisperapp.utils.Constants.SESSION_EMAIL;
import static info.trongdat.whisperapp.utils.Constants.SESSION_ID;
import static info.trongdat.whisperapp.utils.Constants.SESSION_INTRO;
import static info.trongdat.whisperapp.utils.Constants.SESSION_LOCATION;
import static info.trongdat.whisperapp.utils.Constants.SESSION_NAME;
import static info.trongdat.whisperapp.utils.Constants.SESSION_USERNAME;
import static info.trongdat.whisperapp.utils.Constants.WHISPER_TEMP_DATA;
import static info.trongdat.whisperapp.utils.Utils.isEmailValid;
import static info.trongdat.whisperapp.utils.Utils.isPasswordValid;

/**
 * Created by Admin on 4/8/2017.
 */

public class UserModel {
    UserModelListener callback;
    Context context;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public UserModel(Context context) {
        this.context = context;
    }

    public UserModel(Context context, UserModelListener callback) {
        this.callback = callback;
        this.context = context;
    }

    public boolean createSession(User user) {
        editor.putInt(SESSION_ID, user.getUserID());
        editor.putString(SESSION_USERNAME, user.getPhoneNumber().trim());
        editor.putString(SESSION_AVATAR, user.getAvatar().trim());
        editor.putString(SESSION_ADDRESS, user.getAddress().trim());
        editor.putString(SESSION_BIRTH_DAY, user.getBirthDay().trim());
        editor.putString(SESSION_EMAIL, user.getEmail().trim());
        editor.putString(SESSION_NAME, user.getFullName().trim());
        editor.putString(SESSION_INTRO, user.getIntro().trim());
        editor.putString(SESSION_LOCATION, user.getLastLocation().trim());

        Log.d("dattt", "createSession: " + user.getAvatar().trim());
        editor.commit();
        return true;
    }


    public int getSessionID() {
        sharedPreferences = context.getSharedPreferences(WHISPER_TEMP_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int sessionID = sharedPreferences.getInt(SESSION_ID, 0);
        return sessionID;
    }

    public User getSession() {
        sharedPreferences = context.getSharedPreferences(WHISPER_TEMP_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int sessionID = sharedPreferences.getInt(SESSION_ID, 0);
        String name = sharedPreferences.getString(SESSION_NAME, "");
        String phone = sharedPreferences.getString(SESSION_USERNAME, "");
        String avatar = sharedPreferences.getString(SESSION_AVATAR, "");
        String address = sharedPreferences.getString(SESSION_ADDRESS, "");
        String birthDay = sharedPreferences.getString(SESSION_BIRTH_DAY, "");
        String email = sharedPreferences.getString(SESSION_EMAIL, "");
        String intro = sharedPreferences.getString(SESSION_INTRO, "");
        String lastLocation = sharedPreferences.getString(SESSION_LOCATION, "");
        Log.d("datttttt", "getSession: " + avatar);
        return new User(sessionID, phone, "", name, avatar, address, birthDay, email, intro, lastLocation);
    }


    public String attemptSession() {
        // WHISPER_TEMP_DATA is file name.
        sharedPreferences = context.getSharedPreferences(WHISPER_TEMP_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String session = sharedPreferences.getString(SESSION_USERNAME, "");
        if (session.equals("")) {
            callback.sessionFail();
        } else callback.sessionSuccess(session);
        return session;
    }

    public boolean attemptSignUp(String phoneNumber, String password, String rePassword, String fullName, String address,
                                 String birthDay, String email, String intro) {
        boolean cancel = false;
        String err = "";
        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumber)) {
            err += "Phone number is required";
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            if (cancel) err += "\n";
            err += "This password is too short";
            cancel = true;
        }
        if (TextUtils.isEmpty(rePassword) || !isPasswordValid(rePassword)) {
            if (cancel) err += "\n";
            err += "This RePassword is too short";
            cancel = true;
        }
        if (!password.equals(rePassword)) {
            if (cancel) err += "\n";
            err += "RePassword not correct";
            cancel = true;
        }

        if (TextUtils.isEmpty(fullName)) {
            if (cancel) err += "\n";
            err += "This FullName is required";
            cancel = true;
        }

        if (TextUtils.isEmpty(birthDay)) {
            if (cancel) err += "\n";
            err += "This BirthDay is required";
            cancel = true;
        }

        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            if (cancel) err += "\n";
            err += "This Email is empty or invalid";
            cancel = true;
        }


        if (cancel) callback.attemptFail(err);
        else callback.attemptSuccess();

        return !cancel;
    }

    public boolean attemptLogin(String phoneNumber, String password) {
        boolean cancel = false;
        String err = "";
        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumber)) {
            err += "Phone number is required";
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            if (cancel) err += "\n";
            err += "This password is too short";
            cancel = true;
        }

        if (cancel) callback.attemptFail(err);
        else callback.attemptSuccess();

        return !cancel;
    }
}
