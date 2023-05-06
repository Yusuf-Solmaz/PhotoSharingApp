package com.yusuf.photosharingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.yusuf.photosharingapp.databinding.ActivityFeedBinding;
import com.yusuf.photosharingapp.databinding.ActivityMainBinding;

public class FeedActivity extends AppCompatActivity {

    ActivityFeedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }


}