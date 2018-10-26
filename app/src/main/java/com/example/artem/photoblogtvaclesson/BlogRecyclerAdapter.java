package com.example.artem.photoblogtvaclesson;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blogPosts;
    public BlogRecyclerAdapter(List<BlogPost> blogPosts) {
        this.blogPosts = blogPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String description = blogPosts.get(position).getDesc();
        holder.setTextDescription(description);

    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView postUserName, postTime, postDescription;
        private ImageView postImage;
        private CircleImageView postUserImage;
        private View view;
//    private CardView postCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            postUserName = (TextView) view.findViewById(R.id.post_user_name);
            postTime = (TextView) view.findViewById(R.id.post_time);
            postDescription = (TextView) view.findViewById(R.id.post_description);
            postImage = (ImageView) view.findViewById(R.id.post_image);
            postUserImage = (CircleImageView) view.findViewById(R.id.post_user_image);
//        postCard = (CardView) view.findViewById(R.id.post_card);
        }
        public void setTextDescription(String description){
            postDescription.setText(description);
        }
    }
}
