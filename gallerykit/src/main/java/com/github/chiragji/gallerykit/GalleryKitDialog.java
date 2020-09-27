package com.github.chiragji.gallerykit;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.chiragji.gallerykit.callbacks.GalleryKitListener;
import com.github.chiragji.gallerykit.enums.GalleryKitViewStyle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

/**
 * This indicates the UI in which, a gallery dialog will open with same functionality as
 * integrating within our own layout.
 * <p>
 * This will open the dialog
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.1.0
 */
public class GalleryKitDialog implements GalleryKitListener {
    private final Builder builder;
    private final BottomSheetDialog sheetDialog;

    private GalleryKitView kitView;

    private GalleryKitDialog(@NonNull Builder builder) {
        this.builder = builder;
        this.sheetDialog = new BottomSheetDialog(builder.fragment.requireContext());
        init();
    }

    private void init() {
        sheetDialog.setContentView(R.layout.layout_gallery_kit_dialog);
        sheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        sheetDialog.setOnCancelListener(dialogInterface -> onDismissAction());
        sheetDialog.setOnDismissListener(dialogInterface -> onDismissAction());

        kitView = sheetDialog.findViewById(R.id.galleryKitSheet);
        if (kitView == null) return;
        kitView.registerGalleryKitListener(this);
        kitView.attach(builder.fragment);

        GalleryKitView.Editor editor = kitView.getEditor();
        editor.setViewStyle(builder.viewStyle);
        editor.setBackButtonImageResource(builder.backBtnImage);
        editor.setCombinedMaxSelections(builder.combinedMaxSelections);
        editor.setMaxImageSelections(builder.maxImageSelections);
        editor.setMaxVideosSelections(builder.maxVideosSelections);
        editor.setShowSelectedResources(builder.showSelectedResources);
        editor.setDoneButtonColor(builder.doneBtnColor != View.NO_ID ? builder.doneBtnColor
                : builder.fragment.requireContext().getColor(R.color.colorHoloBlueDark));

        editor.applyChanges();
    }

    private void onDismissAction() {
        kitView.notifyBackPressed(true);
    }

    public void setSelectedData(@NonNull List<String> selectedData) {
        kitView.setSelectedData(selectedData);
    }

    public void show() {
        sheetDialog.show();
        kitView.sync();
    }

    public void dismiss() {
        sheetDialog.dismiss();
    }

    @Override
    public void onGalleryKitBackAction() {
        dismiss();
        builder.listener.onGalleryKitBackAction();
    }

    @Override
    public void onGalleryKitSelectionConfirmed(@NonNull List<String> selectedDataUris) {
        dismiss();
        builder.listener.onGalleryKitSelectionConfirmed(selectedDataUris);
    }

    public static class Builder {
        private final Fragment fragment;
        private GalleryKitListener listener;
        private GalleryKitViewStyle viewStyle = GalleryKitViewStyle.SEPARATE;
        private int backBtnImage = R.drawable.ic_close, doneBtnColor = View.NO_ID;
        private int combinedMaxSelections = -1, maxImageSelections = -1, maxVideosSelections = -1;
        private boolean showSelectedResources = true;

        public Builder(@NonNull Fragment fragment, @NonNull GalleryKitListener listener) {
            this.fragment = fragment;
            this.listener = listener;
        }

        public Builder setViewStyle(@NonNull GalleryKitViewStyle viewStyle) {
            this.viewStyle = viewStyle;
            return this;
        }

        public Builder setBackButtonImageResource(@DrawableRes int backBtnImage) {
            this.backBtnImage = backBtnImage;
            return this;
        }

        public Builder setCombinedMaxSelections(int combinedMaxSelections) {
            this.combinedMaxSelections = combinedMaxSelections;
            return this;
        }

        public Builder setMaxImageSelections(int maxImageSelections) {
            this.maxImageSelections = maxImageSelections;
            return this;
        }

        public Builder setMaxVideosSelections(int maxVideosSelections) {
            this.maxVideosSelections = maxVideosSelections;
            return this;
        }

        public Builder setDoneButtonColor(@ColorInt int doneBtnColor) {
            this.doneBtnColor = doneBtnColor;
            return this;
        }

        public Builder setShowSelectedResources(boolean showSelectedResources) {
            this.showSelectedResources = showSelectedResources;
            return this;
        }

        public GalleryKitDialog build() {
            return new GalleryKitDialog(this);
        }
    }
}