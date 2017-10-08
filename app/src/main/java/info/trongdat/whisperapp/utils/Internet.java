package info.trongdat.whisperapp.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;

import info.trongdat.whisperapp.models.entities.User;
import info.trongdat.whisperapp.presenters.UserPresenter;
import info.trongdat.whisperapp.presenters.services.AppListener;
import io.socket.client.IO;
import io.socket.client.Socket;

import static info.trongdat.whisperapp.utils.Constants.SERVER_URL;

/**
 * Created by Admin on 4/8/2017.
 */

public class Internet {
    Context context;
    private static Socket socket = null;
    static UserPresenter userPresenter;
    static User user;

    public static void initSocket(Context context) {
        try {
            userPresenter = new UserPresenter(context);
            user = userPresenter.getSession();
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = false;
            options.query = "userId=" + user.getUserID();

            socket = IO.socket(SERVER_URL, options);
            if (!socket.connected())
                socket.connect();
            context.startService(new Intent(context, AppListener.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Internet(Context context) {
        this.context = context;
    }

    public boolean getState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
            return true;
        return false;
    }

    public static Socket getIOSocket(Context context) {
        try {
            if (socket == null || !socket.connected()) {
                initSocket(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            initSocket(context);
        }
        return socket;
    }

    public Location myLocation() {
        Location locationGPS = null, locationNet = null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (locationGPS != null) return locationGPS;
        return locationNet;
    }

    public double distance(Location location) {
        return myLocation().distanceTo(location);
    }

    public String distanceTo(Location location) {
        double distance = distance(location);
        if (distance < 1000) return (int) distance + " m";
        else return round((distance / 1000), 1) + " km";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
