package com.ouslsmartactivitydiary.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_ACTIVITY_DIARY = "OuslSmartActivityDiary.db";

    //course related variables
    private static final String COURSE_TABLE = "my_courses";
    private static final String COURSE_ID = "COURSE_ID";
    private static final String COURSE_CODE = "COURSE_CODE";
    private static final String COURSE_NAME = "COURSE_NAME";

    //User profile details related database variables
    public static final String TABLE_USER_DETAILS = "user_details_table";
    public static final String USER_ID = "USER_ID"; //0
    public static final String USER_NAME = "USER_NAME"; //1
    public static final String USER_REG_NO = "USER_REG_NO"; //2
    public static final String USER_S_NUMBER = "USER_S_NUMBER"; //3
    public static final String USER_EMAIL = "USER_EMAIL"; //4
    public static final String USER_PROGRAMME = "USER_PROGRAMME"; //5
    public static final String USER_CENTER = "USER_CENTER"; //6
    public static final String USER_PHONE = "USER_PHONE"; //7
    public static final String USER_ADDRESS = "USER_ADDRESS"; //8

    //Settings table related database variables
    public static final String TABLE_SETTINGS = "setting_table";
    public static final String SETTING_ID = "SETTING_ID";
    public static final String SETTING_NAME = "SETTING_NAME";
    public static final String SETTING_STATE = "SETTING_STATE";

    //Notification table related database variables
    public static final String TABLE_NOTIFICATIONS = "notification_table";
    public static final String NOTIFICATION_ID = "ID"; //0
    public static final String NOTIFICATION_DATE = "DATE"; //1
    public static final String NOTIFICATION_TIME = "TIME"; //2
    public static final String NOTIFICATION_STATE = "STATE"; //3
    public static final String NOTIFICATION_TOPIC = "TOPIC"; //4
    public static final String NOTIFICATION_DETAILS = "DETAILS"; //5

    //Color table related database variables
    public static final String TABLE_COLORS = "color_table";
    public static final String COLOR_ID = "COLOR_ID";
    public static final String COLOR_NAME = "COLOR_NAME";
    public static final String COLOR_VALUE = "COLOR_VALUE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_ACTIVITY_DIARY, null, 1);
        SQLiteDatabase database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create my_courses table
        String COURSE_TABLE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + COURSE_TABLE + " (" +
                COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_CODE + " TEXT, " +
                COURSE_NAME + " TEXT)";
        db.execSQL(COURSE_TABLE_TABLE_QUERY);

        //create user details table
        String CREATE_USER_DETAILS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_USER_DETAILS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_NAME + " TEXT, " +
                USER_REG_NO + " TEXT, " +
                USER_S_NUMBER + " TEXT, " +
                USER_EMAIL + " TEXT, " +
                USER_PROGRAMME + " TEXT, " +
                USER_CENTER + " TEXT, " +
                USER_PHONE + " TEXT, " +
                USER_ADDRESS + " TEXT)";
        db.execSQL(CREATE_USER_DETAILS_TABLE_QUERY);

        //create settings table
        String CREATE_SETTINGS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + " (" +
                SETTING_ID + " INTEGER PRIMARY KEY, " +
                SETTING_NAME + " TEXT, " +
                SETTING_STATE + " INTEGER)";
        db.execSQL(CREATE_SETTINGS_TABLE_QUERY);

        //create notification table
        String CREATE_NOTIFICATION_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " (" +
                NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTIFICATION_DATE + " TEXT, " +
                NOTIFICATION_TIME + " TEXT, " +
                NOTIFICATION_STATE + " TEXT, " +
                NOTIFICATION_TOPIC + " TEXT, " +
                NOTIFICATION_DETAILS + " TEXT)";
        db.execSQL(CREATE_NOTIFICATION_TABLE_QUERY);

        //create settings table
        String CREATE_COLOR_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_COLORS + " (" +
                COLOR_ID + " INTEGER PRIMARY KEY, " +
                COLOR_NAME + " TEXT, " +
                COLOR_VALUE + " INTEGER)";
        db.execSQL(CREATE_COLOR_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLORS);
    }

    //////////////////////////// COURSE DETAILS /////////////////////////////////////

    //insert profile details
    public boolean insertCourse(String code, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_CODE, code);
        contentValues.put(COURSE_NAME, name);
        long result = db.insert(COURSE_TABLE, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get all profiles a from accounts table
    public Cursor getAllCourses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + COURSE_TABLE, null);
        return result;
    }

    //get color from colors table by Name
    public Cursor getCourseByCourseCode(String cc) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_CODE + " = ?", new String[]{cc});
        return result;
    }

    //delete all profile details by user ID
    public void deleteCourseByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(COURSE_TABLE, "COURSE_ID = ? ", new String[]{String.valueOf(id)});
        }
        db.close();
    }
    public boolean deleteCourseByCode(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(COURSE_TABLE, "COURSE_CODE = ? ", new String[]{code});
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //////////////////////////// USER DETAILS /////////////////////////////////////

    //insert profile details
    public boolean insertProfileDetails(String strName, String strRegNo, String strSNumber, String strEmail,
                                        String strProgramme, String strCenter, String strPhone, String strAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME, strName);
        contentValues.put(USER_REG_NO, strRegNo);
        contentValues.put(USER_S_NUMBER, strSNumber);
        contentValues.put(USER_EMAIL, strEmail);
        contentValues.put(USER_PROGRAMME, strProgramme);
        contentValues.put(USER_CENTER, strCenter);
        contentValues.put(USER_PHONE, strPhone);
        contentValues.put(USER_ADDRESS, strAddress);
        long result = db.insert(TABLE_USER_DETAILS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // update the profile details using account id
    public boolean updateProfileDetails(int id, String strName, String strRegNo, String strSNumber, String strEmail,
                                        String strProgramme, String strCenter, String strPhone, String strAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_NAME, strName);
        contentValues.put(USER_REG_NO, strRegNo);
        contentValues.put(USER_S_NUMBER, strSNumber);
        contentValues.put(USER_EMAIL, strEmail);
        contentValues.put(USER_PROGRAMME, strProgramme);
        contentValues.put(USER_CENTER, strCenter);
        contentValues.put(USER_PHONE, strPhone);
        contentValues.put(USER_ADDRESS, strAddress);
        long count = db.update(TABLE_USER_DETAILS, contentValues,
                "USER_ID = ?", new String[]{String.valueOf(id)});
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //delete all profile details by user ID
    public void deleteProfileDetails(int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (accountID > -1) {
            db.delete(TABLE_USER_DETAILS, "USER_ID = ? ", new String[]{String.valueOf(accountID)});
        }
        db.close();
    }

    //get all profiles a from accounts table
    public Cursor getAllProfiles() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_USER_DETAILS, null);
        return result;
    }

    //get all the data of specific user from accounts table
    public Cursor getUserData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_USER_DETAILS + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    ////////////////////////// SETTINGS ///////////////////////////////////////

    //add Image to user details table
    public void addSettings(int id, String name, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SETTING_ID, id);
        values.put(SETTING_NAME, name);
        values.put(SETTING_STATE, state);
        long count = db.insert(TABLE_SETTINGS, null, values);
        if (count > 0) {
        } else {
        }
    }

    // update settings
    public boolean updateSettings(int id, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_ID, id);
        contentValues.put(SETTING_STATE, state);
        long count = db.update(TABLE_SETTINGS, contentValues,
                "SETTING_ID = ?", new String[]{String.valueOf(id)});
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //get settings
    public Cursor getSetting(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + SETTING_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    ///////////////////////// NOTIFICATION TABLE /////////////////////////

    //add notifications
    public boolean addNotification(String date, String time, String state, String topic, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_DATE, date);
        contentValues.put(NOTIFICATION_TIME, time);
        contentValues.put(NOTIFICATION_STATE, state);
        contentValues.put(NOTIFICATION_TOPIC, topic);
        contentValues.put(NOTIFICATION_DETAILS, details);
        long result = db.insert(TABLE_NOTIFICATIONS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get all notifications from notification table
    public Cursor getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS, null);
        return result;
    }

    //delete all notifications
    public void deleteAllNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATIONS, null, null);
        db.close();
    }

    //get one notifications by ID
    public Cursor getNotificationByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    //update notification state by Notification ID
    public void updateNotificationState(int id, String state) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_STATE, state);
        db.update(TABLE_NOTIFICATIONS , values , "ID = ?" , new String[]{String.valueOf(id)});
    }

    //delete notification one by one in notifications
    public void deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_NOTIFICATIONS, "ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    ///////////////////////// COLOR TABLE /////////////////////////

    //add color
    public boolean addColor(int id, String name, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLOR_ID, id);
        contentValues.put(COLOR_NAME, name);
        contentValues.put(COLOR_VALUE, value);
        long result = db.insert(TABLE_COLORS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get all colors from colors table
    public Cursor getAllColors() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_COLORS, null);
        return result;
    }

    //update color value by Color ID
    public void updateColorValueByID(int id, int value) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLOR_VALUE, value);
        db.update(TABLE_COLORS , values , "COLOR_ID = ?" , new String[]{String.valueOf(id)});
    }

    //update color value by Color ID
    public void updateColorValueByName(String name, int value) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLOR_VALUE, value);
        db.update(TABLE_COLORS , values , "COLOR_NAME = ?" , new String[]{name});
    }

    //get color from colors table by ID
    public Cursor getColorByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_COLORS + " WHERE " + COLOR_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    //get color from colors table by Name
    public Cursor getColorByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_COLORS + " WHERE " + COLOR_NAME + " = ?", new String[]{name});
        return result;
    }

    //delete color by id
    public void deleteColorByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_COLORS, "COLOR_ID = ? ", new String[]{String.valueOf(id)});
        }
    }

}
