package com.ouslsmartactivitydiary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.ouslsmartactivitydiary.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    TextView textView, textView2;
    private String fullText;
    private int charIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = findViewById(R.id.textView);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "Agbalumo-Regular.ttf");
        textView.setTypeface(customFont, Typeface.BOLD);

        textView2 = findViewById(R.id.textView2);
        Typeface customFont2 = Typeface.createFromAsset(getAssets(), "Agbalumo-Regular.ttf");
        textView2.setTypeface(customFont);
        fullText = textView2.getText().toString();
        startTypingAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentDashboard = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intentDashboard);
            }
        }, 3000);

    }

    private void startTypingAnimation() {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (charIndex <= fullText.length()) {
                    // Update the text with the next character
                    textView2.setText(fullText.substring(0, charIndex));
                    charIndex++;
                    // Call the handler again after a delay
                    sendMessageDelayed(obtainMessage(), 100);
                }
            }
        };

        // Start the typing animation
        handler.sendMessageDelayed(handler.obtainMessage(), 100);
    }
}