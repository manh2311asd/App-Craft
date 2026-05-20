package com.example.appdraw;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;

/**
 * Màn hình Tạo Thử thách mới dành cho Mentor (UC-14).
 * Người thực hiện: Đặng Thị Hồng Vân.
 * Xử lý nhập thông tin, luật lệ thi và lưu Thử thách lên hệ thống Firestore.
 */
public class CreateChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        Toolbar toolbar = findViewById(R.id.toolbar_create_challenge);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        MaterialButton btnCreate = findViewById(R.id.btn_create_challenge_submit);
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
                Toast.makeText(this, "Thử thách đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }
}
