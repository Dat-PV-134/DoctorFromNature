package com.android45.doctorfromnature;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Adapter.MyNotificationAdapter;
import com.android45.doctorfromnature.databinding.NotificationFragmentBinding;
import com.android45.doctorfromnature.models.MyFavoriteModel;
import com.android45.doctorfromnature.models.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    NotificationFragmentBinding binding;
    List<NotificationModel> notificationModels;
    MyNotificationAdapter adapter;

    FirebaseAuth auth;
    FirebaseFirestore db;

    public static NotificationFragment newInstance() {
        
        Bundle args = new Bundle();
        
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.notification_fragment, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        notificationModels = new ArrayList<>();
        adapter = new MyNotificationAdapter(getContext(), notificationModels);

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        binding.rvUserNotification.setLayoutManager(layoutManager02);
        binding.rvUserNotification.setAdapter(adapter);


        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Notification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        NotificationModel model = documentSnapshot.toObject(NotificationModel.class);
                        notificationModels.add(model);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return binding.getRoot();
    }
}
