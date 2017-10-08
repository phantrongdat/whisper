package info.trongdat.whisperapp.models.entities;

import java.io.Serializable;

/**
 * Created by Alone on 3/13/2017.
 */

public class User implements Serializable {
    int userID;
    String phoneNumber, password, fullName, avatar, address, birthDay, email, intro;
    String lastLocation;

    public User() {
    }

    public User(int userID, String phoneNumber, String password, String fullName, String address, String birthDay, String email, String intro) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.address = address;
        this.birthDay = birthDay;
        this.email = email;
        this.intro = intro;
    }

    public User(int userID, String phoneNumber, String password, String fullName, String avatar, String address, String birthDay, String email, String intro) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.address = address;
        this.birthDay = birthDay;
        this.email = email;
        this.intro = intro;
    }

    public User(int userID, String phoneNumber, String password, String fullName, String avatar, String address, String birthDay, String email, String intro, String lastLocation) {
        this.userID = userID;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.fullName = fullName;
        this.avatar = avatar;
        this.address = address;
        this.birthDay = birthDay;
        this.email = email;
        this.intro = intro;
        this.lastLocation = lastLocation;
    }

    public int getUserID() {
        return userID;
    }

    public User setUserID(int userID) {
        this.userID = userID;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public User setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public User setBirthDay(String birthDay) {
        this.birthDay = birthDay;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getIntro() {
        return intro;
    }

    public User setIntro(String intro) {
        this.intro = intro;
        return this;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
