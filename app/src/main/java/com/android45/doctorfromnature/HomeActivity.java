package com.android45.doctorfromnature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.android45.doctorfromnature.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    boolean checkHome = true, checkFav = false, checkCart = false, checkNotifi = false, checkMe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        changeButton();
        getFragment(HomeFragment.newInstance());

        binding.vHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheck(1);
                changeButton();
                getFragment(HomeFragment.newInstance());
            }
        });

        binding.vFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheck(2);
                changeButton();
                getFragment(FavoriteFragment.newInstance());
            }
        });

        binding.vCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheck(3);
                changeButton();
                getFragment(CartFragment.newInstance());
            }
        });

        binding.vNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheck(4);
                changeButton();
                getFragment(NotificationFragment.newInstance());
            }
        });

        binding.vMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCheck(5);
                changeButton();
                getFragment(ProfileFragment.newInstance());
            }
        });
    }

    private void changeButton() {
        if (checkHome) {
            binding.imgHome.setImageResource(R.drawable.icon_home_fill);
            binding.tvHome.setTextColor(getResources().getColor(R.color.app_main_color));
        } else {
            binding.imgHome.setImageResource(R.drawable.icon_home_nofill);
            binding.tvHome.setTextColor(getResources().getColor(R.color.app_main_color2));
        }

        if (checkFav) {
            binding.imgFavorites.setImageResource(R.drawable.icon_favorite_fill);
            binding.tvFavorites.setTextColor(getResources().getColor(R.color.app_main_color));
        } else {
            binding.imgFavorites.setImageResource(R.drawable.icon_favorite_basic);
            binding.tvFavorites.setTextColor(getResources().getColor(R.color.app_main_color2));
        }

        if (checkCart) {
            binding.imgCart.setImageResource(R.drawable.icon_cart_fill);
            binding.tvCart.setTextColor(getResources().getColor(R.color.app_main_color));
        } else {
            binding.imgCart.setImageResource(R.drawable.icon_cart_nofill);
            binding.tvCart.setTextColor(getResources().getColor(R.color.app_main_color2));
        }

        if (checkNotifi) {
            binding.imgNotification.setImageResource(R.drawable.icon_notification_fill);
            binding.tvNotification.setTextColor(getResources().getColor(R.color.app_main_color));
        } else {
            binding.imgNotification.setImageResource(R.drawable.icon_notification);
            binding.tvNotification.setTextColor(getResources().getColor(R.color.app_main_color2));
        }

        if (checkMe) {
            binding.imgMe.setImageResource(R.drawable.icon_me_fill);
            binding.tvMe.setTextColor(getResources().getColor(R.color.app_main_color));
        } else {
            binding.imgMe.setImageResource(R.drawable.icon_me_nofill);
            binding.tvMe.setTextColor(getResources().getColor(R.color.app_main_color2));
        }
    }

    private void changeCheck(int n) {
        if (n == 1) {
            checkHome = true;
            checkFav = false;
            checkCart = false;
            checkNotifi = false;
            checkMe = false;
        } else if (n == 2) {
            checkHome = false;
            checkFav = true;
            checkCart = false;
            checkNotifi = false;
            checkMe = false;
        } else if (n == 3) {
            checkHome = false;
            checkFav = false;
            checkCart = true;
            checkNotifi = false;
            checkMe = false;
        } else if (n == 4) {
            checkHome = false;
            checkFav = false;
            checkCart = false;
            checkNotifi = true;
            checkMe = false;
        } else {
            checkHome = false;
            checkFav = false;
            checkCart = false;
            checkNotifi = false;
            checkMe = true;
        }
    }

    void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentID, fragment)
                .commit();
    }
}