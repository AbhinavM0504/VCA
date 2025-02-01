package com.vivo.vivorajonboarding.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vivo.vivorajonboarding.fragment.BankDetailsFragment;
import com.vivo.vivorajonboarding.fragment.BasicInfoFragment;
import com.vivo.vivorajonboarding.fragment.CompanyDetailsFragment;
import com.vivo.vivorajonboarding.fragment.EmergencyContactFragment;

public class StepperAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments;

    public StepperAdapter(FragmentActivity activity) {
        super(activity);
        fragments = new Fragment[]{
                new BasicInfoFragment(),
                new BankDetailsFragment(),
                new CompanyDetailsFragment(),
                new EmergencyContactFragment()
        };
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    public Fragment getFragment(int position) {
        return fragments[position];
    }
}
