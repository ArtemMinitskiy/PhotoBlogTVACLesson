package com.example.artem.photoblogtvaclesson;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postView;

    private View view;

    private List<BlogPost> blogPosts;
    private BlogRecyclerAdapter adapter;

    private FirebaseFirestore firebaseFirestore;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        postView = (RecyclerView) view.findViewById(R.id.post_view);
        blogPosts = new ArrayList<>();
        adapter = new BlogRecyclerAdapter(blogPosts);
        postView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        blogPosts.add(blogPost);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        return view;
    }
}
