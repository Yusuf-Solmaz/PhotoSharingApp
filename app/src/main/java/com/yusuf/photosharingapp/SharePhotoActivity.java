package com.yusuf.photosharingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.yusuf.photosharingapp.databinding.ActivityMainBinding;
import com.yusuf.photosharingapp.databinding.ActivitySharePhotoBinding;

public class SharePhotoActivity extends AppCompatActivity {

    ActivitySharePhotoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharePhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }





    public void share(View view){

    }

    public void selectImage(View view){

    }
}