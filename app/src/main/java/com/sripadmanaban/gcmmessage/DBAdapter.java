package com.sripadmanaban.gcmmessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sripadmanaban on 1/22/2015.
 */
public class DBAdapter
{
    private static final String KEY_ROW_ID = "_id";
    private static final String KEY_FIRST_NAME = "FirstName";
    private static final String KEY_LAST_NAME = "LastName";
    private static final String KEY_EMAIL = "Email";

    private static final String DATABASE_NAME = "MyDatabase";
    private static final String DATABASE_TABLE = "Contacts";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_TABLE =
            "CREATE TABLE contacts(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "FirstName TEXT NOT NULL, LastName TEXT NOT NULL, Email TEXT NOT NULL);";


    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    private static DBAdapter adapter;

    public static DBAdapter getInstance(Context context)
    {
        if(adapter == null)
        {
            adapter = new DBAdapter(context);
        }

        return adapter;
    }

    private DBAdapter(Context context)
    {
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper
    {

        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS Contacts");
        }
    }

    public DBAdapter open()
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }

    public long insertContact(String firstName, String lastName, String email)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FIRST_NAME, firstName);
        initialValues.put(KEY_LAST_NAME, lastName);
        initialValues.put(KEY_EMAIL, email);

        return db.insert(DATABASE_TABLE, null, initialValues);
    }



}
