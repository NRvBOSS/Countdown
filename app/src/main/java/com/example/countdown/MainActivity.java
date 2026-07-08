package com.example.countdown;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

    private TextView tvDate, tvDay, tvHour, tvMinute, tvSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("countdown", MODE_PRIVATE);

        tvDate = findViewById(R.id.tvDate);
        tvDay = findViewById(R.id.tvDay);
        tvHour = findViewById(R.id.tvHour);
        tvMinute = findViewById(R.id.tvMinute);
        tvSecond = findViewById(R.id.tvSecond);

        Button btnSelectDate = findViewById(R.id.btnSelectDate);

        long savedTarget = sharedPreferences.getLong("target_date", 0);

        if (savedTarget > System.currentTimeMillis()) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(savedTarget);

            tvDate.setText(
                    c.get(Calendar.DAY_OF_MONTH) + "/" +
                            (c.get(Calendar.MONTH) + 1) + "/" +
                            c.get(Calendar.YEAR) + " " +
                            String.format("%02d:%02d",
                                    c.get(Calendar.HOUR_OF_DAY),
                                    c.get(Calendar.MINUTE))
            );

            startCountdown(savedTarget);
        }

        btnSelectDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            Calendar target = Calendar.getInstance();

            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {

                        new TimePickerDialog(
                                this,
                                (timeView, hour, minute) -> {

                                    target.set(year, month, day, hour, minute, 0);

                                    long targetMillis = target.getTimeInMillis();

                                    tvDate.setText(
                                            day + "/" +
                                                    (month + 1) + "/" +
                                                    year + " " +
                                                    String.format("%02d:%02d", hour, minute)
                                    );

                                    sharedPreferences.edit()
                                            .putLong("target_date", targetMillis)
                                            .apply();

                                    startCountdown(targetMillis);

                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        ).show();

                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startCountdown(long targetMillis) {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long diff = targetMillis - System.currentTimeMillis();

        if (diff <= 0) {
            finishCountdown();
            return;
        }

        countDownTimer = new CountDownTimer(diff, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long totalSeconds = millisUntilFinished / 1000;

                long days = totalSeconds / 86400;
                long hours = (totalSeconds % 86400) / 3600;
                long minutes = (totalSeconds % 3600) / 60;
                long seconds = totalSeconds % 60;

                tvDay.setText(days + " Gün");
                tvHour.setText(hours + " Saat");
                tvMinute.setText(minutes + " Dəqiqə");
                tvSecond.setText(seconds + " Saniyə");
            }

            @Override
            public void onFinish() {
                finishCountdown();
            }
        };

        countDownTimer.start();
    }

    private void finishCountdown() {

        tvDay.setText("0 Gün");
        tvHour.setText("0 Saat");
        tvMinute.setText("0 Dəqiqə");
        tvSecond.setText("Bitdi!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}