package com.github.chiragji.gallerykit.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.chiragji.gallerykit.api.AbstractFragment;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class GalleryKitAdapter extends FragmentStateAdapter {
    private final AbstractFragment[] fragments;

    public GalleryKitAdapter(@NonNull FragmentActivity fragmentActivity, @NonNull AbstractFragment[] fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    public GalleryKitAdapter(@NonNull Fragment fragment, @NonNull AbstractFragment[] fragments) {
        super(fragment);
        this.fragments = fragments;
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
}