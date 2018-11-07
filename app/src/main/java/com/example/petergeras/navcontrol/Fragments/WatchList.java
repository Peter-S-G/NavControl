package com.example.petergeras.navcontrol.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.petergeras.navcontrol.Adapter.AdapterCompany;
import com.example.petergeras.navcontrol.Helper.RecyclerItemTouchHelper;
import com.example.petergeras.navcontrol.Helper.RecyclerItemTouchHelperListener;
import com.example.petergeras.navcontrol.Model.Company;
import com.example.petergeras.navcontrol.Model.CompanyDao;
import com.example.petergeras.navcontrol.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class WatchList extends Fragment implements RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;
    private RelativeLayout rootLayout;
    private RelativeLayout emptyLayoutWL;


    private AdapterCompany adapterCompany;

    private FloatingActionButton fab;

    private Dialog dialog;

    private EditText companyET;
    private EditText stockET;
    private EditText logoET;
    private Button btnOK;
    private Button btnCancel;

    private String cName;
    private String cLogo;
    private String cStock;

    private RequestQueue rq;

    private List<Company> companies;
    private CompanyDao companyDao;

    private boolean undo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);


        recyclerView = view.findViewById(R.id.rv_watch_list);
        rootLayout = view.findViewById(R.id.rootLayoutWL);
        emptyLayoutWL = view.findViewById(R.id.emptylayoutWL);
        fab = view.findViewById(R.id.fab);

        rq = Volley.newRequestQueue(WatchList.this.getActivity());

        companyDao = new CompanyDao(this.getContext());
        companies = companyDao.getData();

        adapterCompany = new AdapterCompany(companies, WatchList.this.getActivity());



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(WatchList.this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(WatchList.this.getActivity(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapterCompany);


        setEmptyLayoutWL();

        updateStock();


        // When the user clicks on the CardView, they can move it up and down, left and right
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);



        // When the user clicks the FloatingActionButton (FAB) a dialog message will appear to enter
        // information for the CardView.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Function is shown at the end
                dialogMessageWL();

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cName = companyET.getText().toString();
                        cStock = stockET.getText().toString();
                        cLogo = logoET.getText().toString();


                        // If the EditTexts are not empty, the CardView will be created for said
                        // company and display on the recyclerView. The information is then added to
                        // Company class and CompanyDao class database (SQLite)
                        if (!cName.equals("") && !cStock.equals("") && !cLogo.equals("")) {

                            Company company = new Company(cName, cStock.toUpperCase(), cLogo, new Double(0));
                            company.setPosition(companies.size());

                            // Check to see if the company name exist or not
                            boolean added = companyDao.addCompany(company);
                            if(added){
                                companies.add(company);
                                adapterCompany.notifyItemInserted(companies.size()-1);
                                setEmptyLayoutWL();
                                sendJSONVolley(companies.size()-1);
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getActivity(),"Company already exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
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
        return view;
    }



    // The function will let the CardView to be moved up and down
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapterCompany.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        companyDao.updateCompanyPosition();

        return true;
    }


    // The function will let the CardView to be swiped left and right. Swiping left will delete the
    // CardView and Swiping right will edit the CardView
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof AdapterCompany.ViewHolder) {


            // To DELETE: LEFT swipe, direction = 4
            if (direction == 4) {

                final String name = companies.get(viewHolder.getAdapterPosition()).getName();

                final Company deletedItem = companies.get(viewHolder.getAdapterPosition());
                final int deleteIndex = viewHolder.getAdapterPosition();
                adapterCompany.removeItem(deleteIndex);

                setEmptyLayoutWL();


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

                        adapterCompany.restoreItem(deletedItem, deleteIndex);

                        undo = true;

                        setEmptyLayoutWL();
                    }
                });

                snackbar.addCallback(new Snackbar.Callback() {

                    // If the user did not press the undo button in the SnackBar, once the
                    // SnackBar disappears from view, the data from the CardView will be deleted
                    // from the CompanyDao database (SQLite).
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {

                        if (undo == false) {
                            companyDao.delete(name);
                        }
                    }
                });

                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            }


            // To EDIT: RIGHT swipe, direction = 8
            if (direction == 8){


                // Function is shown at the end
                dialogMessageWL();

                // When dialog appears, the company's information will appear in the EditText
                companyET.setText(companies.get(viewHolder.getAdapterPosition()).getName());
                stockET.setText(companies.get(viewHolder.getAdapterPosition()).getStockSymbol());
                logoET.setText(companies.get(viewHolder.getAdapterPosition()).getImageURL());


                // When user swipes RIGHT the foreground CardView will reappear
                final int selectedIndex = viewHolder.getAdapterPosition();
                adapterCompany.notifyItemChanged(selectedIndex);


                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cName = companyET.getText().toString();
                        cStock = stockET.getText().toString();
                        cLogo = logoET.getText().toString();


                        // If the EditTexts are not empty, the CardView will be updated to
                        // Company class and CompanyDao class database (SQLite)
                        if (!cName.equals("") && !cStock.equals("") && !cLogo.equals("")) {

                            Company company = new Company(cName, cStock.toUpperCase(), cLogo, new Double(companies.get(selectedIndex).getStockPrice()));
                            company.setPosition(selectedIndex);

                            // Check to see if the company name exist or not when updating
                            boolean updated = companyDao.update(company, selectedIndex);
                            if(updated){
                                adapterCompany.notifyItemChanged(selectedIndex);
                                sendJSONVolley(selectedIndex);
                                dialog.dismiss();
                            }
                            else {
                                Toast.makeText(getActivity(),"Company already exist",
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


    // If there is no cardView in the recyclerView then it will display an image and text
    public void setEmptyLayoutWL(){
        if (companies.isEmpty()){
            emptyLayoutWL.setVisibility(View.VISIBLE);
        }else {
            emptyLayoutWL.setVisibility(View.INVISIBLE);
        }
    }


    // The function below sets up the volley to get the JSON for the stock price in the API.
    public void sendJSONVolley(final int position) {

        final Company company = companyDao.getData().get(position);

        String url = "https://www.alphavantage.co/query?function=BATCH_STOCK_QUOTES&symbols="
                + company.getStockSymbol() + "&apikey=U36CYA5ATWW1OJA5";


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("Stock Quotes");

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    Double stock = jsonObject.getDouble("2. price");


                    company.setStockPrice(stock);
                    company.setPosition(position);

                    companyDao.update(company, position);


                    adapterCompany.notifyItemChanged(position);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(jsonObjectRequest);

    }


    // The function below updates the stock price of all the CardViews in the WatchList every
    // 60 seconds
    public void updateStock(){

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                for(int i=0; i<companyDao.getData().size(); i++){
                    sendJSONVolley(i);
                }
                handler.postDelayed(this, 60000);
            }
        }; handler.postDelayed(runnable, 0);
    }


    // As this function is repeated twice and can be overbearing when scrolling through the class,
    // it is but here to make it easier. The function sets up the Dialog Message when clicking the
    // FAB
    public void dialogMessageWL(){

        dialog = new Dialog(WatchList.this.getActivity());
        dialog.setContentView(R.layout.dialog_add_wl);

        // Dialog message comes up and the lines below allows the dialog message be designed
        // to match the width of the screen and height of the EditText and buttons
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();


        // set the custom dialog components - texts and image
        companyET = dialog.findViewById(R.id.companyName);
        stockET = dialog.findViewById(R.id.stockName);
        logoET = dialog.findViewById(R.id.imageURLWL);

        btnOK = dialog.findViewById(R.id.btn_okWL);
        btnCancel = dialog.findViewById(R.id.btn_cancelWL);
    }

}
