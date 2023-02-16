package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.SPInfo;

public class SPDBHelper extends SQLiteOpenHelper {
    //public SQLiteDatabase SPDBConn;
    Context mContext;


    public SPDBHelper(Context context)
    {
        super(context, "SPDB.db", null, 1);
        mContext = context;

    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table spinfo "+
                "(spname text, propname text, contact text, altcontact text, email text, address text, place text, state text, zipcode text, " +
                "status text, closestatus text, closedfrom text, closedtill text, closedreason text, closedon text, logopath text, dbversion integer, slogan text, policy text" +
                ",  UNIQUE(spname) ON CONFLICT IGNORE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

    /*public void cleanup()
    {
        SPDBConn.execSQL("VACUUM");
    }

    public void initVendorDB()
    {
        SPDBConn = this.getWritableDatabase();
    }*/

    /*public SQLiteDatabase getVendorDBConn()
    {
        return SPDBConn;
    }*/

    public boolean insertSPInfo (SPInfo spinfo)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("spname", spinfo.spname);
        contentValues.put("propname", spinfo.propname);
        contentValues.put("contact", spinfo.contact);
        contentValues.put("altcontact", spinfo.altcontact);
        contentValues.put("email", spinfo.email);
        contentValues.put("address", spinfo.address);
        contentValues.put("place", spinfo.place);
        contentValues.put("state", spinfo.state);
        contentValues.put("zipcode", spinfo.zipcode);
        contentValues.put("status", spinfo.status);
        contentValues.put("closestatus", spinfo.closestatus);
        contentValues.put("closedfrom", spinfo.closedfrom);
        contentValues.put("closedtill", spinfo.closedtill);
        contentValues.put("closedreason", spinfo.closedtill);
        contentValues.put("closedon", spinfo.closedon);
        contentValues.put("logopath", spinfo.logopath);
        contentValues.put("dbversion", spinfo.dbversion);
        contentValues.put("slogan", spinfo.zipcode);
        contentValues.put("policy", spinfo.status);

        SQLiteDatabase dbconn = this.getWritableDatabase();
        long ret =  dbconn.insert("spinfo", null, contentValues);
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearSPInfo()
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        dbconn.delete("spinfo", null,null);
        dbconn.close();

        return true;
    }

    /*public boolean setSPInfo(SPInfo spinfo)
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        dbconn.delete("spinfo", null,null);

        ContentValues contentValues = new ContentValues();
        contentValues.put("spname", spinfo.spname);
        contentValues.put("propname", spinfo.propname);
        contentValues.put("contact", spinfo.contact);
        contentValues.put("altcontact", spinfo.altcontact);
        contentValues.put("email", spinfo.email);
        contentValues.put("address", spinfo.address);
        contentValues.put("place", spinfo.place);
        contentValues.put("state", spinfo.state);
        contentValues.put("zipcode", spinfo.zipcode);
        contentValues.put("status", spinfo.status);
        contentValues.put("closestatus", spinfo.closestatus);
        contentValues.put("closedfrom", spinfo.closedfrom);
        contentValues.put("closedtill", spinfo.closedtill);
        contentValues.put("closedon", spinfo.closedon);
        contentValues.put("logopath", spinfo.logopath);
        contentValues.put("dbversion", spinfo.dbversion);

        long ret =  dbconn.insert("spinfo", null, contentValues);
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }*/

    public boolean updateSPInfo(SPInfo spinfo)
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("spname", spinfo.spname);
        contentValues.put("propname", spinfo.propname);
        contentValues.put("contact", spinfo.contact);
        contentValues.put("altcontact", spinfo.altcontact);
        contentValues.put("email", spinfo.email);
        contentValues.put("address", spinfo.address);
        contentValues.put("place", spinfo.place);
        contentValues.put("state", spinfo.state);
        contentValues.put("zipcode", spinfo.zipcode);
        contentValues.put("status", spinfo.status);
        contentValues.put("closestatus", spinfo.closestatus);
        contentValues.put("closedfrom", spinfo.closedfrom);
        contentValues.put("closedtill", spinfo.closedtill);
        contentValues.put("closedon", spinfo.closedon);
        contentValues.put("logopath", spinfo.logopath);
        contentValues.put("dbversion", spinfo.dbversion);

