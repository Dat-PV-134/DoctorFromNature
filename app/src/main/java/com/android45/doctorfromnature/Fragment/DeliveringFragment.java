package com.android45.doctorfromnature.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.Activity.ChangeActivity;
import com.android45.doctorfromnature.Activity.DeliverDetailActivity;
import com.android45.doctorfromnature.Adapter.DeliveringAdapter;
import com.android45.doctorfromnature.Interface.OnClickItemDelivering;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.DeliverModel;
import com.android45.doctorfromnature.models.EventBus.ChangeListFavoriteEvent;
import com.android45.doctorfromnature.models.EventBus.OkOrderEvent;
import com.android45.doctorfromnature.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeliveringFragment extends Fragment {
    List<DeliverModel> models;
    DeliveringAdapter adapter;

    RecyclerView rvDelivering;


    FirebaseFirestore firestore;
    FirebaseAuth auth;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            EventBus.getDefault().postSticky(new OkOrderEvent());
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delivering, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        rvDelivering = (RecyclerView) v.findViewById(R.id.rvDelivering);

        models = new ArrayList<>();
        adapter = new DeliveringAdapter(getContext(), models, new OnClickItemDelivering() {
            @Override
            public void onClickItemDelivering(DeliverModel model) {
                openChangeActivityForResult(model);
            }
        });

        GridLayoutManager layoutManager02 = new GridLayoutManager(getContext(), 1, RecyclerView.VERTICAL, false);
        rvDelivering.setLayoutManager(layoutManager02);
        rvDelivering.setAdapter(adapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Delivering").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public void openChangeActivityForResult(DeliverModel model) {
        Intent intent = new Intent(getContext(), DeliverDetailActivity.class);

        Bundle bundle = new Bundle();

        bundle.putSerializable("Deliver", model);
        intent.putExtras(bundle);

        someActivityResultLauncher.launch(intent);
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
    public void eventOkOrder(OkOrderEvent event) {
        if (event != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
