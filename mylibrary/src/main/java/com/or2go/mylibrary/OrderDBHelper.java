package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.OrderItem;

import java.util.ArrayList;

public class OrderDBHelper extends SQLiteOpenHelper {

    private static OrderDBHelper sInstance;

    public SQLiteDatabase orderDBConn;
    Context mContext;

    public OrderDBHelper(Context context)
    {
        super(context, "or2goOrderDB.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table orderinfo "+
                "( orderid text, reqid text, time DATETIME,type text, status INTEGER, vendor text, subtotal text, discount text, delicharge text, total text,paymode INTEGER,paystatus INTEGER, payid text," +
                "deliaddress text, deliplace text, delistatus INTEGER, daid text, daname text, dacontact text, pickotp text, custreq text, picktime DATETIME, lastupdate DATETIME,  UNIQUE(orderid) ON CONFLICT IGNORE)");
        db.execSQL("create table orderitems "+
                "(orderid text, itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS orderinfo");

        db.execSQL("DROP TABLE IF EXISTS orderitems");

        onCreate(db);
    }

    public void cleanup()
    {
        orderDBConn.execSQL("VACUUM");
    }


    public static synchronized OrderDBHelper getInstance(Context context, String extender) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new OrderDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    public void InitOrderDB()
    {
        orderDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getSalesDBConn()
    {
        return orderDBConn;
    }


    public boolean insertorder  (String orderid, String reqid, String type, String vend, Integer status, String time,
                                  String subtotal, String charge, String total,
                                  String discount, String addr, String deliloc,
                                  Integer paymode, String custreq/*, boolean accptcharge*/)
    {


        //orderid text, reqid text, time DATETIME,type text, status text, vendor text, subtotal text, discount text, delicharge text, total text,paymode text,paystatus text," +
        //"deliaddress  text, deliplace text, custreq text, picktime DATETIME, lastupdate DATETIME, acceptcharge

        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("orderid", orderid);
        contentValues.put("reqid", reqid);
        contentValues.put("type", type);
        contentValues.put("time", time);

        contentValues.put("status", status);
        contentValues.put("vendor", vend);

        contentValues.put("subtotal", subtotal);
        contentValues.put("discount", discount);
        contentValues.put("delicharge", charge);
        contentValues.put("total", total);

        contentValues.put("paymode", paymode);
        contentValues.put("paystatus", "Pending");       //Always 0 at at insert
        contentValues.put("deliaddress", addr);
        contentValues.put("deliplace", deliloc);
        contentValues.put("custreq", custreq);
        contentValues.put("picktime", "");
        contentValues.put("lastupdate", time);
        //contentValues.put("acceptcharge", (accptcharge)? 1 :0);

        long newid = orderDBConn.insert("orderinfo", null, contentValues);

        if(newid < 0)
            return false;
        else
            return true;


    }

    public boolean insertorderitem (String orderid,  String itemid, String itemname, String price, String priceunit, String quantity,String unit,String total, String disc)
    {

        ///"(id text, itemno text, itemname text, quantity text, orderunit text, discount text, itemtotal text)");

        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("orderid", orderid);
        contentValues.put("itemid", itemid);
        contentValues.put("itemname", itemname);
        contentValues.put("price", price);
        contentValues.put("priceunit", priceunit);
        contentValues.put("quantity", quantity);
        contentValues.put("orderunit", unit);
        contentValues.put("itemtotal", total);
        contentValues.put("discount", disc);

        long ret = orderDBConn.insert("orderitems", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean updateOrderStatus(String orderid, Integer status)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);

        orderDBConn.update("orderinfo", contentValues,"orderid="+orderid, null);

        return true;
    }

    public boolean updatePaymentInfo(Or2goOrderInfo ordinfo)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("status", ordinfo.getStatus());
        contentValues.put("paymode", ordinfo.oPayMode);
        contentValues.put("paystatus", ordinfo.getPayStatus());       //Always 0 at at insert
        //contentValues.put("payid", ordinfo.opay);

        orderDBConn.update("orderinfo", contentValues,"orderid="+ordinfo.getId(), null);

        return true;
    }

    public boolean updateDeliveryInfo(Or2goOrderInfo ordinfo)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("delistatus", ordinfo.getDeliveryStatus());
        contentValues.put("daid", ordinfo.oDeliveryAssistantId);
        contentValues.put("daname", ordinfo.oDeliveryAssistantName);       //Always 0 at at insert
        contentValues.put("dacontact", ordinfo.oDeliveryAssistantContact);

        orderDBConn.update("orderinfo", contentValues,"orderid="+ordinfo.getId(), null);

