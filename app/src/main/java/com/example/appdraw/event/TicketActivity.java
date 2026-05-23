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
public class TicketActivity extends AppCompatActivity {

    private MaterialButton btnRemindMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        btnRemindMe = findViewById(R.id.btn_remind_me);

        findViewById(R.id.toolbar_ticket).setOnClickListener(v -> onBackPressed());

        btnRemindMe.setOnClickListener(v -> showNoticeSuccessDialog());
    }

    private void showNoticeSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_notice_success);
        
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

    private void setRemindMeActive() {
        // Làm sáng nút Nhắc tôi
        btnRemindMe.setIconTintResource(R.color.primary_blue);
        btnRemindMe.setTextColor(getResources().getColor(R.color.primary_blue));
        btnRemindMe.setStrokeColorResource(R.color.primary_blue);
        btnRemindMe.setText("Đã đặt nhắc");
    }
}

