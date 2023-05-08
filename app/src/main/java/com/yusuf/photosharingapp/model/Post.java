package com.yusuf.photosharingapp.model;

public class Post {

    private String name;
    private String comment;
    private String imageUrl;

    public Post(String name, String comment, String imageUrl) {
        this.name = name;
        this.comment = comment;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