        return true;
    }

    public boolean updatePickupInfo(Or2goOrderInfo ordinfo)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("status", ordinfo.getStatus());
        contentValues.put("pickupotp", ordinfo.getPickupOTP());

        orderDBConn.update("orderinfo", contentValues,"orderid="+ordinfo.getId(), null);

        return true;
    }



    public int getOrderCount()
    {
        Cursor cursor;
        int count=0;

        cursor = orderDBConn.rawQuery("SELECT * FROM orderinfo ", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public boolean deleteOrder(String orderid)
    {

        int ret = orderDBConn.delete("orderitems", "orderid = ? ", new String[] { orderid });

        ret = orderDBConn.delete("orderinfo", "orderid = ? ", new String[] { orderid });

        return true;
    }


    public  ArrayList<Or2goOrderInfo> getOrderData() {
        ArrayList<Or2goOrderInfo> orderlist = new ArrayList<Or2goOrderInfo>();
        Cursor cursor;
        int count = 0;

        cursor = orderDBConn.rawQuery("SELECT * FROM orderinfo ", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {

                //orderid text, reqid text, time DATETIME,type text, status text, vendor text, subtotal text, discount text, delicharge text, total text,paymode text,paystatus text," +
                //"deliaddress  text, deliplace text, custreq text, picktime DATETIME, lastupdate DATETIME, acceptcharge

                String orderid = cursor.getString(cursor.getColumnIndex("orderid"));
                String reqid = cursor.getString(cursor.getColumnIndex("reqid"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                Integer type = cursor.getInt(cursor.getColumnIndex("type"));
                Integer status = cursor.getInt(cursor.getColumnIndex("status"));
                String vendor = cursor.getString(cursor.getColumnIndex("vendor"));
                String subtotal = cursor.getString(cursor.getColumnIndex("subtotal"));
                String discount = cursor.getString(cursor.getColumnIndex("discount"));
                String delicharge = cursor.getString(cursor.getColumnIndex("delicharge"));
                String total = cursor.getString(cursor.getColumnIndex("total"));
                Integer paymode = cursor.getInt(cursor.getColumnIndex("paymode"));
                Integer paystatus = cursor.getInt(cursor.getColumnIndex("paystatus"));
                String deliaddress = cursor.getString(cursor.getColumnIndex("deliaddress"));
                String deliplace = cursor.getString(cursor.getColumnIndex("deliplace"));
                String custreq = cursor.getString(cursor.getColumnIndex("custreq"));
                String picktime = cursor.getString(cursor.getColumnIndex("picktime"));
                String lastupdate = cursor.getString(cursor.getColumnIndex("lastupdate"));
                //Integer acceptcharge = cursor.getInt(cursor.getColumnIndex("acceptcharge"));

                Or2goOrderInfo orderdata = new Or2goOrderInfo(orderid,type, vendor, "", status, time,
                        subtotal, delicharge, total,
                        discount, deliaddress, deliplace,
                        paymode, custreq);
                //orderdata.setLocalId(Integer.parseInt(reqid));
                orderdata.oOr2goId = orderid;

                // TBD add paystatus, lastupdate, picktime

                orderlist.add(0,orderdata);


                //get items fro this order
                Cursor itemcursor = orderDBConn.rawQuery("SELECT * FROM orderitems WHERE orderid = '" + orderid + "'", null);
                int itemcount = itemcursor.getCount();
                if (itemcount > 0) {
                    ArrayList<OrderItem> itemList = new ArrayList<OrderItem>();
                    ;
                    itemcursor.moveToFirst();

                    for (int j = 0; j < itemcount; j++) {
                        //itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal
                        String itemid = itemcursor.getString(itemcursor.getColumnIndex("itemid"));
                        String itemname = itemcursor.getString(itemcursor.getColumnIndex("itemname"));
                        String price = itemcursor.getString(itemcursor.getColumnIndex("price"));
                        String priceunit = itemcursor.getString(itemcursor.getColumnIndex("priceunit"));
                        String quantity = itemcursor.getString(itemcursor.getColumnIndex("quantity"));
                        String orderunit = itemcursor.getString(itemcursor.getColumnIndex("orderunit"));
                        String itemtotal = itemcursor.getString(itemcursor.getColumnIndex("itemtotal"));

                        OrderItem saleitem = new OrderItem(Integer.parseInt(itemid), itemname, Float.valueOf(price), Float.valueOf(quantity), Integer.valueOf(orderunit), 0, 0);

                        itemList.add(saleitem);

                        itemcursor.moveToNext();
                    }

                    orderdata.setItemList(itemList);

                    itemcursor.close();
                }

                cursor.moveToNext();
            }
        }

        cursor.close();

        return orderlist;
    }



}
