package com.github.chiragji.gallerykit.fragments;

import androidx.annotation.NonNull;

import com.github.chiragji.gallerykit.enums.MediaType;

public class VideosFragment extends AbstractGalleryFragment {
    private static final String TAG = "VideosFragment";

    @NonNull
    @Override
    protected MediaType getMediaType() {
        return MediaType.VIDEO;
    }
}
