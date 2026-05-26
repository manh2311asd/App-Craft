package com.example.appdraw.event;

import com.example.appdraw.R;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Màn hình Quản lý Vé Sự kiện của người dùng (UC-17).
 * Người thực hiện: Đặng Thị Hồng Vân.
 * Hiển thị mã QR và thông tin vé mà người dùng đã đăng ký thành công.
 */
/**
 * Lớp TicketActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file TicketActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class TicketActivity extends AppCompatActivity {

    /**
     * Biến `btnRemindMe` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private MaterialButton btnRemindMe;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        btnRemindMe = findViewById(R.id.btn_remind_me);

        findViewById(R.id.toolbar_ticket).setOnClickListener(v -> onBackPressed());

        btnRemindMe.setOnClickListener(v -> showNoticeSuccessDialog());
    }

    /**
     * Hàm showNoticeSuccessDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showNoticeSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notice_success);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnOk = dialog.findViewById(R.id.btn_dialog_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            setRemindMeActive();
        });

        dialog.show();
    }

    /**
     * Hàm setRemindMeActive() thực hiện một phần xử lý trong luồng chức năng của lớp TicketActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void setRemindMeActive() {
        // Làm sáng nút Nhắc tôi
        btnRemindMe.setIconTintResource(R.color.primary_blue);
        btnRemindMe.setTextColor(getResources().getColor(R.color.primary_blue));
        btnRemindMe.setStrokeColorResource(R.color.primary_blue);
        btnRemindMe.setText("Đã đặt nhắc");
    }
}

