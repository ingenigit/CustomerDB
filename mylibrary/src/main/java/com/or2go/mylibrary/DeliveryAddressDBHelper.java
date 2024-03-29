package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.DeliveryAddrInfo;

import java.util.ArrayList;

public class DeliveryAddressDBHelper extends SQLiteOpenHelper {

    private static DeliveryAddressDBHelper sInstance;

    public SQLiteDatabase addrDBConn;
    Context mContext;

    public DeliveryAddressDBHelper(Context context)
    {
        super(context, "DeliveryAddrDB.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table deliveryaddr "+
                "( nickname text, addr text, place text, locality text, sublocality text, landmark text, altcontact text, zipcode text, geocoordinate text, latest DATETIME,  UNIQUE(nickname) ON CONFLICT IGNORE, UNIQUE(addr) ON CONFLICT IGNORE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS deliveryaddr");


        onCreate(db);
    }

    public void cleanup()
    {
        addrDBConn.execSQL("VACUUM");
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
        addrDBConn = this.getWritableDatabase();
        //salesDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getDeliveryAddrDBConn()
    {
        return addrDBConn;
    }


    public boolean insertaddr  (String nickname, String addr, String place, String locality, String sublocality, String landmark, String zipcode, String altcontact, String geoloc)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("addr", addr);
        contentValues.put("place", place);
        contentValues.put("locality", locality);
        contentValues.put("sublocality", sublocality);
        contentValues.put("landmark", landmark);
        contentValues.put("nickname", nickname);
        contentValues.put("altcontact", altcontact);
        contentValues.put("zipcode", zipcode);
        contentValues.put("geocoordinate", geoloc);


        long newid = addrDBConn.insert("deliveryaddr", null, contentValues);

        if(newid < 0)
            return false;
        else
            return true;


    }

    //expecting profile address to be the first row
    public boolean updateProfileAddr(String nickname, String addr, String place, String locality, String sublocality, String landmark, String zipcode, String altcontact, String geoloc)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("addr", addr);
        contentValues.put("place", place);
        contentValues.put("locality", locality);
        contentValues.put("sublocality", sublocality);
        contentValues.put("landmark", landmark);
        //contentValues.put("nickname", nickname);
        contentValues.put("altcontact", altcontact);
        contentValues.put("zipcode", zipcode);
        contentValues.put("geocoordinate", geoloc);


        ///String where = "rowid=(SELECT MIN(rowid) FROM " + "deliveryaddr" + ")";
        int ret = addrDBConn.update("deliveryaddr", contentValues, "nickname = ? ", new String[]{nickname});

        if(ret <= 0)
            return false;
        else
            return true;
    }

    public boolean deleteAddr(String name)
    {
        int delcnt = addrDBConn.delete("deliveryaddr","nickname = ? ", new String[] { name });

        if (delcnt >0)
            return true;
        else
            return false;

    }



    public ArrayList<DeliveryAddrInfo> getAddrList() {
        ArrayList<DeliveryAddrInfo> addrlist = new ArrayList<DeliveryAddrInfo>();
        Cursor cursor;
        int count = 0;

        cursor = addrDBConn.rawQuery("SELECT * FROM deliveryaddr ", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {

                String addr = cursor.getString(cursor.getColumnIndexOrThrow("addr"));
                String place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
                String locality = cursor.getString(cursor.getColumnIndexOrThrow("locality"));
                String sublocality = cursor.getString(cursor.getColumnIndexOrThrow("sublocality"));
                String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
                String landmark = cursor.getString(cursor.getColumnIndexOrThrow("landmark"));
                String zipcode = cursor.getString(cursor.getColumnIndexOrThrow("zipcode"));
                String altcontact = cursor.getString(cursor.getColumnIndexOrThrow("altcontact"));
                String latest = cursor.getString(cursor.getColumnIndexOrThrow("latest"));
                String geoloc = cursor.getString(cursor.getColumnIndexOrThrow("geocoordinate"));

                DeliveryAddrInfo addrinfo =new DeliveryAddrInfo(nickname,addr, place,locality, sublocality, landmark, zipcode, altcontact);
                addrinfo.latest = latest;
                addrinfo.setGeoPosition(geoloc);
                addrlist.add(addrinfo);

                cursor.moveToNext();
            }
        }

        cursor.close();

        return addrlist;
    }

    public boolean isAddressExist(String name)
    {
        int count = 0;

        //cursor = addrDBConn.rawQuery("SELECT * FROM deliveryaddr where nickname="+name+"", null);
        Cursor cursor =  addrDBConn.rawQuery( "select * from deliveryaddr where  nickname = '"+name+"'", null );
        count = cursor.getCount();

        if (count >0) return true;
        else
            return false;

    }

    public DeliveryAddrInfo getAddrInfo(String name)
    {

        Cursor cursor;
        int count = 0;
        String sname="";

        sname = name.replaceAll("'", "''");
        ////System.out.println("DeliveryAddressDB: get address info contains special characters : "+ name +" escapestring name:"+sname);
        if (name.contains("'"))
        {
            //sname = android.database.DatabaseUtils.sqlEscapeString(name);
            String p_query = "select * from deliveryaddr where nickname = ?";
            cursor =  addrDBConn.rawQuery(p_query, new String[] { sname });
        }
        else {
            sname = name;
            cursor =  addrDBConn.rawQuery( "select * from deliveryaddr where  nickname = '"+sname+"'", null );
        }
        //cursor = addrDBConn.rawQuery("SELECT * FROM deliveryaddr where nickname="+name+"", null);


        //String p_query = "select * from deliveryaddr where nickname = ?";
        //Cursor cursor =  addrDBConn.rawQuery(p_query, new String[] { android.database.DatabaseUtils.sqlEscapeString(name) });

        count = cursor.getCount();

        if (count >0) {
            cursor.moveToFirst();

            String addr = cursor.getString(cursor.getColumnIndexOrThrow("addr"));
            String place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
            String locality = cursor.getString(cursor.getColumnIndexOrThrow("locality"));
            String sublocality = cursor.getString(cursor.getColumnIndexOrThrow("sublocality"));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
            String landmark = cursor.getString(cursor.getColumnIndexOrThrow("landmark"));
            String zipcode = cursor.getString(cursor.getColumnIndexOrThrow("zipcode"));
            String altcontact = cursor.getString(cursor.getColumnIndexOrThrow("altcontact"));
            String latest = cursor.getString(cursor.getColumnIndexOrThrow("latest"));
            String geoloc = cursor.getString(cursor.getColumnIndexOrThrow("geocoordinate"));

            DeliveryAddrInfo addrinfo =new DeliveryAddrInfo(nickname,addr, place, locality, sublocality, landmark, zipcode, altcontact);
            addrinfo.latest = latest;
            addrinfo.setGeoPosition(geoloc);

            cursor.close();

            return addrinfo;
        }

        return null;

    }




}
