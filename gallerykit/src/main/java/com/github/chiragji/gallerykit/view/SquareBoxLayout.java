package com.github.chiragji.gallerykit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This is the layout which is inflated as square box. Mainly this will be rendered the height of
 * layout as same as the width calculated, making all the elements as same in width and height.
 * <p>
 * Useful in grid layout managers, where all the data needs to be displayed in a grid of data
 * All elements are adjusted accordingly
 *
 * @author Chirag [apps.chiragji@outlook.com]
 * @version 1
 * @since 1.0.0
 */
public class SquareBoxLayout extends FrameLayout {
    public SquareBoxLayout(@NonNull Context context) {
        super(context);
    }

    public SquareBoxLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBoxLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SquareBoxLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
