package com.kaeru.shutthebox.ui.dashboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    public DashboardPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new RankingFragment();  // Puan sıralamaları
        } else {
            return new RatioFragment();  // Puan / Oynama Oranı
        }
    }

    @Override
    public int getItemCount() {
        return 2;  // 2 sekme olacak
    }
}
