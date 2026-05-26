package com.example.appdraw.challenge;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp UserScoreDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file UserScoreDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class UserScoreDetailActivity extends AppCompatActivity {

    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `challengeTitle` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String challengeTitle;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_score_detail);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        challengeTitle = getIntent().getStringExtra("CHALLENGE_TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeTitle == null) {
            Toast.makeText(this, "Không tìm thấy thử thách", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_user_score);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        loadSubmissionDetails();
    }

    /**
     * Hàm loadSubmissionDetails() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadSubmissionDetails() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null) return;

        db.collection("Challenge_Submissions")
            .whereEqualTo("userId", user.getUid())
            .whereEqualTo("challengeTitle", challengeTitle)
            .get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(this, "Chưa tìm thấy bài nộp của bạn", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                QueryDocumentSnapshot doc = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                
                // Content
                String imageUrl = doc.getString("imageUrl");
                String note = doc.getString("note");
                
                ImageView ivArtwork = findViewById(R.id.iv_score_artwork);
                TextView tvNote = findViewById(R.id.tv_score_artwork_note);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (note != null && !note.trim().isEmpty()) {
                    tvNote.setText(note);
                } else {
                    tvNote.setText("Không có mô tả.");
                }

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    byte[] decodedString = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    Glide.with(this).asBitmap().load(decodedString).fitCenter().into(ivArtwork);
                }

                // Average Score
                Number scoreObj = doc.getDouble("score");
                TextView tvScoreAvg = findViewById(R.id.tv_score_average);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (scoreObj != null) {
                    tvScoreAvg.setText(String.valueOf(scoreObj.intValue()));
                }

                // Grades List
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                List<Map<String, Object>> grades = (List<Map<String, Object>>) doc.get("grades");
                LinearLayout container = findViewById(R.id.ll_mentor_grades_container);
                TextView tvNoFeedbacks = findViewById(R.id.tv_no_feedbacks);

                container.removeAllViews();
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (grades == null || grades.isEmpty()) {
                    tvNoFeedbacks.setVisibility(android.view.View.VISIBLE);
                } else {
                    tvNoFeedbacks.setVisibility(android.view.View.GONE);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    for (Map<String, Object> g : grades) {
                        String mName = (String) g.get("mentorName");
                        Number mScore = (Number) g.get("score");
                        String mFeedback = (String) g.get("feedback");

                        // Actually, I should just build a clean Layout programmatically to avoid creating too many XML files.
                        
                        LinearLayout itemLayout = new LinearLayout(this);
                        itemLayout.setOrientation(LinearLayout.VERTICAL);
                        itemLayout.setBackgroundResource(android.R.color.white);
                        
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0, 0, 0, 32);
                        itemLayout.setLayoutParams(params);
                        itemLayout.setPadding(32, 24, 32, 24);
                        
                        // Border radius and elevation fallback natively
                        itemLayout.setBackgroundColor(android.graphics.Color.WHITE);

                        TextView tvMentor = new TextView(this);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvMentor.setText(mName != null ? mName : "Mentor");
                        tvMentor.setTextColor(android.graphics.Color.parseColor("#4272D0"));
                        tvMentor.setTextSize(14f);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvMentor.setTypeface(null, android.graphics.Typeface.BOLD);
                        
                        LinearLayout rowScore = new LinearLayout(this);
                        rowScore.setOrientation(LinearLayout.HORIZONTAL);
                        rowScore.setPadding(0, 8, 0, 8);
                        
                        TextView tvScoreLabel = new TextView(this);
                        tvScoreLabel.setText("Điểm: ");
                        tvScoreLabel.setTextColor(android.graphics.Color.parseColor("#333333"));
                        tvScoreLabel.setTextSize(14f);
                        
                        TextView tvScoreVal = new TextView(this);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvScoreVal.setText((mScore != null ? mScore.intValue() : 0) + "/100");
                        tvScoreVal.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
                        tvScoreVal.setTextSize(14f);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvScoreVal.setTypeface(null, android.graphics.Typeface.BOLD);
                        
                        rowScore.addView(tvScoreLabel);
                        rowScore.addView(tvScoreVal);
                        
                        TextView tvFb = new TextView(this);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvFb.setText("\"" + (mFeedback != null ? mFeedback : "Không có nhận xét") + "\"");
                        tvFb.setTextColor(android.graphics.Color.parseColor("#555555"));
                        tvFb.setTextSize(14f);
                        tvFb.setPadding(0, 4, 0, 0);

                        itemLayout.addView(tvMentor);
                        itemLayout.addView(rowScore);
                        itemLayout.addView(tvFb);

                        // Card wrapper for shadow
                        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
                        card.setRadius(24f);
                        card.setCardElevation(4f);
                        card.setUseCompatPadding(true);
                        card.addView(itemLayout);

                        container.addView(card);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi tải kết quả", Toast.LENGTH_SHORT).show();
            });
    }
}
