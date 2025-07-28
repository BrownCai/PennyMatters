package cz.adaptech.tesseract4android.sample.preparationPages;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cz.adaptech.tesseract4android.sample.R;


public class welcomePage extends AppCompatActivity {
    private TextView countDownText;
    private CountDownTimer timer;
    private long timeLeftInMillis = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcomePage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        countDownText = findViewById(R.id.countDown_text);
        startCountDown();
    }

    private void startCountDown(){
        timer =new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                countDownText.setText(secondsRemaining +" s");
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(welcomePage.this, login.class));
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }
    }
}