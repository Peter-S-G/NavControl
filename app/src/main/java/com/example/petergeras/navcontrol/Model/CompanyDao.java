package com.example.petergeras.navcontrol.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.petergeras.navcontrol.Helper.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.COMPANY;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.COMPANY_NAME;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.LOGO_URL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.POSITION_WL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.STOCK_PRICE;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.STOCK_SYMBOL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.TABLE_PL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.TABLE_WL;

public class CompanyDao {

    private DatabaseHelper dbHelper;
    private List<Company> companies;

    public CompanyDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        companies = new ArrayList<>();
    }

    // Adds company from WatchList class to SQL
    public boolean addCompany(Company company){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Sees if the company name already exist
        Cursor cursor = db.rawQuery("select * from " + TABLE_WL + " where " + COMPANY + " = ?", new String[]{company.getName()});
        if(cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();


        ContentValues values = new ContentValues();
        values.put(COMPANY, company.getName());
        values.put(STOCK_SYMBOL, company.getStockSymbol());
        values.put(STOCK_PRICE, company.getStockPrice());
        values.put(LOGO_URL, company.getLogoUrl());
        values.put(POSITION_WL, company.getPosition());

        db.insert(TABLE_WL, null, values);

        return true;

    }


    // Gets company data from SQL
    public List<Company> getData() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_WL + " order by " + POSITION_WL, null);

        companies.clear();
        if(cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String stock = cursor.getString(2);
                Double price = cursor.getDouble(3);
                String url = cursor.getString(4);
                Company company = new Company(name, stock, url, price);
                company.setPosition( cursor.getInt(5));
                companies.add(company);

            }while (cursor.moveToNext());
        }
        cursor.close();

        return companies;
    }


    // Updates company data from SQL
    public boolean update(Company company, int selectedIndex){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Sees if the company name already exist if updating the CardView by checking the company
        // name and CardView position when updating
        Cursor cursor = db.rawQuery("select * from " + TABLE_WL + " where COMPANY = ? and POSITION_WL != ?", new String[]{company.getName(), company.getPosition().toString()});
        if(cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();


        Company companyBeforeEdit = getData().get(selectedIndex);

        ContentValues values = new ContentValues();
        values.put(COMPANY, company.getName());
        values.put(STOCK_SYMBOL, company.getStockSymbol());
        values.put(STOCK_PRICE, company.getStockPrice());
        values.put(LOGO_URL, company.getLogoUrl());

        db.update(TABLE_WL, values, "POSITION_WL = ?", new String[] { companyBeforeEdit.getPosition().toString() });

        // Here the SQL is updating the COMPANY_NAME in the ProductDao class. This is done so when
        // the user clicks on the company CardView in the WatchList to the ProductList, the CardViews
        // that are in the ProductList match the company it is associated with
        ContentValues valuesProd = new ContentValues();
        valuesProd.put(COMPANY_NAME, company.getTitle());
        db.update(TABLE_PL, valuesProd, "COMPANY_NAME = ?", new String[] { companyBeforeEdit.getTitle() });


        getData();

        return true;
    }


    // Updates the position of the company(s) to the SQL
    public boolean updateCompanyPosition(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i < companies.size(); i++){
            Company company = companies.get(i);
            ContentValues values = new ContentValues();
            values.put(POSITION_WL, i);
            db.update(TABLE_WL, values,    "COMPANY = ?", new String[] { company.getName() });
         }

        getData();

        return true;
    }


    // Deletes company data from SQL
    public Integer delete(String company) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_WL, "COMPANY = ?", new String[] { company });
    }


}
