package com.example.appdraw;

import com.example.appdraw.profile.ProfileActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.appdraw.community.CommunityFragment;
import com.example.appdraw.drawing.DrawingActivity;
import com.example.appdraw.explore.ExploreFragment;
import com.example.appdraw.main.HomeFragment;
import com.example.appdraw.utils.FloatingChatbotManager;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp MainActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file MainActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Biến `navProfile` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View navHome, navExplore, navCommunity, navProfile;
    /**
     * Biến `ivProfile` lưu dữ liệu/trạng thái quan trọng kiểu ImageView ivHome, ivExplore, ivCommunity,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ImageView ivHome, ivExplore, ivCommunity, ivProfile;
    /**
     * Biến `tvProfile` lưu dữ liệu/trạng thái quan trọng kiểu TextView tvHome, tvExplore, tvCommunity,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvHome, tvExplore, tvCommunity, tvProfile;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupNavigation();

        // Default fragment
        loadFragment(new HomeFragment());
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        checkNavigationIntent(getIntent());
    }

    @Override
    /**
     * Hàm onNewIntent() thực hiện một phần xử lý trong luồng chức năng của lớp MainActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param intent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
    protected void onNewIntent(Intent intent) {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        super.onNewIntent(intent);
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        setIntent(intent);
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        checkNavigationIntent(intent);
    }

    /**
     * Hàm checkNavigationIntent() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     * @param intent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
    private void checkNavigationIntent(Intent intent) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (intent != null && intent.getBooleanExtra("NAVIGATE_TO_COMMUNITY", false)) {
            navCommunity.performClick();
        }
    }

    /**
     * Hàm initViews() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void initViews() {
        navHome = findViewById(R.id.nav_home);
        navExplore = findViewById(R.id.nav_explore);
        navCommunity = findViewById(R.id.nav_community);
        navProfile = findViewById(R.id.nav_profile);

        ivHome = findViewById(R.id.iv_home);
        ivExplore = findViewById(R.id.iv_explore);
        ivCommunity = findViewById(R.id.iv_community);
        ivProfile = findViewById(R.id.iv_profile);

        tvHome = findViewById(R.id.tv_home);
        tvExplore = findViewById(R.id.tv_explore);
        tvCommunity = findViewById(R.id.tv_community);
        tvProfile = findViewById(R.id.tv_profile);

        findViewById(R.id.fab_draw).setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(MainActivity.this, com.example.appdraw.community.CreatePostActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Hàm setupNavigation() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavUI(0);
            FloatingChatbotManager.getInstance().setVisibility(true);
        });

        navExplore.setOnClickListener(v -> {
            loadFragment(new ExploreFragment());
            updateNavUI(1);
            FloatingChatbotManager.getInstance().setVisibility(false);
        });

        navCommunity.setOnClickListener(v -> {
            loadFragment(new CommunityFragment());
            updateNavUI(2);
            FloatingChatbotManager.getInstance().setVisibility(false);
        });

        navProfile.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Hàm loadFragment() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param fragment tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Hàm updateNavUI() thực hiện một phần xử lý trong luồng chức năng của lớp MainActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param selectedIndex tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void updateNavUI(int selectedIndex) {
        int activeColor = ContextCompat.getColor(this, R.color.primary_blue);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_gray);

        ivHome.setColorFilter(selectedIndex == 0 ? activeColor : inactiveColor);
        tvHome.setTextColor(selectedIndex == 0 ? activeColor : inactiveColor);

        ivExplore.setColorFilter(selectedIndex == 1 ? activeColor : inactiveColor);
        tvExplore.setTextColor(selectedIndex == 1 ? activeColor : inactiveColor);

        ivCommunity.setColorFilter(selectedIndex == 2 ? activeColor : inactiveColor);
        tvCommunity.setTextColor(selectedIndex == 2 ? activeColor : inactiveColor);

        // Profile is an activity, so we don't necessarily "select" it in the same way here 
        // if it stays on top of MainActivity, but we can reset the colors anyway.
        ivProfile.setColorFilter(inactiveColor);
        tvProfile.setTextColor(inactiveColor);
    }
}
