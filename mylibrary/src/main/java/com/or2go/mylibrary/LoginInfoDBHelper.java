package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.StoreLoginInfo;

import java.util.ArrayList;

public class LoginInfoDBHelper extends SQLiteOpenHelper {
    private static VendorDBHelper sInstance;

    public SQLiteDatabase loginDBConn;
    Context mContext;


    public LoginInfoDBHelper(Context context)
    {
        super(context, "loginInfo.db", null, 1);
        mContext = context;

        initStoreDB();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table logininfotbl" +
                "(vendorid text, storeid text, loginmode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void cleanup()
    {
        loginDBConn.execSQL("VACUUM");
    }

    public void initStoreDB()
    {
        loginDBConn = this.getWritableDatabase();
    }

    public SQLiteDatabase getStoreDBConn()
    {
        return loginDBConn;
    }


    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = loginDBConn.rawQuery("SELECT * FROM logininfotbl", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public boolean insertLoginStore (StoreLoginInfo storeLoginInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vendorid", storeLoginInfo.getVendorid());
        contentValues.put("storeid", storeLoginInfo.getStoreid());
        contentValues.put("loginmode", storeLoginInfo.getLoginmode());
        long ret = loginDBConn.insert("logininfotbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean deleteStoreLoginTable() {
        //loginDBConn.execSQL("Delete FROM logininfotbl");
        loginDBConn.delete("logininfotbl", null, null);
        return true;
    }

    public ArrayList<StoreLoginInfo> getStoresLoginData() {
        ArrayList<StoreLoginInfo> storeList;
        Cursor cursor;
        int count = 0;
        cursor = loginDBConn.rawQuery("SELECT * FROM logininfotbl", null);
        count = cursor.getCount();
        if (count <=0)
            return null;
        else {
            storeList = new ArrayList<StoreLoginInfo>();
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {
                String Vid = cursor.getString(cursor.getColumnIndexOrThrow("vendorid"));
                String Sid = cursor.getString(cursor.getColumnIndexOrThrow("storeid"));
                Integer LMode = cursor.getInt(cursor.getColumnIndexOrThrow("loginmode"));
                StoreLoginInfo storeLoginInfo = new StoreLoginInfo(Vid, Sid, LMode);
                storeList.add(storeLoginInfo);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return storeList;
    }

    public StoreLoginInfo existsStoreId(String storeId) {
        StoreLoginInfo result;
        Cursor cursor = loginDBConn.query(false, "logininfotbl", new String[]{ "vendorid", "storeid", "loginmode"}, "storeid=?",new String[]{ storeId },
                null, null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            String Vid = cursor.getString(cursor.getColumnIndexOrThrow("vendorid"));
            String Sid = cursor.getString(cursor.getColumnIndexOrThrow("storeid"));
            Integer LMode = cursor.getInt(cursor.getColumnIndexOrThrow("loginmode"));
            result = new StoreLoginInfo(Vid, Sid, LMode);
        }
        else
            return null;
        cursor.close();

        return result;
    }
}
