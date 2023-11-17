package com.ouslsmartactivitydiary;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ouslsmartactivitydiary.fragment.CourseWiseFragment;
import com.ouslsmartactivitydiary.fragment.MyCalendarFragment;
import com.ouslsmartactivitydiary.fragment.MyCoursesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:return new MyCalendarFragment();
            case 1: return new MyCoursesFragment();
            case 2: return new CourseWiseFragment();
            default: return new MyCalendarFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
