package com.quintus.labs.datingapp.Matched;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quintus.labs.datingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class ActiveUserAdapter extends RecyclerView.Adapter<ActiveUserAdapter.MyViewHolder> {
    List<Users> usersList;
    Context context;

    public ActiveUserAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ActiveUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_user_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveUserAdapter.MyViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.name.setText(users.getName());
        switch (users.getProfileImageUrl()) {
            case "defaultFemale":
                Glide.with(context).load(R.drawable.default_woman).into(holder.imageView);
                break;
            case "defaultMale":
                Glide.with(context).load(R.drawable.default_man).into(holder.imageView);
                break;
            default:
                Glide.with(context).load(users.getProfileImageUrl()).into(holder.imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name,bio;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.aui_image);
            name = itemView.findViewById(R.id.aui_name);
        }
    }
}
