package info.trongdat.whisperapp.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;

import info.trongdat.whisperapp.models.entities.Contact;
import info.trongdat.whisperapp.models.entities.User;

/**
 * Created by Alone on 5/7/2017.
 */

public class FriendModel {
    HashSet<Contact> tmps;
    ArrayList<Contact> list;
    ArrayList<User> suggestions;
    Context context;


    public FriendModel(Context context) {
        this.context = context;
    }

    public ArrayList<Contact> getContacts() {
        tmps = new HashSet<Contact>();
        list = new ArrayList<Contact>();
        String phoneNumber = null;
        String name = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String _ID = ContactsContract.Contacts._ID;

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                while (phoneCursor.moveToNext())
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

                phoneCursor.close();
                // Add the contact to the ArrayList
                tmps.add(new Contact(name, phoneNumber));
            }
            cursor.close();
        }
        list.addAll(tmps);
        return list;
    }

    public ArrayList<User> getSuggestion() {

        return suggestions;
    }
}
