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
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yusuf.photosharingapp.databinding.ActivityMainBinding;
import com.yusuf.photosharingapp.databinding.ActivitySharePhotoBinding;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class SharePhotoActivity extends AppCompatActivity {

    ActivitySharePhotoBinding binding;

    ActivityResultLauncher<Intent> resultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    private FirebaseAuth auth;
    private StorageReference storageReference;

    CollectionReference usersCollectionRef = FirebaseFirestore.getInstance().collection("users");

    Uri imageUri;
    String userName;
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
        storageReference = storage.getReference();


        registerLauncher();
    }





    public void share(View view){

        UUID uuid = UUID.randomUUID();
        String imageName = "images/"+uuid+".jpg";


        usersCollectionRef.whereEqualTo("email", auth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    userName = documentSnapshot.getString("username");

                }
            }
        });

        if (imageUri != null){
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newStorageReference = storage.getReference(imageName);
                    newStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(SharePhotoActivity.this,"Shared",Toast.LENGTH_LONG).show();

                            String imageUrl = uri.toString();
                            String comment = binding.descriptionText.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();

                            String email = user.getEmail();


                            HashMap<String, Object> post = new HashMap<>();

                            post.put("name",userName);
                            post.put("email",email);
                            post.put("comment",comment);
                            post.put("imageUrl",imageUrl);
                            post.put("date", FieldValue.serverTimestamp());

                            firestore.collection("posts")
                                .add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Intent intent = new Intent(SharePhotoActivity.this,FeedActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SharePhotoActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                        }
                                    });


                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SharePhotoActivity.this,"There is an error.",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Toast.makeText(SharePhotoActivity.this,"Images can not be empty.",Toast.LENGTH_LONG).show();
        }
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
                        imageUri = intentFromData.getData();
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


}
