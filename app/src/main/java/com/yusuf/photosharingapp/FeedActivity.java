package com.yusuf.photosharingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yusuf.photosharingapp.adapter.PostsAdapter;
import com.yusuf.photosharingapp.databinding.ActivityFeedBinding;
import com.yusuf.photosharingapp.databinding.ActivityMainBinding;
import com.yusuf.photosharingapp.model.Post;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    ActivityFeedBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    ArrayList<Post> postArrayList;
PostsAdapter postsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList=new ArrayList<>();

        auth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        getData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsAdapter = new PostsAdapter(postArrayList);
        binding.recyclerView.setAdapter(postsAdapter);

    }

    public void getData(){

        firestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });

        firestore.collection("posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (value != null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String,Object> data = snapshot.getData();

                        String name = (String) data.get("name");
                        String comment = (String) data.get("comment");
                        String url = (String) data.get("imageUrl");

                        Post post = new Post(name,comment,url);
                        postArrayList.add(post);
                    }
                    postsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.feedmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.shareImage){
            Intent intent = new Intent(FeedActivity.this, SharePhotoActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId()==R.id.logOut) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
            builder.setTitle("Log Out");
            builder.setMessage("Are You Sure?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    auth.signOut();
                    Intent intent = new Intent(FeedActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
}