        //long ret =  dbconn.update("spinfo", contentValues, "spname = ? ", new String[]{spname});
        long ret =  dbconn.update("spinfo", contentValues, "rowid = ? ", new String[]{"1"});
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean isSPInfoSet()
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        Cursor  cursor = dbconn.rawQuery("SELECT * FROM spinfo", null);
        int count = cursor.getCount();

        cursor.close();
        dbconn.close();

        if (count >0) return true;
        else return false;
    }

    public SPInfo getSPInfo()
    {
        Cursor cursor;
        int count = 0;
        Integer curid=0;

        SPInfo spinfo = null;

        SQLiteDatabase dbconn = this.getWritableDatabase();
        cursor = dbconn.rawQuery("SELECT * FROM spinfo", null);
        count = cursor.getCount();
        if (count >0)
        {
            cursor.moveToFirst();

            String spname = cursor.getString(cursor.getColumnIndexOrThrow("spname"));
            String propname = cursor.getString(cursor.getColumnIndexOrThrow("propname"));
            String contact = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
            String altcontact = cursor.getString(cursor.getColumnIndexOrThrow("altcontact"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            String place = cursor.getString(cursor.getColumnIndexOrThrow("place"));
            String state = cursor.getString(cursor.getColumnIndexOrThrow("state"));
            String pincode = cursor.getString(cursor.getColumnIndexOrThrow("zipcode"));
            String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String closestatus = cursor.getString(cursor.getColumnIndexOrThrow("closestatus"));
            String closedfrom = cursor.getString(cursor.getColumnIndexOrThrow("closedfrom"));
            String closedtill = cursor.getString(cursor.getColumnIndexOrThrow("closedtill"));
            String closedreason = cursor.getString(cursor.getColumnIndexOrThrow("closedreason"));
            String closedon = cursor.getString(cursor.getColumnIndexOrThrow("closedon"));
            String logopath = cursor.getString(cursor.getColumnIndexOrThrow("logopath"));
            int dbversion = cursor.getInt(cursor.getColumnIndexOrThrow("dbversion"));

            spinfo = new SPInfo(spname, propname, contact, altcontact, email, address, place, state, pincode, status, logopath, closestatus, closedfrom,
                    closedtill, closedreason, closedon, dbversion);
            
        }

        cursor.close();
        dbconn.close();
        return spinfo;
    }

    public boolean setShutdownInfo(String  spname, String from, String till, String cause)
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        ////dbconn.delete("spinfo", null,null);

        ContentValues contentValues = new ContentValues();
        contentValues.put("closedfrom", from);
        contentValues.put("closedtill", till);
        contentValues.put("closedreason", cause);

        //long ret =  dbconn.update("spinfo", contentValues, "spname = ? ", new String[]{spname});
        long ret =  dbconn.update("spinfo", contentValues, "rowid = ? ", new String[]{"1"});
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }

    public boolean clearShutdownInfo(String  spname)
    {
        SQLiteDatabase dbconn = this.getWritableDatabase();
        ///dbconn.delete("spinfo", null,null);

        ContentValues contentValues = new ContentValues();
        contentValues.put("closedfrom", "");
        contentValues.put("closedtill", "");
        contentValues.put("closedreason", "");

        //long ret =  dbconn.update("spinfo", contentValues, "spname = ? ", new String[]{spname});
        long ret =  dbconn.update("spinfo", contentValues, "rowid = ? ", new String[]{"1"});
        dbconn.close();
        if(ret== -1)
            return false;
        else
            return true;
    }
}
