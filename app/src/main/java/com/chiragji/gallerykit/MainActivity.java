package com.chiragji.gallerykit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.chiragji.gallerykit.adapter.MainViewAdapter;
import com.chiragji.gallerykit.fragments.GalleryFragment;
import com.chiragji.gallerykit.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static MainActivity mainActivity;

    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = findViewById(R.id.pager);

        init();
    }

    private void init() {
        Bundle args = new Bundle();
        MainFragment mainFragment = new MainFragment();
        mainFragment.setArguments(args);
        GalleryFragment galleryFragment = new GalleryFragment(mainFragment);
        galleryFragment.setArguments(args);
        pager.setAdapter(new MainViewAdapter(this, mainFragment, galleryFragment));
        pager.setUserInputEnabled(false);
    }

    public static void toggleGalleryFragment(boolean open) {
        mainActivity.pager.setCurrentItem(open ? 1 : 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivity = null;
    }
}