package com.fourarc.videostatus.adapter;

/**
 * Created by hsn on 08/10/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.config.CategoryPojo;
import com.fourarc.videostatus.ui.AllCategoryActivity;
import com.fourarc.videostatus.ui.CategoryActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by hsn on 28/09/2017.
 */

public class CategoryVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CategoryPojo> categoryList =new ArrayList<>();
    private Activity activity;

    public CategoryVideoAdapter(List<CategoryPojo> categoryList, Activity activity) {
        this.categoryList = categoryList;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_category_status, null);
                viewHolder = new CategoryHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_category_all,null);
                viewHolder = new AllHolder(v2);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        switch (getItemViewType(position)) {

            case 1: {
                CategoryHolder categoryHolder = (CategoryHolder) holder;

                int   bg_index  = 1;
                for (int i = 0; i < position ; i++) {
                    if (position>11){
                        bg_index = 0;
                    }
                    bg_index ++  ;
                }
                switch (bg_index){
                    case 1:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#F44336"));
                        break;
                    }
                    case 2:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#009688"));
                        break;
                    }
                    case 3:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#FF9800"));
                        break;
                    }
                    case 4:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#3F51B5"));
                        break;
                    }
                    case 5:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#607D8B"));
                        break;
                    }
                    case 6:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#795548"));
                        break;
                    }
                    case 7:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#673AB7"));
                        break;
                    }
                    case 8:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#E91E63"));
                        break;
                    }
                    case 9:{
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                        break;

                    }
                    case 10: {
                        categoryHolder.card_view_category_status.setCardBackgroundColor(Color.parseColor("#0097A7"));
                        break;
                    }


                }

            categoryHolder.text_view_item_category_status.setText(categoryList.get(position).getTag_name());
            Picasso.with(activity.getApplicationContext()).load(categoryList.get(position).getTag_image()).error(R.drawable.placeholder_circle).placeholder(R.drawable.placeholder_circle).into(((CategoryHolder) holder).image_view_item_category_status);
            categoryHolder.text_view_item_category_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("id",categoryList.get(position).getTag_id());
                    intent.putExtra("title",categoryList.get(position).getTag_name());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
            categoryHolder.image_view_item_category_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("id",categoryList.get(position).getTag_id());
                    intent.putExtra("title",categoryList.get(position).getTag_name());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
            break;
        }case 2: {
                AllHolder allHolder = (AllHolder) holder;
                allHolder.relative_layout_show_all_categories_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity.getApplicationContext(), AllCategoryActivity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }
            break;
        }
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        private  CardView card_view_category_status;
        private ImageView image_view_item_category_status;
        private TextView text_view_item_category_status;

        public CategoryHolder(View view) {
            super(view);
            this.card_view_category_status = (CardView) itemView.findViewById(R.id.card_view_category_status);
            this.text_view_item_category_status = (TextView) itemView.findViewById(R.id.text_view_item_category_status);
            this.image_view_item_category_status = (ImageView) itemView.findViewById(R.id.image_view_item_category_status);
        }
    }
    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    private class AllHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relative_layout_show_all_categories_all;

        public AllHolder(View v2) {
            super(v2);
            this.relative_layout_show_all_categories_all=(RelativeLayout) v2.findViewById(R.id.relative_layout_show_all_categories_all);
        }
    }
    @Override
    public int getItemViewType(int position) {
        if (categoryList.get(position)==null){
            return 2;
        }else{
            return 1;
        }
    }
}
