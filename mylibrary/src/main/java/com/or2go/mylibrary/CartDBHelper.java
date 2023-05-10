package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.or2go.core.CartItem;
import com.or2go.core.UnitManager;

import java.util.ArrayList;

public class CartDBHelper extends SQLiteOpenHelper {

    private static CartDBHelper sInstance;

    public SQLiteDatabase cartDBConn;
    Context mContext;

    UnitManager mUnitMgr;

    public CartDBHelper(Context context)
    {
        super(context, "cartDB.db", null, 1);
        mContext = context;

        mUnitMgr = new UnitManager();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table curstore "+
                "(curstore text)");

        db.execSQL("create table cartitems "+
                "(itemid text, itemname text, price text, quantity text, orderunit integer, skuid integer)");

        db.execSQL("create table deliloc "+
                "(deliname text, deliplace text, deliaddr text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void cleanup()
    {
        cartDBConn.execSQL("VACUUM");
    }

    public void InitCartDB()
    {
        cartDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getCartDBConn()
    {
        return cartDBConn;
    }

    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = cartDBConn.rawQuery("SELECT * FROM cartitems", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public boolean insertItem (String itemid, String itemname, String price, String quantity,String unit,Integer skuid )
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("itemid", itemid);
        contentValues.put("itemname", itemname);
        contentValues.put("price", price);
        contentValues.put("quantity", quantity);
        contentValues.put("orderunit", unit);
        contentValues.put("skuid", skuid);
        //contentValues.put("priceid", priceid);

        long ret = cartDBConn.insert("cartitems", null, contentValues);
        Log.i("CartDB "," Inserting Cart Item="+itemname + "skuid="+skuid);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean updateItemQnty(String itemid, String qnty)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", qnty);

        Log.i("CartDB "," Updating Cart Items="+itemid + "quantity to="+qnty);

        cartDBConn.update("cartitems", contentValues,"itemid="+itemid, null);

        return true;
    }

    public boolean updateItemQnty(String itemid, Integer skuid, String qnty)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", qnty);
        Log.i("CartDB "," Updating Quantity of Cart Item="+itemid + "  pack id="+skuid+ "  quantity to="+qnty);

        //cartDBConn.update("cartitems", contentValues,"itemid="+itemid , null);
        int ret = cartDBConn.update("cartitems", contentValues,
                "itemid = ? AND skuid = ?",
                new String[]{itemid, skuid.toString()});
        Log.i("CartDB "," Update qnty result="+ret + "  pack id="+skuid);
        return true;
    }

    public boolean deleteItem(String itemid)
    {
        int ret = cartDBConn.delete("cartitems", "itemid = ? ", new String[] { itemid });
        return true;
    }

    public boolean deleteItem(String itemid, Integer skuid)
    {

        Log.i("CartDB "," Deleting Cart Item="+itemid + "  pack id="+skuid);
        int ret = cartDBConn.delete("cartitems", "itemid = ? AND skuid = ?", new String[] { itemid, skuid.toString() });
        Log.i("CartDB "," Deleting result="+ret + "  priceid="+skuid);
        return true;
    }


    public boolean clearCart()
    {
        cartDBConn.delete("curstore", null,null);
        cartDBConn.delete("cartitems", null,null);
        cartDBConn.delete("deliloc", null,null);

        return true;
    }

    //CurrentVendor DB APIs
    /*public boolean insertVendor (String vend) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vendor", vend);

        long ret = cartDBConn.insert("curvendor", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }*/
    public boolean insertStore (String storeid) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("curstore", storeid);

        long ret = cartDBConn.insert("curstore", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearStore()
    {
        cartDBConn.delete("curstore", null,null);

        return true;
    }

    public String getCartStore()
    {
        Cursor cursor;
        int count = 0;
        String store="";

        cursor = cartDBConn.rawQuery("SELECT * FROM curstore", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            store = cursor.getString(cursor.getColumnIndexOrThrow("curstore"));
        }

        return store;
    }



    ///Delivery Location DB API
    public boolean insertDeliveryAddrName (String loc) {
        String addrname = loc.replace("'","\'");
        ContentValues contentValues = new ContentValues();
        contentValues.put("deliname", addrname);

        long ret = cartDBConn.insert("deliloc", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearDeliveryAddrName()
    {
        cartDBConn.delete("deliloc", null,null);

        return true;
    }

    public String getDeliveryAddrName()
    {
        Cursor cursor;
        int count = 0;
        String loc="";

        cursor = cartDBConn.rawQuery("SELECT * FROM deliloc", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            loc = cursor.getString(cursor.getColumnIndexOrThrow("deliname"));
        }

        return loc;
    }

    public boolean isDeliveryAddrNameExist()
    {
        Cursor cursor;
        int count = 0;
        String loc="";

        cursor = cartDBConn.rawQuery("SELECT * FROM deliloc", null);
        count = cursor.getCount();

        if (count >0) return true;
        else
            return false;
    }

    //////
    public ArrayList<CartItem> getCartItems() {

        ArrayList<CartItem> itemList;
        Cursor cursor;
            int count = 0;

            cursor = cartDBConn.rawQuery("SELECT * FROM cartitems", null);
            count = cursor.getCount();
        Log.i("CartDB "," Retrieving Cart  Items Count="+count);

            if (count <=0)
                return null;
            else
            {
                itemList = new ArrayList<CartItem>();

                cursor.moveToFirst();
                for(int i=0;i<count;i++) {

                //orderid text, itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal text
                String itemid = cursor.getString(cursor.getColumnIndexOrThrow("itemid"));
                String itemname = cursor.getString(cursor.getColumnIndexOrThrow("itemname"));
                Integer skuid = cursor.getInt(cursor.getColumnIndexOrThrow("skuid"));
                //Integer priceid = cursor.getInt(cursor.getColumnIndexOrThrow("priceid"));
                String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                String quantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
                Integer orderunit = cursor.getInt(cursor.getColumnIndexOrThrow("orderunit"));

                //Integer ounit = mUnitMgr.getUnitFromName(orderunit);

                CartItem saleitem = new CartItem(Integer.parseInt(itemid), itemname, Float.valueOf(price), Float.valueOf(quantity), orderunit, skuid);

                itemList.add(saleitem);

                Log.i("CartDB "," Retrieving Cart  Items  item="+itemname+"  skuid="+skuid+ " Qnty="+quantity);

                cursor.moveToNext();
            }

            cursor.close();
        }


        return itemList;
    }






}
