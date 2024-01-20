package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.PubNotice;
import com.or2go.core.StoreLoginInfo;

import java.util.ArrayList;

public class PublicNoticeInfoDBHelper extends SQLiteOpenHelper {

    private static VendorDBHelper sInstance;

    Context mContext;
    SQLiteDatabase dbconn;

    public PublicNoticeInfoDBHelper(Context context)
    {
        super(context, "publicNoticeInfoDB.db", null, 2);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table publicnoticeinfo "+
                "(noticeid integer, name text,   UNIQUE(noticeid) ON CONFLICT IGNORE)" );
        db.execSQL("create table publicnotice "+
                "(noticeid integer, title text, notice text, noticeimg text, loctype integer, tagloc text, frequencytype integer," +
                "timeperiod text, endtime text, UNIQUE(noticeid) ON CONFLICT IGNORE)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion < 2){
            db.execSQL("CREATE TABLE IF NOT EXISTS publicnotice "+
                    "(noticeid integer, title text, notice text, noticeimg text," +
                    "loctype integer, tagloc text, frequencytype integer," +
                    "timeperiod text, endtime text, UNIQUE(noticeid) ON CONFLICT IGNORE)");
        }
    }

    public void InitDB()
    {
        dbconn = this.getWritableDatabase();
    }

    public boolean insertNoticeInfo (PubNotice pubNotice)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("noticeid", pubNotice.noticeID);
        contentValues.put("title", pubNotice.title);
        contentValues.put("notice", pubNotice.notice);
        contentValues.put("noticeimg", pubNotice.image);
        contentValues.put("loctype", pubNotice.targetloctype);
        contentValues.put("tagloc", pubNotice.targetloc);
        contentValues.put("frequencytype", pubNotice.frequencytype);
        contentValues.put("timeperiod", pubNotice.timeperiod);
        contentValues.put("endtime", pubNotice.end);

        long ret =  dbconn.insert("publicnotice", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearNoticeInfo()
    {
        dbconn.delete("publicnotice", null,null);
        return true;
    }

    public ArrayList<PubNotice> getNoticeInfo()
    {
        ArrayList<PubNotice> pubNoticeArrayList;
        Cursor cursor;
        int count = 0;
        Integer curid=0;

        cursor = dbconn.rawQuery("SELECT * FROM publicnotice", null);
        count = cursor.getCount();
        if (count <=0)
            return null;
        else {
            pubNoticeArrayList = new ArrayList<PubNotice>();
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {
                Integer noticeid = cursor.getInt(cursor.getColumnIndexOrThrow("noticeid"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String notice = cursor.getString(cursor.getColumnIndexOrThrow("notice"));
                String noticeimg = cursor.getString(cursor.getColumnIndexOrThrow("noticeimg"));
                Integer loctype = cursor.getInt(cursor.getColumnIndexOrThrow("loctype"));
                Integer frequencytype = cursor.getInt(cursor.getColumnIndexOrThrow("frequencytype"));
                String tagloc = cursor.getString(cursor.getColumnIndexOrThrow("tagloc"));
                String timeperiod = cursor.getString(cursor.getColumnIndexOrThrow("timeperiod"));
                String end = cursor.getString(cursor.getColumnIndexOrThrow("end"));
                PubNotice pubNotice = new PubNotice(noticeid, title, notice, noticeimg, loctype, frequencytype, tagloc, timeperiod, "", end, "");
                pubNoticeArrayList.add(pubNotice);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return pubNoticeArrayList;
    }

    public PubNotice getByNoticeId(String noticeId) {
        PubNotice result;
        Cursor cursor = dbconn.query(false, "publicnotice", new String[]{ "title", "notice", "noticeimg", "end"}, "noticeid=?",new String[]{ noticeId },
                null, null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String notice = cursor.getString(cursor.getColumnIndexOrThrow("notice"));
            String img = cursor.getString(cursor.getColumnIndexOrThrow("noticeimg"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("end"));
            result = new PubNotice(-1,
                    title, notice, img,
                    -1,-1,"","","",
                    time, "");
        }
        else
            return null;
        cursor.close();
        return result;
    }
}
