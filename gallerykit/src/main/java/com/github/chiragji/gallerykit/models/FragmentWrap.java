package com.github.chiragji.gallerykit.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.chiragji.gallerykit.api.AbstractFragment;

import java.util.Objects;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class FragmentWrap {
    private final AbstractFragment fragment;
    private final int selectedImgRes, unselectedImgRes;

    public FragmentWrap(@NonNull AbstractFragment fragment, @DrawableRes int selectedImgRes, @DrawableRes int unselectedImgRes) {
        this.fragment = fragment;
        this.selectedImgRes = selectedImgRes;
        this.unselectedImgRes = unselectedImgRes;
    }

    public AbstractFragment getFragment() {
        return fragment;
    }

    public int getSelectedImgRes() {
        return selectedImgRes;
    }

    public int getUnselectedImgRes() {
        return unselectedImgRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return fragment.equals(((FragmentWrap) o).fragment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fragment);
    }
}