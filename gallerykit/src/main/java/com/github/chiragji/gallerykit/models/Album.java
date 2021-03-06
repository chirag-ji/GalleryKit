package com.github.chiragji.gallerykit.models;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The model data wrapper shows the gallery data containing in a single album
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.0.0
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public class Album {
    private final int id;

    private final String name;

    private final String coverUri;

    private final ArrayList<GalleryData> contents = new ArrayList<>();

    public Album(int id, String name, String coverUri) {
        this.id = id;
        this.name = name;
        this.coverUri = coverUri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public ArrayList<GalleryData> getContents() {
        return contents;
    }

    public void addContents(@NonNull GalleryData galleryData) {
        contents.add(galleryData);
    }

    public void addContents(@NonNull Album album) {
        contents.addAll(album.contents);
    }

    @Override
    @NonNull
    public String toString() {
        return "Albums{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coverUri='" + coverUri + '\'' +
                ", contents=" + contents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return name.equals(album.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}