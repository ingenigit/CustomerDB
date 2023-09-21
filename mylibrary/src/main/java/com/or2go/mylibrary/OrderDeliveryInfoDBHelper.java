package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;

import com.or2go.core.OrderDeliveryInfo;
import com.or2go.core.OrderItem;

import java.util.ArrayList;

public class OrderDeliveryInfoDBHelper extends SQLiteOpenHelper {

    private static DeliveryAddressDBHelper sInstance;

    public SQLiteDatabase orderDeliInfoDBConn;
    Context mContext;

    public OrderDeliveryInfoDBHelper(Context context)
    {
        super(context, "OrderDeliveryInfo.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table orderdeliveryinfo "+
                "(orderid text, delistatus int, daid text, daname text, dacontact text, delitime DATETIME, comment text, UNIQUE(orderid) ON CONFLICT IGNORE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS deliveryaddr");


        onCreate(db);
    }

    public void cleanup()
    {
        orderDeliInfoDBConn.execSQL("VACUUM");
    }


    public static synchronized DeliveryAddressDBHelper getInstance(Context context, String extender) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DeliveryAddressDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public void InitDB()
    {
        orderDeliInfoDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getDeliveryAddrDBConn()
    {
        return orderDeliInfoDBConn;
    }

    public boolean insertdeliinfo (String ordid, Integer sts, String daid, String name, String cont,
                                    String delitime, String desc)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("orderid", ordid);
        contentValues.put("delistatus", sts);
        contentValues.put("daid", daid);
        contentValues.put("daname", name);
        contentValues.put("dacontact", cont);
        contentValues.put("delitime", delitime);
        contentValues.put("comment", desc);

        long newid = orderDeliInfoDBConn.insert("orderdeliveryinfo", null, contentValues);

        if(newid < 0)
            return false;
        else
            return true;
    }


    public OrderDeliveryInfo getOrderDeliveryInfo(String orderid)
    {
        Cursor cursor = orderDeliInfoDBConn.rawQuery("SELECT * FROM orderitems WHERE orderid = '" + orderid + "'", null);
        int itemcount = cursor.getCount();
        if (itemcount > 0) {
            cursor.moveToFirst();

            String daid = cursor.getString(cursor.getColumnIndexOrThrow("daid"));
            String daname = cursor.getString(cursor.getColumnIndexOrThrow("daname"));
            String dacontact = cursor.getString(cursor.getColumnIndexOrThrow("dacontact"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("delitime"));
            Integer sts = cursor.getInt(cursor.getColumnIndexOrThrow("delistatus"));

            cursor.close();

            OrderDeliveryInfo deliinfo = new OrderDeliveryInfo(orderid, daid, daname, dacontact, time, sts);

            return deliinfo;
        }

        return null;
    }

}
