package com.github.chiragji.gallerykit.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chiragji.gallerykit.R;
import com.github.chiragji.gallerykit.enums.MediaType;
import com.github.chiragji.gallerykit.models.GalleryData;
import com.github.chiragji.gallerykit.utils.CollectionUtils;
import com.github.chiragji.gallerykit.utils.TimeUtils;

import java.util.ArrayList;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private static final String TAG = "GalleryAdapter";

    private final Context context;
    private final OnSelectionUpdateListener listener;
    private final int threshold;

    private ArrayList<GalleryData> dataList = new ArrayList<>();
    private boolean disabled;
    private int selectedData;

    public GalleryAdapter(@NonNull Context context, @NonNull OnSelectionUpdateListener listener, int threshold) {
        this.context = context;
        this.listener = listener;
        this.threshold = threshold;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryData data = dataList.get(position);
        if (data.getMediaType() == MediaType.VIDEO) {
            holder.videoDetailsLayout.setVisibility(View.VISIBLE);
            holder.videoDurationTV.setText(TimeUtils.getMillsToTime(data.getDuration()));
        } else {
            holder.videoDetailsLayout.setVisibility(View.GONE);
        }

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.contentImg.setAlpha(0.4F);
                holder.contentImg.setEnabled(false);
                holder.checkboxImg.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        };
        Glide.with(context).load(data.getDataUri()).thumbnail(0.1f)
                .apply(new RequestOptions().centerCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(requestListener)
                .placeholder(R.drawable.ic_album_placeholder)
                .into(holder.contentImg);
        holder.contentCard.setOnClickListener(view -> {
            selectedData += data.isSelected() ? -1 : 1;
            data.setSelected(!data.isSelected());
            updateSelectedIcon(holder, data);
            if (threshold != -1) {
                if (selectedData >= threshold) {
                    disableUnSelected(true);
                    disabled = true;
                } else if (disabled) {
                    disableUnSelected(false);
                    disabled = false;
                }
            }
            listener.onSelectionUpdated(data, data.isSelected());
        });
        updateSelectedIcon(holder, data);
        if (data.isEnabled()) {
            holder.contentCard.setAlpha(1F);
            holder.contentCard.setClickable(true);
        } else {
            holder.contentCard.setAlpha(0.5F);
            holder.contentCard.setClickable(false);
        }
    }

    private void disableUnSelected(boolean disable) {
        new Handler().post(() -> {
            for (int i = 0; i < dataList.size(); i++) {
                GalleryData data = dataList.get(i);
                if (!data.isSelected()) {
                    data.setEnabled(!disable);
                }
            }
            notifyDataSetChanged();
        });
    }

    public void removedSelectedData(@NonNull GalleryData data) {
        int idx = getDataIndex(data);
        if (idx != -1) {
            data.setSelected(false);
            selectedData--;
            if (disabled)
                disableUnSelected(false);
            else notifyItemChanged(idx);
        }
    }

    public int getDataIndex(GalleryData data) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).equals(data))
                return i;
        }
        return -1;
    }

    private void updateSelectedIcon(ViewHolder viewHolder, GalleryData data) {
        viewHolder.checkboxImg.setImageResource(data.isSelected() ? R.drawable.ic_ticked : R.drawable.ic_un_ticked);
    }

    public void setData(@NonNull ArrayList<GalleryData> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void applyData(@NonNull GalleryData galleryData) {
        dataList.add(galleryData);
        notifyItemInserted(dataList.size());
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getCollectionSize(dataList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxImg, contentImg;
        private CardView contentCard;
        private View videoDetailsLayout;
        private TextView videoDurationTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxImg = itemView.findViewById(R.id.checkboxImg);
            contentImg = itemView.findViewById(R.id.contentImg);
            contentCard = itemView.findViewById(R.id.contentCard);
            videoDetailsLayout = itemView.findViewById(R.id.videoDetailsLayout);
            videoDurationTV = itemView.findViewById(R.id.videoDurationTV);
        }
    }

    @FunctionalInterface
    public interface OnSelectionUpdateListener {

        @UiThread
        void onSelectionUpdated(@NonNull GalleryData data, boolean selected);
    }
}