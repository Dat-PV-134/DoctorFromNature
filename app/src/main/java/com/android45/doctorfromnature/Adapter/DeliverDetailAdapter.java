package com.android45.doctorfromnature.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.DeliverItemModel;
import com.android45.doctorfromnature.models.DeliverModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DeliverDetailAdapter extends RecyclerView.Adapter<DeliverDetailAdapter.ViewHolder> {
    Context context;
    List<DeliverItemModel> models;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public DeliverDetailAdapter(Context context, List<DeliverItemModel> models) {
        this.context = context;
        this.models = models;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.deliver_item, parent, false);

        DeliverDetailAdapter.ViewHolder viewHolder = new DeliverDetailAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliverItemModel model = models.get(position);

        Glide.with(context).load(model.getProductImg()).into(holder.img);
        holder.name.setText(model.getProductName());
        holder.price.setText(model.getProductPrice() + "₫");
        holder.amount.setText("Số lượng: " + model.getProductQuantity());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, price, amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imgMyDeliver);
            name = itemView.findViewById(R.id.tvDeliverProduct);
            price = itemView.findViewById(R.id.tvDeliverPrice);
            amount = itemView.findViewById(R.id.tvDeliverAmount);
        }
    }
}
