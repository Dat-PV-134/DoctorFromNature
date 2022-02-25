package com.android45.doctorfromnature;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android45.doctorfromnature.Activity.ChangeActivity;
import com.android45.doctorfromnature.Activity.ChangePassActivity;
import com.android45.doctorfromnature.Activity.OrderedActivity;
import com.android45.doctorfromnature.databinding.ProfileFragmentBinding;
import com.android45.doctorfromnature.models.UserModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    ProfileFragmentBinding binding;

    UserModel userProfile = new UserModel();

    FirebaseUser user;
    DatabaseReference reference;
    String userID;

    public static ProfileFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false);

        binding.profileProgressBar.setVisibility(View.VISIBLE);
        binding.vProfile.setVisibility(View.GONE);
        binding.vAction.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        binding.tvAccount.setText(user.getEmail());

        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userProfile = snapshot.getValue(UserModel.class);

                binding.tvName.setText(userProfile.getName());

                Glide.with(getContext()).load(userProfile.getProfileImg()).fitCenter().into(binding.imgProfile);

                binding.profileProgressBar.setVisibility(View.GONE);
                binding.vProfile.setVisibility(View.VISIBLE);
                binding.vAction.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.vChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeActivity.class);
                startActivity(intent);
            }
        });

        binding.vChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangePassActivity.class);
                startActivity(intent);
            }
        });

        binding.vHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderedActivity.class);
                startActivity(intent);
            }
        });

        binding.vAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLoginInProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}