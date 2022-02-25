package com.android45.doctorfromnature;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android45.doctorfromnature.Activity.ChangeActivity;
import com.android45.doctorfromnature.Adapter.MyOrderAdapter;
import com.android45.doctorfromnature.Interface.OnClickItemInCart;
import com.android45.doctorfromnature.databinding.ActivityOrderPlaceBinding;
import com.android45.doctorfromnature.models.EventBus.ChangeInformation;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.android45.doctorfromnature.models.UserModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class OrderPlaceActivity extends AppCompatActivity {
    ActivityOrderPlaceBinding binding;

    long totalCost = 0, totalOrder = 0;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore firestore;

    final HashMap<String, Object> cartMap = new HashMap<>();
    final HashMap<String, Object> cartMapNotifi = new HashMap<>();
    final HashMap<String, Object> cartMapDelivering = new HashMap<>();

    MyCartAdapter orderAdapter;
    List<MyCartModel> cartModelList = new ArrayList<>();

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            changeOrderInformation();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_place);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.progressBarInOrderPlace.setVisibility(View.VISIBLE);
        binding.vOrderPlace01.setVisibility(View.GONE);
        binding.vOrderPlace02.setVisibility(View.GONE);
        binding.vOrderPlace03.setVisibility(View.GONE);

        cartModelList = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        binding.tvOrderPerson.setText(userModel.getName());
                        binding.tvOrderNumber.setText(userModel.getPhoneNumber());
                        binding.tvOrderAddress.setText(userModel.getAddress());

                        if (binding.tvOrderPerson.getText().toString() == null || binding.tvOrderPerson.getText().toString().equals("")) {
                            binding.tvOrderPerson.setText("Người dùng");
                        }

                        if (binding.tvOrderNumber.getText().toString() == null || binding.tvOrderNumber.getText().toString().equals("")) {
                            binding.tvOrderNumber.setText("Số điện thoại");
                        }

                        if (binding.tvOrderAddress.getText().toString() == null || binding.tvOrderAddress.getText().toString().equals("")) {
                            binding.tvOrderAddress.setText("Địa chỉ người dùng");
                        }

                        binding.progressBarInOrderPlace.setVisibility(View.GONE);
                        binding.vOrderPlace01.setVisibility(View.VISIBLE);
                        binding.vOrderPlace02.setVisibility(View.VISIBLE);
                        binding.vOrderPlace03.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        calculateTotalCost(cartModelList);
        calculateTotalOrder();

        binding.vInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeActivityForResult();
            }
        });

        if (cartModelList != null && cartModelList.size() > 0) {
            orderAdapter = new MyCartAdapter(getBaseContext(), cartModelList, new OnClickItemInCart() {
                @Override
                public void onClickItemInCart(MyCartModel model) {
                    gotoDetailFromOrderPlace(model);
                }
            });

            GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(), 1, RecyclerView.VERTICAL, false);
            binding.rvMyOrderProducts.setLayoutManager(layoutManager);
            binding.rvMyOrderProducts.setAdapter(orderAdapter);

            calculateTotalCost(cartModelList);
        }

        binding.btnSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModelList.size() == 0) {
                    return;
                }

                String products = "";
                String imgUrl = "";
                String quantity = "";
                String deliveringName = "";
                String deliveringProductPrice = "";

                for (MyCartModel model : cartModelList) {
                    if (binding.tvOrderPerson.getText().toString().equals("Người dùng") || binding.tvOrderNumber.getText().toString().equals("Số điện thoại")
                        || binding.tvOrderAddress.getText().toString().equals("Địa chỉ người dùng")) {
                        Toast.makeText(getBaseContext(), "Xin vui lòng cập nhật thông tin nhận hàng", Toast.LENGTH_SHORT).show();
                        openChangeActivityForResult();
                        return;
                    }

                    products += model.getProductName() + " x " + model.getTotalQuantity() + ", ";
                    imgUrl += model.getProductImg() + ";";
                    quantity += model.getTotalQuantity() + ";";
                    deliveringName += model.getProductName() + ";";
                    deliveringProductPrice += model.getProductPrice() + ";";


//                    String saveCurrentDate, saveCurrentTime;
//                    Calendar calendar = Calendar.getInstance();
//
//                    SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
//                    saveCurrentDate = currentDate.format(calendar.getTime());
//
//                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
//                    saveCurrentTime = currentTime.format(calendar.getTime());
//
//                    cartMap.put("productName", model.getProductName());
//                    cartMap.put("productPrice", model.getProductPrice());
//                    cartMap.put("quantity", model.getTotalQuantity());
//                    cartMap.put("totalPrice", model.getTotalPrice());
//                    cartMap.put("orderDate", saveCurrentDate);
//                    cartMap.put("orderTime", saveCurrentTime);
//                    cartMap.put("customerName", binding.tvOrderPerson.getText().toString());
//                    cartMap.put("customerPhoneNumber",  binding.tvOrderNumber.getText().toString());
//                    cartMap.put("customerAddress", binding.tvOrderAddress.getText().toString());
//
//                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
//                            .collection("Order").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentReference> task) {
//
//                        }
//                    });

                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("AddToCart")
                            .document(model.getDocumentID())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });

                }
