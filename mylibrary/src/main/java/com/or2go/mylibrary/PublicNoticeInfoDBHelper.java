package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PublicNoticeInfoDBHelper extends SQLiteOpenHelper {

    private static VendorDBHelper sInstance;

    Context mContext;

    public PublicNoticeInfoDBHelper(Context context)
    {
        super(context, "publicNoticeInfoDB.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table publicnoticeinfo "+
                "(noticeid integer, name text,   UNIQUE(noticeid) ON CONFLICT IGNORE)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public boolean insertNoticeInfo (Integer id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("noticeid", id);

        SQLiteDatabase dbconn = this.getWritableDatabase();
        long ret =  dbconn.insert("publicnoticeinfo", null, contentValues);
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearNoticeInfo()
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        dbconn.delete("publicnoticeinfo", null,null);
        dbconn.close();

        return true;
    }

    public Integer getNoticeInfo()
    {
        Cursor cursor;
        int count = 0;
        Integer curid=0;

        SQLiteDatabase dbconn = this.getWritableDatabase();
        cursor = dbconn.rawQuery("SELECT * FROM publicnoticeinfo", null);
        count = cursor.getCount();
        if (count >0)
        {
            cursor.moveToFirst();
            curid = cursor.getInt(cursor.getColumnIndex("noticeid"));
        }

        dbconn.close();
        return curid;
    }

}
