package com.github.chiragji.gallerykit;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.github.chiragji.gallerykit.adapters.GalleryKitAdapter;
import com.github.chiragji.gallerykit.api.AbstractFragment;
import com.github.chiragji.gallerykit.callbacks.GalleryKitListener;
import com.github.chiragji.gallerykit.callbacks.SelectionUpdateListener;
import com.github.chiragji.gallerykit.enums.GalleryKitViewStyle;
import com.github.chiragji.gallerykit.fragments.AbstractGalleryFragment;
import com.github.chiragji.gallerykit.fragments.CombinedViewFragment;
import com.github.chiragji.gallerykit.fragments.PhotosFragment;
import com.github.chiragji.gallerykit.fragments.VideosFragment;
import com.github.chiragji.gallerykit.helpers.FragmentsHelper;
import com.github.chiragji.gallerykit.models.FragmentWrap;
import com.github.chiragji.gallerykit.models.GalleryData;
import com.github.chiragji.gallerykit.utils.Preconditions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the layout which can be embedded to the views created with XML. And manages all the
 * process
 * <p>
 * It can be fully customizable
 * <p>
 * It's an direct implementor of {@link FrameLayout}.
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 4
 * @since 1.0.0
 */
public class GalleryKitView extends FrameLayout implements SelectionUpdateListener {
    private static final String TAG = "GalleryKitView";

    private GalleryKitListener listener;
    private GalleryKitViewStyle viewStyle;

    @DrawableRes
    private int backBtnImageRes;

    @ColorInt
    private int doneBtnColor;

    private int combinedMaxSelections, maxImageSelections, maxVideosSelections;
    private boolean showSelectedImages, hideBackButton;

    private ViewPager2 pager;
    private TabLayout tabLayout;
    private ImageButton backBtn;
    private MaterialButton doneBtn;

    private LinkedList<FragmentWrap> fragments = new LinkedList<>();
    private LinkedList<String> selectedData = new LinkedList<>();
    private boolean attached, initDone;

    // These list are used to track data selection and revert in case of back key pressed
    private LinkedList<String> addedData, removedData;

    private final Editor editor = new Editor();

    // Changes for GalleryKitAdapter, used to re-render the controls
    private FragmentActivity attachedActivity;
    private Fragment attachedFragment;

    private GalleryKitAdapter kitAdapter;

    public GalleryKitView(@NonNull Context context) {
        super(context);
    }

