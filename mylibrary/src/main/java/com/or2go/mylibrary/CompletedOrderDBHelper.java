package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.Or2goOrderInfo;
import com.or2go.core.OrderHistoryInfo;
import com.or2go.core.OrderItem;

import java.util.ArrayList;

public class CompletedOrderDBHelper extends SQLiteOpenHelper {
    private static OrderDBHelper sInstance;

    public SQLiteDatabase completedOrderDBConn;
    Context mContext;

    public CompletedOrderDBHelper(Context context)
    {
        super(context, "or2goCompletedOrderDB.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table orderinfo "+
                "( orderid text, ordertime DATETIME,type text, status INTEGER, store text, subtotal text, discount text, delicharge text, total text,paymode INTEGER," +
                "deliaddress  text, custreq text, comptime DATETIME,  UNIQUE(orderid) ON CONFLICT IGNORE)");
        db.execSQL("create table orderitems "+
                "(orderid text, itemid integer, itemname text, price text, quantity text, orderunit integer, priceid integer,skuid integer, discount text, itemtotal text)");

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
        completedOrderDBConn.execSQL("VACUUM");
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
        completedOrderDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getSalesDBConn()
    {
        return completedOrderDBConn;
    }

    public boolean insertCompletedOrder(Or2goOrderInfo ordinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("orderid", ordinfo.getId());
        contentValues.put("type", ordinfo.getType());
        contentValues.put("ordertime", ordinfo.getOrderTime());

        contentValues.put("status", ordinfo.getStatus());
        contentValues.put("store", ordinfo.getStoreId());

        contentValues.put("subtotal", ordinfo.getSubTotal());
        contentValues.put("discount", ordinfo.getDiscount());
        contentValues.put("delicharge", ordinfo.getDeliveryCharge());
        contentValues.put("total", ordinfo.getTotal());

        contentValues.put("paymode", ordinfo.oPayMode);
        contentValues.put("deliaddress", ""/*ordinfo.getDeliveryAddrInfo().addr*/);
        contentValues.put("custreq", ordinfo.getCustReq());
        contentValues.put("comptime", ordinfo.oDeliveryTime);


        long newid = completedOrderDBConn.insert("orderinfo", null, contentValues);

        if(newid < 0)
            return false;
        else
            return true;

    }

    public boolean insertCompletedOrderItems(Or2goOrderInfo ordinfo)
    {
        ArrayList<OrderItem> itemlist = ordinfo.getItemList();
        int itemcnt = itemlist.size();

        for(int i=0; i< itemcnt; i++)
        {
            OrderItem item = itemlist.get(i);

            ContentValues contentValues = new ContentValues();
            contentValues.put("orderid", ordinfo.getId());
            contentValues.put("itemid", item.getId());
            contentValues.put("itemname", item.getName());

            contentValues.put("price", item.getPrice().toString());
            contentValues.put("quantity", item.getQnty());

            contentValues.put("orderunit", item.getOrderUnit());
            contentValues.put("priceid", item.getPriceId());
            contentValues.put("skuid", item.getSKUId());
            contentValues.put("discount", "");
            contentValues.put("itemtotal", item.getItemTotal());

            long newid = completedOrderDBConn.insert("orderitems", null, contentValues);

//            if(newid < 0)
//                return false;
//            else
//                return true;
        }

        return true;

    }

    public int getCompletedOrderCount()
    {
        Cursor cursor;
        int count=0;

        cursor = completedOrderDBConn.rawQuery("SELECT * FROM orderinfo ", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public ArrayList<OrderHistoryInfo> getCompletedOrders(int requestcnt)
    {
        ArrayList<OrderHistoryInfo> orderlist = new ArrayList<OrderHistoryInfo>();
        Cursor cursor;
        int count = 0;
        int ordcount=0;

        cursor = completedOrderDBConn.rawQuery("SELECT * FROM orderinfo ", null);
        count = cursor.getCount();
        if (requestcnt == 0) ordcount = count;
        else if (count > requestcnt) ordcount = requestcnt;
        else if (count <= requestcnt) ordcount = count;


        //Get the completed order in latest first order
        cursor.moveToLast();
        for(int i=0; i < ordcount;i++)
        {
            String orderid = cursor.getString(cursor.getColumnIndexOrThrow("orderid"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("ordertime"));
            Integer type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            Integer status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
            String store = cursor.getString(cursor.getColumnIndexOrThrow("store"));
            String subtotal = cursor.getString(cursor.getColumnIndexOrThrow("subtotal"));
            String discount = cursor.getString(cursor.getColumnIndexOrThrow("discount"));
            String delicharge = cursor.getString(cursor.getColumnIndexOrThrow("delicharge"));
            String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
            Integer paymode = cursor.getInt(cursor.getColumnIndexOrThrow("paymode"));
            String deliaddress = cursor.getString(cursor.getColumnIndexOrThrow("deliaddress"));
            String custreq = cursor.getString(cursor.getColumnIndexOrThrow("custreq"));
            String comptime = cursor.getString(cursor.getColumnIndexOrThrow("comptime"));

            /*Or2goOrderInfo orderdata = new Or2goOrderInfo(orderid,type, store, "", status, time,
                    subtotal, delicharge, total,
                    discount, deliaddress, "",
                    paymode, custreq);
            orderdata.setoDeliveryTime(comptime);*/

            OrderHistoryInfo orderdata = new OrderHistoryInfo(orderid, store, time);
            orderdata.setType(type);
            orderdata.oPaymode = paymode;
            orderdata.setStatus(status);
            orderdata.setTotal(total);
            orderdata.setPaymode(paymode);
            orderdata.setSubTotal(subtotal);
            orderdata.setDiscount(discount);
            orderdata.setDeliveryCharge(delicharge);
            orderdata.setDeliveryAddress(deliaddress);
            orderdata.setCompletionTime(comptime);
            orderdata.oCustReq=custreq;



            //get items fro this order
            Cursor itemcursor = completedOrderDBConn.rawQuery("SELECT * FROM orderitems WHERE orderid = '" + orderid + "'", null);
            int itemcount = itemcursor.getCount();
            if (itemcount > 0) {
                //ArrayList<OrderItem> itemList = new ArrayList<OrderItem>();

                itemcursor.moveToFirst();

                for (int j = 0; j < itemcount; j++) {
                    //itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal
                    Integer itemid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("itemid"));
                    String itemname = itemcursor.getString(itemcursor.getColumnIndexOrThrow("itemname"));
                    String price = itemcursor.getString(itemcursor.getColumnIndexOrThrow("price"));
                    String quantity = itemcursor.getString(itemcursor.getColumnIndexOrThrow("quantity"));
                    Integer orderunit = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("orderunit"));
                    Integer priceid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("priceid"));
                    Integer skuid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("skuid"));
                    String itemtotal = itemcursor.getString(itemcursor.getColumnIndexOrThrow("itemtotal"));

                    OrderItem orditem = new OrderItem(itemid, itemname, Float.parseFloat(price), Float.parseFloat(quantity),
                            orderunit, priceid, skuid);

                    orderdata.addOrderedItem(orditem);
                    //itemList.add(orditem);

                    itemcursor.moveToNext();
                }

                //orderdata.setItemList(itemList);

                itemcursor.close();
            }

            orderlist.add(0,orderdata);
            //cursor.moveToNext();
            cursor.moveToPrevious();

        }

        cursor.close();

        return orderlist;

    }


