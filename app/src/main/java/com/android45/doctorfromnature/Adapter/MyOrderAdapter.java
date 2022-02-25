package com.android45.doctorfromnature.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Interface.BtnDecIncOnClick;
import com.android45.doctorfromnature.MyCartAdapter;
import com.android45.doctorfromnature.OrderPlaceActivity;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    Context context;
    List<MyCartModel> cartModelList;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    long total = 0;

    public MyOrderAdapter(Context context, List<MyCartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.order_place_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel model = cartModelList.get(position);

        Glide.with(context).load(model.getProductImg()).into(holder.img);
        holder.name.setText(model.getProductName());
        holder.price.setText(model.getTotalPrice());
        holder.quantity.setText(model.getTotalQuantity());

        holder.setListener(new BtnDecIncOnClick() {
            @Override
            public void decIncOnClick(View view, int pos, int value) {
                if (value == 1) {
                    if (Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) > 1) {
                        int newValue = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) - 1;
                        cartModelList.get(pos).setTotalQuantity(String.valueOf(newValue));
                    }
                } else if (value == 2) {
                    if (Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) < 10) {
                        int newValue = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) + 1;
                        cartModelList.get(pos).setTotalQuantity(String.valueOf(newValue));
                    }
                } else if (value == 3) {
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Order")
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

                holder.quantity.setText(cartModelList.get(pos).getTotalQuantity());
                String process = cartModelList.get(pos).getProductPrice().replace(".", "");
                long totalPrice = Integer.parseInt(cartModelList.get(pos).getTotalQuantity()) * Long.parseLong(process);
                holder.price.setText(createDotInAdapter(String.valueOf(totalPrice)) + "₫");
                EventBus.getDefault().postSticky(new CountTotalEvent());
            }
        });
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgMyOrderHerbal);
            btnDelete = itemView.findViewById(R.id.btnOrderDelete);
            name = itemView.findViewById(R.id.tvOrderProductName);
            price = itemView.findViewById(R.id.tvOrderProductCost);
            quantity = itemView.findViewById(R.id.tvOrderQuantity);

            btnDecrease = itemView.findViewById(R.id.btnOrderDecrease);
            btnIncrease = itemView.findViewById(R.id.btnOrderIncrease);

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
