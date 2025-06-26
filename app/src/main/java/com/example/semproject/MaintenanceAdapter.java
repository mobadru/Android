package com.example.semproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MaintenanceAdapter extends BaseAdapter {

    private Context context;
    private List<MaintenanceReport> reportList;
    private LayoutInflater inflater;

    public MaintenanceAdapter(Context context, List<MaintenanceReport> reportList) {
        this.context = context;
        this.reportList = reportList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return reportList.size();
    }

    @Override
    public Object getItem(int position) {
        return reportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reportList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_maintenance_report, parent, false);
            holder = new ViewHolder();
            holder.tvRoom = convertView.findViewById(R.id.tvRoom);
            holder.tvDescription = convertView.findViewById(R.id.tvDescription);
            holder.tvStatus = convertView.findViewById(R.id.tvStatus);
            holder.tvDate = convertView.findViewById(R.id.tvDateReported);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MaintenanceReport report = reportList.get(position);
        holder.tvRoom.setText("Room: " + report.getRoomNumber()); // Make sure roomNumber is set in the report
        holder.tvDescription.setText("Issue: " + report.getDescription());
        holder.tvStatus.setText("Status: " + report.getStatus());
        holder.tvDate.setText("Reported: " + report.getDateReported());

        return convertView;
    }

    private static class ViewHolder {
        TextView tvRoom, tvDescription, tvStatus, tvDate;
    }
}
