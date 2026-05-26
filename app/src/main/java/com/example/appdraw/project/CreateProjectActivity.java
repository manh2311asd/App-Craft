package com.example.appdraw.project;

import com.example.appdraw.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appdraw.model.Project;
import com.example.appdraw.project.ProjectDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Màn hình Tạo Dự án mới (UC-12).
 * Người thực hiện: Vũ Quang Vinh.
 * Quản lý khởi tạo dự án nghệ thuật dài hạn và cập nhật tiến độ lưu trên Firestore.
 */
/**
 * Lớp CreateProjectActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file CreateProjectActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class CreateProjectActivity extends AppCompatActivity {

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        Toolbar toolbar = findViewById(R.id.toolbar_create_project);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        EditText etName = findViewById(R.id.et_project_name);
        EditText etGoal = findViewById(R.id.et_project_goal);
        EditText etDescription = findViewById(R.id.et_project_description);
        MaterialButton btnStart = findViewById(R.id.btn_start_now);
        
        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String goal = etGoal.getText().toString().trim();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            String description = etDescription != null ? etDescription.getText().toString().trim() : "";
            
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Vui lòng nhập tên dự án!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            String uid = FirebaseAuth.getInstance().getUid();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (uid == null) {
                Toast.makeText(this, "Yêu cầu đăng nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnStart.setEnabled(false);
            btnStart.setText("Đang tạo Dự Án...");

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            String docId = db.collection("Projects").document().getId();

            Project newProj = new Project(docId, uid, name, goal, description, "", 0, System.currentTimeMillis());

            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Projects").document(docId).set(newProj)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Tạo Dự án thành công!", Toast.LENGTH_SHORT).show();
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(this, ProjectDetailActivity.class);
                        intent.putExtra("PROJECT_ID", docId);
                        intent.putExtra("PROJECT_NAME", name);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnStart.setEnabled(true);
                        btnStart.setText("Bắt đầu ngay");
                        Toast.makeText(this, "Lỗi tạo dự án: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}

