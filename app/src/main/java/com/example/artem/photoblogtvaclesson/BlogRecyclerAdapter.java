package com.example.artem.photoblogtvaclesson;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blogPosts;
    public BlogRecyclerAdapter(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item, viewGroup, false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        final String blogPostID = blogPosts.get(position).BlogPostID;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String user_id = blogPosts.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserName(userName);
                    holder.setUserImage(userImage);
                }else {

                }
            }
        });

        String description = blogPosts.get(position).getDesc();
        String image_url = blogPosts.get(position).getImage_url();
        String thumb_url = blogPosts.get(position).getImage_thumb();

        holder.setTextDescription(description);
        holder.setImage(image_url, thumb_url);

        long milliseconds = blogPosts.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MM/dd/yyyy", new Date(milliseconds)).toString();
        holder.setTime(dateString);

        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()){
                    int count = queryDocumentSnapshots.size();

                    holder.updateLikesCount(count);
                }else {
                    holder.updateLikesCount(0);
                }
            }
        });

        firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    holder.likeBtn.setImageDrawable(context.getDrawable(R.mipmap.ic_favorite_like));
                }else{
                    holder.likeBtn.setImageDrawable(context.getDrawable(R.mipmap.ic_favorite));
                }
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostID + "/Likes").document(currentUserId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (!task.getResult().exists()){
                                    Map<String, Object> likesMap = new HashMap<>();
                                    likesMap.put("timestamp", FieldValue.serverTimestamp());
                                    firebaseFirestore.collection("Posts/" + blogPostID + "/Likes")
                                            .document(currentUserId)
                                            .set(likesMap);
                                }else{
                                    firebaseFirestore.collection("Posts/" + blogPostID + "/Likes")
                                            .document(currentUserId)
                                            .delete();
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView postUserName, postTime, postDescription, blogLikeCount;
        private ImageView postImage, likeBtn;
        private CircleImageView postUserImage;
        private View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            postUserName = (TextView) view.findViewById(R.id.post_user_name);
            postTime = (TextView) view.findViewById(R.id.post_time);
            postDescription = (TextView) view.findViewById(R.id.post_description);
            blogLikeCount = (TextView) view.findViewById(R.id.blog_like_count);
            postImage = (ImageView) view.findViewById(R.id.post_image);
            likeBtn = (ImageView) view.findViewById(R.id.blog_like_btn);
            postUserImage = (CircleImageView) view.findViewById(R.id.post_user_image);


        }

        public void setUserName(String name){
            postUserName.setText(name);
        }
        public void setTime(String time){
            postTime.setText(time);
        }
        public void setTextDescription(String description){
            postDescription.setText(description);
        }
        public void setUserImage(String downloadUrl){
            Glide.with(context).load(downloadUrl).into(postUserImage);
        }
        public void setImage(String downloadUrl, String thumb_url){
            RequestOptions placeholder = new RequestOptions();
            placeholder.placeholder(R.drawable.user_default);

            Glide.with(context).applyDefaultRequestOptions(placeholder)
                    .load(downloadUrl)
                    .thumbnail(Glide.with(context).load(thumb_url))
                    .into(postImage);
        }

        public void updateLikesCount(int count){
            blogLikeCount.setText(count + " Likes");

        }
    }
}
