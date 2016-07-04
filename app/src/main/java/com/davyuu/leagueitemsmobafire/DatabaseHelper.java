package com.davyuu.leagueitemsmobafire;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String DB_NAME = "league_items.sqlite";

    private static final String TABLE_DETAILS = "league_items_mobafire";
    private static final String TABLE_IMAGES = "league_items_mobafire_image";
    private static final String TABLE_CATEGORY = "league_items_mobafire_category";

    public static final String COL_NAME = "item_name";
    public static final String COL_TOTAL_PRICE = "item_total_price";
    public static final String COL_RECIPE_PRICE = "item_recipe_price";
    public static final String COL_IMAGE_NAME = "item_image_src";
    public static final String COL_FILTER = "item_filter";

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

    public Cursor getCursorDetails(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " ORDER BY " + COL_NAME + " ASC", null);
        return results;
    }

    public Cursor getCursorNames(){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder("SELECT ")
                .append(COL_NAME).append("\n")
                .append("FROM ")
                .append(TABLE_DETAILS).append("\n")
                .append("ORDER BY ")
                .append(COL_NAME).append(" ASC");
        Cursor results = db.rawQuery(sb.toString(), null);
        return results;
    }

    public Cursor getCursorImages(){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder("SELECT ")
                .append("*").append("\n")
                .append("FROM ")
                .append(TABLE_IMAGES).append("\n")
                .append("ORDER BY ")
                .append(COL_NAME).append(" ASC");
        Cursor results = db.rawQuery(sb.toString(), null);
        return results;
    }

    public Cursor getCursorCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder("SELECT ")
                .append("*").append("\n")
                .append("FROM ")
                .append(TABLE_CATEGORY).append("\n")
                .append("ORDER BY ")
                .append(COL_NAME).append(" ASC");
        Cursor results = db.rawQuery(sb.toString(), null);
        return results;
    }

    /*public Cursor getCursorImages(){
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder sb = new StringBuilder("SELECT ")
                .append(TABLE_DETAILS).append(".").append(COL_NAME).append(",")
                .append(TABLE_IMAGES).append(".").append(COL_IMAGE_NAME).append("\n")
                .append("FROM ")
                .append(TABLE_DETAILS).append(",").append(TABLE_IMAGES).append("\n")
                .append("WHERE ")
                .append(TABLE_DETAILS).append(".").append(COL_NAME).append(" = ")
                .append(TABLE_IMAGES).append(".").append(COL_NAME).append("\n")
                .append("ORDER BY ")
                .append(TABLE_DETAILS).append(".").append(COL_NAME).append(" ASC");
        Cursor results = db.rawQuery(sb.toString(), null);
        return results;
    }*/

    public Cursor getSearchData(String searchText){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + TABLE_DETAILS + " WHERE instr(" + COL_NAME
                + ", '" + searchText + "') > 0 ORDER BY " + COL_NAME + " ASC", null);
        return results;
    }

    public LeagueItemList getAllItems(){
        Cursor cursor = getCursorDetails();

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
        Cursor detailsCursor = getCursorNames();
        List<String> nameList = new ArrayList<>();

        detailsCursor.moveToFirst();
        while (!detailsCursor.isAfterLast()) {
            int colIndex = detailsCursor.getColumnIndex(COL_NAME);
            if(colIndex == -1){
                return nameList;
            }
            String name = detailsCursor.getString(colIndex);
            nameList.add(name);
            detailsCursor.moveToNext();
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

    public Map<String, List<String>> getAllCategories(){
        Cursor cursor = getCursorCategory();
        Map<String, List<String>> categoryMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int filterIndex = cursor.getColumnIndex(COL_FILTER);
            if(nameIndex == -1 || filterIndex == -1){
                return categoryMap;
            }
            String name = cursor.getString(nameIndex);
            String filterAll = cursor.getString(filterIndex);
            List<String> filterList = formatFilter(filterAll);
            categoryMap.put(name, filterList);
            cursor.moveToNext();
        }

        return categoryMap;
    }

    private List<String> formatFilter(String filterAll){
        String[] filterArray = filterAll.split("\\t\\t\\t\\t\\t| ");
        List<String> filterList = new ArrayList<>();
        for (String filter : filterArray){
            if(filter.startsWith("filter")){
                filter = filter.replace("filter-","");
                filterList.add(filter);
            }
        }
        return filterList;
    }

    public Map<String, Integer> getAllImageIds(){
        Cursor cursor = getCursorImages();
        Map<String, Integer> imageIdMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int imageIndex = cursor.getColumnIndex(COL_IMAGE_NAME);
            if(nameIndex == -1 || imageIndex == -1){
                return imageIdMap;
            }
            String name = cursor.getString(nameIndex);
            String imageUrl = cursor.getString(imageIndex);
            String imageName = formatImageUrl(imageUrl);
            int imageId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
            imageIdMap.put(name, imageId);
            cursor.moveToNext();
        }

        return imageIdMap;
    }

    private String formatImageUrl(String imageURL){
        String[] imageUrlArray = imageURL.split("/");
        String imageName = imageUrlArray[imageUrlArray.length-1];
        imageName = imageName.replace("-", "_");
        imageName = imageName.split("\\.")[0];
        return imageName;
    }

    public Map<String, String> getAllTotalPrices(){
        Cursor cursor = getCursorDetails();
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

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}