package com.example.appdraw.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdraw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp RegisterInterestsActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file RegisterInterestsActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class RegisterInterestsActivity extends AppCompatActivity {
    
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private final java.util.List<String> selectedInterests = new java.util.ArrayList<>();
    
    // Arrays for IDs
    /**
     * Biến `blockIds` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private final int[] blockIds = {
        R.id.ll_interest_1, R.id.ll_interest_2, R.id.ll_interest_3, R.id.ll_interest_4,
        R.id.ll_interest_5, R.id.ll_interest_6, R.id.ll_interest_7, R.id.ll_interest_8
    };
    /**
     * Biến `lineIds` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private final int[] lineIds = {
        R.id.view_line_1, R.id.view_line_2, R.id.view_line_3, R.id.view_line_4,
        R.id.view_line_5, R.id.view_line_6, R.id.view_line_7, R.id.view_line_8
    };
    /**
     * Biến `interests` lưu dữ liệu/trạng thái quan trọng kiểu String[], được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private final String[] interests = {
        "Vẽ chì màu", "Vẽ màu nước", "Vẽ màu sáp", "Vẽ phong cảnh", 
        "Vẽ chân dung", "Tranh sơn dầu", "Kí họa", "Đồ thủ công"
    };

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_interests);

        // Setup selection click listeners
        for (int i = 0; i < blockIds.length; i++) {
            final int index = i;
            findViewById(blockIds[i]).setOnClickListener(v -> {
                selectInterest(index);
            });
        }

        findViewById(R.id.btn_interest_next).setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (selectedInterests.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 sở thích", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Save to SharedPreferences temporarily (role chưa xác định)
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (user != null) {
                String joinedInterests = android.text.TextUtils.join(", ", selectedInterests);
                getSharedPreferences("RegisterTemp", MODE_PRIVATE).edit()
                    .putString("interests", joinedInterests)
                    .apply();
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, RegisterLevelActivity.class));
            } else {
                Toast.makeText(this, "Bạn chưa đăng nhập or phiên kết thúc", Toast.LENGTH_SHORT).show();
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }
    
    /**
     * Hàm selectInterest() thực hiện một phần xử lý trong luồng chức năng của lớp RegisterInterestsActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param selectedIndex tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void selectInterest(int selectedIndex) {
        String item = interests[selectedIndex];
        View line = findViewById(lineIds[selectedIndex]);
        
        if (selectedInterests.contains(item)) {
            selectedInterests.remove(item);
            line.setVisibility(View.INVISIBLE);
        } else {
            selectedInterests.add(item);
            line.setVisibility(View.VISIBLE);
        }
    }
}
