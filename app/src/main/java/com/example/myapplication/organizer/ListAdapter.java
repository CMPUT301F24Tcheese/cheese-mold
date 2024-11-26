package com.example.myapplication.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.objects.Users;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<Users> users;

    private List<String> selectedUserIds = new ArrayList<>();
    private boolean showCheckBox;

    public ListAdapter(List<Users> users, boolean showCheckBox) {
        this.users = users;
        this.showCheckBox = showCheckBox;
    }

    public List<String> getSelectedUserIds() {
        return selectedUserIds;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Users user = users.get(position);
        holder.nameTextView.setText(user.getName());
        Glide.with(holder.itemView.getContext())
                .load(user.getProfilePicture())
                .into(holder.avatarImageView);

        if (showCheckBox) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedUserIds.contains(user.getUserId()));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedUserIds.add(user.getUserId());
                } else {
                    selectedUserIds.remove(user.getUserId());
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView avatarImageView;
        CheckBox checkBox;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            avatarImageView = itemView.findViewById(R.id.imageAvatar);
            checkBox = itemView.findViewById(R.id.checkBoxSelectUser);
        }
    }
}
