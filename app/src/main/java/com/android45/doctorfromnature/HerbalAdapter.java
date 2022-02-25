package com.android45.doctorfromnature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.List;

public class HerbalAdapter extends RecyclerView.Adapter<HerbalAdapter.ViewHolder> {
    private List<Herbal> herbalList;
    private OnClickItemInterface onClickItemInterface;

    private Context context;

    public HerbalAdapter(List<Herbal> herbalList, OnClickItemInterface onClickItemInterface, Context context) {
        this.herbalList = herbalList;
        this.onClickItemInterface = onClickItemInterface;
        this.context = context;
    }

    @NonNull
    @Override
    public HerbalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.icon_herbal, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HerbalAdapter.ViewHolder holder, int position) {
        final Herbal herbal = herbalList.get(position);

        Glide.with(context).load(herbal.getImg()).into(holder.imgHerbal);
        holder.tvHerbalName.setText(herbal.getName());
        holder.tvHerbalPrice.setText(herbal.getPrice() + "₫");

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemInterface.onClickItemHerbal(herbal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return herbalList.size();
    }

    public class ViewHolder  extends  RecyclerView.ViewHolder {
        ImageView imgHerbal;
        private TextView tvHerbalName, tvHerbalPrice;
        private RelativeLayout layoutItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layoutItem = itemView.findViewById(R.id.layoutHerbal);
            imgHerbal = itemView.findViewById(R.id.imgHerbal);
            tvHerbalName = itemView.findViewById(R.id.tvHerbalName);
            tvHerbalPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
