package com.kys.kyspartners.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kys.kyspartners.AppConfig;
import com.kys.kyspartners.Callbacks.ShopsClickListener;
import com.kys.kyspartners.Information.Products;
import com.kys.kyspartners.R;

import java.util.ArrayList;

/**
 * Created by sanniAdewale on 04/02/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {

    public Context context;
    public LayoutInflater inflater;
    public ArrayList<Products> productArrayList = new ArrayList<>();
    public ShopsClickListener shopsClickListener;

    public ProductAdapter(Context context, ShopsClickListener shopsClickListener) {
        this.context = context;
        this.shopsClickListener = shopsClickListener;
        inflater = LayoutInflater.from(context);
    }

    public void LoadRecyclerView(ArrayList<Products> products) {
        this.productArrayList = products;
        notifyDataSetChanged();
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_layout, parent, false);
        ProductHolder productHolder = new ProductHolder(view);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(final ProductHolder holder, int position) {
        Products current = productArrayList.get(position);
        String url = AppConfig.WEB_URL2 + "images/" + current.product_logo;
        String _url = url.replace(" ", "%20");

        holder.sName.setText(current.product_name);
        holder.sDesc.setText(current.product_description);
        holder.price_stock.setText("Price: ₦" + current.product_price + " | in-stock: " + current.inStock);
        Glide.with(context).load(_url).centerCrop().fitCenter().placeholder(R.drawable.no_logo).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        ImageView iv;
        TextView price_stock;
        TextView sName, sDesc;

        ProductHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.shop_logo);
            price_stock = (TextView) itemView.findViewById(R.id.p_price_stock);
            sName = (TextView) itemView.findViewById(R.id.shop_name);
            sDesc = (TextView) itemView.findViewById(R.id.shop_desc);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.root);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (shopsClickListener != null) {
                        shopsClickListener.onShopsClickListener(v, getPosition());
                    }
                }
            });
        }
    }
}
