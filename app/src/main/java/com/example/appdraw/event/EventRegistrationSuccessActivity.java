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
/**
 * Lớp EventRegistrationSuccessActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventRegistrationSuccessActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class EventRegistrationSuccessActivity extends AppCompatActivity {

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration_success);

        findViewById(R.id.ll_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_back_to_calendar).setOnClickListener(v -> {
            finish();
        });

        findViewById(R.id.btn_view_ticket).setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(this, TicketActivity.class);
            startActivity(intent);
        });
    }
}

