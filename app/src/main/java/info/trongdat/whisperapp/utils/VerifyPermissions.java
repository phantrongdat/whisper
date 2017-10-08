package info.trongdat.whisperapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static info.trongdat.whisperapp.utils.Constants.TAG;

/**
 * Created by Alone on 5/14/2017.
 */

public class VerifyPermissions {
    private static final int REQUEST_CODE = 1;
    private static String[] PERMISSIONS_LOCATION = {
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION
    };
    private static String[] PERMISSIONS_CONTACT = {
            READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    

    //persmission method.
    public static void verify(Activity activity) {
        // Check if we have read or write permission
        int accessFineLocation = ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
        int accessCoarseLocation = ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION);
        int readContacts = ActivityCompat.checkSelfPermission(activity, READ_CONTACTS);

        if (accessFineLocation != PackageManager.PERMISSION_GRANTED || accessCoarseLocation != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_CODE
            );
        }
        if (readContacts != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "verify: not grant read contacts");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, READ_CONTACTS)) {
                Toast.makeText(activity, "Contact read permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivityForResult(intent, 789);
            }else {
                activity.requestPermissions(new String[]{READ_CONTACTS}, 123);
            }
            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_CONTACT,
//                    REQUEST_CODE
//            );
        }
    }
}