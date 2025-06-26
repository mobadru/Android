package com.example.semproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnRoomActionListener listener;


    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(Room room);
    }

    public RoomAdapter(List<Room> roomList, OnRoomActionListener listener) {
        this.roomList = roomList;
        this.listener = listener;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomNumber, txtRoomType, txtRoomRent, txtRoomStatus;
        Button btnEditRoom, btnDeleteRoom;

        public RoomViewHolder(View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.txtRoomNumber);
            txtRoomType = itemView.findViewById(R.id.txtRoomType);
            txtRoomRent = itemView.findViewById(R.id.txtRoomRent);
            txtRoomStatus = itemView.findViewById(R.id.txtRoomStatus);
            btnEditRoom = itemView.findViewById(R.id.btnEditRoom);
            btnDeleteRoom = itemView.findViewById(R.id.btnDeleteRoom);
        }
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.txtRoomNumber.setText("Room No: " + room.getRoomNumber());
        holder.txtRoomType.setText("Type: " + room.getType());
        holder.txtRoomRent.setText("Rent:" + room.getRentAmount()+ "  TZS");
        holder.txtRoomStatus.setText("Status: " + room.getStatus());

        holder.btnEditRoom.setOnClickListener(v -> listener.onEdit(room));
        holder.btnDeleteRoom.setOnClickListener(v -> listener.onDelete(room));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
