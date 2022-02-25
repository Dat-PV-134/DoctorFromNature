package com.android45.doctorfromnature.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android45.doctorfromnature.Adapter.DeliverDetailAdapter;
import com.android45.doctorfromnature.OrderPlaceActivity;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.databinding.ActivityDeliverDetailBinding;
import com.android45.doctorfromnature.models.DeliverItemModel;
import com.android45.doctorfromnature.models.DeliverModel;
import com.android45.doctorfromnature.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeliverDetailActivity extends AppCompatActivity {
    ActivityDeliverDetailBinding binding;

    DeliverModel model = new DeliverModel();
    int check;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    List<DeliverItemModel> itemModels;
    DeliverDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deliver_detail);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        model = (DeliverModel) getIntent().getSerializableExtra("Deliver");

        if (model.getCheckDeliver().equals("1")) {
            binding.btnSubmitDeliver.setText("Mua lại");
        }

        binding.tvDeliverPerson.setText(model.getCustomerName());
        binding.tvDeliverNumber.setText(model.getCustomerPhoneNumber());
        binding.tvDeliverAddress.setText(model.getCustomerAddress());
        binding.productDeliverCostMoney.setText(model.getTotalProductsPrice());
        binding.TotalDeliverMoney.setText(model.getTotalPrice());

        itemModels = new ArrayList<>();

        addItem(model);

        adapter = new DeliverDetailAdapter(getBaseContext(), itemModels);

        GridLayoutManager layoutManager02 = new GridLayoutManager(getBaseContext(), 1, RecyclerView.VERTICAL, false);
        binding.rvMyDeliverProducts.setLayoutManager(layoutManager02);
        binding.rvMyDeliverProducts.setAdapter(adapter);

        binding.btnComebackInDeliverDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (model.getCheckDeliver().equals("1")) {
            return;
        }

        binding.btnSubmitDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackToDelivering();
            }
        });
    }

    private void addItem(DeliverModel model) {
        String[] itemImg = model.getProductImg().split(";", 0);
        String[] itemName = model.getProductsName().split(";", 0);
        String[] itemPrice = model.getProductsPrice().split(";", 0);
        String[] itemQuantity = model.getProductsQuantity().split(";", 0);

        for (int i = 0; i < itemImg.length; i++) {
            DeliverItemModel model1 =  new DeliverItemModel(itemImg[i], itemName[i], itemPrice[i], itemQuantity[i]);
            itemModels.add(model1);
        }
    }

    private void onClickBackToDelivering() {
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
                            Intent intent = new Intent(getBaseContext(), OrderedActivity.class);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
    }
}