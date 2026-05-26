package com.example.appdraw.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdraw.MainActivity;
import com.example.appdraw.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp RegisterLevelActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file RegisterLevelActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class RegisterLevelActivity extends AppCompatActivity {

    /**
     * Biến `selectedRole` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String selectedRole = null;
    /**
     * Biến `cardAdvanced` lưu dữ liệu/trạng thái quan trọng kiểu MaterialCardView cardBasic,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private MaterialCardView cardBasic, cardAdvanced;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_level);

        cardBasic = findViewById(R.id.card_level_basic);
        cardAdvanced = findViewById(R.id.card_level_advanced);

        cardBasic.setOnClickListener(v -> selectLevel("user"));
        cardAdvanced.setOnClickListener(v -> selectLevel("mentor"));

        findViewById(R.id.btn_level_start).setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (selectedRole == null) {
                Toast.makeText(this, "Vui lòng chọn trình độ của bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (user != null) {
                findViewById(R.id.btn_level_start).setEnabled(false);
                saveUserWithRole(user.getUid(), selectedRole);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    /**
     * Lưu đầy đủ dữ liệu user theo cấu trúc Table Inheritance:
     *   - Users/{uid}    : thông tin chung (username, email, role, avatar_url, bio, global_recent_colors) + interests (nếu có)
     *   - Mentors/{uid}  : thông tin riêng mentor (specialization, verificationStatus, studentsCount, rating)
     */
    /**
     * Hàm saveUserWithRole() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param uid tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param role tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void saveUserWithRole(String uid, String role) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("RegisterTemp", MODE_PRIVATE);

        String avatarUrl   = prefs.getString("avatarUrl", "");
        String bio         = prefs.getString("bio", "");
        String interests   = prefs.getString("interests", "");

        // 1. Cập nhật thông tin chung vào collection Users cho TẤT CẢ mọi người
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        Map<String, Object> userUpdate = new HashMap<>();
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userUpdate.put("role", role);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (!avatarUrl.isEmpty()) {
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            userUpdate.put("avatar_url", avatarUrl);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (!bio.isEmpty()) {
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            userUpdate.put("bio", bio);
        }
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userUpdate.put("global_recent_colors", "[]"); // mảng màu gần dùng, mặc định rỗng
        
        // Thêm interests chỉ cho User
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (role.equals("user") && !interests.isEmpty()) {
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            userUpdate.put("interests", interests);
        }

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(uid)
            .set(userUpdate, SetOptions.merge())
            .addOnSuccessListener(unused -> {

                if (role.equals("mentor")) {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // 2. Nếu là Mentor, tạo thêm Document trong collection Mentors với các trường đặc thù
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    Map<String, Object> mentorData = new HashMap<>();
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    mentorData.put("specialization", "Digital Art"); // Khởi tạo mặc định
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    mentorData.put("verificationStatus", "PENDING"); // Chờ duyệt
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    mentorData.put("portfolioUrl", ""); // Đường dẫn portfolio rỗng ban đầu
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    mentorData.put("experienceYears", 0); // Số năm kinh nghiệm

                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Mentors").document(uid)
                        .set(mentorData, SetOptions.merge())
                        .addOnSuccessListener(unused2 -> finishRegistration(prefs))
                        .addOnFailureListener(e -> {
                            findViewById(R.id.btn_level_start).setEnabled(true);
                            Toast.makeText(this, "Lỗi tạo profile Mentor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                } else {
                    // Nếu là User thì xong
                    finishRegistration(prefs);
                }
            })
            .addOnFailureListener(e -> {
                findViewById(R.id.btn_level_start).setEnabled(true);
                Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    /**
     * Hàm finishRegistration() thực hiện một phần xử lý trong luồng chức năng của lớp RegisterLevelActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param prefs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void finishRegistration(SharedPreferences prefs) {
        // 3. Xoá dữ liệu tạm
        prefs.edit().clear().apply();

        // 4. Vào app
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Hàm selectLevel() thực hiện một phần xử lý trong luồng chức năng của lớp RegisterLevelActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param role tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void selectLevel(String role) {
        selectedRole = role;
        int activeColor  = Color.parseColor("#4272D0");
        int inactiveColor = Color.parseColor("#CCCCCC");

        cardBasic.setStrokeColor(role.equals("user") ? activeColor : inactiveColor);
        cardBasic.setStrokeWidth(role.equals("user") ? 6 : 2);

        cardAdvanced.setStrokeColor(role.equals("mentor") ? activeColor : inactiveColor);
        cardAdvanced.setStrokeWidth(role.equals("mentor") ? 6 : 2);
    }
}
