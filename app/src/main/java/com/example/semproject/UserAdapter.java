package com.example.semproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEdit(User user);
        void onDelete(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, roleTextView;
        Button editButton, deleteButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userName);
            emailTextView = itemView.findViewById(R.id.userEmail);
            phoneTextView = itemView.findViewById(R.id.userPhone); // <-- Add this line
            roleTextView = itemView.findViewById(R.id.userRole);
            editButton = itemView.findViewById(R.id.btnEditUser);
            deleteButton = itemView.findViewById(R.id.btnDeleteUser);
        }
    }


    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getName());
        holder.phoneTextView.setText(user.getPhone());
        holder.emailTextView.setText(user.getEmail());
        holder.roleTextView.setText(user.getRole());

        holder.editButton.setOnClickListener(v -> listener.onEdit(user));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
