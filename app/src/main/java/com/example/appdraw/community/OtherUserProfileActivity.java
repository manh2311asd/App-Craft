package com.example.appdraw.community;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
/**
 * Lớp OtherUserProfileActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file OtherUserProfileActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class OtherUserProfileActivity extends AppCompatActivity {

    /**
     * Biến `isFollowing` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isFollowing = false;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String userId = getIntent().getStringExtra("USER_ID");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (userId == null) {
            finish();
            return;
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String currentUid = FirebaseAuth.getInstance().getUid();

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_profile);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        
        // Hide settings button
        View btnSettings = findViewById(R.id.btn_profile_settings);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSettings != null) btnSettings.setVisibility(android.view.View.GONE);
        
        // Hide "Vẽ ngay" button
        View btnStartDrawing = findViewById(R.id.btn_start_drawing);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnStartDrawing != null) btnStartDrawing.setVisibility(android.view.View.GONE);

        // Bind views
        TextView tvOtherName = findViewById(R.id.tv_profile_name);
        ImageView ivAvatar = findViewById(R.id.iv_profile_avatar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> showAvatarFullscreen());
        }
        TextView tvBio = findViewById(R.id.tv_profile_bio);
        com.google.android.material.button.MaterialButton btnFollow = findViewById(R.id.btn_follow);

        TextView tvFollowers = findViewById(R.id.tv_profile_followers);
        TextView tvFollowing = findViewById(R.id.tv_profile_following);
        TextView tvPosts = findViewById(R.id.tv_profile_posts);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Setup Follow Button
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnFollow != null) {
            if (userId.equals(currentUid)) {
                btnFollow.setVisibility(android.view.View.GONE);
            } else {
                btnFollow.setVisibility(android.view.View.VISIBLE);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (currentUid != null) {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentReference followRef = db.collection("Follows").document(currentUid + "_" + userId);
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    followRef.addSnapshotListener(this, (doc, e) -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (e != null) return;
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (doc != null && doc.exists()) {
                            isFollowing = true;
                            btnFollow.setText("Đang theo dõi");
                            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFEFEF")));
                            btnFollow.setTextColor(android.graphics.Color.parseColor("#1A1A1A"));
                        } else {
                            isFollowing = false;
                            btnFollow.setText("+ Theo dõi");
                            btnFollow.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4272D0")));
                            btnFollow.setTextColor(android.graphics.Color.WHITE);
                        }
                        btnFollow.setEnabled(true);
                    });

                    btnFollow.setOnClickListener(v -> {
                        btnFollow.setEnabled(false);
                        if (!isFollowing) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> data = new java.util.HashMap<>();
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("follower", currentUid);
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("following", userId);
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("timestamp", System.currentTimeMillis());
                            followRef.set(data).addOnSuccessListener(aVoid -> {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(userId).update("followersCount", com.google.firebase.firestore.FieldValue.increment(1));
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(1));
                                Toast.makeText(this, "Đã theo dõi", Toast.LENGTH_SHORT).show();
                                com.example.appdraw.utils.NotificationHelper.sendNotification(userId, "FOLLOW", "đánh giá cao tác phẩm và bắt đầu theo dõi bạn.", currentUid);
                            });
                        } else {
                            followRef.delete().addOnSuccessListener(aVoid -> {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(userId).update("followersCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                Toast.makeText(this, "Bỏ theo dõi", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }

        // Load User Info
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(userId).addSnapshotListener(this, (doc, e) -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (e != null || doc == null || !doc.exists()) return;
            Long posts = doc.getLong("postCount");
            String role = doc.getString("role");
            
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            long postsVal = posts != null ? Math.max(0, posts) : 0;
            
            ImageView ivMentorBadge = findViewById(R.id.iv_mentor_badge);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivMentorBadge != null) {
                if ("mentor".equals(role)) {
                    ivMentorBadge.setVisibility(android.view.View.VISIBLE);
                } else {
                    ivMentorBadge.setVisibility(android.view.View.GONE);
                }
            }
            
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvPosts != null) tvPosts.setText(String.valueOf(postsVal));

            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Fetch followers logically by counting 'Follows' sub-documents natively
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("Follows")
                .whereEqualTo("following", userId)
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
                .whereEqualTo("follower", userId)
                .count()
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                .get(com.google.firebase.firestore.AggregateSource.SERVER)
                .addOnSuccessListener(task -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvFollowing != null) tvFollowing.setText(String.valueOf(task.getCount()));
                });
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvPosts != null) tvPosts.setText(String.valueOf(postsVal));

                if (doc.contains("profile")) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profile != null) {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvOtherName != null) {
                            String fName = (String) profile.get("fullName");
                            tvOtherName.setText(fName);
                            TextView tvToolbar = findViewById(R.id.tv_toolbar_title);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvToolbar != null && fName != null) {
                                tvToolbar.setText("Hồ sơ của " + fName);
                            }
                        }
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvBio != null) tvBio.setText((String) profile.get("bio"));
                        
                        String interest = doc.getString("interest");
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
                        
                        String avatarUrl = (String) profile.get("avatarUrl");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivAvatar != null) {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                                byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                Glide.with(this).load(b).circleCrop().into(ivAvatar);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
                            } else {
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                            }
                        }
                    }
                }
        });
        
        // Fetch their artworks
        LinearLayout llProfilePosts = findViewById(R.id.ll_profile_posts);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        androidx.recyclerview.widget.RecyclerView rvProfileArtworks = findViewById(R.id.rv_profile_artworks);
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        java.util.List<com.example.appdraw.model.Post> postList = new java.util.ArrayList<>();
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        java.util.List<com.example.appdraw.model.Post> allPostList = new java.util.ArrayList<>();
        final com.example.appdraw.community.PostMediaAdapter[] postAdapter = new com.example.appdraw.community.PostMediaAdapter[1];

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvProfileArtworks != null) {
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProfileArtworks.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
            postAdapter[0] = new com.example.appdraw.community.PostMediaAdapter(postList, post -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(this, FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", post.getImageUrl());
                startActivity(intent);
            });
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvProfileArtworks.setAdapter(postAdapter[0]);
        }
        
        final int[] activeTab = {1}; // 1 = post, 0 = artwork
        
        db.collection("Posts")
            .whereEqualTo("uid", userId)
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
                    for (com.google.firebase.firestore.QueryDocumentSnapshot d : value) {
                        com.example.appdraw.model.Post post = d.toObject(com.example.appdraw.model.Post.class);
                        allPostList.add(post);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                            postList.add(post);
                        }
                    }
                    allPostList.sort((p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                    postList.sort((p1, p2) -> Long.compare(p2.getCreatedAt(), p1.getCreatedAt()));
                    
                    TextView profilePostsTv = findViewById(R.id.tv_profile_posts);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profilePostsTv != null) profilePostsTv.setText(String.valueOf(allPostList.size()));
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (postAdapter[0] != null) postAdapter[0].notifyDataSetChanged();
                    renderTwitterLikePosts(llProfilePosts, allPostList);
                } else {
                    TextView profilePostsTv = findViewById(R.id.tv_profile_posts);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profilePostsTv != null) profilePostsTv.setText("0");
                }
                
                TextView tPost = findViewById(R.id.tab_post);
                TextView tArt = findViewById(R.id.tab_artwork);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (activeTab[0] == 1 && tPost != null) tPost.performClick();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                else if (activeTab[0] == 0 && tArt != null) tArt.performClick();
            });
            
        // Setup Tabs (3 tabs: Bài viết, Tác phẩm, Thành tích)
        TextView tabPost = findViewById(R.id.tab_post);
        TextView tabArtwork = findViewById(R.id.tab_artwork);
        TextView tabProject = findViewById(R.id.tab_project);
        TextView tabSaved = findViewById(R.id.tab_saved);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabPost != null && tabPost.getParent() instanceof LinearLayout) {
            ((LinearLayout)tabPost.getParent()).setVisibility(android.view.View.VISIBLE);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabPost != null) {
            tabPost.setText("Bài viết");
            tabPost.setTextColor(android.graphics.Color.parseColor("#4272D0"));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabPost.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabArtwork != null) {
            tabArtwork.setText("Tác phẩm");
            tabArtwork.setTextColor(android.graphics.Color.parseColor("#888888"));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabArtwork.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabProject != null) {
            tabProject.setText("Thành tích");
            tabProject.setTextColor(android.graphics.Color.parseColor("#888888"));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            tabProject.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabSaved != null) {
            tabSaved.setVisibility(android.view.View.GONE);
        }
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabPost != null && tabArtwork != null) {
            tabPost.setOnClickListener(v -> {
                activeTab[0] = 1;
                tabPost.setTextColor(android.graphics.Color.parseColor("#4272D0"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                tabPost.setTypeface(null, android.graphics.Typeface.BOLD);
                tabArtwork.setTextColor(android.graphics.Color.parseColor("#888888"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                tabArtwork.setTypeface(null, android.graphics.Typeface.NORMAL);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(android.view.View.GONE);
                LinearLayout emptyView = findViewById(R.id.ll_empty_artworks);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (allPostList.isEmpty()) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (llProfilePosts != null) llProfilePosts.setVisibility(android.view.View.GONE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (emptyView != null) {
                        emptyView.setVisibility(android.view.View.VISIBLE);
                        if (emptyView.getChildCount() > 2 && emptyView.getChildAt(1) instanceof TextView) {
                            ((TextView)emptyView.getChildAt(1)).setText("Người dùng này chưa có bài viết nào");
                        }
                        if (emptyView.getChildCount() > 2 && emptyView.getChildAt(2) instanceof TextView) {
                            ((TextView)emptyView.getChildAt(2)).setVisibility(android.view.View.GONE);
                        }
                    }
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (llProfilePosts != null) llProfilePosts.setVisibility(android.view.View.VISIBLE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (emptyView != null) emptyView.setVisibility(android.view.View.GONE);
                }
            });
            tabArtwork.setOnClickListener(v -> {
                activeTab[0] = 0;
                tabArtwork.setTextColor(android.graphics.Color.parseColor("#4272D0"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                tabArtwork.setTypeface(null, android.graphics.Typeface.BOLD);
                tabPost.setTextColor(android.graphics.Color.parseColor("#888888"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                tabPost.setTypeface(null, android.graphics.Typeface.NORMAL);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llProfilePosts != null) llProfilePosts.setVisibility(android.view.View.GONE);
                LinearLayout emptyView = findViewById(R.id.ll_empty_artworks);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (postList.isEmpty()) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(android.view.View.GONE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (emptyView != null) {
                        emptyView.setVisibility(android.view.View.VISIBLE);
                        if (emptyView.getChildCount() > 2 && emptyView.getChildAt(1) instanceof TextView) {
                            ((TextView)emptyView.getChildAt(1)).setText("Người dùng này chưa có tác phẩm nào");
                        }
                        if (emptyView.getChildCount() > 2 && emptyView.getChildAt(2) instanceof TextView) {
                            ((TextView)emptyView.getChildAt(2)).setVisibility(android.view.View.GONE);
                        }
                    }
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rvProfileArtworks != null) rvProfileArtworks.setVisibility(android.view.View.VISIBLE);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (emptyView != null) emptyView.setVisibility(android.view.View.GONE);
                }
            });
        }
    }

    /**
     * Hàm renderTwitterLikePosts() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param llProfilePosts tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param posts tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private void renderTwitterLikePosts(LinearLayout llProfilePosts, java.util.List<com.example.appdraw.model.Post> posts) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llProfilePosts == null) return;
        llProfilePosts.removeAllViews();
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();

        for (com.example.appdraw.model.Post post : posts) {
            View postView = inflater.inflate(R.layout.item_post, llProfilePosts, false);
            
            ImageView ivPostImg = postView.findViewById(R.id.iv_post_image);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivPostImg != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                    ivPostImg.setVisibility(android.view.View.VISIBLE);
                    if (post.getImageUrl().startsWith("data:image")) {
                        byte[] decodedBytes = android.util.Base64.decode(post.getImageUrl().split(",")[1], android.util.Base64.DEFAULT);
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(decodedBytes).into(ivPostImg);
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(post.getImageUrl()).into(ivPostImg);
                    }
                    ivPostImg.setOnClickListener(v -> {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(this, FullScreenImageActivity.class);
                        intent.putExtra("IMAGE_URL", post.getImageUrl());
                        startActivity(intent);
                    });
                } else {
                    ivPostImg.setVisibility(android.view.View.GONE);
                }
            }

            TextView tvContent = postView.findViewById(R.id.tv_post_content);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvContent != null) tvContent.setText(post.getContent());

            TextView tvFollowStatus = postView.findViewById(R.id.tv_follow_status);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvFollowStatus != null) tvFollowStatus.setVisibility(android.view.View.GONE);

            TextView tvCommentCount = postView.findViewById(R.id.tv_comment_count);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvCommentCount != null) tvCommentCount.setText(String.valueOf(post.getCommentsCount()));

            TextView tvName = postView.findViewById(R.id.tv_user_name);
            ImageView ivAvatar = postView.findViewById(R.id.iv_user_avatar);
            
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Users").document(post.getUid())
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
                            ImageView ivMentorBadge = postView.findViewById(R.id.iv_mentor_badge);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivMentorBadge != null) {
                                if ("mentor".equals(userDoc.getString("role"))) {
                                    ivMentorBadge.setVisibility(android.view.View.VISIBLE);
                                } else {
                                    ivMentorBadge.setVisibility(android.view.View.GONE);
                                }
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivAvatar != null) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                                    byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(this).load(b).circleCrop().into(ivAvatar);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
                                } else {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
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
                    Intent intent = new Intent(this, PostDetailActivity.class);
                    intent.putExtra("POST_ID", post.getId());
                    startActivity(intent);
                });
            }

            View llLike = postView.findViewById(R.id.ll_like);
            ImageView ivLike = postView.findViewById(R.id.iv_like);
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
                    FirebaseFirestore.getInstance().collection("Posts").document(post.getId())
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
     * Hàm showAvatarFullscreen() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showAvatarFullscreen() {
        ImageView ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivProfileAvatar == null || ivProfileAvatar.getDrawable() == null) return;
        
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, 
                android.view.ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setBackgroundColor(android.graphics.Color.BLACK);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(ivProfileAvatar.getDrawable());
        
        imageView.setOnClickListener(v -> dialog.dismiss());
        
        dialog.setContentView(imageView);
        dialog.show();
    }
}
