package com.github.chiragji.gallerykit.logic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.chiragji.gallerykit.enums.MediaType;
import com.github.chiragji.gallerykit.models.Album;
import com.github.chiragji.gallerykit.models.GalleryData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class GalleryExtractor {
    private static final String TAG = "GalleryExtractor";
    private final Builder builder;

    private GalleryExtractor(Builder builder) {
        this.builder = builder;
    }

    @FunctionalInterface
    public interface OnDataFoundListener {
        void accept(@NonNull GalleryData galleryData, @NonNull MediaType mediaType);
    }

    public interface OnCompleteListener {
        void accept(@NonNull Map<String, Album> albumMap);

        void onEmptyData();
    }

    private Map<String, Album> getImagesData() {
        String[] projections = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
        };
        Cursor cursor = builder.context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            Log.i(TAG, "getImagesData: image cursor is empty");
            return Collections.emptyMap();
        }
        HashMap<String, Album> albumMap = new HashMap<>();
        try {
            if (cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int dataColIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int dateColIdx = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                do {
                    GalleryData data = new GalleryData(cursor.getString(dataColIdx), MediaType.IMAGE, cursor.getString(dateColIdx));
                    data.setId(Integer.parseInt(cursor.getString(idColIdx)));
                    notifyDataFound(data, MediaType.IMAGE);
                    if (albumMap.containsKey(data.getAlbumName())) {
                        Album album = albumMap.get(data.getAlbumName());
                        if (album != null) album.addContents(data);
                    } else {
                        Album album = new Album(0, data.getAlbumName(), data.getDataUri());
                        album.addContents(data);
                        albumMap.putIfAbsent(data.getAlbumName(), album);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return albumMap;
    }

    private Map<String, Album> getVideosData() {
        String[] projections = {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DATE_ADDED,
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DURATION
        };
        Cursor cursor = builder.context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projections, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            Log.i(TAG, "getImagesData: image cursor is empty");
            return Collections.emptyMap();
        }
        HashMap<String, Album> albumMap = new HashMap<>();
        try {
            if (cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID);
                int dataColIdx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
                int dateColIdx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_ADDED);
                int titleColIdx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE);
                int durationColIdx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION);
                do {
                    GalleryData data = new GalleryData(cursor.getString(dataColIdx), MediaType.VIDEO, cursor.getString(dateColIdx));
                    data.setDuration(Integer.parseInt(cursor.getString(durationColIdx)));
//                    VideoData data = new VideoData(cursor.getString(dataColIdx), cursor.getString(dateColIdx),
//                            Integer.parseInt(cursor.getString(durationColIdx)));
                    data.setId(Integer.parseInt(cursor.getString(idColIdx)));
                    notifyDataFound(data, MediaType.VIDEO);
                    if (albumMap.containsKey(data.getAlbumName())) {
                        Album album = albumMap.get(data.getAlbumName());
                        if (album != null) album.addContents(data);
                    } else {
                        Album album = new Album(0, data.getAlbumName(), data.getDataUri());
                        album.addContents(data);
                        albumMap.putIfAbsent(data.getAlbumName(), album);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return albumMap;
    }

    private void notifyDataFound(GalleryData data, MediaType mediaType) {
        if (builder.onDataFoundListener != null)
            builder.onDataFoundListener.accept(data, mediaType);
    }

    public static class Builder {
        private final Context context;
        private final OnCompleteListener onCompleteListener;

        private OnDataFoundListener onDataFoundListener;

        public Builder(@NonNull Context context, @NonNull OnCompleteListener onCompleteListener) {
            this.context = context;
            this.onCompleteListener = onCompleteListener;
        }

        public Builder subscribeOnDataFound(@NonNull OnDataFoundListener onDataFoundListener) {
            this.onDataFoundListener = onDataFoundListener;
            return this;
        }

        public void buildFor(@NonNull MediaType mediaType) {
            GalleryExtractor harvester = new GalleryExtractor(this);
            Map<String, Album> albumMap;
            if (mediaType == MediaType.IMAGE)
                albumMap = harvester.getImagesData();
            else if (mediaType == MediaType.VIDEO)
                albumMap = harvester.getVideosData();
            else {
                albumMap = harvester.getImagesData();
                Map<String, Album> videosMap = harvester.getVideosData();
                for (Map.Entry<String, Album> entry : videosMap.entrySet()) {
                    Album album = albumMap.get(entry.getKey());
                    if (album != null)
                        album.addContents(entry.getValue());
                    else albumMap.put(entry.getKey(), entry.getValue());
                }
            }
            if (albumMap.isEmpty())
                onCompleteListener.onEmptyData();
            else onCompleteListener.accept(albumMap);
        }
    }
}