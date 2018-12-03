package com.fourarc.videostatus.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.entity.MoreApp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoreAppAdapter extends BaseAdapter {
    Context context;
    ArrayList<MoreApp> arrayList_jobs;

    public MoreAppAdapter(Context context, ArrayList<MoreApp> items) {
        this.context = context;
        this.arrayList_jobs = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtname,txt_des,txt_mb,install;
        LinearLayout linearLayout ;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.more_app, null);
            holder = new ViewHolder();
            holder.txtname = (TextView) convertView.findViewById(R.id.txt_more);
            holder.txt_mb = (TextView) convertView.findViewById(R.id.txt_mb);
            holder.install = (TextView) convertView.findViewById(R.id.install);
            holder.txt_des = (TextView) convertView.findViewById(R.id.txt_des);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imag_more);
            holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MoreApp rowItem = arrayList_jobs.get(position);

        holder.txtname.setText(rowItem.getAd_title());
        holder.txt_mb.setText(rowItem.getAd_size());
        Picasso.with(context).load(rowItem.getAd_image()).fit().into(holder.imageView);
        holder.txt_des.setText(rowItem.getAd_desc());

        holder.install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(rowItem.getAd_url());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    context.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(rowItem.getAd_url())));
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {return arrayList_jobs.size();}

    @Override
    public Object getItem(int position) {return arrayList_jobs.get(position);}

    @Override
    public long getItemId(int position) {return arrayList_jobs.indexOf(getItem(position));}

}
