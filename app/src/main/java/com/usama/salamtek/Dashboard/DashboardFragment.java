package com.usama.salamtek.Dashboard;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.usama.salamtek.Interfaces.DashboardListener;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements DashboardListener {
    private RecyclerView recyclerView;
    private ChartAdapter adapter;
    private List<Chart> chartList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        chartList = new ArrayList<>();
        adapter = new ChartAdapter(getActivity().getApplicationContext(), chartList);
        recyclerView = view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(12), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setChartAdapterListener(this);
        recyclerView.setAdapter(adapter);
        prepareAlbums();
        return view;
    }


    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.waterr,
                R.drawable.vitamins2,
                R.drawable.eat,
                R.drawable.bed,
        };

        Chart a = new Chart("Water", covers[0]);
        chartList.add(a);

        Chart b = new Chart("Vitamins", covers[1]);
        chartList.add(b);

        Chart c = new Chart("Eat", covers[2]);
        chartList.add(c);

        Chart d = new Chart("Sleep", covers[3]);
        chartList.add(d);


        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickDashboardItem(int position) {
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("user_data")) {
            if (position == 0){
                Intent chartIntent = new Intent(getActivity(), ChartAction.class);
                chartIntent.putExtra("pregnancyCurrWeek", intent.getLongExtra("pregnancyCurrWeek", 0));
                chartIntent.putExtra("user_data", (User) intent.getParcelableExtra("user_data"));
                chartIntent.putExtra("type","Water");
                startActivity(chartIntent);
            }else if (position == 3){
                Intent chartIntent = new Intent(getActivity(), ChartAction.class);
                chartIntent.putExtra("pregnancyCurrWeek", intent.getLongExtra("pregnancyCurrWeek", 0));
                chartIntent.putExtra("user_data", (User) intent.getParcelableExtra("user_data"));
                chartIntent.putExtra("type","sleep");
                startActivity(chartIntent);
            }else if (position == 1){
                Intent chartIntent = new Intent(getActivity(), ChartActionVE.class);
                chartIntent.putExtra("pregnancyCurrWeek", intent.getLongExtra("pregnancyCurrWeek", 0));
                chartIntent.putExtra("user_data", (User) intent.getParcelableExtra("user_data"));
                chartIntent.putExtra("type","vitamins");
                startActivity(chartIntent);
            }else if (position == 2){
                Intent chartIntent = new Intent(getActivity(), ChartActionVE.class);
                chartIntent.putExtra("pregnancyCurrWeek", intent.getLongExtra("pregnancyCurrWeek", 0));
                chartIntent.putExtra("user_data", (User) intent.getParcelableExtra("user_data"));
                chartIntent.putExtra("type","food");
                startActivity(chartIntent);
            }

        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge{
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}