    public OrderHistoryInfo getCompletedOrder(String orderid)
    {
        Cursor cursor = completedOrderDBConn.rawQuery("SELECT * FROM orderinfo WHERE orderid = '" + orderid + "'", null);
        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        //String orderid = cursor.getString(cursor.getColumnIndex("orderid"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("ordertime"));
        Integer type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
        Integer status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
        String store = cursor.getString(cursor.getColumnIndexOrThrow("store"));
        String subtotal = cursor.getString(cursor.getColumnIndexOrThrow("subtotal"));
        String discount = cursor.getString(cursor.getColumnIndexOrThrow("discount"));
        String delicharge = cursor.getString(cursor.getColumnIndexOrThrow("delicharge"));
        String total = cursor.getString(cursor.getColumnIndexOrThrow("total"));
        Integer paymode = cursor.getInt(cursor.getColumnIndexOrThrow("paymode"));
        String deliaddress = cursor.getString(cursor.getColumnIndexOrThrow("deliaddress"));
        String custreq = cursor.getString(cursor.getColumnIndexOrThrow("custreq"));
        String comptime = cursor.getString(cursor.getColumnIndexOrThrow("comptime"));

        OrderHistoryInfo orderdata = new OrderHistoryInfo(orderid, store, time);
        orderdata.oPaymode = paymode;
        orderdata.setStatus(status);
        orderdata.setTotal(total);
        orderdata.setPaymode(paymode);
        orderdata.setSubTotal(subtotal);
        orderdata.setDiscount(discount);
        orderdata.setDeliveryCharge(delicharge);
        orderdata.setDeliveryAddress(deliaddress);
        orderdata.setCompletionTime(comptime);

        //get items fro this order
        Cursor itemcursor = completedOrderDBConn.rawQuery("SELECT * FROM orderitems WHERE orderid = '" + orderid + "'", null);
        int itemcount = itemcursor.getCount();
        if (itemcount > 0) {
            itemcursor.moveToFirst();
            for (int j = 0; j < itemcount; j++) {
                //itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal
                Integer itemid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("itemid"));
                String itemname = itemcursor.getString(itemcursor.getColumnIndexOrThrow("itemname"));
                String price = itemcursor.getString(itemcursor.getColumnIndexOrThrow("price"));
                String quantity = itemcursor.getString(itemcursor.getColumnIndexOrThrow("quantity"));
                Integer orderunit = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("orderunit"));
                Integer priceid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("priceid"));
                Integer skuid = itemcursor.getInt(itemcursor.getColumnIndexOrThrow("skuid"));
                String itemtotal = itemcursor.getString(itemcursor.getColumnIndexOrThrow("itemtotal"));

                OrderItem orditem = new OrderItem(itemid, itemname, Float.parseFloat(price), Float.parseFloat(quantity),
                        orderunit, priceid, skuid);

                orderdata.addOrderedItem(orditem);
                itemcursor.moveToNext();
            }

            itemcursor.close();
        }

        cursor.close();

        return orderdata;
    }

}
