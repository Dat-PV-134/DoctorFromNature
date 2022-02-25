package com.android45.doctorfromnature.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android45.doctorfromnature.R;
import com.android45.doctorfromnature.models.NotificationModel;

import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.ViewHolder> {
    Context context;
    List<NotificationModel>  notificationModels;

    public MyNotificationAdapter(Context context, List<NotificationModel> notificationModels) {
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.notification_item, parent, false);

        MyNotificationAdapter.ViewHolder viewHolder = new MyNotificationAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = notificationModels.get(position);

        holder.notification.setText(model.getNotification());
        holder.dateAndTime.setText(model.getDateAndTime());
    }

    @Override
    public int getItemCount() {
        return notificationModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notification, dateAndTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification = itemView.findViewById(R.id.tvNotifi);
            dateAndTime = itemView.findViewById(R.id.tvDateAndTime);
        }
    }
}
