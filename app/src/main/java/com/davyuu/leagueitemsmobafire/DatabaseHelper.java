package com.davyuu.leagueitemsmobafire;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/data/com.davyuu.leagueitemsmobafire/databases/";
    private static final String DB_NAME = "league_items_mobafire.sqlite";
    private static final String TABLE_NAME = "league_items_mobafire";

    public static final String COL_NAME = "item_name";
    public static final String COL_TOTAL_PRICE = "item_total_price";
    public static final String COL_RECIPE_PRICE = "item_recipe_price";
    public static final String COL_IMAGE_NAME = "item_image_name";

    private SQLiteDatabase myDatabase;
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public boolean openDataBase() throws SQLException
    {
        if(checkDataBase()){
            String mPath = DB_PATH + DB_NAME;
            //Log.v("mPath", mPath);
            myDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            return myDatabase != null;
        }
        else{
            return false;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_NAME + " ASC", null);
        return results;
    }

    public Cursor getSearchData(String searchText){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE instr(" + COL_NAME
                + ", '" + searchText + "') > 0 ORDER BY " + COL_NAME + " ASC", null);
        return results;
    }

    public LeagueItemList getAllItems(){
        Cursor cursor = getAllData();

        List<String> nameList = new ArrayList<>();
        Map<String, Integer> imageIdMap = new HashMap<>();
        Map<String, String> totalPriceMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getCount() == -1){
                return null;
            }
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int imageIndex = cursor.getColumnIndex(COL_IMAGE_NAME);
            int priceIndex = cursor.getColumnIndex(COL_TOTAL_PRICE);
            String name = cursor.getString(nameIndex);
            String imageName = cursor.getString(imageIndex);
            int imageId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            String totalPrice = cursor.getString(priceIndex);
            nameList.add(name);
            imageIdMap.put(name, imageId);
            totalPriceMap.put(name, totalPrice);
            cursor.moveToNext();
        }
        LeagueItemList itemList = new LeagueItemList(nameList, imageIdMap, totalPriceMap);
        return itemList;
    }

    public List<String> getAllNames(){
        Cursor cursor = getAllData();

        List<String> nameList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int colIndex = cursor.getColumnIndex(COL_NAME);
            if(colIndex == -1){
                return nameList;
            }
            String name = cursor.getString(colIndex);
            nameList.add(name);
            cursor.moveToNext();
        }
        return nameList;
    }

    public List<String> getNames(String searchText){
        Cursor cursor = getSearchData(searchText);

        List<String> nameList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int colIndex = cursor.getColumnIndex(COL_NAME);
            if(colIndex == -1){
                return nameList;
            }
            String name = cursor.getString(colIndex);
            nameList.add(name);
            cursor.moveToNext();
        }
        return nameList;
    }

    public Map<String, Integer> getAllImageIds(){
        Cursor cursor = getAllData();

        Map<String, Integer> imageIdMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int imageIndex = cursor.getColumnIndex(COL_IMAGE_NAME);
            if(nameIndex == -1 || imageIndex == -1){
                return imageIdMap;
            }
            String name = cursor.getString(nameIndex);
            String imageName = cursor.getString(imageIndex);
            int imageId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            imageIdMap.put(name, imageId);
            cursor.moveToNext();
        }

        return imageIdMap;
    }

    public Map<String, String> getAllTotalPrices(){
        Cursor cursor = getAllData();

        Map<String, String> totalPriceMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int priceIndex = cursor.getColumnIndex(COL_TOTAL_PRICE);
            if(nameIndex == -1 || priceIndex == -1){
                return totalPriceMap;
            }
            String name = cursor.getString(nameIndex);
            String totalPrice = cursor.getString(priceIndex);
            totalPriceMap.put(name, totalPrice);
            cursor.moveToNext();
        }
        return totalPriceMap;
    }

    /*public Bitmap getImage(String name){
        Cursor cursor = getAllData();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(cursor.getColumnIndex(COL_1)).equalsIgnoreCase(name)){
                int index = cursor.getColumnIndex(COL_2);
                byte[] image = cursor.getBlob(index);
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                return bmp;
            }
            cursor.moveToNext();
        }
        return null;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}