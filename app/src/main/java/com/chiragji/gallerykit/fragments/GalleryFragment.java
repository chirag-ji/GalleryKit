package com.chiragji.gallerykit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chiragji.gallerykit.R;
import com.github.chiragji.gallerykit.GalleryKitView;
import com.github.chiragji.gallerykit.callbacks.GalleryKitListener;

public class GalleryFragment extends Fragment {

    private final GalleryKitListener listener;

    public GalleryFragment(@NonNull GalleryKitListener kitListener) {
        this.listener = kitListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GalleryKitView galleryKitView = view.findViewById(R.id.galleryKitView);
        galleryKitView.attachToFragment(this);
        galleryKitView.registerGalleryKitListener(this.listener);
    }
}