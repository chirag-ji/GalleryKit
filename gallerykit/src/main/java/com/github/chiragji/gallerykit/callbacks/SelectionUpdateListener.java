package com.github.chiragji.gallerykit.callbacks;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.chiragji.gallerykit.models.GalleryData;

/**
 * An internal listener, used to delegate the selections to {@link com.github.chiragji.gallerykit.GalleryKitView}
 *
 * @author Chirag
 * @version 1
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public interface SelectionUpdateListener {
    /**
     * Called when an item from picker is selected
     *
     * @param data an instance of {@link GalleryData}
     */
    void onSelectionAdded(@NonNull GalleryData data);

    /**
     * Called when an item from picker is un-selected
     *
     * @param data an instance of {@link GalleryData}
     */
    void onSelectionRemoved(@NonNull GalleryData data);
}