package com.example.petergeras.navcontrol.Helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.petergeras.navcontrol.Adapter.AdapterCompany;
import com.example.petergeras.navcontrol.Adapter.AdapterProduct;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if(listener != null){
            listener.onMove(recyclerView, viewHolder, target);
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener != null){
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }


    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        if( viewHolder instanceof  AdapterCompany.ViewHolder ) {
            View foregroundView = ((AdapterCompany.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        if( viewHolder instanceof  AdapterProduct.ViewHolder ) {
            View foregroundView = ((AdapterProduct.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }


    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){

            if( viewHolder instanceof  AdapterCompany.ViewHolder ) {
                View foregroundView = ((AdapterCompany.ViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            if( viewHolder instanceof  AdapterProduct.ViewHolder ) {
                View foregroundView = ((AdapterProduct.ViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }
        }
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        // WATCHLIST class
        if( viewHolder instanceof  AdapterCompany.ViewHolder ) {
            View foregroundView = ((AdapterCompany.ViewHolder)viewHolder).viewForeground;
            View backgroundView = ((AdapterCompany.ViewHolder)viewHolder).viewBackground;
            View deleteText = ((AdapterCompany.ViewHolder)viewHolder).deleteText;
            View editText = ((AdapterCompany.ViewHolder)viewHolder).editText;
            View deleteImage = ((AdapterCompany.ViewHolder)viewHolder).deleteImage;
            View editImage = ((AdapterCompany.ViewHolder)viewHolder).editImage;



            // If the CardView is swiped to the RIGHT, it will change the background color to BLUE.
            // The delete and trashcan (delete image) will go invisible.
            if (dX > 0){
                Log.i("Dx", "Dx------> "+dX);
                backgroundView.setBackgroundColor(Color.BLUE);
                editText.setVisibility(View.VISIBLE);
                editImage.setVisibility(View.VISIBLE);
                deleteText.setVisibility(View.INVISIBLE);
                deleteImage.setVisibility(View.INVISIBLE);
            }
            // If the CardView is swiped to the LEFT, it will change the background color to RED
            // The edit and the pencil (edit image) will go invisible.
            if (dX < 0) {
                backgroundView.setBackgroundColor(Color.RED);
                deleteText.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);
                editImage.setVisibility(View.INVISIBLE);
            }

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, 0, actionState, isCurrentlyActive);
        }



        // PRODUCTLIST class
        if( viewHolder instanceof  AdapterProduct.ViewHolder ) {
            View foregroundView = ((AdapterProduct.ViewHolder)viewHolder).viewForeground;
            View backgroundView =((AdapterProduct.ViewHolder)viewHolder).viewBackground;
            View deleteText = ((AdapterProduct.ViewHolder)viewHolder).deleteText;
            View editText = ((AdapterProduct.ViewHolder)viewHolder).editText;
            View deleteImage = ((AdapterProduct.ViewHolder)viewHolder).deleteImage;
            View editImage = ((AdapterProduct.ViewHolder)viewHolder).editImage;


            // If the CardView is swiped to the RIGHT, it will change the background color to BLUE.
            // The delete and trashcan (delete image) will go invisible.
            if (dX > 0){
                backgroundView.setBackgroundColor(Color.BLUE);
                editText.setVisibility(View.VISIBLE);
                editImage.setVisibility(View.VISIBLE);
                deleteText.setVisibility(View.INVISIBLE);
                deleteImage.setVisibility(View.INVISIBLE);
            }
            // If the CardView is swiped to the LEFT, it will change the background color to RED
            // The edit and the pencil (edit image) will go invisible.
            if (dX < 0) {
                backgroundView.setBackgroundColor(Color.RED);
                deleteText.setVisibility(View.VISIBLE);
                deleteImage.setVisibility(View.VISIBLE);
                editText.setVisibility(View.INVISIBLE);
                editImage.setVisibility(View.INVISIBLE);
            }

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, 0, actionState, isCurrentlyActive);
        }
    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if( viewHolder instanceof  AdapterCompany.ViewHolder ) {
            View foregroundView = ((AdapterCompany.ViewHolder)viewHolder).viewForeground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, 0, actionState, isCurrentlyActive);
        }

        if( viewHolder instanceof  AdapterProduct.ViewHolder ) {
            View foregroundView = ((AdapterProduct.ViewHolder)viewHolder).viewForeground;

            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, 0, actionState, isCurrentlyActive);
        }
    }
}
