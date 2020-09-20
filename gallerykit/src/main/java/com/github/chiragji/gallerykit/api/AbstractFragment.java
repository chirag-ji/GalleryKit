package com.github.chiragji.gallerykit.api;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;

/**
 * An abstract implementor of Fragment class which provides some common methods in shortcuts
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class AbstractFragment extends Fragment {
    protected final void runOnUiThread(@NonNull Runnable runnable) {
        requireActivity().runOnUiThread(runnable);
    }

    protected final int[] getIntArray(@ArrayRes int resId) {
        return requireContext().getResources().getIntArray(resId);
    }

    protected final String[] getStringArray(@ArrayRes int resId) {
        return requireContext().getResources().getStringArray(resId);
    }
}