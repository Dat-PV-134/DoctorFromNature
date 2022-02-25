package com.android45.doctorfromnature.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Interface.OnClickItemFavorite;
import com.android45.doctorfromnature.Interface.OnClickUnFavorite;
import com.android45.doctorfromnature.MyCartAdapter;
import com.android45.doctorfromnature.OnClickItemInterface;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.android45.doctorfromnature.models.MyFavoriteModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.ViewHolder> {
    Context context;
    List<MyFavoriteModel> favoriteModels;

    private OnClickItemFavorite onClickItemInterface;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyFavoriteAdapter(Context context, List<MyFavoriteModel> favoriteModels, OnClickItemFavorite onClickItemInterface) {
        this.context = context;
        this.favoriteModels = favoriteModels;
        this.onClickItemInterface = onClickItemInterface;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.my_favorite_item, parent, false);

        MyFavoriteAdapter.ViewHolder viewHolder = new MyFavoriteAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyFavoriteModel model = favoriteModels.get(position);

        Glide.with(context).load(model.getProductImg()).into(holder.img);
        holder.tvName.setText(model.getProductName());
        holder.tvPrice.setText(model.getProductPrice() + "₫");

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemInterface.onClickItemVav(model);
            }
        });

        holder.setUnFavorite(new OnClickUnFavorite() {
            @Override
            public void onClickUnFav(View view, int pos) {
                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Favorite")
                        .document(model.getDocumentID())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    favoriteModels.remove(model);
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img, favorite;
        TextView tvName, tvPrice;
        RelativeLayout layout;
        OnClickUnFavorite unFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.vOnClickFavorite);

            img = itemView.findViewById(R.id.imgMyFavoriteHerbal);
            favorite = itemView.findViewById(R.id.btnFavorite);
            tvName = itemView.findViewById(R.id.tvFavoriteProductName);
            tvPrice = itemView.findViewById(R.id.tvFavoriteProductCost);

            favorite.setOnClickListener(this);
        }

        public void setUnFavorite(OnClickUnFavorite unFavorite) {
            this.unFavorite = unFavorite;
        }

        @Override
        public void onClick(View v) {
            unFavorite.onClickUnFav(v, getAdapterPosition());
        }
    }
}
