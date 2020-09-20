package com.github.chiragji.gallerykit.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.github.chiragji.gallerykit.api.AbstractFragment;

/**
 * An adapter that inflates the screens to the main window
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.0.0
 */
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