package com.github.chiragji.gallerykit.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.chiragji.gallerykit.GalleryKitView;
import com.github.chiragji.gallerykit.enums.MediaType;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class PhotosFragment extends AbstractGalleryFragment {
    private static String TAG = "PhotosFragment";

    public PhotosFragment(@NonNull GalleryKitView kitView) {
        super(kitView);
    }

    @NonNull
    @Override
    protected MediaType getMediaType() {
        return MediaType.IMAGE;
    }
}