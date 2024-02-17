package com.or2go.mylibrary;

import static com.or2go.core.Or2goConstValues.OR2GO_VENDOR_PRODUCTLIST_EXIST;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.Or2GoStore;
import com.or2go.core.StoreLoginInfo;

import java.util.ArrayList;

public class StoreDBHelper extends SQLiteOpenHelper {
    private static VendorDBHelper sInstance;

    public SQLiteDatabase storeDBConn;
    Context mContext;


    public StoreDBHelper(Context context)
    {
        super(context, "storeDB.db", null, 1);
        mContext = context;

        initStoreDB();

    }

    /* 2024.02.17 : pricedbversion which is not used anymore is now used for storing store delivery option */

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table storetbl "+
                "(storeid text, name text, servicetype text, storetype text, description text, tags text, address text, place text, locality text, state text, " +
                "pincode text, status integer, worktime text, closedon text, infoversion integer, productdbversion integer, minorder text, policy text, favproducts text," +
                "pricedbversion integer, skudbversion integer, orderoption integer, payoption integer, invcontrol integer, geolocation text, contact text"+
                ",  UNIQUE(storeid) ON CONFLICT IGNORE)");

        /*db.execSQL("create table loginstoretbl" +
                "(vendorid text, storeid text, loginmode integer)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void cleanup()
    {
        storeDBConn.execSQL("VACUUM");
    }

    public void initStoreDB()
    {
        storeDBConn = this.getWritableDatabase();
    }

    public SQLiteDatabase getStoreDBConn()
    {
        return storeDBConn;
    }


    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = storeDBConn.rawQuery("SELECT * FROM storetbl", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }



    public boolean insertStore (String storeid, String name, String service, String storetype, String desc, String tags,
                                 String address, String place,String locality, String state, String vpin,
                                 String status, String minord, String worktime, String closedon, String policy,
                                 Integer proddbver, Integer infover, Integer skudbver, Integer deliveryoption,
                                 Integer orderoption, Integer payoption, Integer invoption, String geolocation,
                                 String contact)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storeid", storeid);
        contentValues.put("name", name);
        contentValues.put("serivcetype", service);
        contentValues.put("storetype", storetype);
        contentValues.put("description", desc);
        contentValues.put("tags", tags);
        contentValues.put("address", address);
        contentValues.put("place", place);
        contentValues.put("locality", locality);
        contentValues.put("state", state);
        contentValues.put("pincode", vpin);
        contentValues.put("status", status);
        contentValues.put("minorder", minord);
        contentValues.put("policy", policy);
        contentValues.put("worktime", worktime);
        contentValues.put("closedon", closedon);
        contentValues.put("geolocation", geolocation);
        contentValues.put("contact", contact);

        contentValues.put("orderoption", orderoption);
        contentValues.put("payoption", payoption);
        contentValues.put("invoption", invoption);

        contentValues.put("productdbversion", proddbver);
        contentValues.put("infoversion", infover);
        contentValues.put("pricedbversion", deliveryoption);
        contentValues.put("skudbversion", skudbver);

        long ret = storeDBConn.insert("vendortbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean insertStore (Or2GoStore vinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("storeid", vinfo.getId());
        contentValues.put("name", vinfo.getName());
        contentValues.put("servicetype", vinfo.getServiceType());
        contentValues.put("storetype", vinfo.getStoreType());
        contentValues.put("description", vinfo.getDescription());
        contentValues.put("tags", vinfo.getTags());
        contentValues.put("address", vinfo.getAddress());
        contentValues.put("place", vinfo.getPlace());
        contentValues.put("locality", vinfo.getLocality());
        contentValues.put("state", vinfo.getState());
        contentValues.put("pincode", vinfo.vPIN);
        contentValues.put("status", vinfo.getStatus());
        contentValues.put("minorder", vinfo.getMinOrder());
        contentValues.put("worktime", vinfo.getWorkTime());
        contentValues.put("closedon", vinfo.getClosedon());
        contentValues.put("policy", vinfo.getPolicy());
        contentValues.put("geolocation", vinfo.getGeoLoc());
        contentValues.put("contact", vinfo.getContact());

        contentValues.put("orderoption", vinfo.getOrderControl());
        contentValues.put("payoption", vinfo.getPayOption());
        contentValues.put("invcontrol", vinfo.getInventoryControl());
        contentValues.put("pricedbversion", vinfo.getDeliveryOption());

        contentValues.put("productdbversion", vinfo.getProductDBVersion());
        contentValues.put("infoversion", vinfo.getInfoVersion());
        contentValues.put("skudbversion", vinfo.getSKUDBVersion());

        System.out.println("Insert Store Fav Products="+vinfo.getFavItems());
        contentValues.put("favproducts", vinfo.getFavItems());

        ///contentValues.put("shuttype", vinfo.getShutDownType());
        ///contentValues.put("shutfrom", vinfo.getShutDownFrom());
        ///contentValues.put("shuttill", vinfo.getShutDownTill());
        ///contentValues.put("shutreason", vinfo.getShutDownReason());

        long ret = storeDBConn.insert("storetbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    /*public boolean insertLoginStore (StoreLoginInfo storeLoginInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vendorid", storeLoginInfo.getVendorid());
        contentValues.put("storeid", storeLoginInfo.getStoreid());
        contentValues.put("loginmode", storeLoginInfo.getLoginmode());
        long ret = storeDBConn.insert("loginstoretbl", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }*/

    public boolean updateStoreInfo(Or2GoStore vinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", vinfo.getName());
        contentValues.put("description", vinfo.getDescription());
        //contentValues.put("type", vinfo.getType());
        contentValues.put("storetype", vinfo.getStoreType());
        contentValues.put("tags", vinfo.getTags());
        contentValues.put("address", vinfo.getAddress());
        contentValues.put("place", vinfo.getPlace());
        contentValues.put("locality", vinfo.getLocality());
        contentValues.put("status", vinfo.getStatus());
        contentValues.put("minorder", vinfo.getMinOrder());
        contentValues.put("worktime", vinfo.getWorkTime());
        contentValues.put("closedon", vinfo.getClosedon());
        contentValues.put("geolocation", vinfo.getGeoLoc());
        contentValues.put("contact", vinfo.getContact());

        ///contentValues.put("shuttype", vinfo.getShutDownType());
        ///contentValues.put("shutfrom", vinfo.getShutDownFrom());
        ///contentValues.put("shuttill", vinfo.getShutDownTill());
        ///contentValues.put("shutreason", vinfo.getShutDownReason());

        contentValues.put("infoversion", vinfo.getInfoDBState().getVer());

        contentValues.put("orderoption", vinfo.getOrderControl());
        contentValues.put("payoption", vinfo.getPayOption());
        contentValues.put("invcontrol", vinfo.getInventoryControl());
        contentValues.put("pricedbversion", vinfo.getDeliveryOption());

        System.out.println("Update Store - Fav Products="+vinfo.getFavItems());
        contentValues.put("favproducts", vinfo.getFavItems());

        //Product DB version and Price DB version should be uipdated afer their update, separately form info updade
        //contentValues.put("dbversion", vinfo.getDBState().getProductVer());
        //contentValues.put("pricedbversion", vinfo.getDBState().getPriceVer());

        //long ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(vinfo.getId())});
        long ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{vinfo.getId()});
        System.out.println("Update Store update result="+ret);
        if(ret== -1)
            return false;
        else
            return true;

    }

    public boolean updateProductDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("productdbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        if(ret> 0 )
            return true;
        else
            return false;
    }

    /*public boolean updatePriceDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("pricedbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        //storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});
        if(ret> 0 )
            return true;
        else
            return false;
    }*/

    public boolean updateSKUDBVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("skudbversion", version);
        int ret = storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{id});
        //storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});
        if(ret> 0 )
            return true;
        else
            return false;
    }

    public boolean updateInfoVersion (String id, int version)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("infoversion", version);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateWorkingTime (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateCloseSchedule (String id, String worktime)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("worktime", worktime);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;
    }

    public boolean updateMinOrder(String id, String minord)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("minorder", minord);
        storeDBConn.update("storetbl", contentValues, "storeid = ? ", new String[]{String.valueOf(id)});

        return true;

    }

    public boolean deleteStore(String id)
    {
        storeDBConn.delete("storetbl", "storeid = ? ",new String[] { id });
        return true;
    }

    /*public boolean deleteStoreLoginTable() {
        storeDBConn.execSQL("Delete FROM loginstoretbl");
        return true;
    }*/

    //////
    public ArrayList<Or2GoStore> getStores() {

        ArrayList<Or2GoStore> vendList;
        Cursor cursor;
        int count = 0;

        cursor = storeDBConn.rawQuery("SELECT * FROM storetbl", null);
        count = cursor.getCount();

        if (count <=0)
            return null;
        else
        {
            vendList = new ArrayList<Or2GoStore>();

            cursor.moveToFirst();
            for(int i=0;i<count;i++) {


                //orderid text, itemid text, itemname text, price text, priceunit, quantity text, orderunit text, discount text, itemtotal text
                String vid = cursor.getString(cursor.getColumnIndexOrThrow("storeid"));
                String vname = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String  vservice= cursor.getString(cursor.getColumnIndexOrThrow("servicetype"));
                String vstoretype = cursor.getString(cursor.getColumnIndexOrThrow("storetype"));
                String vdesc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow("tags"));
                String vaddr = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String vplace = cursor.getString(cursor.getColumnIndexOrThrow("place"));
                String vlocality = cursor.getString(cursor.getColumnIndexOrThrow("locality"));
                String vstate = cursor.getString(cursor.getColumnIndexOrThrow("state"));
                String vpin = cursor.getString(cursor.getColumnIndexOrThrow("pincode"));
                Integer vstatus = cursor.getInt(cursor.getColumnIndexOrThrow("status"));
                String vminord = cursor.getString(cursor.getColumnIndexOrThrow("minorder"));
                String voptime = cursor.getString(cursor.getColumnIndexOrThrow("worktime"));
                String vclosed = cursor.getString(cursor.getColumnIndexOrThrow("closedon"));

                Integer proddbver = cursor.getInt(cursor.getColumnIndexOrThrow("productdbversion"));
                Integer infover = cursor.getInt(cursor.getColumnIndexOrThrow("infoversion"));
                Integer skuver = cursor.getInt(cursor.getColumnIndexOrThrow("skudbversion"));
                String geolocation = cursor.getString(cursor.getColumnIndexOrThrow("geolocation"));
                String contact = cursor.getString(cursor.getColumnIndexOrThrow("contact"));

                Integer ordcontrol = cursor.getInt(cursor.getColumnIndexOrThrow("orderoption"));
                Integer payoption = cursor.getInt(cursor.getColumnIndexOrThrow("payoption"));
                Integer invcontrol = cursor.getInt(cursor.getColumnIndexOrThrow("invcontrol"));
                String favitems = cursor.getString(cursor.getColumnIndexOrThrow("favproducts"));
                Integer deliveryopt = cursor.getInt(cursor.getColumnIndexOrThrow("pricedbversion"));

                //String shutfrom = cursor.getString(cursor.getColumnIndex("shutfrom"));
                //String shuttill = cursor.getString(cursor.getColumnIndex("shuttill"));
                //String shutres = cursor.getString(cursor.getColumnIndex("shutreason"));
                //Integer shuttype = cursor.getInt(cursor.getColumnIndex("shuttype"));



                if (vminord == null) vminord="0";


                Or2GoStore storeinfo = new Or2GoStore(vid, vname, vservice, vstoretype, vdesc, tag,
                                                        vaddr, vplace, vlocality, vstate, vpin, vstatus,
                                                        vminord, voptime, vclosed, proddbver,infover, skuver,
                                                        geolocation, contact,
                                                        payoption, ordcontrol, invcontrol, deliveryopt);
                //storeinfo.setShutdownInfo(shutfrom,shuttill,shutres,shuttype);
                storeinfo.setProductStatus(OR2GO_VENDOR_PRODUCTLIST_EXIST);
                //storeinfo.setOrderControl(ordcontrol);
                //storeinfo.setPayOption(payoption);
                storeinfo.setFavItems(favitems);
                vendList.add(storeinfo);

                cursor.moveToNext();
            }

            cursor.close();
        }


        return vendList;
    }

    /*public ArrayList<StoreLoginInfo> getStoresLoginData() {
        ArrayList<StoreLoginInfo> storeList;
        Cursor cursor;
        int count = 0;
        cursor = storeDBConn.rawQuery("SELECT * FROM loginstoretbl", null);
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
    }*/
}