    public GalleryKitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(context, attrs);
    }

    public GalleryKitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(context, attrs);
    }

    public GalleryKitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupAttributes(context, attrs);
    }

    private void setupAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.GalleryKitView);
        backBtnImageRes = array.getColor(R.styleable.GalleryKitView_backButtonImageSrc, R.drawable.ic_back);
        viewStyle = GalleryKitViewStyle.values()[array.getInt(R.styleable.GalleryKitView_viewStyle, 0)];
        combinedMaxSelections = array.getInt(R.styleable.GalleryKitView_combinedMaxSelections, -1);
        showSelectedImages = array.getBoolean(R.styleable.GalleryKitView_showSelectedResources, true);
        maxImageSelections = array.getInt(R.styleable.GalleryKitView_maxImageSelections, -1);
        maxVideosSelections = array.getInt(R.styleable.GalleryKitView_maxVideoSelections, -1);
        hideBackButton = array.getBoolean(R.styleable.GalleryKitView_hideBackButton, false);
        doneBtnColor = array.getColor(R.styleable.GalleryKitView_doneButtonColor, getColor(R.color.colorHoloBlueDark));
        array.recycle();
    }

    private int getColor(@ColorRes int res) {
        return getContext().getColor(res);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = inflate(getContext(), R.layout.layout_gallery_kit, this);
        pager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tabLayout);
        backBtn = view.findViewById(R.id.backBtn);
        doneBtn = view.findViewById(R.id.doneBtn);

        setupControls();
        initDone = true;
    }

    private void setupControls() {
        fragments.clear();
        if (!initDone || editor.hideButtonEdited || editor.backBtnImgEdited) {
            if (hideBackButton) {
                backBtn.setVisibility(GONE);
            } else {
                backBtn.setVisibility(VISIBLE);
                backBtn.setOnClickListener(view -> notifyBackPressed(true));
                backBtn.setImageResource(backBtnImageRes);
                addedData = new LinkedList<>();
                removedData = new LinkedList<>();
            }
        }
        if (!initDone || editor.doneButtonColorEdited) {
            doneBtn.setTextColor(doneBtnColor);
            doneBtn.setOnClickListener(view -> onDoneAction());
            doneBtn.setVisibility(INVISIBLE);
        }
        if (!initDone || isRenderingEdited()) {
            Bundle data = new Bundle();
            data.putBoolean(AbstractGalleryFragment.KEY_SHOW_SELECTED_IMAGES, showSelectedImages);
            data.putInt(AbstractGalleryFragment.KEY_MAX_IMAGES_SELECTION, maxImageSelections);
            data.putInt(AbstractGalleryFragment.KEY_MAX_VIDEO_SELECTION, maxVideosSelections);
            data.putInt(AbstractGalleryFragment.KEY_COMBINED_MAX_SELECTION, combinedMaxSelections);

            switch (viewStyle) {
                case COMBINE:
                    fragments.add(new FragmentWrap(FragmentsHelper.getInstance(CombinedViewFragment.class, data, this),
                            R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                    break;
                case SEPARATE:
                    fragments.add(new FragmentWrap(FragmentsHelper.getInstance(PhotosFragment.class, data, this),
                            R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                    fragments.add(new FragmentWrap(FragmentsHelper.getInstance(VideosFragment.class, data, this),
                            R.drawable.ic_video_selected, R.drawable.ic_video_unselected));
                    break;
                case IMAGES_ONLY:
                    fragments.add(new FragmentWrap(FragmentsHelper.getInstance(PhotosFragment.class, data, this),
                            R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                    break;
                case VIDEOS_ONLY:
                    fragments.add(new FragmentWrap(FragmentsHelper.getInstance(VideosFragment.class, data, this),
                            R.drawable.ic_video_selected, R.drawable.ic_video_unselected));
                    break;
            }
            setupTabLayout();
        }
        if (isRenderingEdited() && initDone) {
            attachInternal(attachedFragment, attachedActivity);
        }
        markEditingComplete();
    }

    private boolean isRenderingEdited() {
        return editor.combinedMaxSelEdited || editor.maxImageSelEdited || editor.maxVideoSelEdited
                || editor.showSelResEdited || editor.viewStyleEdited;
    }

    private void markEditingComplete() {
        editor.backBtnImgEdited = false;
        editor.combinedMaxSelEdited = false;
        editor.maxImageSelEdited = false;
        editor.maxVideoSelEdited = false;
        editor.doneButtonColorEdited = false;
        editor.hideButtonEdited = false;
        editor.showSelResEdited = false;
        editor.viewStyleEdited = false;
    }

    private void setupTabLayout() {
        tabLayout.setVisibility(VISIBLE);
        tabLayout.removeAllTabs();
        for (FragmentWrap wrap : fragments) {
            tabLayout.addTab(tabLayout.newTab().setIcon(wrap.getUnselectedImgRes()));
            if (wrap.getFragment() instanceof AbstractGalleryFragment) {
                ((AbstractGalleryFragment) wrap.getFragment()).setSelectionUpdateListener(this);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(fragments.get(tab.getPosition()).getSelectedImgRes());
                pager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(fragments.get(tab.getPosition()).getUnselectedImgRes());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void onDoneAction() {
        Preconditions.checkNotNull(listener, "GalleryKitListener not registered");
        listener.onGalleryKitSelectionConfirmed(selectedData);
        clearStackData();
    }

    /**
     * @see #attach(Fragment)
     * @deprecated
     */
    @Deprecated
    public void attachToFragment(@NonNull Fragment fragment) {
        markAttached();
        setupPager(new GalleryKitAdapter(fragment, getFragments()));
    }

    /**
     * @see #attach(FragmentActivity)
     * @deprecated
     */
    @Deprecated
    public void attachToFragmentActivity(@NonNull FragmentActivity fragmentActivity) {
        markAttached();
        setupPager(new GalleryKitAdapter(fragmentActivity, getFragments()));
    }

    public void attach(@NonNull Fragment fragment) {
        Preconditions.checkNotNull(fragment, "Fragment is null");
        attachInternal(fragment, null);
    }

    public void attach(@NonNull FragmentActivity fragmentActivity) {
        Preconditions.checkNotNull(fragmentActivity, "FragmentActivity is null");
        attachInternal(null, fragmentActivity);
    }

    private void attachInternal(Fragment fragment, FragmentActivity fragmentActivity) {
        if (fragmentActivity == null)
            kitAdapter = new GalleryKitAdapter(fragment, getFragments());
        else kitAdapter = new GalleryKitAdapter(fragmentActivity, getFragments());
        setupPager(kitAdapter);
        attached = true;
        attachedFragment = fragment;
        attachedActivity = fragmentActivity;
    }

    @Deprecated
    private void markAttached() {
        if (attached)
            throw new IllegalStateException("GalleryKit already attached to fragment/activity");
        attached = true;
    }

    private void setupPager(GalleryKitAdapter kitAdapter) {
        pager.setAdapter(kitAdapter);
        pager.setCurrentItem(0);
        pager.setUserInputEnabled(false);
    }

    public void registerGalleryKitListener(@NonNull GalleryKitListener listener) {
        this.listener = listener;
    }

    private AbstractFragment[] getFragments() {
        AbstractFragment[] fragments = new AbstractFragment[this.fragments.size()];
        for (int i = 0; i < this.fragments.size(); i++) {
            fragments[i] = this.fragments.get(i).getFragment();
        }
        return fragments;
    }

    @Override
    public void onSelectionAdded(@NonNull GalleryData data) {
        this.selectedData.add(data.getDataUri());
        doneBtn.setVisibility(VISIBLE);
        if (!hideBackButton) {
            addedData.add(data.getDataUri());
            removedData.remove(data.getDataUri());
        }
    }

    @Override
    public void onSelectionRemoved(@NonNull GalleryData data) {
        this.selectedData.remove(data.getDataUri());
        if (selectedData.isEmpty())
            doneBtn.setVisibility(INVISIBLE);
        if (!hideBackButton) {
            addedData.remove(data.getDataUri());
            removedData.add(data.getDataUri());
        }
    }

    @NonNull
    public List<String> getSelectedData() {
        return Collections.unmodifiableList(selectedData);
    }

    public void setSelectedData(@NonNull List<String> dataUriList) {
        this.selectedData.clear();
        this.selectedData.addAll(dataUriList);
    }

    private void clearStackData() {
        if (hideBackButton) return;
        addedData.clear();
        removedData.clear();
    }

    /**
     * Call to this method to notify gallery kit about back key pressed on the fragment or activity
     *
     * @param notifyListener notify attached listener or not
     */
    public final void notifyBackPressed(boolean notifyListener) {
        if (notifyListener)
            Preconditions.checkNotNull(listener, "GalleryKitListener not registered").onGalleryKitBackAction();
        selectedData.removeAll(addedData);
        selectedData.addAll(removedData);
        clearStackData();
        if (notifyListener) listener.onGalleryKitBackAction();
    }

    public Editor getEditor() {
        return editor;
    }

    /**
     * Internal method, intended to be called up by {@link GalleryKitDialog}
     */
    void sync() {
        if (initDone && kitAdapter != null)
            kitAdapter.notifySync();
    }

    /**
     * Class or wrapper, used to edit the view after it is rendered
     */
    public final class Editor {
        private boolean backBtnImgEdited, combinedMaxSelEdited, maxImageSelEdited, maxVideoSelEdited,
                doneButtonColorEdited, hideButtonEdited, showSelResEdited, viewStyleEdited;

        private Editor() {
        }

        /**
         * apply all the changes made
         */
        public void applyChanges() {
            if (!GalleryKitView.this.attached)
                throw new IllegalStateException("GalleryKitView not attached to Fragment/FragmentActivity");
            GalleryKitView.this.setupControls();
        }

        /**
         * Set the back button custom image
         *
         * @param backBtnImage resource id for image
         */
        public void setBackButtonImageResource(@DrawableRes int backBtnImage) {
            GalleryKitView.this.backBtnImageRes = backBtnImage;
            backBtnImgEdited = true;
        }

        /**
         * Set the maximum selection for {@link GalleryKitViewStyle#COMBINE}
         *
         * @param combinedMaxSelections maximum combined selection
         */
        public void setCombinedMaxSelections(int combinedMaxSelections) {
            GalleryKitView.this.combinedMaxSelections = combinedMaxSelections;
            combinedMaxSelEdited = true;
        }

        /**
         * Set the maximum selection for {@link GalleryKitViewStyle#IMAGES_ONLY}
         *
         * @param maxImageSelections maximum image selection
         */
        public void setMaxImageSelections(int maxImageSelections) {
            GalleryKitView.this.maxImageSelections = maxImageSelections;
            maxImageSelEdited = true;
        }

        /**
         * Set the maximum selection for {@link GalleryKitViewStyle#VIDEOS_ONLY}
         *
         * @param maxVideosSelections maximum video selection
         */
        public void setMaxVideosSelections(int maxVideosSelections) {
            GalleryKitView.this.maxVideosSelections = maxVideosSelections;
            maxVideoSelEdited = true;
        }

        /**
         * Set the color for the Done Button
         *
         * @param doneBtnColor ColorInt for setting
         */
        public void setDoneButtonColor(@ColorInt int doneBtnColor) {
            GalleryKitView.this.doneBtnColor = doneBtnColor;
            doneButtonColorEdited = true;
        }

        /**
         * Weather to hide the back button or not
         * <p>
         * Default <code>false</code>
         *
         * @param hideBackButton true to hide back button
         */
        public void setHideBackButton(boolean hideBackButton) {
            GalleryKitView.this.hideBackButton = hideBackButton;
            hideButtonEdited = true;
        }

        /**
         * Displays the selected resources on top of the list
         * <p>
         * Default <code>true</code>
         *
         * @param showSelectedResources false to hide the selection list
         */
        public void setShowSelectedResources(boolean showSelectedResources) {
            GalleryKitView.this.showSelectedImages = showSelectedResources;
            showSelResEdited = true;
        }

        /**
         * Sets the viewing style to GalleryKit
         *
         * @param viewStyle an constant from {@link GalleryKitViewStyle}
         */
        public void setViewStyle(GalleryKitViewStyle viewStyle) {
            GalleryKitView.this.viewStyle = viewStyle;
            viewStyleEdited = true;
        }
    }
}