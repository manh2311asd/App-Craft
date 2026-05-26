package com.example.appdraw.community;

import com.example.appdraw.R;
import com.example.appdraw.model.Post;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
/**
 * Lớp PostDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file PostDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class PostDetailActivity extends AppCompatActivity {
    /**
     * Biến `postId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String postId;

    /**
     * Biến `commentsListener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private com.google.firebase.firestore.ListenerRegistration commentsListener;
    /**
     * Biến `currentCommentFilter` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String currentCommentFilter = "Cũ nhất";
    /**
     * Biến `replyingToCommentId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String replyingToCommentId = null;
    /**
     * Biến `replyingToUserName` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String replyingToUserName = null;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_post_detail);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        postId = getIntent().getStringExtra("POST_ID");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (postId != null) {
            loadPostDetails();
            loadComments();
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            setupCommentInput();
            setupCommentFilter();
        }
    }

    /**
     * Hàm setupCommentFilter() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupCommentFilter() {
        TextView tvFilter = findViewById(R.id.tv_comment_filter);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvFilter != null) {
            tvFilter.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(this, tvFilter);
                popup.getMenu().add("Mới nhất");
                popup.getMenu().add("Cũ nhất");
                popup.setOnMenuItemClickListener(item -> {
                    String title = item.getTitle().toString();
                    tvFilter.setText(title + " ▼");
                    currentCommentFilter = title;
                    loadComments();
                    return true;
                });
                popup.show();
            });
        }
    }

    /**
     * Hàm setupCommentInput() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
    private void setupCommentInput() {
        ImageView btnSend = findViewById(R.id.btn_send_comment);
        android.widget.EditText etComment = findViewById(R.id.et_comment);
        
        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (text.isEmpty()) return;
            
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (uid == null) {
                android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            btnSend.setEnabled(false);
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            String commentId = db.collection("Posts").document(postId).collection("Comments").document().getId();
            com.example.appdraw.model.Comment comment = new com.example.appdraw.model.Comment(commentId, uid, text, System.currentTimeMillis());
            comment.setParentId(replyingToCommentId);
            
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Posts").document(postId).collection("Comments").document(commentId)
                .set(comment)
                .addOnSuccessListener(aVoid -> {
                    etComment.setText("");
                    btnSend.setEnabled(true);
                    cancelReply();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Posts").document(postId).update("commentsCount", com.google.firebase.firestore.FieldValue.increment(1));
                    android.widget.Toast.makeText(this, "Đã gửi bình luận", android.widget.Toast.LENGTH_SHORT).show();
                    
                    // Gửi thông báo cho tác giả
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Posts").document(postId).get().addOnSuccessListener(doc -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (doc.exists() && doc.getString("uid") != null && !doc.getString("uid").equals(uid)) {
                            com.example.appdraw.utils.NotificationHelper.sendNotification(doc.getString("uid"), "COMMENT", "đã bình luận về bài viết của bạn: " + text, postId);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    btnSend.setEnabled(true);
                    android.widget.Toast.makeText(this, "Lỗi gửi bình luận", android.widget.Toast.LENGTH_SHORT).show();
                });
        });

        ImageView btnCancelReply = findViewById(R.id.btn_cancel_reply);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnCancelReply != null) {
            btnCancelReply.setOnClickListener(v -> cancelReply());
        }
    }

    /**
     * Hàm cancelReply() thực hiện một phần xử lý trong luồng chức năng của lớp PostDetailActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void cancelReply() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        replyingToCommentId = null;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        replyingToUserName = null;
        View llReplyStatus = findViewById(R.id.ll_reply_status);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llReplyStatus != null) llReplyStatus.setVisibility(View.GONE);
        android.widget.EditText etComment = findViewById(R.id.et_comment);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (etComment != null) {
            etComment.setHint("Viết bình luận...");
            etComment.setText("");
        }
    }

    /**
     * Hàm loadComments() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadComments() {
        android.widget.LinearLayout llCommentsContainer = findViewById(R.id.ll_comments_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llCommentsContainer == null) return;
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (commentsListener != null) {
            commentsListener.remove();
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.Query.Direction direction = currentCommentFilter.equals("Mới nhất") ? com.google.firebase.firestore.Query.Direction.DESCENDING : com.google.firebase.firestore.Query.Direction.ASCENDING;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        commentsListener = FirebaseFirestore.getInstance().collection("Posts").document(postId).collection("Comments")
            .orderBy("createdAt", direction)
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addSnapshotListener((value, error) -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (error != null || value == null) return;
                llCommentsContainer.removeAllViews();
                
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                java.util.List<com.example.appdraw.model.Comment> topLevelComments = new java.util.ArrayList<>();
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                java.util.List<com.example.appdraw.model.Comment> replies = new java.util.ArrayList<>();
                
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : value) {
                    com.example.appdraw.model.Comment comment = doc.toObject(com.example.appdraw.model.Comment.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
                        topLevelComments.add(comment);
                    } else {
                        replies.add(comment);
                    }
                }
                
                if (currentCommentFilter.equals("Mới nhất")) {
                    java.util.Collections.sort(replies, (c1, c2) -> Long.compare(c1.getCreatedAt(), c2.getCreatedAt()));
                }
                
                int totalComments = topLevelComments.size() + replies.size();
                TextView tvCommentCount = findViewById(R.id.tv_comment_count);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvCommentCount != null) {
                    tvCommentCount.setText(String.valueOf(totalComments));
                }
                
                // Cập nhật lại số lượng bình luận chuẩn xác lên server để đồng bộ với Home Feed
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                FirebaseFirestore.getInstance().collection("Posts").document(postId).update("commentsCount", totalComments);
                
                for (com.example.appdraw.model.Comment topComment : topLevelComments) {
                    renderComment(topComment, llCommentsContainer, false);
                    for (com.example.appdraw.model.Comment reply : replies) {
                        if (topComment.getId().equals(reply.getParentId())) {
                            renderComment(reply, llCommentsContainer, true);
                        }
                    }
                }
            });
    }

    /**
     * Hàm renderComment() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param comment tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param container tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param isReply tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void renderComment(com.example.appdraw.model.Comment comment, android.widget.LinearLayout container, boolean isReply) {
        View commentView = getLayoutInflater().inflate(R.layout.item_comment, container, false);
        
        if (isReply) {
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int marginPx = (int) (40 * getResources().getDisplayMetrics().density);
            params.setMargins(marginPx, 0, 0, 0);
            commentView.setLayoutParams(params);
        }
        
        TextView tvCommentContent = commentView.findViewById(R.id.tv_comment_content);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvCommentContent != null) tvCommentContent.setText(comment.getContent());
        
        TextView tvCommentTime = commentView.findViewById(R.id.tv_comment_time);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvCommentTime != null) {
             long time = comment.getCreatedAt();
             long now = System.currentTimeMillis();
             if (time < 1000000000000L) time *= 1000;
             long diff = now - time;
             if (diff < 60 * 1000) tvCommentTime.setText("Vừa xong");
             else if (diff < 60 * 60 * 1000) tvCommentTime.setText((diff / (60 * 1000)) + " phút trước");
             else if (diff < 24 * 60 * 60 * 1000) tvCommentTime.setText((diff / (60 * 60 * 1000)) + " giờ trước");
             else {
                 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                 tvCommentTime.setText(sdf.format(new java.util.Date(time)));
             }
        }
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        FirebaseFirestore.getInstance().collection("Users").document(comment.getUid())
            .get().addOnSuccessListener(userDoc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (userDoc.exists() && userDoc.contains("profile")) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profile != null) {
                        String fullName = (String) profile.get("fullName");
                        String avatarUrl = (String) profile.get("avatarUrl");
                        TextView tvName = commentView.findViewById(R.id.tv_comment_name);
                        ImageView ivAvatar = commentView.findViewById(R.id.iv_comment_avatar);
                        ImageView ivMentorBadge = commentView.findViewById(R.id.iv_comment_mentor_badge);
                        
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
            
        TextView tvLike = commentView.findViewById(R.id.tv_comment_like);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvLike != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            boolean isLiked = comment.getLikedBy() != null && comment.getLikedBy().contains(currentUid);
            
            tvLike.setText("Thích");
            if (isLiked) {
                tvLike.setTextColor(android.graphics.Color.parseColor("#1877F2"));
            } else {
                tvLike.setTextColor(android.graphics.Color.parseColor("#65676B"));
            }

            View llLikeCount = commentView.findViewById(R.id.ll_comment_like_count);
            TextView tvLikeCountNumber = commentView.findViewById(R.id.tv_comment_like_count_number);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llLikeCount != null && tvLikeCountNumber != null) {
                if (comment.getLikesCount() > 0) {
                    llLikeCount.setVisibility(View.VISIBLE);
                    tvLikeCountNumber.setText(String.valueOf(comment.getLikesCount()));
                } else {
                    llLikeCount.setVisibility(View.GONE);
                }
            }
            
            tvLike.setOnClickListener(v -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (currentUid == null) {
                    android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                com.google.firebase.firestore.DocumentReference commentRef = FirebaseFirestore.getInstance().collection("Posts").document(postId).collection("Comments").document(comment.getId());
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                FirebaseFirestore.getInstance().runTransaction(transaction -> {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(commentRef);
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.example.appdraw.model.Comment c = snapshot.toObject(com.example.appdraw.model.Comment.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (c != null) {
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (c.getLikedBy() == null) c.setLikedBy(new java.util.ArrayList<>());
                        boolean currentlyLiked = c.getLikedBy().contains(currentUid);
                        if (currentlyLiked) {
                            c.getLikedBy().remove(currentUid);
                            c.setLikesCount(Math.max(0, c.getLikesCount() - 1));
                        } else {
                            c.getLikedBy().add(currentUid);
                            c.setLikesCount(c.getLikesCount() + 1);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (c.getUid() != null && !c.getUid().equals(currentUid)) {
                                com.example.appdraw.utils.NotificationHelper.sendNotification(c.getUid(), "COMMENT_LIKE", "đã thích bình luận của bạn.", postId);
                            }
                        }
                        transaction.set(commentRef, c);
                        return !currentlyLiked;
                    }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    return null;
                }).addOnSuccessListener(newIsLiked -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (newIsLiked != null) {
                        if ((Boolean) newIsLiked) {
                            tvLike.setTextColor(android.graphics.Color.parseColor("#1877F2"));
                        } else {
                            tvLike.setTextColor(android.graphics.Color.parseColor("#65676B"));
                        }
                    }
                });
            });
        }

        TextView tvReply = commentView.findViewById(R.id.tv_comment_reply);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvReply != null) {
            tvReply.setOnClickListener(v -> {
                android.widget.EditText etComment = findViewById(R.id.et_comment);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (etComment != null) {
                    TextView tvName = commentView.findViewById(R.id.tv_comment_name);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    String name = tvName != null ? tvName.getText().toString() : "Người dùng";
                    
                    replyingToCommentId = isReply ? comment.getParentId() : comment.getId();
                    replyingToUserName = name;
                    
                    View llReplyStatus = findViewById(R.id.ll_reply_status);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (llReplyStatus != null) llReplyStatus.setVisibility(View.VISIBLE);
                    TextView tvReplyStatus = findViewById(R.id.tv_reply_status);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvReplyStatus != null) tvReplyStatus.setText("Đang trả lời " + name);
                    
                    etComment.setHint("Viết phản hồi...");
                    etComment.setText("");
                    etComment.requestFocus();
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imm != null) imm.showSoftInput(etComment, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String currentUidForDelete = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (currentUidForDelete != null && currentUidForDelete.equals(comment.getUid())) {
            commentView.setOnLongClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xóa bình luận")
                    .setMessage("Bạn có chắc chắn muốn xóa bình luận này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Posts").document(postId).collection("Comments").document(comment.getId())
                            .delete().addOnSuccessListener(aVoid -> {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Posts").document(postId).update("commentsCount", com.google.firebase.firestore.FieldValue.increment(-1));
                                android.widget.Toast.makeText(this, "Đã xóa bình luận", android.widget.Toast.LENGTH_SHORT).show();
                            });
                    })
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    .setNegativeButton("Hủy", null)
                    .show();
                return true;
            });
        }

        container.addView(commentView);
    }

    /**
     * Hàm loadPostDetails() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadPostDetails() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        FirebaseFirestore.getInstance().collection("Posts").document(postId)
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addSnapshotListener((doc, postError) -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (postError != null) return;
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (doc != null && doc.exists()) {
                    Post post = doc.toObject(Post.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (post != null) {
                        View includedPost = findViewById(R.id.included_post);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (includedPost != null) {
                            TextView tvContent = includedPost.findViewById(R.id.tv_post_content);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvContent != null) tvContent.setText(post.getContent());

                            ImageView ivPostImg = includedPost.findViewById(R.id.iv_post_image);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (ivPostImg != null && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                ivPostImg.setVisibility(View.VISIBLE);
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
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            } else if (ivPostImg != null) {
                                ivPostImg.setVisibility(View.GONE);
                            }

                            TextView tvLikeCount = includedPost.findViewById(R.id.tv_like_count);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvLikeCount != null) tvLikeCount.setText(String.valueOf(post.getLikesCount()));

                            TextView tvCommentCount = includedPost.findViewById(R.id.tv_comment_count);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvCommentCount != null) tvCommentCount.setText(String.valueOf(post.getCommentsCount()));
                            
                            TextView tvTime = includedPost.findViewById(R.id.tv_post_time);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvTime != null) {
                                long time = post.getCreatedAt();
                                long now = System.currentTimeMillis();
                                if (time < 1000000000000L) time *= 1000;
                                long diff = now - time;
                                if (diff < 60 * 1000) tvTime.setText("Vừa xong");
                                else if (diff < 60 * 60 * 1000) tvTime.setText((diff / (60 * 1000)) + " phút trước");
                                else if (diff < 24 * 60 * 60 * 1000) tvTime.setText((diff / (60 * 60 * 1000)) + " giờ trước");
                                else {
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                                    tvTime.setText(sdf.format(new java.util.Date(time)));
                                }
                            }

                            // Xử lý nút Theo dõi
                            TextView tvFollowStatus = includedPost.findViewById(R.id.tv_follow_status);
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            String currentUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvFollowStatus != null && currentUid != null) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (post.getUid() != null && post.getUid().equals(currentUid)) {
                                    tvFollowStatus.setVisibility(View.GONE);
                                } else {
                                    tvFollowStatus.setVisibility(View.VISIBLE);
                                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFEFEF")));
                                            tvFollowStatus.setTextColor(android.graphics.Color.parseColor("#1A1A1A"));
                                            tvFollowStatus.setTag(true);
                                        } else {
                                            tvFollowStatus.setText("Theo dõi");
                                            tvFollowStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4272D0")));
                                            tvFollowStatus.setTextColor(android.graphics.Color.WHITE);
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
                                                android.widget.Toast.makeText(this, "Đã theo dõi", android.widget.Toast.LENGTH_SHORT).show();
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
                                                android.widget.Toast.makeText(this, "Bỏ theo dõi", android.widget.Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                    });
                                }
                            }

                            // Like Logic
                            View llLike = includedPost.findViewById(R.id.ll_like);
                            ImageView ivLike = includedPost.findViewById(R.id.iv_like);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (llLike != null && ivLike != null && tvLikeCount != null) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                boolean isLiked = post.getLikedBy() != null && post.getLikedBy().contains(currentUid);
                                if (isLiked) {
                                    ivLike.setImageResource(R.drawable.ic_heart);
                                    ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
                                } else {
                                    ivLike.setImageResource(R.drawable.ic_heart);
                                    ivLike.setColorFilter(android.graphics.Color.parseColor("#888888"));
                                }

                                llLike.setOnClickListener(v -> {
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (currentUid == null) {
                                        android.widget.Toast.makeText(this, "Vui lòng đăng nhập!", android.widget.Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                    com.google.firebase.firestore.DocumentReference postRef = doc.getReference();
                                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                    FirebaseFirestore.getInstance().runTransaction(transaction -> {
                                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                        com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(postRef);
                                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                        Post p = snapshot.toObject(Post.class);
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        if (p != null) {
                                            // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                            if (p.getLikedBy() == null) p.setLikedBy(new java.util.ArrayList<>());
                                            boolean currentlyLiked = p.getLikedBy().contains(currentUid);
                                            if (currentlyLiked) {
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
                                            return !currentlyLiked;
                                        }
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        return null;
                                    }).addOnSuccessListener(newIsLiked -> {
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        if (newIsLiked != null) {
                                            if ((Boolean) newIsLiked) {
                                                ivLike.setImageResource(R.drawable.ic_heart);
                                                ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
                                                int currentCount = Integer.parseInt(tvLikeCount.getText().toString());
                                                tvLikeCount.setText(String.valueOf(currentCount + 1));
                                            } else {
                                                ivLike.setColorFilter(android.graphics.Color.parseColor("#888888"));
                                                int currentCount = Integer.parseInt(tvLikeCount.getText().toString());
                                                tvLikeCount.setText(String.valueOf(Math.max(0, currentCount - 1)));
                                            }
                                        }
                                    }).addOnFailureListener(e -> android.widget.Toast.makeText(this, "Lỗi mạng", android.widget.Toast.LENGTH_SHORT).show());
                                });
                            }

                            // Tải thông tin tác giả bài viết
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
                                            TextView tvName = includedPost.findViewById(R.id.tv_user_name);
                                            ImageView ivAvatar = includedPost.findViewById(R.id.iv_user_avatar);
                                            ImageView ivMentorBadge = includedPost.findViewById(R.id.iv_mentor_badge);
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
                        }
                    }
                }
            });
    }
}
