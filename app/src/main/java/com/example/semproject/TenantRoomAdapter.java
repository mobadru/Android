package com.example.semproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TenantRoomAdapter extends RecyclerView.Adapter<TenantRoomAdapter.RoomViewHolder> {

    private List<Room> roomList;
    private OnRoomLeaseClickListener listener;

    public interface OnRoomLeaseClickListener {
        void onLeaseClick(Room room);
    }

    public TenantRoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    public void setOnRoomLeaseClickListener(OnRoomLeaseClickListener listener) {
        this.listener = listener;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView txtRoomNumber, txtRoomType, txtRoomRent;
        Button btnLeaseRoom;

        public RoomViewHolder(View itemView) {
            super(itemView);
            txtRoomNumber = itemView.findViewById(R.id.txtRoomNumber);
            txtRoomType = itemView.findViewById(R.id.txtRoomType);
            txtRoomRent = itemView.findViewById(R.id.txtRoomRent);
            btnLeaseRoom = itemView.findViewById(R.id.btnLeaseRoom);
        }
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item_tenant, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.txtRoomNumber.setText("Room No: " + room.getRoomNumber());
        holder.txtRoomType.setText("Type: " + room.getType());
        holder.txtRoomRent.setText("Rent: " + room.getRentAmount() + " TZS");

        holder.btnLeaseRoom.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLeaseClick(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
