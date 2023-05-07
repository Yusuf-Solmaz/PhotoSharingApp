package com.yusuf.photosharingapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.yusuf.photosharingapp.databinding.ActivityMainBinding;
import com.yusuf.photosharingapp.databinding.ActivitySharePhotoBinding;

public class SharePhotoActivity extends AppCompatActivity {

    ActivitySharePhotoBinding binding;

    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private FirebaseAuth auth;

    //Bitmap selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharePhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firestore=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        registerLauncher();
    }





    public void share(View view){

    }

    public void selectImage(View view){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed for upload images.",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(galleryIntent);
            }
        }
        else {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for upload images.",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                resultLauncher.launch(galleryIntent);
            }
        }
    }
    private void registerLauncher(){
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK){
                    Intent intentFromData = result.getData();
                    if (intentFromData != null){
                        Uri imageUri = intentFromData.getData();
                        binding.imageView.setImageURI(imageUri);
                        /*
                        try {
                            if (Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),imageUri);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                            else {
                                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                                binding.imageView.setImageBitmap(selectedImage);
                            }

                        }
                        catch (Exception e){
                            Toast.makeText(SharePhotoActivity.this,"Something went wrong.",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }*/

                    }
                }

            }
        });

        permissionLauncher =registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    resultLauncher.launch(galleryIntent);
                }
                else {
                    Toast.makeText(SharePhotoActivity.this,"Permission Needed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public Bitmap toSmallImage(@NonNull Bitmap image, int maxSize){
        int width = image.getWidth();
        int height = image.getHeight();
        float sizeRatio = (float) width/height;

        if (sizeRatio>1){
            width = maxSize;
            height = (int) (width/sizeRatio);
        }
        else {
            height = maxSize;
            width = (int) (height*sizeRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }

}