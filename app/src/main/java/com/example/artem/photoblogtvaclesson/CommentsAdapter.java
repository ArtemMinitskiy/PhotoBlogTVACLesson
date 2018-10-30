package com.example.artem.photoblogtvaclesson;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;

    public CommentsAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_list_item, viewGroup, false);
        context = viewGroup.getContext();
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

    }

    @Override
    public int getItemCount() {
        if (commentsList != null){
            return commentsList.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView comment_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            comment_message = (TextView) view.findViewById(R.id.comment_message);
        }

        public void setComment_message(String commentMessage) {
            comment_message.setText(commentMessage);
        }
    }
}
