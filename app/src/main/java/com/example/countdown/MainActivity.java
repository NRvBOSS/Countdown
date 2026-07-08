package com.example.countdown;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private CountDownTimer countDownTimer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("countdown", MODE_PRIVATE);

        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvDay = findViewById(R.id.tvDay);
        TextView tvHour = findViewById(R.id.tvHour);
        TextView tvMinute = findViewById(R.id.tvMinute);
        TextView tvSecond = findViewById(R.id.tvSecond);
        Button btnSelectDate = findViewById(R.id.btnSelectDate);

        btnSelectDate.setOnClickListener(v -> {

            Calendar current = Calendar.getInstance();
            Calendar target = Calendar.getInstance();

            int year = current.get(Calendar.YEAR);
            int month = current.get(Calendar.MONTH);
            int day = current.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {

                        tvDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);

                        target.set(year1, month1, dayOfMonth, 0, 0, 0);

                        sharedPreferences.edit()
                                .putLong("target_date", target.getTimeInMillis())
                                .apply();

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                        }

                        countDownTimer = new CountDownTimer(
                                target.getTimeInMillis() - System.currentTimeMillis(),
                                1000
                        ) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                                long totalSeconds = millisUntilFinished / 1000;

                                long days = totalSeconds / (24 * 60 * 60);
                                long hours = (totalSeconds % (24 * 60 * 60)) / (60 * 60);
                                long minutes = (totalSeconds % (60 * 60)) / 60;
                                long seconds = totalSeconds % 60;

                                tvDay.setText(days + " Gün");
                                tvHour.setText(hours + " Saat");
                                tvMinute.setText(minutes + " Dəqiqə");
                                tvSecond.setText(seconds + " Saniyə");
                            }

                            @Override
                            public void onFinish() {
                                tvDay.setText("0 Gün");
                                tvHour.setText("0 Saat");
                                tvMinute.setText("0 Dəqiqə");
                                tvSecond.setText("0 Saniyə");
                            }
                        };

                        countDownTimer.start();

                    },
                    year,
                    month,
                    day
            ).show();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}