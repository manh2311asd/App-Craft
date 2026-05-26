package com.example.appdraw.explore;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp ArtistDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ArtistDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ArtistDetailActivity extends AppCompatActivity {
    /**
     * Biến `artistId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String artistId;
    /**
     * Biến `isFollowing` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isFollowing = false;
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `currentUid` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String currentUid;

    /**
     * Biến `btnFollow` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private MaterialButton btnFollow;
    /**
     * Biến `tvFollowersCount` lưu dữ liệu/trạng thái quan trọng kiểu TextView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvFollowersCount;

    /**
     * Biến `postAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private com.example.appdraw.community.PostMediaAdapter postAdapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private java.util.List<com.example.appdraw.model.Post> artworkList = new java.util.ArrayList<>();
    /**
     * Biến `rvArtworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private androidx.recyclerview.widget.RecyclerView rvArtworks;
    /**
     * Biến `llEmptyArtworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private LinearLayout llEmptyArtworks;

    /**
     * Biến `llArtistLessonsContainer` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private LinearLayout llArtistLessonsContainer;
    /**
     * Biến `tvEmptyLessons` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private TextView tvEmptyLessons;

    /**
     * Biến `btnCreateLesson` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private MaterialButton btnCreateLesson;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        artistId = getIntent().getStringExtra("ARTIST_ID");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String name = getIntent().getStringExtra("ARTIST_NAME");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String avatarUrl = getIntent().getStringExtra("ARTIST_AVATAR");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String bio = getIntent().getStringExtra("ARTIST_BIO");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (name != null) ((TextView) findViewById(R.id.tv_artist_name_detail)).setText(name);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (bio != null) ((TextView) findViewById(R.id.tv_artist_bio)).setText(bio);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
            Glide.with(this).load(avatarUrl).circleCrop().into((ImageView) findViewById(R.id.iv_artist_large));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Setup Tabs
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        LinearLayout llTabLessons = findViewById(R.id.ll_tab_lessons);
        LinearLayout llTabArtworks = findViewById(R.id.ll_tab_artworks);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                /**
                 * Hàm onTabSelected() thực hiện một phần xử lý trong luồng chức năng của lớp ArtistDetailActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param tab tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        // Tác phẩm
                        llTabLessons.setVisibility(View.GONE);
                        llTabArtworks.setVisibility(View.VISIBLE);
                    } else {
                        // Khóa học
                        llTabLessons.setVisibility(View.VISIBLE);
                        llTabArtworks.setVisibility(View.GONE);
                    }
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });
        }

        // Setup Artworks Grid
        rvArtworks = findViewById(R.id.rv_artist_artworks);
        llEmptyArtworks = findViewById(R.id.ll_empty_artist_artworks);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvArtworks != null) {
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvArtworks.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
            postAdapter = new com.example.appdraw.community.PostMediaAdapter(artworkList, post -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                android.content.Intent intent = new android.content.Intent(this, com.example.appdraw.community.PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                startActivity(intent);
            });
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvArtworks.setAdapter(postAdapter);
            
            fetchArtistPosts();
        }

        llArtistLessonsContainer = findViewById(R.id.ll_artist_lessons_container);
        tvEmptyLessons = findViewById(R.id.tv_empty_lessons);
        fetchArtistLessons();

        // Setup Follow System
        btnFollow = findViewById(R.id.btn_follow);
        tvFollowersCount = findViewById(R.id.tv_followers_count);
        btnCreateLesson = findViewById(R.id.btn_create_lesson);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (artistId != null && currentUid != null) {
            getFollowersCount(); // LUÔN LUÔN hiển thị số người theo dõi

            if (artistId.equals(currentUid)) {
                btnFollow.setVisibility(View.GONE);
                checkMentorRoleAndShowCreateButton();
            } else {
                checkFollowStatus();
                btnFollow.setOnClickListener(v -> toggleFollow());
            }
        } else {
            btnFollow.setVisibility(View.GONE);
        }
    }

    /**
     * Hàm checkMentorRoleAndShowCreateButton() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void checkMentorRoleAndShowCreateButton() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(currentUid).get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(documentSnapshot -> {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (documentSnapshot.exists()) {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    String role = documentSnapshot.getString("role");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (role != null && role.equalsIgnoreCase("mentor")) {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (btnCreateLesson != null) {
                            btnCreateLesson.setVisibility(View.VISIBLE);
                            btnCreateLesson.setOnClickListener(v -> {
                                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                startActivity(new android.content.Intent(ArtistDetailActivity.this, CreateLessonActivity.class));
                            });
                        }
                    }
                }
            });
    }

    /**
     * Hàm checkFollowStatus() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     */
    private void checkFollowStatus() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Follows").document(currentUid + "_" + artistId)
            .get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(documentSnapshot -> {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (documentSnapshot.exists()) {
                    isFollowing = true;
                    updateFollowButtonUI();
                }
            });
    }

    /**
     * Hàm getFollowersCount() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void getFollowersCount() {
        db.collection("Follows")
            .whereEqualTo("following", artistId)
            .count()
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            .get(com.google.firebase.firestore.AggregateSource.SERVER)
            .addOnSuccessListener(task -> {
                tvFollowersCount.setText(task.getCount() + " người theo dõi");
            });
    }

    /**
     * Hàm toggleFollow() thực hiện một phần xử lý trong luồng chức năng của lớp ArtistDetailActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void toggleFollow() {
        btnFollow.setEnabled(false); // disable while processing
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        DocumentReference followRef = db.collection("Follows").document(currentUid + "_" + artistId);

        if (isFollowing) {
            // Unfollow
            followRef.delete().addOnSuccessListener(aVoid -> {
                isFollowing = false;
                
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(artistId).update("followersCount", FieldValue.increment(-1));
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(currentUid).update("followingCount", FieldValue.increment(-1));
                
                updateFollowButtonUI();
                getFollowersCount();
                btnFollow.setEnabled(true);
            }).addOnFailureListener(e -> btnFollow.setEnabled(true));
        } else {
            // Follow
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            java.util.Map<String, Object> followData = new java.util.HashMap<>();
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            followData.put("follower", currentUid);
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            followData.put("following", artistId);
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            followData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
            
            followRef.set(followData).addOnSuccessListener(aVoid -> {
                isFollowing = true;
                
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(artistId).update("followersCount", FieldValue.increment(1));
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(currentUid).update("followingCount", FieldValue.increment(1));
                
                com.example.appdraw.utils.NotificationHelper.sendNotification(artistId, "FOLLOW", "rất thích các tác phẩm và đã bắt đầu theo dõi bạn.", currentUid);
                
                updateFollowButtonUI();
                getFollowersCount();
                btnFollow.setEnabled(true);
            }).addOnFailureListener(e -> btnFollow.setEnabled(true));
        }
    }

    /**
     * Hàm updateFollowButtonUI() thực hiện một phần xử lý trong luồng chức năng của lớp ArtistDetailActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateFollowButtonUI() {
        if (isFollowing) {
            btnFollow.setText("Đang theo dõi");
            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE0E0E0)); // Grey
            btnFollow.setTextColor(0xFF555555);
        } else {
            btnFollow.setText("Theo dõi");
            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4272D0)); // Primary Blue
            btnFollow.setTextColor(0xFFFFFFFF);
        }
    }

    /**
     * Hàm fetchArtistPosts() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchArtistPosts() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (artistId == null) return;
        db.collection("Posts").whereEqualTo("uid", artistId).get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                artworkList.clear();
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    com.example.appdraw.model.Post post = doc.toObject(com.example.appdraw.model.Post.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (post != null && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                        artworkList.add(post);
                    }
                }
                
                // Sắp xếp mới nhất lên đầu
                artworkList.sort((p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (artworkList.isEmpty()) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rvArtworks != null) rvArtworks.setVisibility(View.GONE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (llEmptyArtworks != null) llEmptyArtworks.setVisibility(View.VISIBLE);
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rvArtworks != null) rvArtworks.setVisibility(View.VISIBLE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (llEmptyArtworks != null) llEmptyArtworks.setVisibility(View.GONE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (postAdapter != null) postAdapter.notifyDataSetChanged();
                }
            });
    }

    /**
     * Hàm fetchArtistLessons() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchArtistLessons() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (artistId == null) return;
        db.collection("Lessons").whereEqualTo("authorId", artistId).get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llArtistLessonsContainer == null) return;
                llArtistLessonsContainer.removeAllViews();
                
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (queryDocumentSnapshots.isEmpty()) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvEmptyLessons != null) tvEmptyLessons.setVisibility(View.VISIBLE);
                    return;
                }
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvEmptyLessons != null) tvEmptyLessons.setVisibility(View.GONE);
                
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<com.google.firebase.firestore.DocumentSnapshot> lessons = new java.util.ArrayList<>();
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    lessons.add(doc);
                }
                
                // Sort by createdAt descending
                lessons.sort((d1, d2) -> {
                    Long c1 = d1.getLong("createdAt");
                    Long c2 = d2.getLong("createdAt");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (c1 != null && c2 != null) return Long.compare(c2, c1);
                    return 0;
                });
                
                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : lessons) {
                    View lessonView = inflater.inflate(R.layout.item_lesson_list, llArtistLessonsContainer, false);
                    android.widget.TextView tvTitle = lessonView.findViewById(R.id.tv_lesson_title);
                    android.widget.TextView tvAuthor = lessonView.findViewById(R.id.tv_author);
                    android.widget.ImageView ivThumb = lessonView.findViewById(R.id.iv_lesson_thumb);
                    android.widget.TextView tvStatus = lessonView.findViewById(R.id.tv_status);
                    android.widget.TextView tvDuration = lessonView.findViewById(R.id.tv_duration);
                    android.widget.RatingBar rb = lessonView.findViewById(R.id.rating_bar);
                    
                    String title = doc.getString("title");
                    String author = doc.getString("authorName");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (author == null) author = doc.getString("author");
                    String imageUrl = doc.getString("thumbnailUrl");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageUrl == null) imageUrl = doc.getString("imageUrl");
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvTitle != null) tvTitle.setText(title);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvAuthor != null) tvAuthor.setText(author != null ? author : "");
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ivThumb != null && imageUrl != null && !imageUrl.isEmpty()) {
                        if (imageUrl.startsWith("data:image")) {
                            try {
                                byte[] imageByteArray = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                com.bumptech.glide.Glide.with(this).load(imageByteArray).centerCrop().into(ivThumb);
                            } catch (Exception e) {}
                        } else {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivThumb);
                        }
                    }
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rb != null) {
                        Double rating = doc.getDouble("rating");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (rating != null) rb.setRating(rating.floatValue());
                        else rb.setRating(5.0f);
                    }
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvDuration != null) {
                        Long duration = doc.getLong("durationMin");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (duration == null) duration = doc.getLong("duration");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (duration != null && duration > 0) {
                            tvDuration.setText(duration + " min");
                        } else {
                            tvDuration.setText("30 min");
                        }
                    }
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvStatus != null) {
                        tvStatus.setText("Xem chi tiết");
                        tvStatus.setBackgroundResource(R.drawable.bg_badge_completed);
                        tvStatus.setTextColor(android.graphics.Color.WHITE);
                    }
                    
                    final String safeAuthor = author;
                    lessonView.setOnClickListener(v -> {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        android.content.Intent intent = new android.content.Intent(ArtistDetailActivity.this, LessonDetailActivity.class);
                        intent.putExtra("LESSON_TITLE", title);
                        intent.putExtra("LESSON_ID", doc.getId());
                        intent.putExtra("CATEGORY", doc.getString("category"));
                        intent.putExtra("IMAGE_RES", doc.getString("imageRes"));
                        intent.putExtra("AUTHOR", safeAuthor);
                        startActivity(intent);
                    });
                    
                    llArtistLessonsContainer.addView(lessonView);
                }
            });
    }
}
