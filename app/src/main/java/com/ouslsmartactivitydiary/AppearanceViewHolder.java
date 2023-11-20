package com.ouslsmartactivitydiary;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AppearanceViewHolder extends RecyclerView.ViewHolder {

    ImageView appearanceIcon;
    TextView appearanceName;
    LinearLayout colorItemCardSelected;

    CardView colorItemCard;

    public AppearanceViewHolder(@NonNull View itemView) {
        super(itemView);

        appearanceIcon = itemView.findViewById(R.id.appearanceIcon);
        appearanceName = itemView.findViewById(R.id.appearanceName);
        colorItemCard = itemView.findViewById(R.id.colorItemCard);
        colorItemCardSelected = itemView.findViewById(R.id.colorItemCardSelected);
    }
}
