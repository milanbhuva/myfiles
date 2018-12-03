package com.fourarc.videostatus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fourarc.videostatus.R;
import com.fourarc.videostatus.config.GetCommPojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hsn on 16/01/2018.
 */


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<GetCommPojo> commentList= new ArrayList<>();
    private Context context;
    public CommentAdapter(List<GetCommPojo> commentList, Context context){
        this.context=context;
        this.commentList=commentList;
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CommentAdapter.CommentHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder,final int position) {
        holder.text_view_time_item_comment.setText(commentList.get(position).getComment_created_date());

        /*byte[] data = Base64.decode(commentList.get(position).getComment_text(), Base64.DEFAULT);
        String Comment_text = "";
        try {
            Comment_text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Comment_text = commentList.get(position).getComment_text();
        }
*/
        holder.text_view_name_item_comment.setText(commentList.get(position).getUser_name());
        Picasso.with(context).load(commentList.get(position).getUser_profile_picture()).placeholder(R.drawable.profile).into(holder.image_view_comment_iten);
            holder.text_view_content_item_comment.setText(commentList.get(position).getComment_text());
            holder.text_view_content_item_comment.setTextColor(context.getResources().getColor(R.color.gray_color));

    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_name_item_comment;
        private final TextView text_view_time_item_comment;
        private final TextView text_view_content_item_comment;
        private final CircleImageView image_view_comment_iten;
        public CommentHolder(View itemView) {
            super(itemView);
            this.image_view_comment_iten=(CircleImageView) itemView.findViewById(R.id.image_view_comment_iten);
            this.text_view_name_item_comment=(TextView) itemView.findViewById(R.id.text_view_name_item_comment);
            this.text_view_time_item_comment=(TextView) itemView.findViewById(R.id.text_view_time_item_comment);
            this.text_view_content_item_comment=(TextView) itemView.findViewById(R.id.text_view_content_item_comment);
        }
    }
}
