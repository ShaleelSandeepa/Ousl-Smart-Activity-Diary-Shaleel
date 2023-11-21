package com.ouslsmartactivitydiary;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaffViewHolder extends RecyclerView.ViewHolder {

    LinearLayout linearStaffDetails;
    TextView txtStaffName, txtStaffEmail, txtStaffDetails;
    ImageView staffDropDown;

    public StaffViewHolder(@NonNull View itemView) {
        super(itemView);

        linearStaffDetails = itemView.findViewById(R.id.linearStaffDetails);
        txtStaffName = itemView.findViewById(R.id.txtStaffName);
        txtStaffEmail = itemView.findViewById(R.id.txtStaffEmail);
        txtStaffDetails = itemView.findViewById(R.id.txtStaffDetails);
        staffDropDown = itemView.findViewById(R.id.staffDropDown);
    }
}
