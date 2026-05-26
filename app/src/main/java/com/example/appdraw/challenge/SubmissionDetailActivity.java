package com.example.appdraw.challenge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.example.appdraw.model.Comment;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp SubmissionDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file SubmissionDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class SubmissionDetailActivity extends AppCompatActivity {
    /**
     * Biến `submissionId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String submissionId;
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
     * Biến `llCommentsContainer` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private LinearLayout llCommentsContainer;
    /**
     * Biến `etComment` lưu dữ liệu/trạng thái quan trọng kiểu EditText, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etComment;
    /**
     * Biến `btnSendComment` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private ImageView btnSendComment;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission_detail);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        currentUid = user != null ? user.getUid() : null;
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        submissionId = getIntent().getStringExtra("SUBMISSION_ID");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (submissionId == null) {
            Toast.makeText(this, "Không tìm thấy bài dự thi", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar_submission_detail);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        llCommentsContainer = findViewById(R.id.ll_comments_container);
        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);

        loadSubmissionDetails();
        loadComments();

        btnSendComment.setOnClickListener(v -> postComment());
    }

    /**
     * Hàm loadSubmissionDetails() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadSubmissionDetails() {
        View includedView = findViewById(R.id.included_submission);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (includedView == null) return;

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Challenge_Submissions").document(submissionId).addSnapshotListener((doc, e) -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (e != null || doc == null || !doc.exists()) return;

            String authorName = doc.getString("userName");
            String authorAvatar = doc.getString("userAvatar");
            String imageUrl = doc.getString("imageUrl");
            String status = doc.getString("status");
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            long commentsCount = doc.getLong("commentsCount") != null ? doc.getLong("commentsCount") : 0;

            // Header Count
            TextView tvHeader = findViewById(R.id.tv_comment_header);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvHeader != null) {
                tvHeader.setText("Bình luận (" + commentsCount + ")");
            }

            // Author text
            TextView tvName = includedView.findViewById(R.id.tv_public_user_name);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvName != null) tvName.setText(authorName != null ? authorName : "Học viên");

            // Avatar
            ShapeableImageView ivAvatar = includedView.findViewById(R.id.iv_public_user_avatar);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivAvatar != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (authorAvatar != null && !authorAvatar.isEmpty()) {
                    if (authorAvatar.startsWith("data:image")) {
                        byte[] decodedString = android.util.Base64.decode(authorAvatar.split(",")[1], android.util.Base64.DEFAULT);
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(decodedString).circleCrop().into(ivAvatar);
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(authorAvatar).circleCrop().into(ivAvatar);
                    }
                }
            }

            // Artwork
            ImageView ivArtwork = includedView.findViewById(R.id.iv_public_artwork);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivArtwork != null && imageUrl != null && !imageUrl.isEmpty()) {
                byte[] decodedString = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(this).asBitmap().load(decodedString).fitCenter().into(ivArtwork);
            }

            // Score logic
            if ("GRADED".equals(status)) {
                View scoreLayout = includedView.findViewById(R.id.ll_public_score);
                TextView tvScore = includedView.findViewById(R.id.tv_public_score);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (scoreLayout != null) scoreLayout.setVisibility(View.VISIBLE);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvScore != null) {
                    Number score = doc.getDouble("score");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    tvScore.setText((score != null ? score.intValue() : 0) + "/100");
                }
            }

            android.widget.LinearLayout llFeedbacks = includedView.findViewById(R.id.ll_public_feedbacks);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llFeedbacks != null) {
                llFeedbacks.removeAllViews();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                java.util.List<Map<String, Object>> grades = (java.util.List<Map<String, Object>>) doc.get("grades");
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (grades != null && !grades.isEmpty()) {
                    llFeedbacks.setVisibility(View.VISIBLE);
                    TextView tvFbTitle = new TextView(this);
                    tvFbTitle.setText("Góc nhận xét của Mentor:");
                    tvFbTitle.setTextColor(Color.parseColor("#E67E22"));
                    tvFbTitle.setTextSize(13);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    tvFbTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvFbTitle.setPadding(0, 0, 0, 4);
                    llFeedbacks.addView(tvFbTitle);
                    
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    for (Map<String, Object> g : grades) {
                        String mName = (String) g.get("mentorName");
                        Number mScore = (Number) g.get("score");
                        String mFeedback = (String) g.get("feedback");
                        
                        TextView tvFb = new TextView(this);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvFb.setText("• " + mName + " (" + (mScore != null ? mScore.intValue() : 0) + " điểm): \"" + mFeedback + "\"");
                        tvFb.setTextColor(Color.parseColor("#444444"));
                        tvFb.setTextSize(13);
                        tvFb.setPadding(0, 2, 0, 6);
                        
                        llFeedbacks.addView(tvFb);
                    }
                } else if ("GRADED".equals(status)) {
                    // Fallback tương thích dữ liệu cũ
                    String existingFeedback = doc.getString("feedback");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (existingFeedback != null && !existingFeedback.isEmpty()) {
                        llFeedbacks.setVisibility(View.VISIBLE);
                        TextView tvFbTitle = new TextView(this);
                        tvFbTitle.setText("Khuyến nghị từ Mentor:");
                        tvFbTitle.setTextColor(Color.parseColor("#E67E22"));
                        tvFbTitle.setTextSize(13);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvFbTitle.setTypeface(null, android.graphics.Typeface.BOLD);
                        tvFbTitle.setPadding(0, 0, 0, 4);
                        llFeedbacks.addView(tvFbTitle);
                        
                        TextView tvFb = new TextView(this);
                        tvFb.setText("\"" + existingFeedback + "\"");
                        tvFb.setTextColor(Color.parseColor("#444444"));
                        tvFb.setTextSize(13);
                        tvFb.setPadding(0, 2, 0, 6);
                        llFeedbacks.addView(tvFb);
                    } else {
                        llFeedbacks.setVisibility(View.GONE);
                    }
                } else {
                    llFeedbacks.setVisibility(View.GONE);
                }
            }

            // Comment count inside card
            TextView tvInnerCommentCount = includedView.findViewById(R.id.tv_public_comment_count);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvInnerCommentCount != null) tvInnerCommentCount.setText(String.valueOf(commentsCount));

            // Like logic
            View btnLike = includedView.findViewById(R.id.btn_public_like);
            ImageView ivLikeIcon = includedView.findViewById(R.id.iv_public_like_icon);
            TextView tvLikeCount = includedView.findViewById(R.id.tv_public_like_count);

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            long likesCount = doc.getLong("likesCount") != null ? doc.getLong("likesCount") : 0;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvLikeCount != null) tvLikeCount.setText(String.valueOf(likesCount));

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (btnLike != null) {
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                List<String> likedBy = (List<String>) doc.get("likedBy");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                boolean initiallyLiked = likedBy != null && likedBy.contains(currentUid);

                if (initiallyLiked) {
                    ivLikeIcon.setColorFilter(Color.parseColor("#E74C3C")); // Red
                    btnLike.setTag(true);
                } else {
                    ivLikeIcon.setColorFilter(Color.parseColor("#888888")); // Gray
                    btnLike.setTag(false);
                }

                btnLike.setOnClickListener(v -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (currentUid == null || currentUid.isEmpty()) return;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    boolean isLiked = (v.getTag() != null && (boolean)v.getTag());

                    try {
                        long currentUiCount = Long.parseLong(tvLikeCount.getText().toString());
                        if (!isLiked) {
                            ivLikeIcon.setColorFilter(Color.parseColor("#E74C3C"));
                            tvLikeCount.setText(String.valueOf(currentUiCount + 1));
                            v.setTag(true);

                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Challenge_Submissions").document(doc.getId()).update(
                                    "likedBy", FieldValue.arrayUnion(currentUid),
                                    "likesCount", FieldValue.increment(1)
                            );
                        } else {
                            ivLikeIcon.setColorFilter(Color.parseColor("#888888"));
                            tvLikeCount.setText(String.valueOf(Math.max(0, currentUiCount - 1)));
                            v.setTag(false);

                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Challenge_Submissions").document(doc.getId()).update(
                                    "likedBy", FieldValue.arrayRemove(currentUid),
                                    "likesCount", FieldValue.increment(-1)
                            );
                        }
                    } catch (Exception ignored) {}
                });
            }
        });
    }

    /**
     * Hàm loadComments() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadComments() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Challenge_Submissions").document(submissionId).collection("Comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener((value, error) -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (error != null) return;
                    llCommentsContainer.removeAllViews();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (value != null) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (QueryDocumentSnapshot doc : value) {
                            Comment comment = doc.toObject(Comment.class);
                            View commentView = getLayoutInflater().inflate(R.layout.item_comment, llCommentsContainer, false);

                            TextView tvContent = commentView.findViewById(R.id.tv_comment_content);
                            TextView tvName = commentView.findViewById(R.id.tv_comment_name);
                            ImageView ivAvatar = commentView.findViewById(R.id.iv_comment_avatar);

                            tvContent.setText(comment.getContent());

                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Users").document(comment.getUid()).get().addOnSuccessListener(userDoc -> {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (userDoc.exists()) {
                                    String role = userDoc.getString("role");
                                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                    Map<String, Object> profile = (Map<String, Object>) userDoc.get("profile");
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (profile != null) {
                                        String fullName = (String) profile.get("fullName");
                                        if ("mentor".equals(role)) {
                                            tvName.setText(fullName + " (Mentor)");
                                            tvName.setTextColor(Color.parseColor("#E67E22")); // Mentor color
                                        } else {
                                            tvName.setText(fullName);
                                            tvName.setTextColor(Color.parseColor("#1A1A1A"));
                                        }

                                        String avatarUrl = (String) profile.get("avatarUrl");
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
                            });
                            llCommentsContainer.addView(commentView);
                        }
                    }
                });
    }

    /**
     * Hàm postComment() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void postComment() {
        String text = etComment.getText().toString().trim();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (text.isEmpty() || currentUid == null) return;

        btnSendComment.setEnabled(false);
        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment(commentId, currentUid, text, System.currentTimeMillis());

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        DocumentReference submissionRef = db.collection("Challenge_Submissions").document(submissionId);

        db.runTransaction(transaction -> {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            DocumentSnapshot snapshot = transaction.get(submissionRef);
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (snapshot.exists()) {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                long currentCount = snapshot.getLong("commentsCount") != null ? snapshot.getLong("commentsCount") : 0;
                transaction.update(submissionRef, "commentsCount", currentCount + 1);
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                transaction.set(submissionRef.collection("Comments").document(commentId), comment);
            }
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            return null;
        }).addOnSuccessListener(aVoid -> {
            etComment.setText("");
            btnSendComment.setEnabled(true);
        }).addOnFailureListener(e -> {
            btnSendComment.setEnabled(true);
            Toast.makeText(this, "Lỗi gửi bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
