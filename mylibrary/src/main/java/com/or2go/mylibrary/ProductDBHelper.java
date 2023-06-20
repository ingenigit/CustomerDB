package com.or2go.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.or2go.core.ProductInfo;
import com.or2go.core.ProductPriceInfo;
import com.or2go.core.ProductSKU;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDBHelper extends SQLiteOpenHelper {

    //public static final String DATABASE_NAME = "gposProductsDB.db";

    public SQLiteDatabase productDBConn;

    public ProductDBHelper(Context context, String dbname)
    {
        super(context, dbname, null, 1);

        InitProductDB();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table prodinfo "+
                "(id INTEGER PRIMARY KEY, name text, shortname text, description text,"+
                "prodcode text, hsncode text, barcode text, prodtype integer, category text , subcategory text, brand text,"+
                "property text, avail integer, tag text, taxincl Integer DEFAULT 0, invcontrol INTEGER DEFAULT 0, imgpath DEFAULT 0, taxrate REAL DEFAULT 0)");
        /*,UNIQUE(name) ON CONFLICT IGNORE*/

        db.execSQL("create table skuinfo "+
                "(skuid INTEGER PRIMARY KEY AUTOINCREMENT, prodid INTEGER, skuname text, description text,"+
                "unit INTEGER, amount REAL, price REAL, maxprice REAL, "+
                "size text, color text, model text, dimension text, weight Integer, pkgtype text)");


        db.execSQL("create table category "+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, category text, refundable INTEGER DEFAULT 0, exchangeable INTEGER DEFAULT 0,  usestock INTEGER DEFAULT 0, UNIQUE(category) ON CONFLICT IGNORE)");

        /*db.execSQL("create table priceinfo "+
                "(priceid INTEGER PRIMARY KEY AUTOINCREMENT, prodid INTEGER, skuid INTEGER, unit INTEGER, amount REAL, saleprice REAL, maxprice REAL, taxincl Integer, manualprice Integer, dbver Integer)");*/

        db.execSQL("create table subcategory "+
                "(id INTEGER PRIMARY KEY   AUTOINCREMENT, category text, subcategory text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InitProductDB()
    {
        productDBConn = this.getWritableDatabase();
        ///productDBConn = this.getReadableDatabase();
    }

    public SQLiteDatabase getProductDBConn()
    {
        return productDBConn;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    //product APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    public int addproduct(Integer itemid, String name, String shortname, String desc, String code, String hsncode, String barcode,
                          String category, String subcategory, String brand, int taxincl, String tag, String property, Integer invctl,
                            Integer img, float taxrate)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(); 
        contentValues.put("id", itemid);
        contentValues.put("name", name);
        contentValues.put("shortname", shortname);
        contentValues.put("description", desc);

        contentValues.put("prodcode", code);
        contentValues.put("hsncode", hsncode);
        contentValues.put("barcode", barcode);

        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);
        contentValues.put("brand", brand);

        contentValues.put("tag", tag);
        contentValues.put("property", property);
        contentValues.put("taxincl", taxincl);
        contentValues.put("invcontrol", invctl);
        contentValues.put("imgpath", img);
        contentValues.put("taxrate", taxrate);

        long ret = productDBConn.insert("prodinfo", null, contentValues);

        return ((int)ret);
    }

    public boolean addProduct(ProductInfo prod)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", prod.id);
        contentValues.put("name", prod.name);
        contentValues.put("shortname", prod.brandname);
        contentValues.put("description", prod.desc);

        contentValues.put("prodcode", prod.code);
        contentValues.put("hsncode", prod.gstcode);
        contentValues.put("barcode", prod.barcode);

        //contentValues.put("prodtype", prod.type);
        contentValues.put("category", prod.category);
        contentValues.put("subcategory", prod.subcategory);
        contentValues.put("brand", "");


        contentValues.put("tag", prod.tag);
        contentValues.put("property", prod.property);
        contentValues.put("invcontrol", prod.invcontrol);
        contentValues.put("taxincl", prod.taxincl);
        contentValues.put("avail", 0/*prod.avail*/);
        contentValues.put("imgpath", prod.imagepath);
        contentValues.put("taxrate", prod.taxrate);

        long ret = productDBConn.insert("prodinfo", null, contentValues);

        if (ret > 0) return true;
        else
            return false;
    }

    public int existsItemName(String name)
    {
        int result;
        ///Cursor cursor =  productDBConn.rawQuery( "select * from goodsmgnt1 where itemname="+name+"", null );
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "name=?",new String[]{ name},
                null, null, null, null);
        if (cr.getCount() <= 0)
            result = 0;
        else
        {
            cr.moveToFirst();
            result = cr.getInt(cr.getColumnIndexOrThrow("id"));
        }
        cr.close();

        return result;
    }


    //name text, shortname text, description text,"+
    //                "prodcode text, gstcode text, barcode text, type text, category text , subcategory text, brand text,"+
    //                "unittype integer , priceunit text , pricetype integer, price REAL, taxinclusion
    public ProductInfo getProductInfo(int itemid)
    {
        ProductInfo iteminfo = new ProductInfo();

        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "icode","itemtype","itemsubtype","itemname", "barcode", "gstcode"}, "id=?",new String[]{String.valueOf(itemid)},
                null, null, null, null);

        if(cr.getCount()>0 )
        {
            cr.moveToFirst();

            iteminfo.id = itemid;
            iteminfo.name = cr.getString(cr.getColumnIndexOrThrow("name"));
            iteminfo.brandname = cr.getString(cr.getColumnIndexOrThrow("shortname"));
            iteminfo.desc = cr.getString(cr.getColumnIndexOrThrow("description"));
            //iteminfo.type = cr.getInt(cr.getColumnIndexOrThrow("category"));
            iteminfo.category = cr.getString(cr.getColumnIndexOrThrow("category"));
            iteminfo.subcategory = cr.getString(cr.getColumnIndexOrThrow("subcategory"));
            iteminfo.code = cr.getString(cr.getColumnIndexOrThrow("prodcode"));
            iteminfo.barcode = cr.getString(cr.getColumnIndexOrThrow("barcode"));
            iteminfo.gstcode = cr.getString(cr.getColumnIndexOrThrow("hsncode"));

            /*iteminfo.packtype = cr.getInt(cr.getColumnIndexOrThrow("packtype"));
            iteminfo.price =cr.getFloat(cr.getColumnIndexOrThrow("price"));
            iteminfo.unit = cr.getInt(cr.getColumnIndexOrThrow("priceunit"));
            iteminfo.taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxincl"));
            iteminfo.taxrate =cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));*/

            iteminfo.tag = cr.getString(cr.getColumnIndexOrThrow("tag"));
            iteminfo.invcontrol = cr.getInt(cr.getColumnIndexOrThrow("invcontrol"));
            iteminfo.taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxincl"));
            iteminfo.property = cr.getString(cr.getColumnIndexOrThrow("property"));
            iteminfo.imagepath = cr.getInt(cr.getColumnIndexOrThrow("imgpath"));
            iteminfo.taxrate = cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));
        }
        else
            return null;;

        cr.close();

        return iteminfo;
    }


    /*public boolean updateproduct (int id,int type, String code, String hsncode, String barcode, Float price, int unit,
                                  int taxincl, Float taxrate, String tag)*/
    public boolean updateproduct (int id, String code, String hsncode, String barcode, String tag, String prop,
                                  Integer taxincl, Integer invctl, Integer imgpath, Float taxrate)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ///contentValues.put("prodtype", type);
        //contentValues.put("category", iteminfo.category);
        //contentValues.put("subcategory", iteminfo.subcategory);
        contentValues.put("prodcode", code);
        contentValues.put("hsncode", hsncode);
        contentValues.put("barcode", barcode);
        /*contentValues.put("price", price);
        contentValues.put("priceunit", unit);
        contentValues.put("taxinclusion", taxincl);
        contentValues.put("taxrate", taxrate);*/
        contentValues.put("tag", tag);
        contentValues.put("property", prop);
        contentValues.put("invcontrol", invctl);
        contentValues.put("taxincl", taxincl);
        contentValues.put("imgpath", imgpath);
        contentValues.put("taxrate", taxrate);

        productDBConn.update("prodinfo", contentValues, "id = ? ", new String[]{String.valueOf(id)});


        return true;
    }

    public int getItemCount()
    {
        Cursor cursor;
        int count=0;

        cursor = productDBConn.rawQuery("SELECT * FROM prodinfo", null);
        count = cursor.getCount();

        cursor.close();
        return count;
    }

    public boolean deleteProductData()
    {
        productDBConn.delete("prodinfo", null, null);
        productDBConn.delete("category", null, null);
        productDBConn.delete("subcategory", null, null);

        return true;
    }

    public boolean deletePriceData()
    {
        productDBConn.delete("priceinfo", null, null);
        return true;
    }

    public boolean deleteSKUData()
    {
        productDBConn.delete("skuinfo", null, null);
        return true;
    }

    /*
    public boolean updateproduct(ProductInfo iteminfo)
    {
        int ret;
        //boolean res;
        ContentValues contentValues = new ContentValues();
        int id = iteminfo.id;

        contentValues.put("name", iteminfo.name);
        contentValues.put("type", iteminfo.type);
        contentValues.put("category", iteminfo.category);
        contentValues.put("subcategory", iteminfo.subcategory);
        contentValues.put("code", iteminfo.code);
        contentValues.put("gstcode", iteminfo.gstcode);
        contentValues.put("barcode", iteminfo.barcode);
        contentValues.put("price", iteminfo.price);
        contentValues.put("priceunit", iteminfo.unit);
        contentValues.put("taxincl", iteminfo.taxincl);

        productDBConn.update("prodinfo", contentValues, "id = ? ", new String[]{String.valueOf(id)});

        return true;

    }
    */

    public Integer deleteproduct (Integer id)
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        int ret=0;
        ret = productDBConn.delete("prodinfo", "id = ? ",new String[] { Integer.toString(id) });


        return ret;
    }

    public ArrayList<ProductInfo> getAllProducts(){
        ArrayList<ProductInfo>  prdlist = new  ArrayList<ProductInfo> ();
        Cursor cr;
        int count=0;

        SQLiteDatabase userDBConn = this.getWritableDatabase();
        cr = userDBConn.rawQuery("SELECT * FROM prodinfo ", null);
        count = cr.getCount();

        if (count >0) {
            cr.moveToFirst();
            for (int i = 0; i < count; i++) {
                ProductInfo iteminfo = new ProductInfo();

                iteminfo.id = cr.getInt(cr.getColumnIndexOrThrow("id"));;
                iteminfo.name = cr.getString(cr.getColumnIndexOrThrow("name"));
                iteminfo.brandname = cr.getString(cr.getColumnIndexOrThrow("shortname"));
                iteminfo.desc = cr.getString(cr.getColumnIndexOrThrow("description"));
                //iteminfo.type = cr.getInt(cr.getColumnIndexOrThrow("category"));
                iteminfo.category = cr.getString(cr.getColumnIndexOrThrow("category"));
                iteminfo.subcategory = cr.getString(cr.getColumnIndexOrThrow("subcategory"));
                iteminfo.code = cr.getString(cr.getColumnIndexOrThrow("prodcode"));
                iteminfo.barcode = cr.getString(cr.getColumnIndexOrThrow("barcode"));
                iteminfo.gstcode = cr.getString(cr.getColumnIndexOrThrow("hsncode"));
                /*iteminfo.price =cr.getFloat(cr.getColumnIndexOrThrow("price"));
                iteminfo.unit = cr.getInt(cr.getColumnIndexOrThrow("priceunit"));
                iteminfo.packtype=cr.getInt(cr.getColumnIndexOrThrow("packtype"));
                iteminfo.maxprice=cr.getFloat(cr.getColumnIndexOrThrow("maxprice"));
                iteminfo.taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxinclusion"));
                iteminfo.taxrate =cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));*/
                iteminfo.tag = cr.getString(cr.getColumnIndexOrThrow("tag"));
                iteminfo.invcontrol= cr.getInt(cr.getColumnIndexOrThrow("invcontrol"));
                iteminfo.taxincl= cr.getInt(cr.getColumnIndexOrThrow("taxincl"));
                iteminfo.property = cr.getString(cr.getColumnIndexOrThrow("property"));
                iteminfo.imagepath= cr.getInt(cr.getColumnIndexOrThrow("imgpath"));
                iteminfo.taxrate= cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));

                prdlist.add(iteminfo);

                cr.moveToNext();
            }
        }

        return prdlist;

    }

    public boolean getAllProducts(HashMap<Integer, ProductInfo> mapprdinfo){
        ArrayList<ProductInfo>  prdlist = new  ArrayList<ProductInfo> ();
        Cursor cr;
        int count=0;
        boolean invcontrol=false;

        SQLiteDatabase userDBConn = this.getWritableDatabase();
        cr = userDBConn.rawQuery("SELECT * FROM prodinfo ", null);
        count = cr.getCount();

        if (count >0) {
            cr.moveToFirst();
            for (int i = 0; i < count; i++) {
                ProductInfo iteminfo = new ProductInfo();

                iteminfo.id = cr.getInt(cr.getColumnIndexOrThrow("id"));;
                iteminfo.name = cr.getString(cr.getColumnIndexOrThrow("name"));
                iteminfo.brandname = cr.getString(cr.getColumnIndexOrThrow("shortname"));
                iteminfo.desc = cr.getString(cr.getColumnIndexOrThrow("description"));
                //iteminfo.type = cr.getInt(cr.getColumnIndexOrThrow("category"));
                iteminfo.category = cr.getString(cr.getColumnIndexOrThrow("category"));
                iteminfo.subcategory = cr.getString(cr.getColumnIndexOrThrow("subcategory"));
                iteminfo.code = cr.getString(cr.getColumnIndexOrThrow("prodcode"));
                iteminfo.barcode = cr.getString(cr.getColumnIndexOrThrow("barcode"));
                iteminfo.gstcode = cr.getString(cr.getColumnIndexOrThrow("hsncode"));
                /*iteminfo.packtype = cr.getInt(cr.getColumnIndexOrThrow("packtype"));
                iteminfo.price =cr.getFloat(cr.getColumnIndexOrThrow("price"));
                iteminfo.unit = cr.getInt(cr.getColumnIndexOrThrow("priceunit"));
                iteminfo.taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxinclusion"));
                iteminfo.taxrate =cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));*/
                iteminfo.tag = cr.getString(cr.getColumnIndexOrThrow("tag"));
                iteminfo.taxincl= cr.getInt(cr.getColumnIndexOrThrow("taxincl"));
                iteminfo.invcontrol= cr.getInt(cr.getColumnIndexOrThrow("invcontrol"));
                iteminfo.property = cr.getString(cr.getColumnIndexOrThrow("property"));
                iteminfo.imagepath= cr.getInt(cr.getColumnIndexOrThrow("imgpath"));
                iteminfo.taxrate= cr.getFloat(cr.getColumnIndexOrThrow("taxrate"));

                if ((!invcontrol) && (iteminfo.invcontrol==1)) invcontrol=true;



                //Log.i("ProductDB","Product name="+iteminfo.name+" InclusiveTax="+iteminfo.taxincl + " Tax Rate="+iteminfo.taxrate);

                //prdlist.add(iteminfo);
                mapprdinfo.put(iteminfo.id, iteminfo);

                cr.moveToNext();
            }
        }

        return invcontrol;//true;

    }

    /*
    public ArrayList<ProductSearchInfo> getProductSearchResult(String productinfo){

        ArrayList<ProductSearchInfo>  itemlist = new  ArrayList<ProductSearchInfo> ();
        Cursor cursor;
        int count=0;

        if (productinfo.equals("all"))
        {
            cursor = productDBConn.rawQuery("SELECT * FROM prodinfo ", null);
            count = cursor.getCount();
            //System.out.println("Sales item count="+count);
        }
        else
        {
            cursor = productDBConn.query(true, "prodinfo", new String[] { "id", "name", "category", "subcategory", "hsncode"}, "name" + " LIKE" + "'%" + productinfo + "%'",
                    null, null, null, null, null);
            count = cursor.getCount();
            //System.out.println("Sales item count="+count);
        }

        if (count >0)
        {
            cursor.moveToFirst();
            for(int i=0;i<count;i++)
            {
                ProductSearchInfo item = new ProductSearchInfo();
                //String qnty;

                item.prid = cursor.getInt(cursor.getColumnIndex("id"));
                item.prtype = cursor.getString(cursor.getColumnIndex("category"));
                item.prsubtype = cursor.getString(cursor.getColumnIndex("subcategory"));
                item.prname =  cursor.getString(cursor.getColumnIndex("name"));
                item.prhsncode = cursor.getString(cursor.getColumnIndex("hsncode"));
                itemlist.add(item);

                cursor.moveToNext();
            }
        }
        return itemlist;
    }

     */

    ////////////////////////////////////////////////////////////////////////////////////////////
    //Price APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    /*public boolean addPriceData(ProductPriceInfo pricedata) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("priceid", pricedata.mPriceId);
        contentValues.put("prodid", pricedata.mProdId);
        contentValues.put("skuid", pricedata.mSKUId);
        contentValues.put("unit", pricedata.mUnit);
        contentValues.put("amount", pricedata.mAmount);
        contentValues.put("saleprice", pricedata.mSalePrice);
        contentValues.put("maxprice", pricedata.mMaxPrice);
        contentValues.put("taxincl", pricedata.mTaxInclusive);
        contentValues.put("manualprice", pricedata.mManualPrice);
        contentValues.put("dbver", pricedata.mDBVer);

        long ret = productDBConn.insert("priceinfo", null, contentValues);
        if (ret > 0)
            return true;
        else
            return false;
    }

    public ArrayList<ProductPriceInfo> getProductPriceData(Integer prodid)
    {
        //new String[]{ "packid", "prodid", "unit" , "unitcount" , "unitamout", "packamount"}
        Cursor cr = productDBConn.query(false, "priceinfo", null,
                                        "prodid=?",new String[]{ prodid.toString()},
                                        null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<ProductPriceInfo> itemlist = new ArrayList<ProductPriceInfo>();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer priceid = cr.getInt(cr.getColumnIndexOrThrow("priceid"));
                //Integer prdid = cr.getInt(cr.getColumnIndex("prodid"));
                Integer skuid = cr.getInt(cr.getColumnIndexOrThrow("skuid"));
                Integer unit = cr.getInt(cr.getColumnIndexOrThrow("unit"));
                String pamnt = cr.getString(cr.getColumnIndexOrThrow("amount"));
                String price = cr.getString(cr.getColumnIndexOrThrow("saleprice"));
                String mrp = cr.getString(cr.getColumnIndexOrThrow("maxprice"));
                Integer taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxincl"));
                Integer manualp = cr.getInt(cr.getColumnIndexOrThrow("manualprice"));
                Integer ver = cr.getInt(cr.getColumnIndexOrThrow("dbver"));

                ProductPriceInfo packinfo = new ProductPriceInfo(priceid, prodid, skuid, unit,
                                                                Float.parseFloat(pamnt),
                                                                Float.parseFloat(price),
                                                                Float.parseFloat(mrp),
                                                                taxincl, manualp, ver);


                itemlist.add(packinfo);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }

    }
    */
    ////////////////////////////////////////////////////////////////////////////////////////////
    //SKU APIs
    ////////////////////////////////////////////////////////////////////////////////////////////
    public boolean addSKUData(ProductSKU skudata) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("skuid", skudata.mSKUId);
        contentValues.put("prodid", skudata.mProdId);
        contentValues.put("skuname", skudata.mName);
        contentValues.put("unit", skudata.mUnit);
        //contentValues.put("unitamount", skudata.mUnitAmount);
        //contentValues.put("unitcount", skudata.mUnitCount);
        contentValues.put("amount", skudata.mAmount);
        contentValues.put("price", skudata.mPrice);
        contentValues.put("maxprice", skudata.mMRP);
        //contentValues.put("taxincl", skudata.mTaxInclusive);
        contentValues.put("size", skudata.mSize);
        contentValues.put("color", skudata.mColor);
        contentValues.put("model", skudata.mModel);
        contentValues.put("dimension", skudata.mDimension);
        contentValues.put("weight", skudata.mWeight);
        contentValues.put("pkgtype", skudata.mPkgType);
        ///contentValues.put("dbver", skudata.mDBVer);

        long ret = productDBConn.insert("skuinfo", null, contentValues);
        if (ret > 0)
            return true;
        else
            return false;
    }


    public ArrayList<ProductSKU> getSKUData(Integer prodid)
    {
        //new String[]{ "packid", "prodid", "unit" , "unitcount" , "unitamout", "packamount"}
        Cursor cr = productDBConn.query(false, "skuinfo", null,
                "prodid=?",new String[]{ prodid.toString()},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<ProductSKU> itemlist = new ArrayList<ProductSKU>();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer skuid = cr.getInt(cr.getColumnIndexOrThrow("skuid"));
                String name = cr.getString(cr.getColumnIndexOrThrow("skuname"));
                String desc = cr.getString(cr.getColumnIndexOrThrow("description"));
                Integer unit = cr.getInt(cr.getColumnIndexOrThrow("unit"));
                //Integer unitamount = cr.getInt(cr.getColumnIndexOrThrow("unitamount"));
                //Integer unitcount = cr.getInt(cr.getColumnIndexOrThrow("unitcount"));
                String amnt = cr.getString(cr.getColumnIndexOrThrow("amount"));

                String price = cr.getString(cr.getColumnIndexOrThrow("price"));
                String mrp = cr.getString(cr.getColumnIndexOrThrow("maxprice"));
                //Integer taxincl = cr.getInt(cr.getColumnIndexOrThrow("taxincl"));

                String size = cr.getString(cr.getColumnIndexOrThrow("size"));
                String color = cr.getString(cr.getColumnIndexOrThrow("color"));
                String model = cr.getString(cr.getColumnIndexOrThrow("model"));
                String dimen = cr.getString(cr.getColumnIndexOrThrow("dimension"));
                String weight = cr.getString(cr.getColumnIndexOrThrow("weight"));
                String pkg = cr.getString(cr.getColumnIndexOrThrow("pkgtype"));
                //Integer ver = cr.getInt(cr.getColumnIndexOrThrow("dbver"));

                ProductSKU packinfo = new ProductSKU(skuid, prodid, name, desc,
                        unit, Float.parseFloat(amnt), Float.parseFloat(price),Float.parseFloat(mrp),
                        size, color, model, dimen, weight, pkg);


                itemlist.add(packinfo);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////
    //category APIs
    ////////////////////////////////////////////////////////////////////////////////////////////

    public int addCategory(String cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", cat);

        long ret = productDBConn.insert("category", null, contentValues);

        return ((int)ret);

    }

    public boolean addCategory(String name, int refund, int exchange, int inventopt)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", name);
        contentValues.put("refundable", refund);
        contentValues.put("exchangeable", exchange);
        contentValues.put("usestock", inventopt);

        long ret = productDBConn.insert("category", null, contentValues);

        if (ret > 0)
            return true;
        else
            return false;

    }

    public boolean addSubCategory(String category, String subcategory)
    {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);

        long ret = productDBConn.insert("subcategory", null, contentValues);

        if (ret > 0)
            return true;
        else
            return false;

    }

    public boolean updateCategoryInfo(String name, int refund, int exchange, int offline, int kotopt, int saleopt, int inventopt, String printopt)
    {
        ContentValues contentValues = new ContentValues();

        //offlinesale , refundable , exchangeable , usekot , nonsale , usestock , printer
        //contentValues.put("type", name);

        contentValues.put("refundable", refund);
        contentValues.put("exchangeable", exchange);
        contentValues.put("usestock", inventopt);

        long ret = productDBConn.update("category", contentValues, "category = ? ", new String[]{String.valueOf(name)});

        if (ret > 0)
            return true;
        else
            return false;

    }

    /*
    public CategoryInfo getCategoryInfo(String name)
    {
        Cursor cr = productDBConn.query(false, "category", new String[]{ "offlinesale","refundable", "exchangeable", "usekot", "nonsale","usestock","printer"}, "category=?",new String[]{ name},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            CategoryInfo catinfo = new CategoryInfo();

            cr.moveToFirst();

            catinfo.name = name;
            catinfo.offline = cr.getInt(cr.getColumnIndex("offlinesale"));
            catinfo.refund = cr.getInt(cr.getColumnIndex("refundable"));
            catinfo.exchnage = cr.getInt(cr.getColumnIndex("exchangeable"));
            catinfo.usekot = cr.getInt(cr.getColumnIndex("usekot"));
            catinfo.nonsale = cr.getInt(cr.getColumnIndex("nonsale"));
            catinfo.useinventory = cr.getInt(cr.getColumnIndex("usestock"));
            catinfo.printer = cr.getString(cr.getColumnIndex("printer"));

            cr.close();
            return catinfo;
        }
        else
        {
            cr.close();
            return null;

        }

    }

     */

    public boolean insertSubCategory  (String category,String subcategory  )
    {
        ////SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("subcategory", subcategory);

        long ret = productDBConn.insert("subcategory", null, contentValues);
        if(ret== -1)
            return false;
        else
            return true;
    }

    public int getCategoryCount()
    {
        long count = DatabaseUtils.queryNumEntries(productDBConn, "category");

        return (int)count;
    }

    public boolean deleteCategory(String category)
    {
        int ret=0;
        ret = productDBConn.delete("category", "category = ? ", new String[] { category });
        if (ret > 0)
        {
            productDBConn.delete("subcategory", "category = ? ", new String[] { category });

            ArrayList<Integer> itemlist = getProductsByType(category);
            if (itemlist != null)
            {
                int itemcnt = itemlist.size();
                for (int i=0; i< itemcnt; i++)
                {
                    int id = itemlist.get(i);

                    deleteproduct(id);
                }

            }
        }

        return false;
    }

    public boolean deleteSubCategory(String subcategory)
    {
        int ret=0;
        ret = productDBConn.delete("subcategory", "subcategory = ? ", new String[] { subcategory });
        if (ret > 0)
        {

            ArrayList<Integer> itemlist = getProductsBySubtype(subcategory);
            if (itemlist != null)
            {
                int itemcnt = itemlist.size();
                for (int i=0; i< itemcnt; i++)
                {
                    int id = itemlist.get(i);

                    deleteproduct(id);
                }

            }

        }

        return false;
    }

    public boolean isCategoryExists(String type)
    {
        boolean exists=false;
        //Cursor res =  typePropDBConn.rawQuery( "select * from categoryinfo where type="+type+"", null );
        Cursor cr = productDBConn.query(false, "category", new String[]{ "category"}, "category=?",new String[]{ type},
                null, null, null, null);
        if (cr.getCount() > 0) exists = true;

        cr.close();

        return exists;
    }

    public boolean getProductsCategories(ArrayList<String> typelist)
    {
        Cursor  cursor = productDBConn.rawQuery("select * from category",null);

        int typecnt = cursor.getCount();
        if ( typecnt <= 0) {
            cursor.close();
            return false;
        }

        cursor.moveToFirst();
        //ArrayList<String> typelist = new ArrayList();

        for(int i=0; i< typecnt; i++)
        {
            String itemtype = cursor.getString(cursor.getColumnIndexOrThrow("category"));

            typelist.add(itemtype);

            cursor.moveToNext();
        }

        cursor.close();

        return true;
    }

    public ArrayList<String> getSubCategories(String category)
    {
        Cursor cursor = productDBConn.query(false, "subcategory", new String[]{ "subcategory"}, "category=?",new String[]{ category},
                null, null, null, null);
        int typecnt = cursor.getCount();
        if ( typecnt <= 0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        ArrayList<String> typelist = new ArrayList();

        for(int i=0; i< typecnt; i++)
        {
            String subcategory = cursor.getString(cursor.getColumnIndexOrThrow("subcategory"));
            typelist.add(subcategory);

            cursor.moveToNext();
        }

        cursor.close();

        return typelist;
    }



    ///////////////////////////////////////////////////////////////////////////////////////
    //MISC APIs
    ///////////////////////////////////////////////////////////////////////////////////////
    private ArrayList<Integer> getProductsByType(String category)
    {
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "category=?",new String[]{ category},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<Integer> itemlist = new ArrayList();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer id = cr.getInt(cr.getColumnIndexOrThrow("id"));
                itemlist.add(id);
                cr.moveToNext();
            }
            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }
    }

    private ArrayList<Integer> getProductsBySubtype(String subcategory)
    {
        Cursor cr = productDBConn.query(false, "prodinfo", new String[]{ "id"}, "subcategory=?",new String[]{ subcategory},
                null, null, null, null);

        int cnt = cr.getCount();
        if(cnt>0 )
        {
            ArrayList<Integer> itemlist = new ArrayList();
            cr.moveToFirst();

            for (int i=0; i < cnt; i++)
            {
                Integer id = cr.getInt(cr.getColumnIndexOrThrow("id"));
                itemlist.add(id);
                cr.moveToNext();
            }

            cr.close();
            return itemlist;
        }
        else
        {
            cr.close();
            return null;
        }
    }
}
