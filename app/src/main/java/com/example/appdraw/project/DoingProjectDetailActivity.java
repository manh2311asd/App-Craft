package com.example.appdraw.project;

import com.example.appdraw.R;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp DoingProjectDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file DoingProjectDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class DoingProjectDetailActivity extends AppCompatActivity {

    /**
     * Biến `progressBar` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private LinearProgressIndicator progressBar;
    /**
     * Biến `tvPercent` lưu dữ liệu/trạng thái quan trọng kiểu TextView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvPercent;
    /**
     * Biến `checkBoxes` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private CheckBox[] checkBoxes;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_project_detail);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String projectName = getIntent().getStringExtra("PROJECT_NAME");
        
        TextView tvTitle = findViewById(R.id.tv_doing_title);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectName != null) {
            tvTitle.setText(projectName);
        }

        progressBar = findViewById(R.id.pb_checklist_progress);
        tvPercent = findViewById(R.id.tv_checklist_percent);

        checkBoxes = new CheckBox[]{
                findViewById(R.id.cb_step1),
                findViewById(R.id.cb_step2),
                findViewById(R.id.cb_step3),
                findViewById(R.id.cb_step4),
                findViewById(R.id.cb_step5)
        };

        for (CheckBox cb : checkBoxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> updateProgress());
        }

        findViewById(R.id.btn_back_doing).setOnClickListener(v -> onBackPressed());
        
        findViewById(R.id.btn_continue_drawing).setOnClickListener(v -> {
            // Logic to continue drawing
            onBackPressed();
        });

        updateProgress(); // Initial calculation
    }

    /**
     * Hàm updateProgress() thực hiện một phần xử lý trong luồng chức năng của lớp DoingProjectDetailActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateProgress() {
        int checkedCount = 0;
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) checkedCount++;
        }
        int percent = (checkedCount * 100) / checkBoxes.length;
        progressBar.setProgress(percent);
        tvPercent.setText(percent + "%");
    }
}

