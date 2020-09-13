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

import java.util.LinkedList;

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
    private boolean attached;

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

    public void registerKitListener(@NonNull GalleryKitListener galleryKitListener) {
        this.listener = galleryKitListener;
    }

    private int getColor(@ColorRes int res) {
        return getContext().getColor(res);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
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
    }

    private void setupControls() {
        fragments.clear();
        if (hideBackButton) {
            backBtn.setVisibility(GONE);
        } else {
            backBtn.setVisibility(VISIBLE);
            backBtn.setOnClickListener(view ->
                    Preconditions.checkNotNull(listener, "GalleryKitListener not registered").onBackKeyPressed());
            backBtn.setImageResource(backBtnImageRes);
        }
        doneBtn.setTextColor(doneBtnColor);
        doneBtn.setOnClickListener(view -> onDoneAction());
        doneBtn.setVisibility(INVISIBLE);
        Bundle data = new Bundle();
        data.putBoolean(AbstractGalleryFragment.KEY_SHOW_SELECTED_IMAGES, showSelectedImages);
        data.putInt(AbstractGalleryFragment.KEY_MAX_IMAGES_SELECTION, maxImageSelections);
        data.putInt(AbstractGalleryFragment.KEY_MAX_VIDEO_SELECTION, maxVideosSelections);
        data.putInt(AbstractGalleryFragment.KEY_COMBINED_MAX_SELECTION, combinedMaxSelections);

        switch (viewStyle) {
            case COMBINE:
                fragments.add(new FragmentWrap(FragmentsHelper.getInstance(CombinedViewFragment.class, data),
                        R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                break;
            case SEPARATE:
                fragments.add(new FragmentWrap(FragmentsHelper.getInstance(PhotosFragment.class, data),
                        R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                fragments.add(new FragmentWrap(FragmentsHelper.getInstance(VideosFragment.class, data),
                        R.drawable.ic_video_selected, R.drawable.ic_video_unselected));
                break;
            case IMAGES_ONLY:
                fragments.add(new FragmentWrap(FragmentsHelper.getInstance(PhotosFragment.class, data),
                        R.drawable.ic_photos_selected, R.drawable.ic_photos_unselected));
                break;
            case VIDEOS_ONLY:
                fragments.add(new FragmentWrap(FragmentsHelper.getInstance(VideosFragment.class, data),
                        R.drawable.ic_video_selected, R.drawable.ic_video_unselected));
                break;
        }
        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.setVisibility(VISIBLE);
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
        listener.onSelectionConfirmed(selectedData);
    }

    public void attachToFragment(@NonNull Fragment fragment) {
        markAttached();
        setupPager(new GalleryKitAdapter(fragment, getFragments()));
    }

    public void attachToFragmentActivity(@NonNull FragmentActivity fragmentActivity) {
        markAttached();
        setupPager(new GalleryKitAdapter(fragmentActivity, getFragments()));
    }

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
    }

    @Override
    public void onSelectionRemoved(@NonNull GalleryData data) {
        this.selectedData.remove(data.getDataUri());
        if (selectedData.isEmpty())
            doneBtn.setVisibility(INVISIBLE);
    }
}