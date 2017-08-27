package com.example.android.firebase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{

    static String FRIENDS_DATABASE = "friends_database";
    static String TABLE_NAME = "friends_table";
    static String EMAIL = "email";
    static String NAME = "name";
    static String IMAGE = "image";



    public DatabaseHelper(Context context) {
        super(context,FRIENDS_DATABASE,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + "(email TEXT PRIMARY KEY,name TEXT,image BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addUser(String user_email, String user_name, byte[] image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL,user_email);
        values.put(NAME,user_name);
        values.put(IMAGE,image);
        long r = db.insert(TABLE_NAME,null,values);
        return r;
    }

    public Cursor getUser()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return c;
    }

    public void deleteUser(String user_email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"email = '" + user_email + "'",null);
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }
}
