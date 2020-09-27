package com.github.chiragji.gallerykit.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.chiragji.gallerykit.GalleryKitView;
import com.github.chiragji.gallerykit.R;
import com.github.chiragji.gallerykit.adapters.GalleryAdapter;
import com.github.chiragji.gallerykit.adapters.SelectionAdapter;
import com.github.chiragji.gallerykit.api.AbstractFragment;
import com.github.chiragji.gallerykit.callbacks.SelectionUpdateListener;
import com.github.chiragji.gallerykit.enums.MediaType;
import com.github.chiragji.gallerykit.logic.GalleryExtractor;
import com.github.chiragji.gallerykit.models.Album;
import com.github.chiragji.gallerykit.models.GalleryData;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The main implementation, where all of the core process happens.
 * Since this is an abstract class, an implementor is required to ask what king of media needs to
 * be inflated and provide the enclosing instance of {@link GalleryKitView},
 * ie, it can be the type defined in {@link MediaType}
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 3
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public abstract class AbstractGalleryFragment extends AbstractFragment implements GalleryExtractor.OnDataFoundListener,
        GalleryAdapter.OnSelectionUpdateListener, SelectionAdapter.OnSelectionClearListener {
    private static final String TAG = "AbstractGalleryFragment";

    public static final String KEY_SHOW_SELECTED_IMAGES = "showSelectedImages";
    public static final String KEY_MAX_IMAGES_SELECTION = "maxImagesSelection";
    public static final String KEY_MAX_VIDEO_SELECTION = "maxVideoSelection";
    public static final String KEY_COMBINED_MAX_SELECTION = "combinedMaxSelection";

    private RecyclerView contentGrid, selectedItemsList;
    private GalleryAdapter adapter;
    private SelectionAdapter selectionAdapter;

    private boolean showSelectedResources = true;
    private int maxImageSelections, maxVideoSelections, maxCombinedSelections;

    private SelectionUpdateListener selectionUpdateListener;

    private final GalleryKitView kitView;

    public AbstractGalleryFragment(@NonNull GalleryKitView galleryKitView) {
        this.kitView = galleryKitView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        contentGrid = view.findViewById(R.id.contentGrid);
        selectedItemsList = view.findViewById(R.id.selectedItemsList);
        setupControls();
    }

    private void setupControls() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            showSelectedResources = bundle.getBoolean(KEY_SHOW_SELECTED_IMAGES);
            maxImageSelections = bundle.getInt(KEY_MAX_IMAGES_SELECTION);
            maxVideoSelections = bundle.getInt(KEY_MAX_VIDEO_SELECTION);
            maxCombinedSelections = bundle.getInt(KEY_COMBINED_MAX_SELECTION);
        }

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        contentGrid.setLayoutManager(gridLayoutManager);

        MediaType mediaType = getMediaType();
        int threshold;
        if (mediaType == MediaType.VIDEO)
            threshold = maxVideoSelections;
        else if (mediaType == MediaType.IMAGE)
            threshold = maxImageSelections;
        else threshold = maxCombinedSelections;

        adapter = new GalleryAdapter(this, threshold);
        contentGrid.setAdapter(adapter);

        if (showSelectedResources) {
            selectedItemsList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
            selectionAdapter = new SelectionAdapter(this);
            selectedItemsList.setAdapter(selectionAdapter);
        }
        selectedItemsList.setVisibility(View.GONE);

        if (hasPermissions())
            runGalleryExtractor(getMediaType());
    }

    @Override
    public void accept(@NonNull GalleryData galleryData, @NonNull MediaType mediaType) {
        adapter.applyData(galleryData);
    }

    @Override
    public void onSelectionUpdated(@NonNull GalleryData data, boolean selected) {
        if (selected) {
            selectionUpdateListener.onSelectionAdded(data);
        } else selectionUpdateListener.onSelectionRemoved(data);
        if (showSelectedResources) {
            this.selectionAdapter.updateSelectedData(data, selected);
            layoutSelectedItemList();
        }
    }

    @Override
    public void onSelectionCleared(@NonNull GalleryData data) {
        adapter.removedSelectedData(data);
        selectionUpdateListener.onSelectionRemoved(data);
        layoutSelectedItemList();
    }

    private void layoutSelectedItemList() {
        if (showSelectedResources)
            selectedItemsList.setVisibility(selectionAdapter.getItemCount() <= 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onImageClicked(@NonNull GalleryData data) {
        int idx = adapter.getDataIndex(data);
        if (idx != -1) {
            contentGrid.smoothScrollToPosition(idx);
        }
    }

    private void runGalleryExtractor(MediaType mediaType) {
        GalleryExtractor.OnCompleteListener listener = new GalleryExtractor.OnCompleteListener() {
            @Override
            public void accept(@NonNull Map<String, Album> albumMap) {
                Log.d(TAG, "accept: albumMap.size = " + albumMap.size());
            }

            @Override
            public void onEmptyData() {
                Log.d(TAG, "onEmptyData: ");
            }
        };
        new Handler().post(() -> new GalleryExtractor.Builder(requireContext(), listener)
                .subscribeOnDataFound(this).buildFor(mediaType));
    }

    public void setSelectionUpdateListener(@NonNull SelectionUpdateListener selectionUpdateListener) {
        this.selectionUpdateListener = selectionUpdateListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && adapter.getItemCount() > 0) {
            List<String> selectedDataList = kitView.getSelectedData();
            List<GalleryData> updatedData = new LinkedList<>();
            boolean removeAll = selectedDataList.isEmpty();
            for (GalleryData data : this.adapter.getDataList()) {
                data.setSelected(!removeAll && selectedDataList.contains(data.getDataUri()));
                updatedData.add(data);
            }
            selectionAdapter.updateData(updatedData);
            adapter.updateData(updatedData);
            layoutSelectedItemList();
        }
    }

    private boolean hasPermissions() {
        String[] requestParams = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(requestParams, 5555);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5555) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                        grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    runGalleryExtractor(getMediaType());
                    break;
                }
            }
        }
    }

    @NonNull
    protected abstract MediaType getMediaType();
}