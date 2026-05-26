package com.example.appdraw.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appdraw.R;
import com.example.appdraw.explore.ChatActivity;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp FloatingChatbotManager thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file FloatingChatbotManager.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class FloatingChatbotManager implements Application.ActivityLifecycleCallbacks {

    /**
     * Biến `instance` lưu dữ liệu/trạng thái quan trọng kiểu FloatingChatbotManager, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private static FloatingChatbotManager instance;
    /**
     * Biến `floatingView` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View floatingView;
    /**
     * Biến `currentX` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int currentX = -1;
    /**
     * Biến `currentY` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int currentY = -1;

    private FloatingChatbotManager() {}

    /**
     * Hàm getInstance() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    public static FloatingChatbotManager getInstance() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (instance == null) {
            instance = new FloatingChatbotManager();
        }
        return instance;
    }

    /**
     * Hàm init() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param application tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    // Exclude specific screens from displaying the floating widget
    /**
     * Hàm isExcluded() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private boolean isExcluded(Activity activity) {
        String name = activity.getClass().getSimpleName();
        return name.equals("SplashActivity") ||
               name.equals("MainActivity") ||
               name.equals("ChatActivity") || 
               name.equals("LoginOptionsActivity") || 
               name.equals("LoginActivity") ||
               name.equals("RegisterActivity") ||
               name.equals("RegisterProfileActivity") ||
               name.equals("RegisterInterestsActivity") ||
               name.equals("RegisterLevelActivity") ||
               name.equals("LiveListActivity") ||
               name.equals("LiveActivity") ||
               name.equals("DrawingActivity") ||
               name.equals("ProfileActivity") ||
               name.equals("OtherUserProfileActivity") ||
               name.equals("FullScreenImageActivity") ||
               name.equals("EditProfileActivity") ||
               name.equals("ProjectListActivity") ||
               name.equals("ProjectDetailActivity") ||
               name.equals("CreateProjectActivity") ||
               name.equals("NotificationsActivity") ||
               name.equals("PostDetailActivity") ||
               name.equals("CreatePostActivity") ||
               name.equals("DoingProjectDetailActivity") ||
               name.equals("SearchActivity") ||
               name.equals("LessonListActivity") ||
               name.equals("AllCategoriesActivity") ||
               name.equals("TrendingDetailActivity") ||
               name.equals("ArtistDetailActivity") ||
               name.equals("CreateChallengeActivity") ||
               name.equals("ChallengeSubmissionsActivity") ||
               name.equals("SubmissionDetailActivity") ||
               name.equals("GradeSubmissionActivity") ||
               name.equals("CreateEventActivity") ||
               name.equals("EventScheduleActivity") ||
               name.equals("EventTicketActivity") ||
               name.equals("ChallengeActivity") ||
               name.equals("ChallengeDetailActivity") ||
               name.equals("SubmitChallengeActivity") ||
               name.equals("UserScoreDetailActivity") ||
               name.equals("ChallengeGradingActivity") ||
               name.equals("ChallengeEntryListActivity") ||
               name.equals("EventDetailActivity");
    }

    /**
     * Hàm setVisibility() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param visible tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setVisibility(boolean visible) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (floatingView != null) {
            floatingView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Hàm attachToActivity() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void attachToActivity(Activity activity) {
        if (isExcluded(activity)) return;

        ViewGroup root = activity.findViewById(android.R.id.content);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (root == null) return;

        // Ensure previously added view is removed just in case (e.g. Activity recreation)
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (floatingView != null && floatingView.getParent() != null) {
             ((ViewGroup) floatingView.getParent()).removeView(floatingView);
        }

        // Inflate a fresh view bound to the current Activity's context
        floatingView = LayoutInflater.from(activity).inflate(R.layout.layout_floating_chatbot, root, false);
        
        setupTouchListener(floatingView, activity);

        root.post(() -> {
            if (currentX == -1 && currentY == -1) {
                // Initialize to bottom-right corner
                int screenWidth = root.getWidth();
                int screenHeight = root.getHeight();
                
                // Convert dp to px for margins
                int marginDpX = 16;
                int marginDpY = 120; // Enough to sit above bottom nav menus
                int marginPxX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDpX, activity.getResources().getDisplayMetrics());
                int marginPxY = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDpY, activity.getResources().getDisplayMetrics());

                currentX = screenWidth - floatingView.getWidth() - marginPxX;
                currentY = screenHeight - floatingView.getHeight() - marginPxY;
            }
            updatePosition();
        });

        root.addView(floatingView);
        updatePosition(); // Apply any known position right away
    }

    /**
     * Hàm detachFromActivity() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void detachFromActivity(Activity activity) {
        if (isExcluded(activity)) return;
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (floatingView != null && floatingView.getParent() != null) {
            ((ViewGroup) floatingView.getParent()).removeView(floatingView);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        floatingView = null; // Clear reference to avoid context leak
    }

    /**
     * Hàm updatePosition() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updatePosition() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (floatingView != null && currentX != -1 && currentY != -1) {
            floatingView.setX(currentX);
            floatingView.setY(currentY);
        }
    }

    /**
     * Hàm setupTouchListener() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupTouchListener(View view, Activity activity) {
        view.setOnTouchListener(new View.OnTouchListener() {
            /**
             * Biến `initialTouchY` lưu dữ liệu/trạng thái quan trọng kiểu float initialX, initialY, initialTouchX,, được sử dụng trong các bước xử lý và hiển thị của lớp.
             */
            private float initialX, initialY, initialTouchX, initialTouchY;
            /**
             * Biến `touchStartTime` lưu dữ liệu/trạng thái quan trọng kiểu long, được sử dụng trong các bước xử lý và hiển thị của lớp.
             */
            private long touchStartTime = 0;

            @Override
            /**
             * Hàm onTouch() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param v tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = view.getX();
                        initialY = view.getY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        touchStartTime = System.currentTimeMillis();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - initialTouchX;
                        float dy = event.getRawY() - initialTouchY;
                        currentX = (int) (initialX + dx);
                        currentY = (int) (initialY + dy);
                        
                        // Keep within screen bounds
                        ViewGroup root = activity.findViewById(android.R.id.content);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (root != null) {
                            int maxW = root.getWidth() - view.getWidth();
                            int maxH = root.getHeight() - view.getHeight();
                            
                            if (currentX < 0) currentX = 0;
                            if (currentX > maxW) currentX = maxW;
                            if (currentY < 0) currentY = 0;
                            if (currentY > maxH) currentY = maxH;
                        }
                        
                        updatePosition();
                        return true;

                    case MotionEvent.ACTION_UP:
                        long touchDuration = System.currentTimeMillis() - touchStartTime;
                        float moveDx = Math.abs(event.getRawX() - initialTouchX);
                        float moveDy = Math.abs(event.getRawY() - initialTouchY);
                        
                        // If tap duration is short and it didn't move much, treat as a click
                        if (touchDuration < 200 && moveDx < 10 && moveDy < 10) {
                            openChatActivity(activity);
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            // Avoid registering the click multiple times in rapid succession
                            touchStartTime = 0;
                        } else {
                            // Snap to edges horizontally if desired (optional polish)
                            /*
                            ViewGroup root = activity.findViewById(android.R.id.content);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (root != null) {
                                int midScreen = root.getWidth() / 2;
                                if (currentX + view.getWidth()/2 < midScreen) {
                                    currentX = 0; // snap to left
                                } else {
                                    currentX = root.getWidth() - view.getWidth(); // snap to right
                                }
                                updatePosition();
                            }
                            */
                        }
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Hàm openChatActivity() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void openChatActivity(Activity activity) {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        Intent intent = new Intent(activity, ChatActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Optional depending on stack back requirements
        activity.startActivity(intent);
    }

    // --- Lifecycle Callbacks --- //

    @Override
    /**
     * Hàm onActivityResumed() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityResumed(@NonNull Activity activity) {
        attachToActivity(activity);
    }

    @Override
    /**
     * Hàm onActivityPaused() thực hiện một phần xử lý trong luồng chức năng của lớp FloatingChatbotManager.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param activity tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityPaused(@NonNull Activity activity) {
        detachFromActivity(activity);
    }

    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onActivityDestroyed(@NonNull Activity activity) {}
}
