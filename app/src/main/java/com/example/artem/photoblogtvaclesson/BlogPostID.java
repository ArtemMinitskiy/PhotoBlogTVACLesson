package com.example.artem.photoblogtvaclesson;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class BlogPostID {

    @Exclude
    public String BlogPostID;

    public <T extends BlogPostID> T withId(@NonNull final String id){
        this.BlogPostID = id;
        return (T) this;
    }
}
