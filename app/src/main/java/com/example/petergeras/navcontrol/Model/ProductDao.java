package com.example.petergeras.navcontrol.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.petergeras.navcontrol.Helper.DatabaseHelper;
import com.example.petergeras.navcontrol.MainActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.COMPANY_NAME;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.POSITION_PL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.PRODUCT;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.PRODUCT_IMAGE_URL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.PRODUCT_PRICE;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.PRODUCT_URL;
import static com.example.petergeras.navcontrol.Helper.DatabaseHelper.TABLE_PL;

public class ProductDao {

    private DatabaseHelper dbHelper;
    private List<Product> products;

    public ProductDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        products = new ArrayList<>();
    }

    // Adds product from ProductList class to SQL
    public boolean addProduct(Product product){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Sees if the product name already exist
        Cursor cursor = db.rawQuery("select * from " +  TABLE_PL + " WHERE " + PRODUCT + " = ?", new String[]{ product.getProductName() });
        if(cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();


        ContentValues values = new ContentValues();
        values.put(PRODUCT, product.getProductName());
        values.put(PRODUCT_PRICE, product.getProductPrice());
        values.put(PRODUCT_URL, product.getProductUrl());
        values.put(PRODUCT_IMAGE_URL, product.getImageURL());
        values.put(POSITION_PL, product.getPosition());

        // Here I set the product's company name to see if the company matches the product
        product.setCompanyName(MainActivity.companyTitle);
        values.put(COMPANY_NAME, product.getCompanyName() );

        db.insert(TABLE_PL, null, values);

        return true;
    }


    // Gets product data from SQL
    public List<Product> getData() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // When we get the data of the product we make sure that it is from the same company
        Cursor cursor = db.rawQuery("select * from " + TABLE_PL + " WHERE " + COMPANY_NAME + " = ?  order by " + POSITION_PL, new String[] {MainActivity.companyTitle} );

        products.clear();
        if(cursor.moveToFirst()) {
            do {
                String product_name = cursor.getString(1);
                Double price = cursor.getDouble(2);
                String product_url = cursor.getString(3);
                String image_url = cursor.getString(4);
                Product product = new Product(product_name, image_url, product_url, price);
                product.setPosition( cursor.getInt(5));
                products.add(product);

            }while (cursor.moveToNext());
        }
        cursor.close();

        return products;
    }


    // Updates product data from SQL
    public boolean update(Product product, int selectedIndex) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Sees if the product name already exist if updating the CardView by checking the product
        // name and CardView position when updating
        Cursor cursor = db.rawQuery("select * from " + TABLE_PL + " WHERE PRODUCT = ? and POSITION_PL != ?", new String[]{product.getProductName(), product.getPosition().toString()});
        if(cursor.moveToFirst()){
            cursor.close();
            return false;
        }
        cursor.close();


        Product productBeforeEdit = getData().get(selectedIndex);

        ContentValues values = new ContentValues();
        values.put(PRODUCT, product.getProductName());
        values.put(PRODUCT_PRICE, product.getProductPrice());
        values.put(PRODUCT_URL, product.getProductUrl());
        values.put(PRODUCT_IMAGE_URL, product.getImageURL());

        db.update(TABLE_PL, values, "POSITION_PL = ?", new String[] { productBeforeEdit.getPosition().toString() });

        getData();

        return true;
    }


    // Updates the position of the product(s) to the SQL
    public boolean updateProductPosition(){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i=0; i < products.size(); i++){
            Product product = products.get(i);
            ContentValues values = new ContentValues();
            values.put(POSITION_PL, i);
            db.update(TABLE_PL, values,    "PRODUCT = ?", new String[] { product.getProductName() });
        }

        getData();

        return true;
    }


    // Deletes company data from SQL
    public Integer delete(String product) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_PL, "PRODUCT = ?", new String[] { product });
    }

}
