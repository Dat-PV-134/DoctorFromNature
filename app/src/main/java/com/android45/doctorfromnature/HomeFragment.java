package com.android45.doctorfromnature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.databinding.HomeFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    HomeFragmentBinding binding;
    FirebaseFirestore db;

    List<Herbal> herbalList;
    List<Herbal> herbalSearchList;

    HerbalAdapter herbalAdapter02, herbalAdapter;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();

        herbalList = new ArrayList<>();

        herbalAdapter02 = new HerbalAdapter(herbalList, new OnClickItemInterface() {
            @Override
            public void onClickItemHerbal(Herbal herbal) {
                goToDetail(herbal);
            }
        }, getActivity());

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.rvAll.setLayoutManager(layoutManager02);
        binding.rvAll.setAdapter(herbalAdapter02);


        db.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Herbal herbal = document.toObject(Herbal.class);
                                herbalList.add(herbal);
                                herbalAdapter02.notifyDataSetChanged();

                                binding.progressBar.setVisibility(View.GONE);
                                binding.scrollView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        herbalSearchList = new ArrayList<>();
        herbalAdapter = new HerbalAdapter(herbalSearchList, new OnClickItemInterface() {
            @Override
            public void onClickItemHerbal(Herbal herbal) {
                goToDetail(herbal);
            }
        }, getActivity());

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false);
        binding.searchRec.setLayoutManager(layoutManager);
        binding.searchRec.setAdapter(herbalAdapter);
        binding.searchRec.setHasFixedSize(true);

        binding.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty() || s.toString().replace(" ", "").isEmpty()) {
                    herbalSearchList.clear();
                    binding.rvAll.setVisibility(View.VISIBLE);
                    herbalAdapter.notifyDataSetChanged();
                } else {
                    searchProduct(s.toString());
                }
            }
        });

        return binding.getRoot();
    }

    private void searchProduct(String name) {
        if (!name.isEmpty()) {
            db.collection("Products").whereGreaterThanOrEqualTo("name", name).whereLessThanOrEqualTo("name",name + "\\uF7FF").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                herbalSearchList.clear();
                                herbalAdapter.notifyDataSetChanged();
                                for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                    Herbal herbal = snapshot.toObject(Herbal.class);

                                    herbal.setDocumentID(snapshot.getId());

                                    herbalSearchList.add(herbal);
                                    herbalAdapter.notifyDataSetChanged();
                                    binding.rvAll.setVisibility(View.GONE);
                                }
                            } else {
                                binding.rvAll.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }

    }

    private void goToDetail(Herbal herbal) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Herbal", herbal);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
