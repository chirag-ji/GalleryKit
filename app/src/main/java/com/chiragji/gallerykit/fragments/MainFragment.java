package com.chiragji.gallerykit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chiragji.gallerykit.MainActivity;
import com.chiragji.gallerykit.R;
import com.chiragji.gallerykit.adapter.ItemListAdapter;
import com.github.chiragji.gallerykit.GalleryKitDialog;
import com.github.chiragji.gallerykit.callbacks.GalleryKitListener;
import com.github.chiragji.gallerykit.enums.GalleryKitViewStyle;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainFragment extends Fragment implements GalleryKitListener {

    private MaterialButton galleryBtn, openGalleryDialogBtn;
    private RecyclerView selectedItemsListView;
    private TextView noSelectionView;

    private ItemListAdapter adapter;

    private GalleryKitDialog kitDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        galleryBtn = view.findViewById(R.id.galleryBtn);
        selectedItemsListView = view.findViewById(R.id.selectedItemsListView);
        noSelectionView = view.findViewById(R.id.noSelectionView);
        openGalleryDialogBtn = view.findViewById(R.id.openGalleryDialogBtn);

        init();
    }

    private void init() {
        galleryBtn.setOnClickListener(view -> MainActivity.toggleGalleryFragment(true));

        selectedItemsListView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ItemListAdapter();
        selectedItemsListView.setAdapter(adapter);

        openGalleryDialogBtn.setOnClickListener(view -> {
            if (kitDialog == null) {
                kitDialog = new GalleryKitDialog.Builder(this, this)
                        .setViewStyle(GalleryKitViewStyle.SEPARATE)
                        .setMaxImageSelections(1).build();
            }
            kitDialog.setSelectedData(getSelectedData());
            kitDialog.show();
        });
        toggleListView();
    }

    private void toggleListView() {
        if (adapter.getItemCount() == 0) {
            noSelectionView.setVisibility(View.VISIBLE);
            selectedItemsListView.setVisibility(View.GONE);
        } else {
            noSelectionView.setVisibility(View.GONE);
            selectedItemsListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGalleryKitBackAction() {
        MainActivity.toggleGalleryFragment(false);
    }

    @Override
    public void onGalleryKitSelectionConfirmed(@NonNull List<String> selectedDataUris) {
        adapter.setAllData(selectedDataUris);
        MainActivity.toggleGalleryFragment(false);
        toggleListView();
    }

    @NonNull
    public List<String> getSelectedData() {
        return adapter.getSelectedDataList();
    }
}