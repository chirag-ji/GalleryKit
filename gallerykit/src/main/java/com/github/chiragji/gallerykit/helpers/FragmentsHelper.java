package com.github.chiragji.gallerykit.helpers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;

import com.github.chiragji.gallerykit.api.AbstractFragment;

import java.lang.reflect.Constructor;

/**
 * A helper class to initiate the instance of fragments quickly
 *
 * @author Chirag
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public abstract class FragmentsHelper {
    /**
     * Call to this method will create a new instance of fragment with the provided bundle arguments
     *
     * @param clazz Class of fragment
     * @param <T>   Type of fragment
     * @return Constructed instance of the fragment
     * @see #getInstance(Class, Bundle)
     */
    public static <T extends AbstractFragment> T getInstance(Class<T> clazz) {
        return getInstance(clazz, new Bundle());
    }

    /**
     * Call to this method will create a new instance of fragment with the provided bundle arguments
     *
     * @param clazz Class of fragment
     * @param data  An instance of {@link Bundle} representing arguments
     * @param <T>   Type of fragment
     * @return Constructed instance of the fragment
     */
    public static <T extends Fragment> T getInstance(Class<T> clazz, @NonNull Bundle data) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T inst = constructor.newInstance();
            inst.setArguments(data);
            return inst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}