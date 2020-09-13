package com.github.chiragji.gallerykit.utils;

import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class contains some of common methods for interacting wih collections
 *
 * @author Chirag
 * @since 1.0.0
 */
public abstract class CollectionUtils {
    /**
     * Safely determines a collection is empty or not
     *
     * @param collection instance of {@link Collection}
     * @return true if collection is null or empty
     */
    public static boolean isEmptyCollection(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Safely determines the size of collection
     *
     * @param collection instance of {@link Collection}
     * @return 0 if collection is null or empty, otherwise the size of collection is returned
     */
    public static int getCollectionSize(@Nullable Collection<?> collection) {
        return isEmptyCollection(collection) ? 0 : collection.size();
    }

    /**
     * Safely determines a map is empty or not
     *
     * @param map instance of {@link Map}
     * @return true if map is null or empty
     */
    public static boolean isEmptyMap(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}