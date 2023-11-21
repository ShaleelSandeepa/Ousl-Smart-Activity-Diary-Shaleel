package com.ouslsmartactivitydiary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ouslsmartactivitydiary.AppearanceAdapter;
import com.ouslsmartactivitydiary.item.AppearanceItem;
import com.ouslsmartactivitydiary.R;
import com.ouslsmartactivitydiary.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AppearanceDialog extends AppCompatDialogFragment {

    Context context;
    AppearanceItem itemPosition, previous;
    RecyclerView recyclerViewColors;
    List<AppearanceItem> appearanceColorsList;
    AppearanceAdapter appearanceAdapter;

    ImageView appearanceIcon;
    TextView appearanceName, appearanceSetDefault;
    ProgressBar progressBar;

    DatabaseHelper databaseHelper;
    Cursor cursor;

    int defaultColor;

    AppearanceDialog.OnDismissListener onDismissListener;

    public static AppearanceDialog newInstance() {
        return new AppearanceDialog();
    }

    public AppearanceDialog() {

    }

    public AppearanceDialog(AppearanceItem itemPosition) {
        this.itemPosition = itemPosition;
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        context = getContext();
        databaseHelper = new DatabaseHelper(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_appearance_dialog, null);

        recyclerViewColors = view.findViewById(R.id.recyclerViewColors);
        appearanceColorsList = new ArrayList<>();
        addColors();

        appearanceIcon = view.findViewById(R.id.appearanceIcon);
        appearanceName = view.findViewById(R.id.appearanceName);
        progressBar = view.findViewById(R.id.progressBar);
        appearanceSetDefault = view.findViewById(R.id.appearanceSetDefault);

        appearanceIcon.setImageResource(itemPosition.getAppearanceIcon());
        appearanceName.setText(itemPosition.getAppearanceName());

        appearanceAdapter = new AppearanceAdapter(context, appearanceColorsList, new AppearanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AppearanceItem position) {

                progressBar.setVisibility(View.VISIBLE);
                databaseHelper.updateColorValueByName(itemPosition.getAppearanceName(), position.getColor());
//                addColors();
//                previous.setState(0);
                previous.setState(0); // update new color state as 1
                previous = position;
                position.setState(1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        appearanceAdapter.notifyDataSetChanged();
                        appearanceAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
//                previous = position;

            }
        });

        recyclerViewColors.setLayoutManager(new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL));
        recyclerViewColors.setAdapter(appearanceAdapter);
        appearanceAdapter.notifyDataSetChanged();

        appearanceSetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                getDefaultColor();
                databaseHelper.updateColorValueByName(itemPosition.getAppearanceName(), defaultColor);

                for (int i=0; i<appearanceColorsList.size(); i++) {
                    if (appearanceColorsList.get(i).getColor() == defaultColor) {
                        appearanceColorsList.get(i).setState(1);
                    } else {
                        appearanceColorsList.get(i).setState(0);
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        appearanceAdapter.notifyDataSetChanged();
                        appearanceAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public void addColors() {
        appearanceColorsList.clear();
        appearanceColorsList.add(new AppearanceItem(2, 0, 1, getResources().getColor(R.color.DarkBlue)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 2, getResources().getColor(R.color.Blue)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 3, getResources().getColor(R.color.DodgerBlue)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 4, getResources().getColor(R.color.LightBlue300)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 5, getResources().getColor(R.color.LightBlue200)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 6, getResources().getColor(R.color.LightBlue100)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 7, getResources().getColor(R.color.DarkPurple)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 8, getResources().getColor(R.color.DarkRose)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 9, getResources().getColor(R.color.MediumRose)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 10, getResources().getColor(R.color.purple_500)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 11, getResources().getColor(R.color.Purple)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 12, getResources().getColor(R.color.purple_200)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 13, getResources().getColor(R.color.DarkGreen)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 14, getResources().getColor(R.color.Green)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 15, getResources().getColor(R.color.LightGreen)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 16, getResources().getColor(R.color.Orange)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 17, getResources().getColor(R.color.DarkYellow)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 18, getResources().getColor(R.color.Yellow)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 20, getResources().getColor(R.color.LightBrown)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 21, getResources().getColor(R.color.LighterBrown)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 22, getResources().getColor(R.color.DarkRed)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 23, getResources().getColor(R.color.Red)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 24, getResources().getColor(R.color.LightRed)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 29, getResources().getColor(R.color.QUIZ)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 25, getResources().getColor(R.color.FINAL)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 26, getResources().getColor(R.color.CAT)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 19, getResources().getColor(R.color.Brown)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 27, getResources().getColor(R.color.TMA)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 28, getResources().getColor(R.color.Theme)));
        appearanceColorsList.add(new AppearanceItem(2, 0, 30, getResources().getColor(R.color.DarkGray)));

        changeState(1);
    }

    public void changeState(int state) {
        cursor = databaseHelper.getColorByName(itemPosition.getAppearanceName());
        if (cursor.moveToFirst()) {
            int color = cursor.getInt(2);
            for (int i=0; i<appearanceColorsList.size(); i++) {
                if (appearanceColorsList.get(i).getColor() == color) {
                    appearanceColorsList.get(i).setState(state);
                    previous = appearanceColorsList.get(i);
                    break;
                }
            }
        }
    }

    public void getDefaultColor() {
        switch (itemPosition.getAppearanceName()) {
            case "LAB":
                defaultColor = getResources().getColor(R.color.LAB);
                break;
            case "TMA":
                defaultColor = getResources().getColor(R.color.TMA);
                break;
            case "DS":
                defaultColor = getResources().getColor(R.color.DS);
                break;
            case "PS":
                defaultColor = getResources().getColor(R.color.PS);
                break;
            case "CAT":
                defaultColor = getResources().getColor(R.color.CAT);
                break;
            case "FINAL":
                defaultColor = getResources().getColor(R.color.FINAL);
                break;
            case "VIVA":
                defaultColor = getResources().getColor(R.color.VIVA);
                break;
            case "QUIZ":
                defaultColor = getResources().getColor(R.color.QUIZ);
                break;
            default:
                defaultColor = getResources().getColor(R.color.DarkGray);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismissListen();
        } else {
            Log.e("AppearanceDialog", "onDismissListener is null");
        }
    }

    public interface OnDismissListener {
        void onDismissListen();
    }

    public void setOnDismissListener(AppearanceDialog.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
