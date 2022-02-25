package com.android45.doctorfromnature;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Activity.ChangeActivity;
import com.android45.doctorfromnature.Adapter.MyFavoriteAdapter;
import com.android45.doctorfromnature.Interface.OnClickItemFavorite;
import com.android45.doctorfromnature.databinding.FavoriteFragmentBinding;
import com.android45.doctorfromnature.models.EventBus.ChangeFavorite;
import com.android45.doctorfromnature.models.EventBus.ChangeListFavoriteEvent;
import com.android45.doctorfromnature.models.EventBus.CountTotalEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.android45.doctorfromnature.models.MyFavoriteModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    FavoriteFragmentBinding binding;

    FirebaseAuth auth;
    FirebaseFirestore db;

    MyFavoriteAdapter favoriteAdapter;
    List<MyFavoriteModel> favoriteModels;

    public static FavoriteFragment newInstance() {

        Bundle args = new Bundle();

        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        favoriteModels = new ArrayList<>();
        favoriteAdapter = new MyFavoriteAdapter(getActivity(), favoriteModels, new OnClickItemFavorite() {
            @Override
            public void onClickItemVav(MyFavoriteModel model) {
                goToDetailFromFav(model);
            }
        });

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        binding.rvFavorite.setLayoutManager(layoutManager02);
        binding.rvFavorite.setAdapter(favoriteAdapter);

        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Favorite").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        String documentId = documentSnapshot.getId();

                        MyFavoriteModel model = documentSnapshot.toObject(MyFavoriteModel.class);

                        model.setDocumentID(documentId);

                        favoriteModels.add(model);
                        favoriteAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void goToDetailFromFav(MyFavoriteModel model) {
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
    public void eventSetFav(ChangeListFavoriteEvent event) {
        if (event != null) {
            favoriteAdapter.notifyDataSetChanged();
        }
    }

}
