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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postView;

    private View view;

    private List<BlogPost> blogPosts;
    private BlogRecyclerAdapter adapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private DocumentSnapshot lastVisible;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        postView = (RecyclerView) view.findViewById(R.id.post_view);
        blogPosts = new ArrayList<>();
        adapter = new BlogRecyclerAdapter(blogPosts);
        postView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            firebaseFirestore = FirebaseFirestore.getInstance();

            postView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachBottom = !recyclerView.canScrollVertically(-1);

                    if (reachBottom){
                        Toast.makeText(container.getContext(), "Reached " + lastVisible.getString("desc"), Toast.LENGTH_SHORT).show();
                    }

                    loadMorePosts();
                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                            blogPosts.add(blogPost);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
        return view;
    }

    public void loadMorePosts(){
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                            blogPosts.add(blogPost);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
