package com.example.petergeras.navcontrol;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.petergeras.navcontrol.Fragments.ProductList;
import com.example.petergeras.navcontrol.Fragments.ProductWebsite;
import com.example.petergeras.navcontrol.Fragments.WatchList;

public class MainActivity extends AppCompatActivity {

    public static WebView webView;

    public static String companyTitle = null;
    public static Drawable companyImage = null;

    public static String productURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_con, new WatchList()).commit();

    }


    //Allows user to go to WatchList to ProductList fragments and back
    public void moveToProduct(){

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_con);
        if (WatchList.class.isInstance(fragment)) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_con, new ProductList())
                    .addToBackStack("WatchList")
                    .commit();
        }
    }


    //Allows user to go to ProductList to ProductWebsite fragments and back
    public void moveToProductURL(){

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_con);
        if (ProductList.class.isInstance(fragment)) {

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_con, new ProductWebsite())
                    .addToBackStack("ProductList")
                    .commit();
        }
    }

    // Uses back button in ActionBar unless in WebView, where it will go through the if statement in
    // onBackPressed().
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
          onBackPressed();
          return (super.onOptionsItemSelected(menuItem));
    }


    // When the user is on the WebView (aka the internet) on ProductWebsite class, they can surf
    // the different websites. The function below uses the phone's back button, where the user can
    // return back to the previous website page until they return to the first website that
    // appeared. In which, the phone's back button will return the user to the ProductList class
    // (if they do not press the back button in the action bar). If the user only goes to the
    // ProductList class and back to the WatchList class, the back button will give a null reference,
    // crashing the app as the user never uses the webView (in the ProductWebsite class). As such the
    // "super.onBackPressed()" is needed to go back without the app crashing. The if statement
    // "webView != null" is needed to be used so that the user can go back when in the
    // ProductWebsite class.
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
