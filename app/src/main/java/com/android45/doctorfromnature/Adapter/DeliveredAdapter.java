package com.android45.doctorfromnature.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Interface.OnClickItemDelivering;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.DeliverModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DeliveredAdapter extends RecyclerView.Adapter<DeliveredAdapter.ViewHolder>{
    Context context;
    List<DeliverModel> deliverModels;

    private OnClickItemDelivering delivering;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public DeliveredAdapter(Context context, List<DeliverModel> deliverModels, OnClickItemDelivering delivering) {
        this.context = context;
        this.deliverModels = deliverModels;
        this.delivering = delivering;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DeliveredAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.delivered_item, parent, false);

        DeliveredAdapter.ViewHolder viewHolder = new DeliveredAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveredAdapter.ViewHolder holder, int position) {
        DeliverModel model = deliverModels.get(position);

        Glide.with(context).load(getFirstImgUrl(model.getProductImg())).into(holder.imgFirstProduct);
        holder.products.setText("Tên các mặt hàng:" + replaceSymbol(model.getProductsName(), model.getProductsQuantity()));
        holder.totalCost.setText("Tổng thành tiền: " + model.getTotalPrice());

        holder.vDelivering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delivering.onClickItemDelivering(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliverModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFirstProduct;
        TextView products, totalCost;
        Button btnComfirm;
        RelativeLayout vDelivering;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFirstProduct = itemView.findViewById(R.id.imgFirstOrderedProduct);
            products = itemView.findViewById(R.id.tvOrderedProductsName);
            totalCost = itemView.findViewById(R.id.tvTotalOrderDelivered);
            btnComfirm = itemView.findViewById(R.id.btnReBuy);

            vDelivering = itemView.findViewById(R.id.vItemDelivered);
        }
    }

    private String getFirstImgUrl(String s) {
        int end = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.compare(s.charAt(i), ';') == 0) {
                end = i;
                break;
            }
        }
        String kq = s.substring(0, end);
        return kq;
    }

    private String replaceSymbol(String s, String quantity) {
        String[] name = s.split(";", 0);
        String[] number = quantity.split(";", 0);

        String process = "";

        for (int i = 0; i < name.length; i++) {
            process += "\n + " +  name[i] + " x " + number[i];
        }

        return process;
    }
}
