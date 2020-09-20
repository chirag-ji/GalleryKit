package com.github.chiragji.gallerykit.callbacks;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * A the globally exposed listener that will delegate the actions performed on the UI to the
 * implementor concrete class
 *
 * @author Chirag
 * @version 1
 * @since 1.0.0
 */
public interface GalleryKitListener {
    /**
     * This method will be called when <code>back</code> button is pressed
     */
    void onGalleryKitBackAction();

    /**
     * This will be called when <code>Done</code> button is pressed
     *
     * @param selectedDataUris list of data uri selected
     */
    void onGalleryKitSelectionConfirmed(@NonNull List<String> selectedDataUris);
}