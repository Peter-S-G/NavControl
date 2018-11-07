package com.example.petergeras.navcontrol.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.petergeras.navcontrol.MainActivity;
import com.example.petergeras.navcontrol.Model.Product;
import com.example.petergeras.navcontrol.R;

import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {

    private List<Product> products;
    private Context context;

    RequestQueue rq;



    public AdapterProduct(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
        rq = Volley.newRequestQueue(context);
    }


    @Override
    public AdapterProduct.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdapterProduct.ViewHolder holder, final int position) {

        Product product = products.get(position);

        holder.product = product;

        holder.textViewTitle.setText(product.getTitle());
        holder.textViewSubtitle.setText(product.getSubtitle());

        // Sets the product image to the ImageView
        ImageRequest request = new ImageRequest(
                product.getImageURL(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Drawable d = new BitmapDrawable(context.getResources(), response);
                holder.imageViewIcon.setImageDrawable(d);
            }
        }, 100,100, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGBA_F16, null);

        rq.add(request);

    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    // Move CardView from old position to new position
    public void moveItem(int oldPos, int newPos) {

        Product product = products.get(oldPos);
        products.remove(oldPos);
        products.add(newPos, product);
        notifyItemMoved(oldPos, newPos);
    }

    // Deletes CardView
    public void removeItem(int position) {
        products.remove(position);
        notifyItemRemoved(position);
    }

    // Restores CardView if the user presses the undo button in the SnackBar
    public void restoreItem(Product product, int position) {
        products.add(position, product);
        notifyItemInserted(position);

    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public Product product;

        public TextView textViewTitle;
        public TextView textViewSubtitle;
        public ImageView imageViewIcon;

        public TextView deleteText;
        public TextView editText;
        public ImageView deleteImage;
        public ImageView editImage;

        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;



        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.title);
            textViewSubtitle = itemView.findViewById(R.id.subtitle);
            imageViewIcon = itemView.findViewById(R.id.image);

            deleteText = itemView.findViewById(R.id.delete_text);
            editText = itemView.findViewById(R.id.edit_text);
            deleteImage = itemView.findViewById(R.id.delete_icon);
            editImage = itemView.findViewById(R.id.edit_icon);

            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);


            // When the front CardView is click, it will bring the user to the ProductWebsite class
            // where the product's website (URL) will be uploaded on the screen.
            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity mainActivity = (MainActivity) v.getContext();
                    MainActivity.productURL = product.getProductUrl();
                    mainActivity.moveToProductURL();
                }
            });
        }
    }
}