//
//                for (MyCartModel model : cartModelList) {
//                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
//                            .collection("AddToCart")
//                            .document(model.getDocumentID())
//                            .delete()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        cartModelList.remove(model);
//                                    }
//                                }
//                            });
//                }

                String saveCurrentDate, saveCurrentTime;
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                cartMap.put("productsName", products);
                cartMap.put("totalPrice", binding.TotalOrderMoney.getText().toString());
                cartMap.put("orderDate", saveCurrentDate);
                cartMap.put("orderTime", saveCurrentTime);
                cartMap.put("customerName", binding.tvOrderPerson.getText().toString());
                cartMap.put("customerPhoneNumber",  binding.tvOrderNumber.getText().toString());
                cartMap.put("customerAddress", binding.tvOrderAddress.getText().toString());

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Order").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });

                cartMapNotifi.put("notification", "Bạn đã đặt hàng thành công. Chúc bạn ngày mới vui vẻ");
                cartMapNotifi.put("DateAndTime", saveCurrentTime + "  " + saveCurrentDate);

                cartMapDelivering.put("productsName", deliveringName);
                cartMapDelivering.put("productsPrice", deliveringProductPrice);
                cartMapDelivering.put("productsQuantity", quantity);
                cartMapDelivering.put("productImg", imgUrl);
                cartMapDelivering.put("totalPrice", binding.TotalOrderMoney.getText().toString());
                cartMapDelivering.put("totalProductsPrice", binding.productCostMoney.getText().toString());
                cartMapDelivering.put("customerName", binding.tvOrderPerson.getText().toString());
                cartMapDelivering.put("customerPhoneNumber",  binding.tvOrderNumber.getText().toString());
                cartMapDelivering.put("customerAddress", binding.tvOrderAddress.getText().toString());
                cartMapDelivering.put("checkDeliver", "0");

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Delivering").add(cartMapDelivering).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Notification").add(cartMapNotifi).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });

                Toast.makeText(getBaseContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnComebackInOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (MyCartModel model : cartModelList) {
//                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
//                            .collection("Order")
//                            .document(model.getDocumentID())
//                            .delete()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        cartModelList.remove(model);
//                                    }
//                                }
//                            });
//                }
                finish();
            }
        });
    }

    private void gotoDetailFromOrderPlace(MyCartModel model) {
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
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

    String createDot(String s) {
        long value = Integer.parseInt(s);
        s = String.format("%,d", value);
        return s;
    }

    public void calculateTotalCost(List<MyCartModel> cartModelList) {
        totalCost = 0;
        for (MyCartModel model : cartModelList) {
            totalCost += Long.parseLong(model.getProductPrice().replace(".", "")) * Long.parseLong(model.getTotalQuantity());
        }

        binding.productCostMoney.setText(createDot(String.valueOf(totalCost)) + getResources().getString(R.string.don_vi));
    }

    public void calculateTotalOrder() {
        totalOrder = totalCost + 25000;

        binding.TotalOrderMoney.setText(createDot(String.valueOf(totalOrder)) + getResources().getString(R.string.don_vi));
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
            calculateTotalOrder();
        }
    }

    public void changeOrderInformation() {
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        binding.tvOrderPerson.setText(userModel.getName());
                        binding.tvOrderNumber.setText(userModel.getPhoneNumber());
                        binding.tvOrderAddress.setText(userModel.getAddress());

                        if (binding.tvOrderPerson.getText().toString() == null || binding.tvOrderPerson.getText().toString().equals("")) {
                            binding.tvOrderPerson.setText("Người dùng");
                        }

                        if (binding.tvOrderNumber.getText().toString() == null || binding.tvOrderNumber.getText().toString().equals("")) {
                            binding.tvOrderNumber.setText("Số điện thoại");
                        }

                        if (binding.tvOrderAddress.getText().toString() == null || binding.tvOrderAddress.getText().toString().equals("")) {
                            binding.tvOrderAddress.setText("Địa chỉ người dùng");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void openChangeActivityForResult() {
        Intent intent = new Intent(getBaseContext(), ChangeActivity.class);
        someActivityResultLauncher.launch(intent);
    }
}