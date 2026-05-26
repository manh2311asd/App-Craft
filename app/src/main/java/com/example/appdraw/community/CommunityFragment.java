package com.example.appdraw.community;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.appdraw.notification.NotificationsActivity;
import com.example.appdraw.R;

/**
 * Fragment hiển thị Bảng tin Cộng đồng - Feed (UC-05).
 * Người thực hiện: Cao Đức Mạnh.
 * Tải danh sách các bài đăng (Post) từ Firestore và xử lý cuộn mượt mà.
 */
/**
 * Lớp CommunityFragment thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file CommunityFragment.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class CommunityFragment extends Fragment {

    /**
     * Biến `currentFilter` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String currentFilter = "Tất cả";
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private java.util.List<com.google.firebase.firestore.QueryDocumentSnapshot> cachedDocs = new java.util.ArrayList<>();
    /**
     * Biến `postContainer` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout postContainer;
    /**
     * Biến `cachedInflater` lưu dữ liệu/trạng thái quan trọng kiểu android.view.LayoutInflater, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private android.view.LayoutInflater cachedInflater;

    /**
     * Biến `isLiked` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isLiked = false;
    /**
     * Biến `likeCount` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int likeCount = 1200;

    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    @Nullable
    @Override
    /**
     * Hàm onCreateView() tạo giao diện cho Fragment từ file layout XML.
     * Sau khi inflate layout, các view và dữ liệu nền sẽ được chuẩn bị để hiển thị cho người dùng.
     * @param inflater tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param container tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        this.cachedInflater = inflater;
        setupFilters(view);






        // --- Post Feed ---
        postContainer = view.findViewById(R.id.ll_post_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (postContainer != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("Posts")
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener((value, error) -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (!isAdded() || getContext() == null) return;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (error != null) return;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (value == null) return;
                    cachedDocs.clear();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                        cachedDocs.add(doc);
                    }
                    renderPosts();
                });
        }

        return view;
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

    /**
     * Hàm showToast() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param msg tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showToast(String msg) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getContext() != null) {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hàm setupFilters() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupFilters(View view) {
        int[] tvIds = new int[]{R.id.tv_filter_all, R.id.tv_filter_watercolor, R.id.tv_filter_sketch, R.id.tv_filter_handmade};
        for (int id : tvIds) {
            TextView tv = view.findViewById(id);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tv != null) {
                tv.setOnClickListener(v -> {
                    for (int _id : tvIds) {
                        TextView _tv = view.findViewById(_id);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (_tv != null) {
                            _tv.setBackgroundResource(R.drawable.rounded_bg_gray);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            _tv.setBackgroundTintList(null);
                            _tv.setTextColor(Color.parseColor("#333333"));
                        }
                    }
                    tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4272D0")));
                    tv.setTextColor(Color.WHITE);
                    currentFilter = tv.getText().toString();
                    renderPosts();
                });
            }
        }
    }

    /**
     * Hàm renderPosts() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void renderPosts() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (postContainer == null || cachedInflater == null || !isAdded()) return;
        postContainer.removeAllViews();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : cachedDocs) {
            com.example.appdraw.model.Post post = doc.toObject(com.example.appdraw.model.Post.class);
            
            if (!currentFilter.equals("Tất cả")) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (post.getTopics() == null || !post.getTopics().contains(currentFilter)) continue;
            }

            View postView = cachedInflater.inflate(R.layout.item_post, postContainer, false);
            
            // Load image if available
            ImageView ivPostImg = postView.findViewById(R.id.iv_post_image);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivPostImg != null && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                ivPostImg.setVisibility(View.VISIBLE);
                if (post.getImageUrl().startsWith("data:image")) {
                    String base64Str = post.getImageUrl().substring(post.getImageUrl().indexOf(",") + 1);
                    byte[] decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT);
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    com.bumptech.glide.Glide.with(requireContext()).load(decodedBytes).into(ivPostImg);
                } else {
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    com.bumptech.glide.Glide.with(requireContext()).load(post.getImageUrl()).into(ivPostImg);
                }
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            } else if (ivPostImg != null) {
                ivPostImg.setVisibility(View.GONE);
            }

            // Load content
            TextView tvContent = postView.findViewById(R.id.tv_post_content);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvContent != null) tvContent.setText(post.getContent());

            // Load time
            TextView tvTime = postView.findViewById(R.id.tv_post_time);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvTime != null) tvTime.setText(getTimeAgo(post.getCreatedAt()));

            // Process Follow Button
            TextView tvFollowStatus = postView.findViewById(R.id.tv_follow_status);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvFollowStatus != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (post.getUid() != null && post.getUid().equals(currentUid)) {
                    tvFollowStatus.setVisibility(View.GONE);
                } else {
                    tvFollowStatus.setVisibility(View.VISIBLE);
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentReference followRef = db.collection("Follows").document(currentUid + "_" + post.getUid());
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    followRef.addSnapshotListener((d, e) -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (e != null) return;
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (d != null && d.exists()) {
                            tvFollowStatus.setText("Đang theo dõi");
                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFEFEF")));
                            tvFollowStatus.setTextColor(Color.parseColor("#1A1A1A"));
                            tvFollowStatus.setTag(true);
                        } else {
                            tvFollowStatus.setText("Theo dõi");
                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4272D0")));
                            tvFollowStatus.setTextColor(Color.WHITE);
                            tvFollowStatus.setTag(false);
                        }
                        tvFollowStatus.setEnabled(true);
                    });

                    tvFollowStatus.setOnClickListener(v -> {
                        tvFollowStatus.setEnabled(false);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        boolean isFollowing = tvFollowStatus.getTag() != null && (boolean)tvFollowStatus.getTag();
                        if (!isFollowing) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> data = new java.util.HashMap<>();
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("follower", currentUid);
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("following", post.getUid());
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            data.put("timestamp", System.currentTimeMillis());
                            followRef.set(data).addOnSuccessListener(aVoid -> {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(post.getUid()).update("followersCount", com.google.firebase.firestore.FieldValue.increment(1));
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(1));
                                showToast("Đã theo dõi");
                                com.example.appdraw.utils.NotificationHelper.sendNotification(post.getUid(), "FOLLOW", "đánh giá cao bài viết và đã bắt đầu theo dõi bạn.", currentUid);
                            });
                        } else {
                            followRef.delete().addOnSuccessListener(aVoid -> {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(post.getUid()).update("followersCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Users").document(currentUid).update("followingCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                showToast("Bỏ theo dõi");
                            });
                        }
                    });
                }
            }

            // Comment Count
            TextView tvCommentCount = postView.findViewById(R.id.tv_comment_count);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvCommentCount != null) {
                tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
            }

            // Fetch Author
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Users").document(post.getUid())
                .get().addOnSuccessListener(userDoc -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (!isAdded() || getContext() == null) return;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (userDoc.exists() && userDoc.contains("profile")) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null) {
                            String fullName = (String) profile.get("fullName");
                            String avatarUrl = (String) profile.get("avatarUrl");
                            TextView tvName = postView.findViewById(R.id.tv_user_name);
                            ImageView ivAvatar = postView.findViewById(R.id.iv_user_avatar);
                            ImageView ivMentorBadge = postView.findViewById(R.id.iv_mentor_badge);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvName != null) tvName.setText(fullName != null ? fullName : "Người dùng");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivMentorBadge != null) {
                                ivMentorBadge.setVisibility("mentor".equals(userDoc.getString("role")) ? View.VISIBLE : View.GONE);
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivAvatar != null) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                                    byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(requireContext()).load(b).circleCrop().into(ivAvatar);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(requireContext()).load(avatarUrl).circleCrop().into(ivAvatar);
                                } else {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(requireContext()).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                                }
                            }
                        }
                    }
                });

            // Like Logic
            View llLike = postView.findViewById(R.id.ll_like);
            ImageView ivLike = postView.findViewById(R.id.iv_like);
            TextView tvLikeCount = postView.findViewById(R.id.tv_like_count);

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llLike != null && ivLike != null && tvLikeCount != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                boolean isLiked = post.getLikedBy() != null && post.getLikedBy().contains(currentUid);
                tvLikeCount.setText(String.valueOf(post.getLikesCount()));
                if (isLiked) {
                    ivLike.setImageResource(R.drawable.ic_heart);
                    ivLike.setColorFilter(Color.parseColor("#E91E63"));
                } else {
                    ivLike.setColorFilter(Color.parseColor("#888888"));
                }

                llLike.setOnClickListener(v -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (currentUid == null) {
                        showToast("Vui lòng đăng nhập!");
                        return;
                    }
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentReference postRef = doc.getReference();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    com.google.firebase.firestore.FirebaseFirestore.getInstance().runTransaction(transaction -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(postRef);
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        com.example.appdraw.model.Post p = snapshot.toObject(com.example.appdraw.model.Post.class);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (p != null) {
                            // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (p.getLikedBy() == null) p.setLikedBy(new java.util.ArrayList<>());
                            if (p.getLikedBy().contains(currentUid)) {
                                p.getLikedBy().remove(currentUid);
                                p.setLikesCount(Math.max(0, p.getLikesCount() - 1));
                            } else {
                                p.getLikedBy().add(currentUid);
                                p.setLikesCount(p.getLikesCount() + 1);
                                if (!p.getUid().equals(currentUid)) {
                                    com.example.appdraw.utils.NotificationHelper.sendNotification(p.getUid(), "LIKE", "đã thích bài viết của bạn.", p.getId());
                                }
                            }
                            transaction.set(postRef, p);
                        }
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        return null;
                    }).addOnFailureListener(e -> showToast("Lỗi mạng"));
                });
            }

            // Click Handlers
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivPostImg != null) {
                ivPostImg.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(getActivity(), FullScreenImageActivity.class);
                    intent.putExtra("IMAGE_URL", post.getImageUrl());
                    startActivity(intent);
                });
            }

            View llComment = postView.findViewById(R.id.ll_comment);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llComment != null) {
                llComment.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    intent.putExtra("POST_ID", post.getId());
                    startActivity(intent);
                });
            }

            View userHeader = postView.findViewById(R.id.ll_user_header);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (userHeader != null) {
                userHeader.setOnClickListener(v -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (post.getUid() != null && post.getUid().equals(currentUid)) {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        startActivity(new Intent(getActivity(), com.example.appdraw.profile.ProfileActivity.class));
                    } else {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
                        intent.putExtra("USER_ID", post.getUid());
                        startActivity(intent);
                    }
                });
            }

            postView.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                startActivity(intent);
            });

            postContainer.addView(postView);
        }
    }
}
