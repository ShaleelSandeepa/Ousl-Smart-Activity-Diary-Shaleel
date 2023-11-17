package com.ouslsmartactivitydiary.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ouslsmartactivitydiary.R;

public class AboutActivity extends AppCompatActivity {

    private ImageView backIcon;
    private AppCompatTextView textView;
    private Toolbar actionBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        actionBar = findViewById(R.id.myActionBar);
        //Change action bar title
        textView = findViewById(R.id.title_actionbar);
        textView.setText("About");

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.white));
        //change the status bar to dark theme
        getWindow().getDecorView().setBackgroundColor(getColor(R.color.white));
        //change status bar color to dark
        if (Build.VERSION.SDK_INT >= 23){
            View decor = getWindow().getDecorView();
            if (decor.getSystemUiVisibility()!= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            else
                decor.setSystemUiVisibility(0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}