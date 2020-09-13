package com.github.chiragji.gallerykit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chiragji.gallerykit.R;
import com.github.chiragji.gallerykit.models.GalleryData;
import com.github.chiragji.gallerykit.utils.CollectionUtils;

import java.util.ArrayList;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {
    private final Context context;
    private final OnSelectionClearListener listener;

    private ArrayList<GalleryData> dataList = new ArrayList<>();

    public SelectionAdapter(@NonNull Context context, @NonNull OnSelectionClearListener listener) {
        this.context = context;
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
        Glide.with(context).load(data.getDataUri()).thumbnail(0.1F)
                .apply(new RequestOptions().centerCrop()).into(holder.selectedImgView);
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