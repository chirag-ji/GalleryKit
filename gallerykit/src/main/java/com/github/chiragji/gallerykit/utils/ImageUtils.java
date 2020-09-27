package com.github.chiragji.gallerykit.utils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.github.chiragji.gallerykit.R;

/**
 * An utility class helps to load media to respective views
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.1.0
 */
public abstract class ImageUtils {
    /**
     * Calling this method will load the image from uri to the provided {@link ImageView}
     *
     * @param dataUri   URI of data to load
     * @param imageView {@link ImageView} to render image
     * @see #loadImage(String, ImageView, RequestListener)
     * @see #loadImage(String, ImageView, RequestOptions, TransitionOptions, RequestListener)
     */
    public static void loadImage(@NonNull String dataUri, @NonNull ImageView imageView) {
        loadImage(dataUri, imageView, null);
    }

    /**
     * Calling this method will load the image from uri to the provided {@link ImageView}
     *
     * @param dataUri   URI of data to load
     * @param imageView {@link ImageView} to render image
     * @param listener  an instance of {@link RequestListener<Drawable>}
     * @see #loadImage(String, ImageView, RequestOptions, TransitionOptions, RequestListener)
     */
    public static void loadImage(@NonNull String dataUri, @NonNull ImageView imageView, @Nullable RequestListener<Drawable> listener) {
        loadImage(dataUri, imageView, new RequestOptions().centerCrop(), DrawableTransitionOptions.withCrossFade(), listener);
    }

    /**
     * Calling this method will load the image from uri to the provided {@link ImageView}
     *
     * @param dataUri           URI of data to load
     * @param imageView         {@link ImageView} to render image
     * @param requestOptions    an instance of {@link RequestOptions}
     * @param transitionOptions can be methods from {@link DrawableTransitionOptions}
     * @param listener          an instance of {@link RequestListener<Drawable>}
     */
    public static void loadImage(@NonNull String dataUri, @NonNull ImageView imageView, @Nullable RequestOptions requestOptions,
                                 @Nullable TransitionOptions<?, ? super Drawable> transitionOptions,
                                 @Nullable RequestListener<Drawable> listener) {
        Preconditions.checkNotNull(dataUri, "Data URI is not defined");
        Preconditions.checkNotNull(imageView, "ImageView is not defined");
        RequestBuilder<Drawable> builder = Glide.with(imageView.getContext()).load(dataUri).thumbnail(0.1f);
        if (requestOptions != null)
            builder = builder.apply(requestOptions);
        if (transitionOptions != null)
            builder = builder.transition(transitionOptions);
        builder.placeholder(R.drawable.ic_album_placeholder).listener(listener).into(imageView);
    }
}