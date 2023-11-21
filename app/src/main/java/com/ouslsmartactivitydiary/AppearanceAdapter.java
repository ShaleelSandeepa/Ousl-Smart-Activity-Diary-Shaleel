package com.ouslsmartactivitydiary;

import static com.ouslsmartactivitydiary.item.AppearanceItem.APPEARANCE;
import static com.ouslsmartactivitydiary.item.AppearanceItem.COLOR;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ouslsmartactivitydiary.data.DatabaseHelper;
import com.ouslsmartactivitydiary.item.AppearanceItem;

import java.util.List;

public class AppearanceAdapter extends RecyclerView.Adapter {

    Context context;
    List<AppearanceItem> appearanceList, colorList;
    AppearanceAdapter.OnItemClickListener onItemClickListener;

    DatabaseHelper databaseHelper;
    Cursor colorCursor, cursor;
    int LAB, QUIZ, PS, VIVA, DS, TMA, CAT, FINAL;
    int newColor;

    public AppearanceAdapter(Context context, List<AppearanceItem> appearanceList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.appearanceList = appearanceList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        switch (appearanceList.get(position).getViewType()) {
            case 1:
                return APPEARANCE;
            case 2:
                return COLOR;
            default:
                return -1;

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case APPEARANCE:
                return new AppearanceViewHolder(LayoutInflater.from(context).inflate(R.layout.item_appearance, parent, false));
            case COLOR:
                return new AppearanceViewHolder(LayoutInflater.from(context).inflate(R.layout.item_color, parent, false));
            default:
                return null;
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        databaseHelper = new DatabaseHelper(context);
        checkColors();

        switch (appearanceList.get(position).getViewType()) {

            case APPEARANCE:
                ((AppearanceViewHolder) holder).appearanceIcon.setImageResource(appearanceList.get(position).getAppearanceIcon());
                ((AppearanceViewHolder) holder).appearanceName.setText(appearanceList.get(position).getAppearanceName());
                ((AppearanceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(appearanceList.get(position));
                    }
                });

                switch (appearanceList.get(position).getAppearanceIcon()) {
                    case R.drawable.ic_labtest_2:
                        newColor = LAB;
                        break;
                    case R.drawable.ic_assignment_2:
                        newColor = TMA;
                        break;
                    case R.drawable.ic_ds_2:
                        newColor = DS;
                        break;
                    case R.drawable.ic_ps_2:
                        newColor = PS;
                        break;
                    case R.drawable.ic_cat_2:
                        newColor = CAT;
                        break;
                    case R.drawable.ic_final_2:
                        newColor = FINAL;
                        break;
                    case R.drawable.ic_viva_2:
                        newColor = VIVA;
                        break;
                    case R.drawable.ic_quiz_2:
                        newColor = QUIZ;
                        break;
                    default:
                        newColor = ContextCompat.getColor(context, R.color.DarkGray);
                }

                // Get the drawable from the ImageView
                Drawable originalDrawable = ((AppearanceViewHolder) holder).appearanceIcon.getDrawable();
                // Create a copy of the drawable to avoid modifying the original
                Drawable wrappedDrawable = DrawableCompat.wrap(originalDrawable).mutate();
                // Apply the color tint
                DrawableCompat.setTint(wrappedDrawable, newColor);

                ((AppearanceViewHolder) holder).appearanceIcon.setImageDrawable(wrappedDrawable);
                break;

            case COLOR:
                if (appearanceList.get(position).getState() == 1) {
                    ((AppearanceViewHolder) holder).colorItemCardSelected.setBackgroundResource(R.drawable.curved_background_all_border);
                } else {
                    ((AppearanceViewHolder) holder).colorItemCardSelected.setBackgroundResource(0);
                }
                ((AppearanceViewHolder) holder).colorItemCard.setCardBackgroundColor(appearanceList.get(position).getColor());
                ((AppearanceViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(appearanceList.get(position));
//                        ((AppearanceViewHolder) holder).itemView.setBackgroundResource(R.drawable.curved_background_all_border);
                    }
                });

                break;

        }


    }

    public void checkColors() {
        colorCursor = databaseHelper.getAllColors();
        while (colorCursor.moveToNext()) {
            switch (colorCursor.getInt(0)) {
                case 1:
                    LAB = colorCursor.getInt(2);
                    break;
                case 2:
                    QUIZ = colorCursor.getInt(2);
                    break;
                case 3:
                    PS = colorCursor.getInt(2);
                    break;
                case 4:
                    VIVA = colorCursor.getInt(2);
                    break;
                case 5:
                    DS = colorCursor.getInt(2);
                    break;
                case 6:
                    TMA = colorCursor.getInt(2);
                    break;
                case 7:
                    CAT = colorCursor.getInt(2);
                    break;
                case 8:
                    FINAL = colorCursor.getInt(2);
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return appearanceList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AppearanceItem position);
    }
}
