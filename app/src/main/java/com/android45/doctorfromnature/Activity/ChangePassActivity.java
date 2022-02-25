package com.android45.doctorfromnature.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.databinding.ActivityChangePassBinding;
import com.android45.doctorfromnature.models.UserModel;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ChangePassActivity extends AppCompatActivity {
    ActivityChangePassBinding binding;

    FirebaseUser user;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseFirestore firestore;

    String userID;

    final HashMap<String, Object> cartMapNotifi = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_change_pass);

        binding.progressBarInChangePass.setVisibility(View.GONE);

        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.btnComebackInChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressBarInChangePass.setVisibility(View.VISIBLE);
                changePass();
            }
        });
    }

    private void changePass() {
        String oldPass = binding.etOldPass.getText().toString();
        String newPass = binding.etNewPass.getText().toString();
        String reNewPass = binding.etReNewPass.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            Toast.makeText(this, "Mật khẩu cũ sai", Toast.LENGTH_SHORT).show();
            binding.progressBarInChangePass.setVisibility(View.GONE);
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới không được ít hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            binding.progressBarInChangePass.setVisibility(View.GONE);
            return;
        }

        if (!newPass.equals(reNewPass)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            binding.progressBarInChangePass.setVisibility(View.GONE);
            return;
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userProfile = snapshot.getValue(UserModel.class);

                if (userProfile.getPassword().equals(oldPass)) {
                    updatePass(newPass);
                    finish();
                } else {
                    unChange();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updatePass(String s) {

        user.updatePassword(s)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .child("password").setValue(binding.etNewPass.getText().toString());
                            Toast.makeText(getBaseContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        cartMapNotifi.put("notification", "Bạn đã đổi mật khẩu thành công");
        cartMapNotifi.put("DateAndTime", saveCurrentTime + "  " + saveCurrentDate);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("Notification").add(cartMapNotifi).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

            }
        });

        binding.progressBarInChangePass.setVisibility(View.GONE);
    }

    private void unChange() {
        Toast.makeText(getBaseContext(), "Mật khẩu cũ sai", Toast.LENGTH_SHORT).show();
        binding.progressBarInChangePass.setVisibility(View.GONE);
    }
}