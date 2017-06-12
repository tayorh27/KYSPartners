package com.kys.kyspartners.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kys.kyspartners.Information.Comments;
import com.kys.kyspartners.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by sanniAdewale on 21/05/2017.
 */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingHolder> {
    public Context context;
    public LayoutInflater inflater;
    public ArrayList<Comments> ratingArrayList = new ArrayList<>();

    public RatingAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void LoadRecyclerView(ArrayList<Comments> ratings) {
        this.ratingArrayList = ratings;
        notifyDataSetChanged();
    }

    @Override
    public RatingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = inflater.inflate(R.layout.shop_rate_layout, parent, false);
        return new RatingHolder(myView);
    }

    @Override
    public void onBindViewHolder(RatingHolder holder, int position) {
        Comments current = ratingArrayList.get(position);
        BigDecimal bd = new BigDecimal(Double.parseDouble(current.star)).setScale(1, RoundingMode.HALF_UP);
        holder.tvR.setText("(" + bd.doubleValue() + ")");
        holder.ratingBar.setRating(Float.parseFloat(current.star));
        holder.tv_title.setText(current.title);
        holder.tv_date_user.setText("By " + current.username);
        holder.tv_comment.setText("Comment: " + current.comment);
        holder.tv_date.setText(current.date);
        String username = current.username;
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        //int color2 = generator.getColor("user@gmail.com")
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .height(48)
                .width(48)
                .toUpperCase()
                .endConfig()
                .buildRoundRect(String.valueOf(username.charAt(0)), color1, 24);
        holder.imageView.setImageDrawable(textDrawable);
        holder.tv_items.setText("Items Bought: " + current.items);
    }

    @Override
    public int getItemCount() {
        return ratingArrayList.size();
    }

    class RatingHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView tv_title, tv_date_user, tv_comment, tv_date, tv_items, tvR;
        ImageView imageView;

        RatingHolder(View itemView) {
            super(itemView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            tv_title = (TextView) itemView.findViewById(R.id.rTitle);
            tv_date_user = (TextView) itemView.findViewById(R.id.rDateUser);
            tv_comment = (TextView) itemView.findViewById(R.id.rComment);
            tv_date = (TextView) itemView.findViewById(R.id.rDate);
            tv_items = (TextView) itemView.findViewById(R.id.rItems);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewDp);
            tvR = (TextView) itemView.findViewById(R.id.rRate);
        }
    }
}
