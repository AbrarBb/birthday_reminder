package com.example.birthday_reminder;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BirthdayDB extends SQLiteOpenHelper {

    public BirthdayDB(Context context) {
        super(context, "BDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE dobinfo  ("
                + "ID TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "phone TEXT,"
                + "dob INT"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");
        // db.execSQL("ALTER table my_table  ......");
    }
    public void insertDOBInfo(String ID, String name, String phone, long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("name", name);
        cols.put("phone", phone);
        cols.put("dob", date);
        db.insert("dobinfo", null ,  cols);
        db.close();
    }
    public void updateDOBInfo(String ID, String name, String phone, long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("name", name);
        cols.put("phone", phone);
        cols.put("dob", date);
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