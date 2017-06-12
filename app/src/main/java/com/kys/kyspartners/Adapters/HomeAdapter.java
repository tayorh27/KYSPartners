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
import com.hardsoftstudio.real.textview.views.RealTextView;
import com.kys.kyspartners.Information.Comments;
import com.kys.kyspartners.Information.Log;
import com.kys.kyspartners.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by sanniAdewale on 27/05/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    public Context context;
    public LayoutInflater inflater;
    public ArrayList<Log> ratingArrayList = new ArrayList<>();

    public HomeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void LoadRecyclerView(ArrayList<Log> ratings) {
        this.ratingArrayList = ratings;
        notifyDataSetChanged();
    }


    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myView = inflater.inflate(R.layout.home_layout, parent, false);
        return new HomeHolder(myView);
    }

    @Override
    public void onBindViewHolder(HomeHolder holder, int position) {
        Log current = ratingArrayList.get(position);
        String type = current.type;
        String[] loc = current.user_location.split(",");
        if (type.contentEquals("Shop")) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.tvR.setVisibility(View.GONE);
            holder.tv_title.setText("Type: " + current.type);
            holder.tv_comment.setHtmlFromString("<p style=\"color: #fff;\">Your shop was viewed by a user at <strong style=\"color:#2196F3;\"><i>" + loc[1] + "</i></strong></p>", false);
        } else if (type.contentEquals("Product")) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.tvR.setVisibility(View.GONE);
            holder.tv_title.setText("Type: " + current.type);
            holder.tv_comment.setHtmlFromString("<p style=\"color: #fff;\">Product <strong style=\"color: #2196F3;\"><i>" + current.product_name + "</i></strong> was viewed by a user at <strong style=\"color:#2196F3; \"><i>" + loc[1] + "</i></strong></p>", false);
        } else if (type.contentEquals("Category")) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.tvR.setVisibility(View.GONE);
            holder.tv_title.setText("Type: " + current.type);
            holder.tv_comment.setHtmlFromString("<p style=\"color:#fff;\">Category <strong style=\"color:#2196F3; \"><i>" + current.category_name + "</i></strong> was viewed by a user at <strong style=\"color:#2196F3; \"><i>" + loc[1] + "</i></strong></p>", false);
        } else if (type.contentEquals("Rating")) {
            BigDecimal bd = new BigDecimal(Double.parseDouble(current.rating)).setScale(1, RoundingMode.HALF_UP);
            holder.tv_title.setText("Type: " + current.type);
            holder.ratingBar.setRating(Float.parseFloat(current.rating));
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.tvR.setVisibility(View.VISIBLE);
            holder.tv_comment.setHtmlFromString("<strong style=\"color:#2196F3; \">Comment: </strong><p style=\"color:#fff;\">" + current.comment + "</p>", false);
            holder.tvR.setText("(" + bd.doubleValue() + ")");
        }
        holder.tv_date.setText(current.date);

        //holder.tv_date_user.setText("By " + current.username);

        String username = current.type;
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
    }

    @Override
    public int getItemCount() {
        return ratingArrayList.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder {

        RatingBar ratingBar;
        TextView tv_title, tv_date_user, tv_date, tvR;
        RealTextView tv_comment;
        ImageView imageView;

        HomeHolder(View itemView) {
            super(itemView);

            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            tv_title = (TextView) itemView.findViewById(R.id.rTitle);
            //tv_date_user = (TextView) itemView.findViewById(R.id.rDateUser);
            tv_comment = (RealTextView) itemView.findViewById(R.id.rComment);
            tv_date = (TextView) itemView.findViewById(R.id.rDate);
            tvR = (TextView) itemView.findViewById(R.id.rRate);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewDp);
        }
    }
}
