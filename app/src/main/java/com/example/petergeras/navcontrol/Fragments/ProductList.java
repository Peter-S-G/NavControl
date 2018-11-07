package com.example.petergeras.navcontrol.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petergeras.navcontrol.Adapter.AdapterProduct;
import com.example.petergeras.navcontrol.Helper.DatabaseHelper;
import com.example.petergeras.navcontrol.Helper.RecyclerItemTouchHelper;
import com.example.petergeras.navcontrol.Helper.RecyclerItemTouchHelperListener;
import com.example.petergeras.navcontrol.MainActivity;
import com.example.petergeras.navcontrol.Model.Product;
import com.example.petergeras.navcontrol.Model.ProductDao;
import com.example.petergeras.navcontrol.R;

import java.util.List;


public class ProductList extends Fragment implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;
    private RelativeLayout rootLayout;
    private RelativeLayout emptyLayoutPL;

    private ImageView upperImage;
    private TextView upperText;

    private Dialog dialog;

    private EditText pNameET;
    private EditText pPriceET;
    private EditText pURLET;
    private EditText pImageET;
    private Button btnOK;
    private Button btnCancel;

    private String pName;
    private String pPrice;
    private String pURL;
    private String pImage;

    private AdapterProduct adapterProduct;

    private FloatingActionButton fab;

    private List<Product> products;
    private ProductDao productDao;

    private boolean undo;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);


        recyclerView = view.findViewById(R.id.rv_product_list);
        rootLayout = view.findViewById(R.id.rootLayoutPL);
        emptyLayoutPL = view.findViewById(R.id.emptylayoutPL);
        fab = view.findViewById(R.id.fab2);
        upperImage = view.findViewById(R.id.imageViewPL);
        upperText = view.findViewById(R.id.textViewPL);


        productDao = new ProductDao(this.getContext());
        products = productDao.getData();


        upperImage.setImageDrawable(MainActivity.companyImage);
        upperText.setText(MainActivity.companyTitle);


        adapterProduct = new AdapterProduct(products, ProductList.this.getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductList.this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ProductList.this.getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapterProduct);

        setEmptyLayoutPL();


        // When the user clicks on the CardView, they can move it up and down, left and right
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);



        // This line will add a back button on the left side in the ActionBar
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        // When the user clicks the FloatingActionButton (FAB) a dialog message will appear to enter
        // information for the CardView.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // The function is shown at the end
                dialogMessagePL();


                // Function changes the EditText of pPriceET (second EditText in the dialog message)
                // when the user starts entering numbers
                pPriceET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }


                    @Override
                    public void afterTextChanged(Editable s) {

                        changePriceText(s);
                    }
                });



                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        pName = pNameET.getText().toString();
                        pPrice = pPriceET.getText().toString().replace("$","");
                        pURL = pURLET.getText().toString();
                        pImage = pImageET.getText().toString();


                        // If the EditTexts are not empty, the CardView will be created for said
                        // product and display on the recyclerView. The information is then added to
                        // Product class and ProductDao class database (SQLite)
                        if (!pName.equals("") && !pPrice.equals("") && !pURL.equals("") && !pImage.equals("")) {

                            Product product = new Product(pName, pImage, pURL, Double.valueOf(pPrice));
                            product.setPosition(products.size());

                            // Check to see if the product name exist or not
                            boolean added = productDao.addProduct(product);
                            if(added){
                                products.add(product);
                                adapterProduct.notifyItemInserted(products.size()-1);
                                setEmptyLayoutPL();
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getActivity(),"Product already exist",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return  view;
    }

    // The function will let the CardView to be moved up and down
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapterProduct.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());

       productDao.updateProductPosition();

        return true;
    }


    // The function will let the CardView to be swiped left and right. Swiping left will delete the
    // CardView and Swiping right will edit the CardView
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterProduct.ViewHolder) {

            // To DELETE: LEFT swipe, direction = 4
            if(direction == 4) {

                final String name = products.get(viewHolder.getAdapterPosition()).getProductName();

                final Product deletedItem = products.get(viewHolder.getAdapterPosition());
                final int deleteIndex = viewHolder.getAdapterPosition();
                adapterProduct.removeItem(deleteIndex);

                setEmptyLayoutPL();


                // If the user swipes to delete the CardView, a SnackBar will appear to give the
                // user the chance to retrieve the CardView. The undo boolean makes a variable to
                // help the determine if the user did delete the CardView or not.
                Snackbar snackbar = Snackbar.make(rootLayout, name + " Deleted", Snackbar.LENGTH_LONG);

                undo = false;

                // If user clicks the undo button in the SnackBar, the CardView will be restored in
                // its original position
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        adapterProduct.restoreItem(deletedItem, deleteIndex);

                        undo = true;

                        setEmptyLayoutPL();
                    }
                });

                snackbar.addCallback(new Snackbar.Callback() {

                    // If the user did not press the undo button in the SnackBar, once the
                    // SnackBar disappears from view, the data from the CardView will be deleted
                    // from the CompanyDao database (SQLite).
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        if (undo == false) {
                            productDao.delete(name);
                        }
                    }
                });

                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            }

            // To EDIT: RIGHT swipe, direction = 8
            if (direction ==8) {

                // Function is shown at the end
                dialogMessagePL();


                // When dialog appears, the company's information will appear in the EditText
                pNameET.setText(products.get(viewHolder.getAdapterPosition()).getProductName());
                pPriceET.setText("$" + products.get(viewHolder.getAdapterPosition()).getProductPrice());
                pURLET.setText(products.get(viewHolder.getAdapterPosition()).getProductUrl());
                pImageET.setText(products.get(viewHolder.getAdapterPosition()).getImageURL());


                // When user swipes RIGHT the foreground CardView will reappear
                final int selectedIndex = viewHolder.getAdapterPosition();
                adapterProduct.notifyItemChanged(selectedIndex);



                pPriceET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        changePriceText(s);
                        }
                    });



                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        pName = pNameET.getText().toString();
                        pPrice = pPriceET.getText().toString().replace("$","");;
                        pURL = pURLET.getText().toString();
                        pImage = pImageET.getText().toString();

                        // If the EditTexts are not empty, the CardView will be updated to
                        // Company class and CompanyDao class database (SQLite)
                        if (!pName.equals("") && !pPrice.equals("") && !pURL.equals("") && !pImage.equals("")) {

                            Product product = new Product(pName, pImage, pURL, Double.valueOf(pPrice));
                            product.setPosition(selectedIndex);

                            // Check to see if the product name exist or not when updating
                            boolean added = productDao.update(product, selectedIndex);
                            if(added){
                                adapterProduct.notifyItemChanged(selectedIndex);
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getActivity(),"Product already exist",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }




    // If there is no CardView in the recyclerView then it will display an text
    public void setEmptyLayoutPL(){
        if (products.isEmpty()){
            emptyLayoutPL.setVisibility(View.VISIBLE);
        }else {
            emptyLayoutPL.setVisibility(View.INVISIBLE);
        }
    }

    // As this function is repeated twice and can be overbearing when scrolling through the class,
    // it is but here to make it easier. The function sets up the Dialog Message when clicking the
    // FAB
    public void dialogMessagePL(){

        dialog = new Dialog(ProductList.this.getActivity());
        dialog.setContentView(R.layout.dialog_add_pl);

        // Dialog message comes up and the lines below allows the dialog message be designed
        // to match the width of the screen and height of the EditText and buttons
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();

        pNameET = dialog.findViewById(R.id.productName);
        pPriceET = dialog.findViewById(R.id.productPrice);
        pURLET = dialog.findViewById(R.id.productURL);
        pImageET = dialog.findViewById(R.id.imageURLPL);

        btnOK = dialog.findViewById(R.id.btn_okPL);
        btnCancel = dialog.findViewById(R.id.btn_cancelPL);
    }


    // When the user clicks on the pPriceET EditText and begins entering the price
    // of the product, it will display the price as "$0.00" where the first number
    // entered start at the 100ths decimal point and moves to the left after
    // additional numbers are entered
    public void changePriceText(Editable s){

        if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
            String userInput = "" + s.toString().replaceAll("[^\\d]", "");
            StringBuilder cashAmountBuilder = new StringBuilder(userInput);

            while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                cashAmountBuilder.deleteCharAt(0);
            }
            while (cashAmountBuilder.length() < 3) {
                cashAmountBuilder.insert(0, '0');
            }
            cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');

            pPriceET.removeTextChangedListener((TextWatcher) ProductList.this.getActivity());
            pPriceET.setText(cashAmountBuilder.toString());

            pPriceET.setTextKeepState("$" + cashAmountBuilder.toString());
            Selection.setSelection(pPriceET.getText(), cashAmountBuilder.toString().length() + 1);

            pPriceET.addTextChangedListener((TextWatcher) ProductList.this.getActivity());
        }

    }



    //When the fragment is destroyed, the back button in the ActionBar get destroyed.
    @Override
    public void onDestroy() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDestroy();
    }
}
