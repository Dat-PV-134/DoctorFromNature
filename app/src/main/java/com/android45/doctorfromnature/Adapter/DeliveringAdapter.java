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
import com.android45.doctorfromnature.Interface.OnClickOkOrder;
import com.android45.doctorfromnature.Interface.OnClickUnFavorite;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.DeliverModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class DeliveringAdapter extends RecyclerView.Adapter<DeliveringAdapter.ViewHolder> {
    Context context;
    List<DeliverModel> deliverModels;

    private OnClickItemDelivering delivering;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public DeliveringAdapter(Context context, List<DeliverModel> deliverModels, OnClickItemDelivering delivering) {
        this.context = context;
        this.deliverModels = deliverModels;
        this.delivering = delivering;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.delivering_item, parent, false);

        DeliveringAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        holder.setOkOrder(new OnClickOkOrder() {
            @Override
            public void onClickOkOrder(View view, int pos) {
                final HashMap<String, Object> cartMapDelivered = new HashMap<>();

                cartMapDelivered.put("productsName", model.getProductsName());
                cartMapDelivered.put("productsPrice", model.getProductsPrice());
                cartMapDelivered.put("productsQuantity", model.getProductsQuantity());
                cartMapDelivered.put("productImg", model.getProductImg());
                cartMapDelivered.put("totalPrice", model.getTotalPrice());
                cartMapDelivered.put("totalProductsPrice", model.getTotalProductsPrice());
                cartMapDelivered.put("customerName", model.getCustomerName());
                cartMapDelivered.put("customerPhoneNumber", model.getCustomerPhoneNumber());
                cartMapDelivered.put("customerAddress", model.getCustomerAddress());
                cartMapDelivered.put("checkDeliver", "1");

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Delivered").add(cartMapDelivered).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Delivering")
                        .document(model.getDocumentID())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    deliverModels.remove(model);
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliverModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgFirstProduct;
        TextView products, totalCost;
        Button btnComfirm;
        RelativeLayout vDelivering;
        OnClickOkOrder okOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFirstProduct = itemView.findViewById(R.id.imgFirstOrderProduct);
            products = itemView.findViewById(R.id.tvOrdersProductsName);
            totalCost = itemView.findViewById(R.id.tvTotalOrderDelivering);
            btnComfirm = itemView.findViewById(R.id.btnOkOrders);

            vDelivering = itemView.findViewById(R.id.vItemDelivering);

            btnComfirm.setOnClickListener(this);
        }

        public void setOkOrder(OnClickOkOrder okOrder) {
            this.okOrder = okOrder;
        }

        @Override
        public void onClick(View v) {
            okOrder.onClickOkOrder(v, getAdapterPosition());
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
