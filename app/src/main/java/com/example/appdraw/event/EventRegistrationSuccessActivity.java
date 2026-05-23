package com.example.appdraw.event;

import com.example.appdraw.R;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
public class EventRegistrationSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration_success);

        findViewById(R.id.ll_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_back_to_calendar).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btn_view_ticket).setOnClickListener(v -> {
            Intent intent = new Intent(this, TicketActivity.class);
            startActivity(intent);
        });
    }
}

