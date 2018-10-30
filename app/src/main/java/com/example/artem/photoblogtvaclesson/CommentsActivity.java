package com.example.artem.photoblogtvaclesson;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    private EditText commentText;
    private ImageButton commentBtn;
    private RecyclerView commentRecyclerView;

    private CommentsAdapter commentsAdapter;
    private List<Comments> commentsList;

    private String blog_post_id;
    private String current_user_id;

    private FirebaseFirestore commentsFirestore;
    private FirebaseAuth commentsAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentToolbar = (Toolbar) findViewById(R.id.comments_toolbar);
        commentText = (EditText) findViewById(R.id.comment_text);
        commentBtn = (ImageButton) findViewById(R.id.comment_btn);
        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_list);

        commentsAuth = FirebaseAuth.getInstance();
        commentsFirestore = FirebaseFirestore.getInstance();

        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        current_user_id = commentsAuth.getCurrentUser().getUid();
        blog_post_id = getIntent().getStringExtra("blog_post_id");

        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentsList);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setAdapter(commentsAdapter);

        commentsFirestore.collection("Posts/" + blog_post_id + "/Comments")
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String commentId = doc.getDocument().getId();
                            Comments comments = doc.getDocument().toObject(Comments.class);
                            commentsList.add(comments);
                            commentsAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment_message = commentText.getText().toString();
                if (!comment_message.isEmpty()){
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("message", comment_message);
                    commentMap.put("user_id", current_user_id);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());
                    commentsFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentMap)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(CommentsActivity.this, "Error Post Comment " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }else {
                                        commentText.setText("");

                                    }
                                }
                            });
                }
            }
        });

    }





    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CommentsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
