package com.example.birthday_reminder;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BirthdayDB extends SQLiteOpenHelper {

    public BirthdayDB(Context context) {
        super(context, "BDB.db", null, 3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE dobinfo  ("
                + "ID TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "phone TEXT,"
                + "dob INT,"
                + "image TEXT,"
                + "user_id TEXT"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE dobinfo ADD COLUMN image TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE dobinfo ADD COLUMN user_id TEXT");
        }
    }
    public void insertDOBInfo(String ID, String name, String phone, long date, String image, String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("name", name);
        cols.put("phone", phone);
        cols.put("dob", date);
        cols.put("image", image);
        cols.put("user_id", user_id);
        db.insert("dobinfo", null ,  cols);
        db.close();
    }
    public void updateDOBInfo(String ID, String name, String phone, long date, String image, String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("name", name);
        cols.put("phone", phone);
        cols.put("dob", date);
        cols.put("image", image);
        cols.put("user_id", user_id);
        db.update("dobinfo", cols, "ID=?", new String[ ] {ID} );
        db.close();
    }
    public void deleteDOBInfo(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("dobinfo", "ID=?", new String[ ] {ID} );
        db.close();
    }
    public Cursor selectDOBs(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
