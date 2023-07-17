package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.SearchInfo;

import java.util.ArrayList;

public class SearchDBHelper extends SQLiteOpenHelper {
    private static SearchDBHelper sInstance;

    public SQLiteDatabase searchDBConn;
    Context mContext;

    public SearchDBHelper(Context context)
    {
        super(context, "searchDB.db", null, 1);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table searchdata "+
                "(prodname text, store text, prodid int, brand text , tag text, UNIQUE(prodname) ON CONFLICT IGNORE)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS searchdata");

        onCreate(db);
    }

    public void InitDB()
    {
        searchDBConn = this.getWritableDatabase();
    }

    public SQLiteDatabase getSearchDBConn()
    {
        return searchDBConn;
    }

    public boolean insertData (String name, String brand, String store, String tag, int id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("prodname", name);
        contentValues.put("store", store);
        contentValues.put("tag", tag);
        contentValues.put("brand", brand);

        long ret = searchDBConn.insert("searchdata", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }


    public ArrayList<SearchInfo> getSearchDataList() {
        ArrayList<SearchInfo> datalist = new ArrayList<SearchInfo>();
        Cursor cursor;
        int count = 0;

        cursor = searchDBConn.rawQuery("SELECT * FROM searchdata ", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {

                SearchInfo info = new SearchInfo();

                //info.name = cursor.getString(cursor.getColumnIndexOrThrow("dname"));
                //info.type = cursor.getString(cursor.getColumnIndexOrThrow("dtype"));

                datalist.add(info);

                cursor.moveToNext();
            }
        }

        //cursor.close();

        return datalist;
    }

    public ArrayList<String> getSearchNames() {
        ArrayList<String> datalist = new ArrayList<String>();
        Cursor cursor;
        int count = 0;

        cursor = searchDBConn.rawQuery("SELECT * FROM searchdata ", null);
        count = cursor.getCount();

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++) {

                //SearchInfo info = new SearchInfo();

                String name = cursor.getString(cursor.getColumnIndexOrThrow("dname"));

                datalist.add(name);

                cursor.moveToNext();
            }
        }

        //cursor.close();

        return datalist;
    }


    public ArrayList<SearchInfo> getSearchInfo(String name)
    {

        ArrayList<SearchInfo> datalist = null;
        //Cursor cursor;
        int count = 0;

        //Cursor cursor =  searchDBConn.rawQuery( "select * from searchdata where  dname = '"+name+"'", null );
        Cursor cursor = searchDBConn.query(true, "searchdata", new String[] { "prodname", "store", "prodid"}, "prodname" + " LIKE" + "'%" + name + "%' OR " +"tag" + " LIKE" + "'%" + name + "%'",
                null, null, null, null, null);

        count = cursor.getCount();

        if (count >0) {
            cursor.moveToFirst();

            datalist = new ArrayList<SearchInfo>();
            for(int i=0;i<count;i++) {

                SearchInfo info = new SearchInfo();

                info.name = cursor.getString(cursor.getColumnIndexOrThrow("prodname"));
                info.prodid = cursor.getInt(cursor.getColumnIndexOrThrow("prodid"));
                info.store = cursor.getString(cursor.getColumnIndexOrThrow("store"));

                datalist.add(info);

                cursor.moveToNext();
            }
        }

        return datalist;

    }

    public boolean getSearchInfo(String name, ArrayList<SearchInfo> datalist)
    {

        //ArrayList<SearchInfo> datalist = null;

        if (datalist == null) return false;

        datalist.clear();

        //Cursor cursor;
        int count = 0;

        //Cursor cursor =  searchDBConn.rawQuery( "select * from searchdata where  dname = '"+name+"'", null );
        Cursor cursor = searchDBConn.query(true, "searchdata", new String[] { "prodname", "store", "prodid"}, "prodname" + " LIKE" + "'%" + name + "%' OR " +"tag" + " LIKE" + "'%" + name + "%'",
                null, null, null, null, null);

        count = cursor.getCount();

        if (count >0) {
            cursor.moveToFirst();

            //datalist = new ArrayList<SearchInfo>();
            for(int i=0;i<count;i++) {

                SearchInfo info = new SearchInfo();

                info.name = cursor.getString(cursor.getColumnIndexOrThrow("prodname"));
                info.prodid = cursor.getInt(cursor.getColumnIndexOrThrow("prodid"));
                info.store = cursor.getString(cursor.getColumnIndexOrThrow("store"));

                datalist.add(info);

                cursor.moveToNext();
            }
        }

        return true;

    }
}
