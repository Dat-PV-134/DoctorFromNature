package com.android45.doctorfromnature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android45.doctorfromnature.databinding.ActivityRegisterBinding;
import com.android45.doctorfromnature.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        binding.progressbar.setVisibility(View.GONE);

        binding.btnComeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressbar.setVisibility(View.VISIBLE);
                createUser();
            }
        });
    }

    private void createUser() {

        String userName = binding.etNewUserName.getText().toString();
        String userEmail = binding.etNewEmail.getText().toString();
        String userPassword = binding.etNewPassWord.getText().toString();
        String userPasswordRepeat = binding.etRepeatPassWord.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Chưa nhập tên người dùng", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Chưa nhập email", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        if (!userPassword.equals(userPasswordRepeat)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            UserModel userModel = new UserModel(userName, userEmail, userPassword, "https://firebasestorage.googleapis.com/v0/b/doctor-from-nature.appspot.com/o/profile_picture%2Fic_launcher_foreground22.png?alt=media&token=50ca82f2-e532-453b-9176-9435aba949e0");
                            String id = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference().child("Users").child(id).setValue(userModel);

                            binding.progressbar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        }
                        else {
                            binding.progressbar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}