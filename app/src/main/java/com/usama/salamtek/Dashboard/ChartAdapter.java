package com.usama.salamtek.Dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.usama.salamtek.R;

import java.util.List;

/**
 * Created by usama on 19/06/2018.
 */

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.MyViewHolder> {

    private Context mContext;
    private List<Chart> chartList;
    private LayoutInflater layoutInflater;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.charttitle);
            thumbnail = (ImageView) view.findViewById(R.id.chartthumbnail);
        }
    }


    public ChartAdapter(Context mContext, List<Chart> chartList) {
        this.mContext = mContext;
        this.chartList = chartList;
        layoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.chart_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Chart chart = chartList.get(position);
        holder.title.setText(chart.getName());
        Glide.with(mContext).load(chart.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }
}
