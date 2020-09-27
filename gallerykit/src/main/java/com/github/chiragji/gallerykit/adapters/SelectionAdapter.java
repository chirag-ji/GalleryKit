package com.github.chiragji.gallerykit.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chiragji.gallerykit.R;
import com.github.chiragji.gallerykit.models.GalleryData;
import com.github.chiragji.gallerykit.utils.CollectionUtils;
import com.github.chiragji.gallerykit.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This will inflate the selected images on the bar above the data list
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 2
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    private final OnSelectionClearListener listener;

    private ArrayList<GalleryData> dataList = new ArrayList<>();

    public SelectionAdapter(@NonNull OnSelectionClearListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_selected_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryData data = dataList.get(position);
        ImageUtils.loadImage(data.getDataUri(), holder.selectedImgView);
        holder.clearSelectedBtn.setOnClickListener(view -> {
            this.dataList.remove(data);
            notifyItemRemoved(position);
            listener.onSelectionCleared(data);
        });
        holder.imageCard.setOnClickListener(view -> listener.onImageClicked(data));
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getCollectionSize(dataList);
    }

    public void updateSelectedData(@NonNull GalleryData data, boolean selected) {
        if (selected)
            this.dataList.add(data);
        else this.dataList.remove(data);
        notifyDataSetChanged();
    }

    public ArrayList<GalleryData> getDataList() {
        return dataList;
    }

    public void updateData(@NonNull List<GalleryData> updatedData) {
        for (int i = 0; i < updatedData.size(); i++) {
            GalleryData data = updatedData.get(i);
            if (!data.isSelected())
                this.dataList.remove(data);
            else if (!this.dataList.contains(data))
                this.dataList.add(data);
        }
        if (!updatedData.isEmpty())
            notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView selectedImgView;
        private ImageButton clearSelectedBtn;
        private CardView imageCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedImgView = itemView.findViewById(R.id.selectedImgView);
            clearSelectedBtn = itemView.findViewById(R.id.clearSelectedBtn);
            imageCard = itemView.findViewById(R.id.imageCard);
        }
    }

    public interface OnSelectionClearListener {

        @UiThread
        void onSelectionCleared(@NonNull GalleryData data);

        @UiThread
        void onImageClicked(@NonNull GalleryData data);
    }
}