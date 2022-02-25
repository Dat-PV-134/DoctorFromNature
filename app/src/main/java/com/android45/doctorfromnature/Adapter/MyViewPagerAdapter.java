package com.android45.doctorfromnature.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android45.doctorfromnature.Fragment.DeliveredFragment;
import com.android45.doctorfromnature.Fragment.DeliveringFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {
    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DeliveringFragment();

            case 1:
                return new DeliveredFragment();

            default:
                return new DeliveringFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
