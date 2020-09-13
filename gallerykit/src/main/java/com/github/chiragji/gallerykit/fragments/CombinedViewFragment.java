package com.github.chiragji.gallerykit.fragments;

import androidx.annotation.NonNull;

import com.github.chiragji.gallerykit.enums.MediaType;

public class CombinedViewFragment extends AbstractGalleryFragment {
    private static final String TAG = "AllViewFragment";

    @NonNull
    @Override
    protected MediaType getMediaType() {
        return MediaType.ALL;
    }
}