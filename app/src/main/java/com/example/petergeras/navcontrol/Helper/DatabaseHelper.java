package com.example.petergeras.navcontrol.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    // Creates the SQLite database

    public static final String DATABASE_NAME = "Company.db";

    //Columns of the WatchList table
    public static final String TABLE_WL = "WL_table";
    public static final String ID_WL = "ID_WL";
    public static final String COMPANY = "COMPANY";
    public static final String STOCK_SYMBOL = "STOCK_SYMBOL";
    public static final String STOCK_PRICE = "STOCK_PRICE";
    public static final String LOGO_URL = "LOGO_URL";
    public static final String POSITION_WL = "POSITION_WL";

    //Columns of the ProductList table
    public static final String TABLE_PL = "PL_table";
    public static final String ID_PL = "ID_PL";
    public static final String PRODUCT = "PRODUCT";
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";
    public static final String PRODUCT_URL = "PRODUCT_URL";
    public static final String PRODUCT_IMAGE_URL = "PRODUCT_IMAGE_URL";
    public static final String COMPANY_NAME = "COMPANY_NAME";
    public static final String POSITION_PL = "POSITION_PL";


    //SQL statement of the WL table creation
    private static final String SQL_CREATE_TABLE_WL = "create table " + TABLE_WL + " ("
            + ID_WL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COMPANY + " TEXT NOT NULL, "
            + STOCK_SYMBOL +" TEXT NOT NULL, "
            + STOCK_PRICE +" DOUBLE NOT NULL, "
            + LOGO_URL + " TEXT NOT NULL, "
            + POSITION_WL + " INTEGER NOT NULL)";

    //SQL statement of the PL table creation
    private static final String SQL_CREATE_TABLE_PL = "create table " + TABLE_PL + "("
            + ID_PL +  " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCT + " TEXT NOT NULL, "
            + PRODUCT_PRICE + " TEXT NOT NULL, "
            + PRODUCT_URL + " TEXT NOT NULL, "
            + PRODUCT_IMAGE_URL + " TEXT NOT NULL, "
            + COMPANY_NAME + " TEXT NOT NULL, "
            + POSITION_PL + " INTEGER NOT NULL)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_WL);
        db.execSQL(SQL_CREATE_TABLE_PL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PL);
        onCreate(db);

    }
}
