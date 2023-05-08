package com.yusuf.photosharingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yusuf.photosharingapp.databinding.PostRowBinding;
import com.yusuf.photosharingapp.model.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PlaceHolder>{

    List<Post> posts;

    public PostsAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostRowBinding binding = PostRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PlaceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) {
        holder.binding.recyeclerViewName.setText(posts.get(position).getName());
        holder.binding.recyeclerViewComment.setText(posts.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    public class PlaceHolder extends RecyclerView.ViewHolder{

        PostRowBinding binding;

        public PlaceHolder(PostRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

        }
    }
}
