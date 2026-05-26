package com.example.appdraw.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appdraw.R;
import com.google.android.material.tabs.TabLayout;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp ChallengeGradingActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChallengeGradingActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChallengeGradingActivity extends AppCompatActivity {

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_grading);

        Toolbar toolbar = findViewById(R.id.toolbar_grading);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ImageView btnAdd = findViewById(R.id.btn_add_challenge_grading);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(this, CreateChallengeActivity.class);
                startActivity(intent);
            });
        }

        setupGradingItems();

        TabLayout tabLayout = findViewById(R.id.tab_layout_grading);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabLayout != null) {
            TabLayout.Tab tab = tabLayout.getTabAt(3);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tab != null) tab.select();
            
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                /**
                 * Hàm onTabSelected() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeGradingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param tab tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() != 3) {
                        finish();
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}
                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
    }

    /**
     * Hàm setupGradingItems() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupGradingItems() {
        // Item 1: Vẽ tranh ngày Trái Đất
        View card1 = findViewById(R.id.card_grading_earth_day);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (card1 != null) {
            card1.setOnClickListener(v -> openEntryList("Vẽ tranh ngày Trái Đất"));
        }
        View btn1 = findViewById(R.id.btn_grade_earth_day);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btn1 != null) {
            btn1.setOnClickListener(v -> openEntryList("Vẽ tranh ngày Trái Đất"));
        }

        // Item 2: Vẽ vật thể cái cốc
        View card2 = findViewById(R.id.card_grading_cup);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (card2 != null) {
            card2.setOnClickListener(v -> openEntryList("Vẽ vật thể: cái cốc"));
        }
        View btn2 = findViewById(R.id.btn_grade_cup);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btn2 != null) {
            btn2.setOnClickListener(v -> openEntryList("Vẽ vật thể: cái cốc"));
        }
    }

    /**
     * Hàm openEntryList() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeGradingActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void openEntryList(String title) {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        Intent intent = new Intent(this, ChallengeEntryListActivity.class);
        intent.putExtra("CHALLENGE_TITLE", title);
        startActivity(intent);
    }
}
