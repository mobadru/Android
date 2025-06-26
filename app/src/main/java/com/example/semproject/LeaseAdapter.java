package com.example.semproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.semproject.LeaseDetails;
import com.example.semproject.R;

import java.util.List;

public class LeaseAdapter extends RecyclerView.Adapter<LeaseAdapter.LeaseViewHolder> {

    private final List<LeaseDetails> leaseList;

    public LeaseAdapter(List<LeaseDetails> leaseList) {
        this.leaseList = leaseList;
    }

    @NonNull
    @Override
    public LeaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lease, parent, false);
        return new LeaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaseViewHolder holder, int position) {
        LeaseDetails lease = leaseList.get(position);

        holder.tvRoomNumber.setText(lease.getRoomNumber());
        holder.tvRentAmount.setText(String.format("$%.2f/month", lease.getRentAmount()));
        holder.tvLeaseStart.setText("Start: " + lease.getLeaseStart());
        holder.tvLeaseEnd.setText("End: " + lease.getLeaseEnd());
    }

    @Override
    public int getItemCount() {
        return leaseList.size();
    }

    static class LeaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvRentAmount, tvLeaseStart, tvLeaseEnd;

        public LeaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRentAmount = itemView.findViewById(R.id.tvRentAmount);
            tvLeaseStart = itemView.findViewById(R.id.tvLeaseStart);
            tvLeaseEnd = itemView.findViewById(R.id.tvLeaseEnd);
        }
    }
}
