package com.chiragji.gallerykit;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.chiragji.gallerykit.GalleryKitView;
import com.github.chiragji.gallerykit.callbacks.GalleryKitListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GalleryKitListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GalleryKitView galleryKitView = findViewById(R.id.galleryKitView);
        galleryKitView.attachToFragmentActivity(this);
        galleryKitView.registerGalleryKitListener(this);
    }

    @Override
    public void onGalleryKitBackAction() {
        Log.d(TAG, "onBackKeyPressed: back key pressed on gallery kit");
    }

    @Override
    public void onGalleryKitSelectionConfirmed(@NonNull List<String> selectedDataUris) {
        Log.d(TAG, "onSelectionConfirmed: selectedDataUris.size = " + selectedDataUris.size());
        selectedDataUris.forEach(selectedUri ->
                Log.d(TAG, "onSelectionConfirmed: selectedUri = " + selectedUri));
    }
}