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
public class RegisterLevelActivity extends AppCompatActivity {

    private String selectedRole = null;
    private MaterialCardView cardBasic, cardAdvanced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_level);

        cardBasic = findViewById(R.id.card_level_basic);
        cardAdvanced = findViewById(R.id.card_level_advanced);

        cardBasic.setOnClickListener(v -> selectLevel("user"));
        cardAdvanced.setOnClickListener(v -> selectLevel("mentor"));

        findViewById(R.id.btn_level_start).setOnClickListener(v -> {
            if (selectedRole == null) {
                Toast.makeText(this, "Vui lòng chọn trình độ của bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                findViewById(R.id.btn_level_start).setEnabled(false);
                saveUserWithRole(user.getUid(), selectedRole);
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    /**
     * Lưu đầy đủ dữ liệu user theo cấu trúc Table Inheritance:
     *   - Users/{uid}    : thông tin chung (username, email, role, avatar_url, bio, global_recent_colors) + interests (nếu có)
     *   - Mentors/{uid}  : thông tin riêng mentor (specialization, verificationStatus, studentsCount, rating)
     */
    private void saveUserWithRole(String uid, String role) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("RegisterTemp", MODE_PRIVATE);

        String avatarUrl   = prefs.getString("avatarUrl", "");
        String bio         = prefs.getString("bio", "");
        String interests   = prefs.getString("interests", "");

        // 1. Cập nhật thông tin chung vào collection Users cho TẤT CẢ mọi người
        Map<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("role", role);
        if (!avatarUrl.isEmpty()) {
            userUpdate.put("avatar_url", avatarUrl);
        }
        if (!bio.isEmpty()) {
            userUpdate.put("bio", bio);
        }
        userUpdate.put("global_recent_colors", "[]"); // mảng màu gần dùng, mặc định rỗng
        
        // Thêm interests chỉ cho User
        if (role.equals("user") && !interests.isEmpty()) {
            userUpdate.put("interests", interests);
        }

        db.collection("Users").document(uid)
            .set(userUpdate, SetOptions.merge())
            .addOnSuccessListener(unused -> {

                if (role.equals("mentor")) {
                    // 2. Nếu là Mentor, tạo thêm Document trong collection Mentors với các trường đặc thù
                    Map<String, Object> mentorData = new HashMap<>();
                    mentorData.put("specialization", "Digital Art"); // Khởi tạo mặc định
                    mentorData.put("verificationStatus", "PENDING"); // Chờ duyệt
                    mentorData.put("portfolioUrl", ""); // Đường dẫn portfolio rỗng ban đầu
                    mentorData.put("experienceYears", 0); // Số năm kinh nghiệm

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

    private void finishRegistration(SharedPreferences prefs) {
        // 3. Xoá dữ liệu tạm
        prefs.edit().clear().apply();

        // 4. Vào app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

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
