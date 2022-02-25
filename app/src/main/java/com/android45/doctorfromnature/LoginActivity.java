package com.android45.doctorfromnature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android45.doctorfromnature.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        binding.progressbar.setVisibility(View.GONE);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressbar.setVisibility(View.VISIBLE);
                loginUser();
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressbar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
                binding.progressbar.setVisibility(View.GONE);
            }
        });
    }

    private void loginUser() {
        String userEmail = binding.etUserName.getText().toString();
        String userPassword = binding.etPassWord.getText().toString();

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
            Toast.makeText(this, "Mật khẩu sai", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
            return;
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.progressbar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            binding.progressbar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}