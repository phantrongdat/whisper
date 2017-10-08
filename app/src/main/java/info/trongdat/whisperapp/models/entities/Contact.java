package info.trongdat.whisperapp.models.entities;

import android.util.Log;

/**
 * Created by Alone on 5/7/2017.
 */

public class Contact {
    String name;
    String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        Log.d("datt", "getPhoneNumber: +84");
        return phoneNumber.replaceAll(" ", "").replaceAll("-", "").replaceAll("\\+84", "0");
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return getName() + "\n" + getPhoneNumber();
    }
}
