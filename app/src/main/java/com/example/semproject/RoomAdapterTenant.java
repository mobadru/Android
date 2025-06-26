package com.example.semproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomAdapterTenant extends RecyclerView.Adapter<RoomAdapterTenant.RoomViewHolder> {

    private final List<Room> roomList;
    private final OnLeaseClickListener leaseClickListener;

    public interface OnLeaseClickListener {
        void onLeaseClick(Room room);
    }

    public RoomAdapterTenant(List<Room> roomList, OnLeaseClickListener listener) {
        this.roomList = roomList;
        this.leaseClickListener = listener;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomNumber, txtRoomType, txtRoomRent, txtRoomStatus;
        Button btnAddLease;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            txtRoomType = itemView.findViewById(R.id.tvType);
            txtRoomRent = itemView.findViewById(R.id.txtRoomRent);
            txtRoomStatus = itemView.findViewById(R.id.tvStatus);
            btnAddLease = itemView.findViewById(R.id.btnLeaseRoom);
        }
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item_tenant, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.txtRoomNumber.setText("Room No: " + room.getRoomNumber());
        holder.txtRoomType.setText("Type: " + room.getType());
        holder.txtRoomRent.setText("Rent: " + room.getRentAmount() + " TZS");
        holder.txtRoomStatus.setText("Status: " + room.getStatus());

        boolean isAvailable = "Available".equalsIgnoreCase(room.getStatus());
        holder.btnAddLease.setVisibility(isAvailable ? View.VISIBLE : View.GONE);

        if (isAvailable) {
            holder.btnAddLease.setOnClickListener(v -> leaseClickListener.onLeaseClick(room));
        } else {
            holder.btnAddLease.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
