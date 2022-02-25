package com.android45.doctorfromnature.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android45.doctorfromnature.OrderPlaceActivity;
import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.databinding.ActivityChangeBinding;
import com.android45.doctorfromnature.models.EventBus.ChangeInformation;
import com.android45.doctorfromnature.models.UserModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChangeActivity extends AppCompatActivity {
    ActivityChangeBinding binding;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore firestore;

    UserModel userModel = new UserModel();

    final HashMap<String, Object> cartMapNotifi = new HashMap<>();

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            Uri profileUri = data.getData();
                            binding.changeProfileImg.setImageURI(profileUri);

                            final StorageReference reference = storage.getReference().child("profile_picture")
                                    .child(FirebaseAuth.getInstance().getUid());

                            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                    .child("profileImg").setValue(uri.toString());

                                            Toast.makeText(getBaseContext(), "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change);

        firestore = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.progressBarInChange.setVisibility(View.VISIBLE);
        binding.changeProfileImg.setVisibility(View.GONE);
        binding.etChangeName.setVisibility(View.GONE);
        binding.etChangePhoneNumber.setVisibility(View.GONE);
        binding.etChangeAddress.setVisibility(View.GONE);

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userModel = snapshot.getValue(UserModel.class);

                        Glide.with(getBaseContext()).load(userModel.getProfileImg()).fitCenter().into(binding.changeProfileImg);

                        binding.etChangeName.setText(userModel.getName());
                        binding.etChangeAddress.setText(userModel.getAddress());
                        binding.etChangePhoneNumber.setText(userModel.getPhoneNumber());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.btnComebackInChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.changeProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSomeActivityForResult();
            }
        });

        binding.progressBarInChange.setVisibility(View.GONE);
        binding.changeProfileImg.setVisibility(View.VISIBLE);
        binding.etChangeName.setVisibility(View.VISIBLE);
        binding.etChangePhoneNumber.setVisibility(View.VISIBLE);
        binding.etChangeAddress.setVisibility(View.VISIBLE);
        
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .child("name").setValue(binding.etChangeName.getText().toString());

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .child("address").setValue(binding.etChangeAddress.getText().toString());

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .child("phoneNumber").setValue(binding.etChangePhoneNumber.getText().toString());

                Toast.makeText(getBaseContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                String saveCurrentDate, saveCurrentTime;
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                onClickBackToOrderPlace();
            }
        });
    }

    private void onClickBackToOrderPlace() {
        Intent intent = new Intent(getBaseContext(), OrderPlaceActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void openSomeActivityForResult() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
    }

}