package com.chiragji.gallerykit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chiragji.gallerykit.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private final ArrayList<String> selectedDataList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dataUri = selectedDataList.get(position);
        holder.imageUri.setText(dataUri);
        Glide.with(holder.itemView).load(dataUri).thumbnail(0.1F)
                .apply(new RequestOptions().centerCrop()).into(holder.image);
        holder.clearButton.setOnClickListener(view -> {
            this.selectedDataList.remove(dataUri);
            notifyDataSetChanged();
        });
    }

    public void addAllData(@NonNull List<String> selectedData) {
        selectedDataList.addAll(selectedData);
        notifyDataSetChanged();
    }

    public void setAllData(@NonNull List<String> selectedData) {
        this.selectedDataList.clear();
        addAllData(selectedData);
    }

    public List<String> getSelectedDataList() {
        return Collections.unmodifiableList(selectedDataList);
    }

    @Override
    public int getItemCount() {
        return selectedDataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView imageUri;
        private ImageButton clearButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageUri = itemView.findViewById(R.id.imageUri);
            clearButton = itemView.findViewById(R.id.clearButton);
        }
    }
}