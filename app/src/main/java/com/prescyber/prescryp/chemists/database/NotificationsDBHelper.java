package com.prescyber.prescryp.chemists.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prescyber.prescryp.chemists.Model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationsDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "PrescrypPatientNotification";
    // Contacts table name
    private static final String TABLE_NAME = "PatientNotificationList";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOTIFICATION_TITLE = "notification_title";
    private static final String KEY_NOTIFICATION_DESCRIPTION = "notification_description";
    private static final String KEY_NOTIFICATION_DATE = "notification_date";

    public NotificationsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTIFICATION_TITLE + " TEXT,"
                + KEY_NOTIFICATION_DESCRIPTION + " TEXT,"
                + KEY_NOTIFICATION_DATE + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Creating tables again
        onCreate(db);
    }

    public List<NotificationItem> getNotificationList(){

        List<NotificationItem> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                result.add(new NotificationItem( cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public void addNotificationItem(NotificationItem chemistLatLngItem){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues(); // Shop Name
        values.put(KEY_NOTIFICATION_TITLE, chemistLatLngItem.getTitle()); // Shop Phone Number
        values.put(KEY_NOTIFICATION_DESCRIPTION, chemistLatLngItem.getDescription());
        values.put(KEY_NOTIFICATION_DATE, chemistLatLngItem.getDate());


        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }



    public void deleteAllNotification(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

}
