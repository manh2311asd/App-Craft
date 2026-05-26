package com.example.appdraw.profile;

import com.example.appdraw.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appdraw.project.ProjectListActivity;

/**
 * Màn hình Quản lý Thông tin cá nhân (UC-02).
 * Người thực hiện: Vũ Quang Vinh.
 * Xử lý hiển thị thông tin Profile, tác phẩm, dự án và tương tác Follow/Unfollow.
 */
/**
 * Lớp ProfileActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ProfileActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ProfileActivity extends AppCompatActivity {

    /**
     * Biến `tabSaved` lưu dữ liệu/trạng thái quan trọng kiểu TextView tabArtwork, tabPost, tabProject,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tabArtwork, tabPost, tabProject, tabSaved;
    /**
     * Biến `rvProfileArtworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private androidx.recyclerview.widget.RecyclerView rvProfileArtworks;
    /**
     * Biến `llEmptyArtworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private LinearLayout llEmptyArtworks;
    /**
     * Biến `postAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private com.example.appdraw.community.PostMediaAdapter postAdapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private java.util.List<com.example.appdraw.model.Post> postList = new java.util.ArrayList<>();
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private java.util.List<com.example.appdraw.model.Post> allPostList = new java.util.ArrayList<>();
    /**
     * Biến `llProfilePosts` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private LinearLayout llProfilePosts;
    /**
     * Biến `activeTab` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int activeTab = 1;

    /**
     * Hàm showAvatarFullscreen() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showAvatarFullscreen() {
        android.widget.ImageView ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivProfileAvatar == null || ivProfileAvatar.getDrawable() == null) return;
        
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        android.widget.ImageView imageView = new android.widget.ImageView(this);
        imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(ivProfileAvatar.getDrawable());
        
        imageView.setOnClickListener(v -> dialog.dismiss());
        
        dialog.setContentView(imageView);
        dialog.show();
    }

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();
        setupTabs();

        // Kiểm tra xem có yêu cầu mở tab Dự án không
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        if (getIntent().getBooleanExtra("OPEN_PROJECT_TAB", false)) {
            openProjectTab();
        }

        // Check if viewing other user profile
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        if (getIntent().getBooleanExtra("IS_OTHER_USER", false)) {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            String otherName = getIntent().getStringExtra("USER_NAME");
            TextView tvName = findViewById(R.id.tv_profile_name);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvName != null && otherName != null) {
                tvName.setText(otherName);
            }
        } else {
            loadCurrentUserProfile();
            
            // Allow viewing avatar in fullscreen
            View ivAvatar = findViewById(R.id.iv_profile_avatar);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivAvatar != null) {
                ivAvatar.setOnClickListener(v -> showAvatarFullscreen());
            }
        }


    }

    /**
     * Hàm initViews() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void initViews() {
        tabArtwork = findViewById(R.id.tab_artwork);
        tabPost = findViewById(R.id.tab_post);
        tabProject = findViewById(R.id.tab_project);
        tabSaved = findViewById(R.id.tab_saved);

        rvProfileArtworks = findViewById(R.id.rv_profile_artworks);
        llEmptyArtworks = findViewById(R.id.ll_empty_artworks);

        llProfilePosts = findViewById(R.id.ll_profile_posts);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvProfileArtworks != null) {
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProfileArtworks.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
            postAdapter = new com.example.appdraw.community.PostMediaAdapter(postList, post -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(this, com.example.appdraw.community.FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", post.getImageUrl());
                startActivity(intent);
            });
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProfileArtworks.setAdapter(postAdapter);
        }
    }

    /**
     * Hàm setupToolbar() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        android.widget.ImageView btnSettings = findViewById(R.id.btn_profile_settings);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> showSettingsDialog());
        }


    }

    /**
     * Hàm setupTabs() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupTabs() {

        View btnStartDrawing = findViewById(R.id.btn_start_drawing);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnStartDrawing != null) {
            btnStartDrawing.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(ProfileActivity.this, com.example.appdraw.drawing.DrawingActivity.class);
                startActivity(intent);
            });
        }

        tabArtwork.setOnClickListener(v -> {
            activeTab = 0;
            resetTabs();
            tabArtwork.setTextColor(getResources().getColor(R.color.primary_blue));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabArtwork.setTypeface(null, android.graphics.Typeface.BOLD);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llProfilePosts != null) llProfilePosts.setVisibility(View.GONE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (postList.isEmpty()) {
                llEmptyArtworks.setVisibility(View.VISIBLE);
                rvProfileArtworks.setVisibility(View.GONE);
                if (llEmptyArtworks.getChildCount() > 2 && llEmptyArtworks.getChildAt(1) instanceof TextView) {
                    ((TextView)llEmptyArtworks.getChildAt(1)).setText("Chưa có tác phẩm nào");
                }
            } else {
                llEmptyArtworks.setVisibility(View.GONE);
                rvProfileArtworks.setVisibility(View.VISIBLE);
            }
        });

        tabPost.setOnClickListener(v -> {
            activeTab = 1;
            resetTabs();
            tabPost.setTextColor(getResources().getColor(R.color.primary_blue));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabPost.setTypeface(null, android.graphics.Typeface.BOLD);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(View.GONE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (allPostList.isEmpty()) {
                llEmptyArtworks.setVisibility(View.VISIBLE);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llProfilePosts != null) llProfilePosts.setVisibility(View.GONE);
                if (llEmptyArtworks.getChildCount() > 2 && llEmptyArtworks.getChildAt(1) instanceof TextView) {
                    ((TextView)llEmptyArtworks.getChildAt(1)).setText("Chưa có bài viết nào");
                }
            } else {
                llEmptyArtworks.setVisibility(View.GONE);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llProfilePosts != null) llProfilePosts.setVisibility(View.VISIBLE);
            }
        });

        tabProject.setOnClickListener(v -> {
            openProjectTab();
            // Chuyển sang trang danh sách dự án
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(ProfileActivity.this, ProjectListActivity.class);
            startActivity(intent);
        });

        tabSaved.setOnClickListener(v -> {
            resetTabs();
            tabSaved.setTextColor(getResources().getColor(R.color.primary_blue));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabSaved.setTypeface(null, android.graphics.Typeface.BOLD);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llEmptyArtworks != null) llEmptyArtworks.setVisibility(View.GONE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(View.GONE);
            Toast.makeText(this, "Mục Đã lưu", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Hàm openProjectTab() thực hiện một phần xử lý trong luồng chức năng của lớp ProfileActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void openProjectTab() {
        resetTabs();
        tabProject.setTextColor(getResources().getColor(R.color.primary_blue));
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        tabProject.setTypeface(null, android.graphics.Typeface.BOLD);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llEmptyArtworks != null) llEmptyArtworks.setVisibility(View.GONE);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(View.GONE);
    }

    /**
     * Hàm resetTabs() thực hiện một phần xử lý trong luồng chức năng của lớp ProfileActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void resetTabs() {
        int gray = Color.parseColor("#888888");
        tabArtwork.setTextColor(gray);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        tabArtwork.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabPost.setTextColor(gray);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        tabPost.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabProject.setTextColor(gray);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        tabProject.setTypeface(null, android.graphics.Typeface.NORMAL);
        tabSaved.setTextColor(gray);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        tabSaved.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    /**
     * Hàm showSettingsDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showSettingsDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        android.view.View view = android.view.LayoutInflater.from(this).inflate(R.layout.dialog_profile_settings, null);
        dialog.setContentView(view);

        
        view.findViewById(R.id.ll_setting_edit_profile).setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            dialog.dismiss();
        });
        
        view.findViewById(R.id.ll_setting_personal).setOnClickListener(v -> {
            Toast.makeText(this, "Liên kết cá nhân", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        view.findViewById(R.id.ll_logout).setOnClickListener(v -> {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(ProfileActivity.this, com.example.appdraw.auth.LoginOptionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dialog.dismiss();
        });
        
        dialog.show();
    }

    /**
     * Hàm loadCurrentUserProfile() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadCurrentUserProfile() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener(this, (documentSnapshot, e) -> {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (e != null || documentSnapshot == null) return;
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (documentSnapshot.exists()) {
                        // Tải số liệu thực tế Followers, Following, Post
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        Long posts = documentSnapshot.getLong("postCount");
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String role = documentSnapshot.getString("role");
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        long postsVal = posts != null ? Math.max(0, posts) : 0;
                        
                        android.widget.ImageView ivMentorBadge = findViewById(R.id.iv_mentor_badge);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivMentorBadge != null) {
                            if ("mentor".equals(role)) {
                                ivMentorBadge.setVisibility(View.VISIBLE);
                            } else {
                                ivMentorBadge.setVisibility(View.GONE);
                            }
                        }
                        
                        TextView tvFollowers = findViewById(R.id.tv_profile_followers);
                        TextView tvFollowing = findViewById(R.id.tv_profile_following);
                        TextView tvPosts = findViewById(R.id.tv_profile_posts);
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvPosts != null) tvPosts.setText(String.valueOf(postsVal));
                        
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Fetch followers logically by counting 'Follows' sub-documents natively
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("Follows")
                            .whereEqualTo("following", user.getUid())
                            .count()
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            .get(com.google.firebase.firestore.AggregateSource.SERVER)
                            .addOnSuccessListener(task -> {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvFollowers != null) tvFollowers.setText(String.valueOf(task.getCount()));
                            });

                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("Follows")
                            .whereEqualTo("follower", user.getUid())
                            .count()
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            .get(com.google.firebase.firestore.AggregateSource.SERVER)
                            .addOnSuccessListener(task -> {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvFollowing != null) tvFollowing.setText(String.valueOf(task.getCount()));
                            });

                        // Tải Avatar
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String photoUrl = documentSnapshot.getString("photoUrl");
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (photoUrl == null || photoUrl.isEmpty()) photoUrl = documentSnapshot.getString("avatar");
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (photoUrl == null || photoUrl.isEmpty()) photoUrl = documentSnapshot.getString("avatar_url");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (photoUrl == null || photoUrl.isEmpty()) {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (user.getPhotoUrl() != null) photoUrl = user.getPhotoUrl().toString();
                        }
                        
                        android.widget.ImageView ivAvatar = findViewById(R.id.iv_profile_avatar);
                        
                        // Default flag indicating if an avatar was found
                        boolean hasAvatar = false;
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivAvatar != null) {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                hasAvatar = true;
                                if (photoUrl.startsWith("data:image")) {
                                    byte[] decodedBytes = android.util.Base64.decode(photoUrl.split(",")[1], android.util.Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(decodedBytes).circleCrop().into(ivAvatar);
                                } else {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(photoUrl).circleCrop().into(ivAvatar);
                                }
                            }
                        }

                        // Tải tags (Kinh nghiệm, Sở thích)
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String interest = documentSnapshot.getString("interest");
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String level = documentSnapshot.getString("level");

                        TextView tvTag1 = findViewById(R.id.tv_tag_1);
                        TextView tvTag2 = findViewById(R.id.tv_tag_2);
                        TextView tvTag3 = findViewById(R.id.tv_tag_3);
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTag1 != null) tvTag1.setVisibility(View.GONE);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTag2 != null) tvTag2.setVisibility(View.GONE);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTag3 != null) tvTag3.setVisibility(View.GONE);

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (interest != null && !interest.isEmpty()) {
                            String[] interests = interest.split(",");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (interests.length > 0 && tvTag1 != null) {
                                tvTag1.setText(interests[0].trim());
                                tvTag1.setVisibility(View.VISIBLE);
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (interests.length > 1 && tvTag2 != null) {
                                tvTag2.setText(interests[1].trim());
                                tvTag2.setVisibility(View.VISIBLE);
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (interests.length > 2 && tvTag3 != null) {
                                tvTag3.setText(interests[2].trim());
                                tvTag3.setVisibility(View.VISIBLE);
                            }
                        }

                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        if (documentSnapshot.contains("profile")) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            java.util.Map<String, Object> profile = (java.util.Map<String, Object>) documentSnapshot.get("profile");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null) {
                                String fullName = (String) profile.get("fullName");
                                String bio = (String) profile.get("bio");
                                String avatarUrl = (String) profile.get("avatarUrl");
                                
                                TextView tvName = findViewById(R.id.tv_profile_name);
                                TextView tvBio = findViewById(R.id.tv_profile_bio);
                                android.widget.ImageView ivProfileAvatar = findViewById(R.id.iv_profile_avatar);

                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvName != null && fullName != null && !fullName.isEmpty()) tvName.setText(fullName);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvBio != null && bio != null && !bio.isEmpty()) tvBio.setText(bio);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (ivProfileAvatar != null && avatarUrl != null && !avatarUrl.isEmpty()) {
                                    hasAvatar = true;
                                    if (avatarUrl.startsWith("data:image")) {
                                        byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                        com.bumptech.glide.Glide.with(this).load(b).circleCrop().into(ivProfileAvatar);
                                    } else {
                                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                        com.bumptech.glide.Glide.with(this).load(avatarUrl).circleCrop().into(ivProfileAvatar);
                                    }
                                }
                            }
                        }

                        // Fallback cho người dùng cũ chưa có 'profile' map
                        TextView tvNameFallback = findViewById(R.id.tv_profile_name);
                        TextView tvBioFallback = findViewById(R.id.tv_profile_bio);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvNameFallback != null && (tvNameFallback.getText() == null || tvNameFallback.getText().toString().isEmpty() || tvNameFallback.getText().toString().equals("Tên người dùng"))) {
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String rootName = documentSnapshot.getString("username");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (rootName != null && !rootName.isEmpty()) tvNameFallback.setText(rootName);
                        }
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvBioFallback != null && (tvBioFallback.getText() == null || tvBioFallback.getText().toString().isEmpty())) {
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String rootBio = documentSnapshot.getString("bio");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (rootBio != null && !rootBio.isEmpty()) tvBioFallback.setText(rootBio);
                        }
                        
                        // Nếu vẫn không có ảnh (photoUrl rỗng và avatarUrl rỗng) => dùng màu trắng làm fallback
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (!hasAvatar && ivAvatar != null) {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                        }
                    } else {
                        // User deleted entirely from DB but auth session lived
                        Toast.makeText(this, "Phiên đăng nhập không hợp lệ hoặc tài khoản đã bị xóa.", Toast.LENGTH_LONG).show();
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(ProfileActivity.this, com.example.appdraw.auth.LoginOptionsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

            // Tải danh sách Posts có hình ảnh giống Twitter Media
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Posts")
                .whereEqualTo("uid", user.getUid())
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener(this, (value, error) -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (error != null) return;
                    postList.clear();
                    allPostList.clear();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (value != null && !value.isEmpty()) {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                            com.example.appdraw.model.Post post = doc.toObject(com.example.appdraw.model.Post.class);
                            allPostList.add(post);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                postList.add(post);
                            }
                        }
                    }
                    postList.sort((p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                    allPostList.sort((p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (postAdapter != null) postAdapter.notifyDataSetChanged();
                    renderTwitterLikePosts(allPostList);
                    
                    TextView tvPosts = findViewById(R.id.tv_profile_posts);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvPosts != null) tvPosts.setText(String.valueOf(allPostList.size()));

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (activeTab == 0 && tabArtwork != null) {
                        tabArtwork.performClick();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    } else if (activeTab == 1 && tabPost != null) {
                        tabPost.performClick();
                    }
                });
        }
    }

    /**
     * Hàm renderTwitterLikePosts() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param posts tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private void renderTwitterLikePosts(java.util.List<com.example.appdraw.model.Post> posts) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llProfilePosts == null) return;
        llProfilePosts.removeAllViews();
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();

        for (com.example.appdraw.model.Post post : posts) {
            View postView = inflater.inflate(R.layout.item_post, llProfilePosts, false);
            
            android.widget.ImageView ivPostImg = postView.findViewById(R.id.iv_post_image);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivPostImg != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    ivPostImg.setVisibility(View.VISIBLE);
                    if (post.getImageUrl().startsWith("data:image")) {
                        byte[] decodedBytes = android.util.Base64.decode(post.getImageUrl().split(",")[1], android.util.Base64.DEFAULT);
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        com.bumptech.glide.Glide.with(this).load(decodedBytes).into(ivPostImg);
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        com.bumptech.glide.Glide.with(this).load(post.getImageUrl()).into(ivPostImg);
                    }
                    ivPostImg.setOnClickListener(v -> {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(ProfileActivity.this, com.example.appdraw.community.FullScreenImageActivity.class);
                        intent.putExtra("IMAGE_URL", post.getImageUrl());
                        startActivity(intent);
                    });
                } else {
                    ivPostImg.setVisibility(View.GONE);
                }
            }

            TextView tvContent = postView.findViewById(R.id.tv_post_content);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvContent != null) tvContent.setText(post.getContent());

            TextView tvTime = postView.findViewById(R.id.tv_post_time);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvTime != null) {
                tvTime.setText(getTimeAgo(post.getCreatedAt()));
            }

            TextView tvFollowStatus = postView.findViewById(R.id.tv_follow_status);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvFollowStatus != null) tvFollowStatus.setVisibility(View.GONE); // Hide "Theo dõi" in self profile

            TextView tvCommentCount = postView.findViewById(R.id.tv_comment_count);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvCommentCount != null) tvCommentCount.setText(String.valueOf(post.getCommentsCount()));

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // User Info (Fetch from Firebase to avoid race conditions with Profile header loading)
            TextView tvName = postView.findViewById(R.id.tv_user_name);
            android.widget.ImageView ivAvatar = postView.findViewById(R.id.iv_user_avatar);
            
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Users").document(post.getUid())
                .get().addOnSuccessListener(userDoc -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (userDoc.exists() && userDoc.contains("profile")) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null) {
                            String fullName = (String) profile.get("fullName");
                            String avatarUrl = (String) profile.get("avatarUrl");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvName != null && fullName != null) tvName.setText(fullName);
                            android.widget.ImageView ivMentorBadge = postView.findViewById(R.id.iv_mentor_badge);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivMentorBadge != null) {
                                if ("mentor".equals(userDoc.getString("role"))) {
                                    ivMentorBadge.setVisibility(View.VISIBLE);
                                } else {
                                    ivMentorBadge.setVisibility(View.GONE);
                                }
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivAvatar != null) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                                    byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(b).circleCrop().into(ivAvatar);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
                                } else {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                                }
                            }
                        }
                    }
                });

            View llComment = postView.findViewById(R.id.ll_comment);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llComment != null) {
                llComment.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(ProfileActivity.this, com.example.appdraw.community.PostDetailActivity.class);
                    intent.putExtra("POST_ID", post.getId());
                    startActivity(intent);
                });
            }

            // Likes
            View llLike = postView.findViewById(R.id.ll_like);
            android.widget.ImageView ivLike = postView.findViewById(R.id.iv_like);
            TextView tvLikeCount = postView.findViewById(R.id.tv_like_count);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llLike != null && ivLike != null && tvLikeCount != null) {
                boolean isLiked = post.getLikedBy().contains(currentUid);
                tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                if (isLiked) {
                    ivLike.setImageResource(R.drawable.ic_heart);
                    ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
                } else {
                    ivLike.setColorFilter(android.graphics.Color.parseColor("#888888"));
                }
                llLike.setOnClickListener(v -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (currentUid == null) return;
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Posts").document(post.getId())
                        .get().addOnSuccessListener(doc -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (!doc.exists()) return;
                            com.example.appdraw.model.Post p = doc.toObject(com.example.appdraw.model.Post.class);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (p != null) {
                                if (p.getLikedBy().contains(currentUid)) {
                                    p.getLikedBy().remove(currentUid);
                                    p.setLikesCount(Math.max(0, p.getLikesCount() - 1));
                                } else {
                                    p.getLikedBy().add(currentUid);
                                    p.setLikesCount(p.getLikesCount() + 1);
                                }
                                doc.getReference().set(p);
                            }
                        });
                });
            }
            llProfilePosts.addView(postView);
        }
    }

    /**
     * Hàm getTimeAgo() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param time tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private String getTimeAgo(long time) {
        if (time < 1000000000000L) time *= 1000;
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) return "Vừa xong";

        final long diff = now - time;
        if (diff < 60 * 1000) return "Vừa xong";
        else if (diff < 60 * 60 * 1000) return diff / (60 * 1000) + " phút trước";
        else if (diff < 24 * 60 * 60 * 1000) return diff / (60 * 60 * 1000) + " giờ trước";
        else if (diff < 30L * 24 * 60 * 60 * 1000) return diff / (24 * 60 * 60 * 1000) + " ngày trước";
        else {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(time));
        }
    }
}

