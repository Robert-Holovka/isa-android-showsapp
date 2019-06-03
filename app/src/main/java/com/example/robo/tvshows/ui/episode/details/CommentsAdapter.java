package com.example.robo.tvshows.ui.episode.details;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.robo.tvshows.R;
import com.example.robo.tvshows.data.models.Comment;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> comments;

    CommentsAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_comment, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsAdapter.ViewHolder holder, int position) {
        TextView email = holder.itemView.findViewById(R.id.text_view_comment_user_email);
        TextView commentText = holder.itemView.findViewById(R.id.text_view_comment);

        final Comment comment = comments.get(position);

        email.setText(comment.getUserEmail());
        commentText.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addNewComment(Comment comment){
        comments.add(comment);
        notifyItemInserted(getItemCount());
    }

}
