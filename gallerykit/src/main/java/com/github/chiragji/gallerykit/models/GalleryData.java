package com.github.chiragji.gallerykit.models;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.chiragji.gallerykit.enums.MediaType;

import java.io.File;
import java.util.Objects;

/**
 * The main model data wrap, contains all the data of a image/video
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class GalleryData {
    private int id, albumId;
    private final String albumName;
    private final String dataUri;
    private final MediaType mediaType;
    private final String dateAdded;
    private String thumbnail;
    private int duration;

    private boolean selected, enabled = true;

    public GalleryData(@NonNull String albumName, @NonNull String dataUri,
                       @NonNull MediaType mediaType, @NonNull String dateAdded) {
        this.albumName = albumName;
        this.dataUri = dataUri;
        this.mediaType = mediaType;
        this.dateAdded = dateAdded;
    }

    public GalleryData(@NonNull String dataUri, @NonNull MediaType mediaType, @NonNull String dateAdded) {
        this.dataUri = dataUri;
        this.mediaType = mediaType;
        this.dateAdded = dateAdded;
        this.albumName = new File(dataUri).getParentFile().getName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getDataUri() {
        return dataUri;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GalleryData data = (GalleryData) o;
        return dataUri.equals(data.dataUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataUri);
    }

    @Override
    @NonNull
    public String toString() {
        return "GalleryData{id=" + id +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                ", dataUri='" + dataUri + '\'' +
                ", mediaType=" + mediaType +
                ", dateAdded='" + dateAdded + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", duration=" + duration +
                ", selected=" + selected +
                ", enabled=" + enabled +
                '}';
    }
}