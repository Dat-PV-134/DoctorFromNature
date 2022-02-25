package com.android45.doctorfromnature.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Activity.DeliverDetailActivity;
import com.android45.doctorfromnature.Adapter.DeliveredAdapter;
import com.android45.doctorfromnature.Adapter.DeliveringAdapter;
import com.android45.doctorfromnature.Interface.OnClickItemDelivering;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.DeliverModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveredFragment extends Fragment {
    List<DeliverModel> models;
    DeliveredAdapter adapter;

    RecyclerView rvDelivered;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_delivered, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        rvDelivered = (RecyclerView) v.findViewById(R.id.rvDelivered);

        models = new ArrayList<>();

        adapter = new DeliveredAdapter(getContext(), models, new OnClickItemDelivering() {
            @Override
            public void onClickItemDelivering(DeliverModel model) {
                Intent intent = new Intent(getContext(), DeliverDetailActivity.class);

                Bundle bundle = new Bundle();

                bundle.putSerializable("Deliver", model);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        rvDelivered.setLayoutManager(layoutManager02);
        rvDelivered.setAdapter(adapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Delivered").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        String documentId = documentSnapshot.getId();

                        DeliverModel model = documentSnapshot.toObject(DeliverModel.class);

                        model.setDocumentID(documentId);

                        models.add(model);
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });

        return v;
    }
}
