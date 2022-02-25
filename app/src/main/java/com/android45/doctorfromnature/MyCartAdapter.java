package com.android45.doctorfromnature;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Interface.BtnDecIncOnClick;
import com.android45.doctorfromnature.Interface.OnClickItemInCart;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    Context context;
    List<MyCartModel> cartModelList;

    private OnClickItemInCart onClickItemInCart;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    long total = 0;

    public MyCartAdapter(Context context, List<MyCartModel> cartModelList, OnClickItemInCart onClickItemInCart) {
        this.context = context;
        this.cartModelList = cartModelList;
        this.onClickItemInCart = onClickItemInCart;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.my_cart_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartAdapter.ViewHolder holder, int position) {
        MyCartModel model = cartModelList.get(position);

        Glide.with(context).load(model.getProductImg()).into(holder.img);
        holder.name.setText(model.getProductName());
        holder.price.setText(model.getTotalPrice());
        holder.quantity.setText(model.getTotalQuantity());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemInCart.onClickItemInCart(model);
            }
        });

//        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        holder.setListener(new BtnDecIncOnClick() {
            @Override
            public void decIncOnClick(View view, int pos, int value) {
                if (value == 1) {
                    if (Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) > 1) {
                        int newValue = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) - 1;
                        cartModelList.get(pos).setTotalQuantity(String.valueOf(newValue));

                        Map<String, Object> map = new HashMap<>();
                        map.put("totalQuantity", String.valueOf(newValue));

                        holder.quantity.setText(cartModelList.get(pos).getTotalQuantity());
                        String process = cartModelList.get(pos).getProductPrice().replace(".", "");
                        long totalPrice = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) * Long.parseLong(process);

                        cartModelList.get(pos).setTotalPrice(createDotInAdapter(String.valueOf(totalPrice)));

                        holder.price.setText(createDotInAdapter(String.valueOf(totalPrice)) + "₫");

                        map.put("totalPrice", createDotInAdapter(String.valueOf(totalPrice)) + "₫");

                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("AddToCart")
                                .document(model.getDocumentID())
                                .update(map);
                    }
                } else if (value == 2) {
                    if (Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) < 10) {
                        int newValue = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) + 1;
                        cartModelList.get(pos).setTotalQuantity(String.valueOf(newValue));

                        Map<String, Object> map = new HashMap<>();
                        map.put("totalQuantity", String.valueOf(newValue));

                        holder.quantity.setText(cartModelList.get(pos).getTotalQuantity());
                        String process = cartModelList.get(pos).getProductPrice().replace(".", "");
                        long totalPrice = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) * Long.parseLong(process);
                        holder.price.setText(createDotInAdapter(String.valueOf(totalPrice)) + "₫");

                        cartModelList.get(pos).setTotalPrice(createDotInAdapter(String.valueOf(totalPrice)));

                        map.put("totalPrice", createDotInAdapter(String.valueOf(totalPrice)) + "₫");

                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("AddToCart")
                                .document(model.getDocumentID())
                                .update(map);
                    }
                } else if (value == 3) {
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("AddToCart")
                            .document(model.getDocumentID())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cartModelList.remove(model);
                                        notifyDataSetChanged();
                                        EventBus.getDefault().postSticky(new CountTotalEvent());
                                    }
                                }
                            });
                }

                EventBus.getDefault().postSticky(new CountTotalEvent());
            }
        });

//        total += Long.parseLong(model.getProductPrice().replace(".", "")) * Long.parseLong(model.getTotalQuantity());
//
//        Intent intent = new Intent("totalCost");
//        intent.putExtra("totalCost", total);
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img, btnDelete;
        TextView name, price, quantity;
        Button btnDecrease, btnIncrease;
        BtnDecIncOnClick listener;

        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.vOnClickItemCart);

            img = itemView.findViewById(R.id.imgMyCartHerbal);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            name = itemView.findViewById(R.id.tvProductName);
            price = itemView.findViewById(R.id.tvProductCost);
            quantity = itemView.findViewById(R.id.tvQuantity);

            btnDecrease = itemView.findViewById(R.id.btnDecreaseCart);
            btnIncrease = itemView.findViewById(R.id.btnIncreaseCart);

            btnDecrease.setOnClickListener(this);
            btnIncrease.setOnClickListener(this);
            btnDelete.setOnClickListener(this);
        }

        public void setListener(BtnDecIncOnClick listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (v == btnDecrease) {
                listener.decIncOnClick(v, getAdapterPosition(), 1);
            } else if (v == btnIncrease) {
                listener.decIncOnClick(v, getAdapterPosition(), 2);
            } else if (v == btnDelete) {
                listener.decIncOnClick(v, getAdapterPosition(), 3);
            }
        }
    }

    String createDotInAdapter(String s) {
        long value = Integer.parseInt(s);
        s = String.format("%,d", value);
        return s;
    }

}
