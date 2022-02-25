package com.android45.doctorfromnature;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Interface.OnClickItemInCart;
import com.android45.doctorfromnature.databinding.CartFragmentBinding;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    CartFragmentBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore db;

    MyCartAdapter cartAdapter;
    List<MyCartModel> cartModelList;

    public static CartFragment newInstance() {

        Bundle args = new Bundle();

        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.cart_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(), cartModelList, new OnClickItemInCart() {
            @Override
            public void onClickItemInCart(MyCartModel model) {
                goToDetailFromCart(model);
            }
        });

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        binding.rvMyCart.setLayoutManager(layoutManager02);
        binding.rvMyCart.setAdapter(cartAdapter);

        binding.cartProgressBar.setVisibility(View.VISIBLE);
        binding.tvNothing.setVisibility(View.GONE);
        binding.tvTotal.setVisibility(View.GONE);
        binding.tvTotalCost.setVisibility(View.GONE);
        binding.btnBuy.setVisibility(View.GONE);

//        LocalBroadcastManager.getInstance(getActivity())
//                .registerReceiver(receiver, new IntentFilter("totalCost"));

        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        String documentId = documentSnapshot.getId();

                        MyCartModel model = documentSnapshot.toObject(MyCartModel.class);

                        model.setDocumentID(documentId);

                        cartModelList.add(model);
                        cartAdapter.notifyDataSetChanged();
                    }

                    if (cartModelList.size() == 0) {
                        binding.tvNothing.setVisibility(View.VISIBLE);
                        binding.cartProgressBar.setVisibility(View.GONE);
                    } else {
                        binding.tvNothing.setVisibility(View.GONE);
                        binding.tvTotal.setVisibility(View.VISIBLE);
                        binding.tvTotalCost.setVisibility(View.VISIBLE);
                        binding.btnBuy.setVisibility(View.VISIBLE);
                        binding.cartProgressBar.setVisibility(View.GONE);
                    }

                    calculateTotalCost(cartModelList);
                }
            }
        });

        binding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderPlaceActivity.class);
                intent.putExtra("itemList", (Serializable) cartModelList);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void goToDetailFromCart(MyCartModel model) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("Products");
        Query query = yourCollRef.whereEqualTo("name", model.getProductName());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Herbal herbal = document.toObject(Herbal.class);

                        bundle.putSerializable("Herbal", herbal);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {

                }
            }
        });
    }


//    public BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            long totalCost = intent.getLongExtra("totalCost", 0);
//            String process = createDot(String.valueOf(totalCost));
//            binding.tvTotalCost.setText(process + );
//        }
//    };

    String createDot(String s) {
        long value = Integer.parseInt(s);
        s = String.format("%,d", value);
        return s;
    }

    public void calculateTotalCost(List<MyCartModel> cartModelList) {
        long totalCost = 0;
        for (MyCartModel model : cartModelList) {
            totalCost += Long.parseLong(model.getProductPrice().replace(".", "")) * Long.parseLong(model.getTotalQuantity());
        }

        binding.tvTotalCost.setText(createDot(String.valueOf(totalCost)) + getResources().getString(R.string.don_vi));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventCountTotal(CountTotalEvent event) {
        if (event != null) {
            calculateTotalCost(cartModelList);
        }
    }

    //    void setTotalCost() {
//        long value = 0;
//        for (int i = 0; i < cartModelList.size(); i++) {
//            MyCartModel model = cartModelList.get(i);
//            String process = model.getTotalPrice();
//            process = process.replace(".", "");
//            value += Integer.parseInt(process);
//        }
//        binding.tvTotalCost.setText(createDot(String.valueOf(value)) + getResources().getString(R.string.don_vi));
//    }
//
//    String createDot(String s) {
//        long value = Integer.parseInt(s);
//        s = String.format("%,d", value);
//        return s;
//    }
}
