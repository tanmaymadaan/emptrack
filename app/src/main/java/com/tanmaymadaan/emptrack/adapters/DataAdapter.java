package com.tanmaymadaan.emptrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.models.UserPOJO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    List<UserPOJO> users;

    public DataAdapter(List<UserPOJO> users){
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTv.setText(users.get(position).getName());
        String swipeStatus = users.get(position).getSwipeStatus();
        if(swipeStatus.equals("true")){
            holder.statusTv.setText("Present");
        } else {
            holder.statusTv.setText("Absent");
        }

        String currCheckIn = users.get(position).getCurrCheckIn();
        if(currCheckIn.equals("Null")){
            holder.checkInTv.setText("Not Checked In");
        } else{
            holder.checkInTv.setText("Currently at: " + currCheckIn);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTv, statusTv, checkInTv;
        public ViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.row_name_tv);
            statusTv = view.findViewById(R.id.row_status_tv);
            checkInTv = view.findViewById(R.id.row_check_in_tv);



        }
    }
}
