package com.tanmaymadaan.emptrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanmaymadaan.emptrack.R;
import com.tanmaymadaan.emptrack.models.CheckInPOJO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CheckInsAdapter extends RecyclerView.Adapter<CheckInsAdapter.ViewHolder> {

    List<CheckInPOJO> checkIns;
    Context mContext;

    public CheckInsAdapter(List<CheckInPOJO> checkins, Context context){
        this.checkIns = checkins;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkins_row, parent, false);
        return new CheckInsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.companyTv.setText(checkIns.get(position).getCompany());
        holder.purposeTv.setText(checkIns.get(position).getPurpose());
        holder.checkInTv.setText(checkIns.get(position).getCheckInTime());
        holder.checkOutTv.setText(checkIns.get(position).getCheckOutTime());
        holder.remarksTv.setText(checkIns.get(position).getRemarks());
    }

    @Override
    public int getItemCount() {
        return checkIns.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView companyTv, purposeTv, checkInTv, checkOutTv, remarksTv;
        private CardView parentCard;
        public ViewHolder(View view) {
            super(view);

            parentCard = view.findViewById(R.id.checkinsCardParent);
            companyTv = view.findViewById(R.id.company_name_tv_checkIns_row);
            purposeTv = view.findViewById(R.id.purpose_tv_checkIns_row);
            checkInTv = view.findViewById(R.id.check_in_time_tv_checkIns_row);
            checkOutTv = view.findViewById(R.id.check_out_time_tv_checkIns_row);
            remarksTv = view.findViewById(R.id.remarks_tv_checkIns_row);

        }
    }
}
