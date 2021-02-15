package com.example.chitchat.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chitchat.ui.QiscusPhotoFragment;

import java.util.List;

public class QiscusPhotoPagerAdapter extends FragmentStatePagerAdapter {
    private List<QiscusPhotoFragment> fragments;

    public QiscusPhotoPagerAdapter(FragmentManager fm, List<QiscusPhotoFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public List<QiscusPhotoFragment> getFragments() {
        return fragments;
    }
}
