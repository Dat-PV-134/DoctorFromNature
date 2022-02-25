package com.android45.doctorfromnature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android45.doctorfromnature.databinding.ActivityDetailBinding;
import com.android45.doctorfromnature.models.EventBus.ChangeFavorite;
import com.android45.doctorfromnature.models.EventBus.ChangeListFavoriteEvent;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyFavoriteModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    Herbal herbal;

    MyFavoriteModel model = new MyFavoriteModel();
    boolean checkFav = false;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        herbal = (Herbal) bundle.get("Herbal");

        Glide.with(getBaseContext()).load(herbal.getImg()).into(binding.imgHerbalDetail);
        binding.tvHerbalPriceDetail.setText(herbal.getPrice() + getResources().getString(R.string.don_vi));
        binding.tvHerbalNameDetail.setText(herbal.getName());
        binding.tvHerbalDetail.setText(herbal.getDescription());

        binding.btnComeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseCount(binding.tvValue);
//                changeButtonColor();
            }
        });

        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseCount(binding.tvValue);
//                changeButtonColor();
            }
        });

        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        model.setDocumentID("0");

        EventBus.getDefault().postSticky(new ChangeFavorite());

        binding.btnAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkFav) {
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Favorite")
                            .document(model.getDocumentID())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    checkFav = false;
                                    changeFav();
                                    EventBus.getDefault().postSticky(new ChangeListFavoriteEvent());
                                }
                            });

                } else {
                    final HashMap<String, Object> cartMap = new HashMap<>();

                    cartMap.put("productImg", herbal.getImg());
                    cartMap.put("productName", herbal.getName());
                    cartMap.put("productPrice", herbal.getPrice());

                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Favorite").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Toast.makeText(DetailActivity.this, "Đã thêm vào sản phẩm yêu thích", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().postSticky(new ChangeFavorite());
                            EventBus.getDefault().postSticky(new ChangeListFavoriteEvent());
                        }
                    });
                }
            }
        });
    }

    void increaseCount(TextView textView) {
        String process = binding.tvValue.getText().toString();
        int value = Integer.parseInt(process);
        if (value < 10) {
            value++;
            String process2 = herbal.getPrice();
            process2 = process2.replace(".", "");
            long price = Integer.parseInt(process2);
            Long result = price * value;
            process2 = String.valueOf(result);
            binding.tvHerbalPriceDetail.setText(createDot(process2) + getResources().getString(R.string.don_vi));
        }
        textView.setText(String.valueOf(value));
    }

    void decreaseCount(TextView textView) {
        String process = binding.tvValue.getText().toString();
        int value = Integer.parseInt(process);
        if (value > 1) {
            value--;
            String process2 = herbal.getPrice();
            process2 = process2.replace(".", "");
            long price = Integer.parseInt(process2);
            Long result = price * value;
            process2 = String.valueOf(result);
            binding.tvHerbalPriceDetail.setText(createDot(process2) + getResources().getString(R.string.don_vi));
        }
        textView.setText(String.valueOf(value));
    }

//    void changeButtonColor() {
//        String process = binding.tvValue.getText().toString();
//        int value = Integer.parseInt(process);
//        if (value == 1) {
//            binding.btnDecrease.setBackground(getResources().getDrawable(R.drawable.button_in_dec_nofill));
//            binding.btnDecrease.setTextColor(getResources().getColor(R.color.app_main_color));
//            binding.btnIncrease.setBackground(getResources().getDrawable(R.drawable.button_inc_dec_fill));
//            binding.btnIncrease.setTextColor(getResources().getColor(R.color.white));
//        }
//        if (value == 10) {
//            binding.btnDecrease.setBackground(getResources().getDrawable(R.drawable.button_inc_dec_fill));
//            binding.btnDecrease.setTextColor(getResources().getColor(R.color.white));
//            binding.btnIncrease.setBackground(getResources().getDrawable(R.drawable.button_in_dec_nofill));
//            binding.btnIncrease.setTextColor(getResources().getColor(R.color.app_main_color));
//        }
//        if (value > 1 && value < 10) {
//            binding.btnDecrease.setBackground(getResources().getDrawable(R.drawable.button_inc_dec_fill));
//            binding.btnDecrease.setTextColor(getResources().getColor(R.color.white));
//            binding.btnIncrease.setBackground(getResources().getDrawable(R.drawable.button_inc_dec_fill));
//            binding.btnIncrease.setTextColor(getResources().getColor(R.color.white));
//        }
//    }

    String createDot(String s) {
        long value = Integer.parseInt(s);
        s = String.format("%,d", value);
        return s;
    }

    String getTotalPrice() {
        String process = binding.tvValue.getText().toString();
        int value = Integer.parseInt(process);
        String process2 = herbal.getPrice();
        process2 = process2.replace(".", "");
        long price = Integer.parseInt(process2);
        Long result = price * value;
        process2 = String.valueOf(result);
        return createDot(process2) + getResources().getString(R.string.don_vi);
    }

    private void addToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("productImg", herbal.getImg());
        cartMap.put("productName", herbal.getName());
        cartMap.put("productPrice", herbal.getPrice());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", binding.tvValue.getText().toString());
        cartMap.put("totalPrice", getTotalPrice());

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(DetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkFavorite() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("Favorite");
        Query query = yourCollRef.whereEqualTo("productName", herbal.getName());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        model.setDocumentID(document.getId());
                        checkFav = true;
                        changeFav();
                    }
                } else {
                    checkFav = false;
                    changeFav();
                }
            }
        });
    }

    private void changeFav() {
        if (checkFav) {
            binding.btnAddFavorite.setImageResource(R.drawable.icon_favorite_fill);
        } else {
            binding.btnAddFavorite.setImageResource(R.drawable.icon_favorite_nofill);
        }
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
    public void eventChangeFavorite(ChangeFavorite event) {
        if (event != null) {
            checkFavorite();
        }
    }
}