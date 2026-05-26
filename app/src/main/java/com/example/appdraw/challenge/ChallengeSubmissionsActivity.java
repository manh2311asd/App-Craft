package com.example.appdraw.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp ChallengeSubmissionsActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChallengeSubmissionsActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChallengeSubmissionsActivity extends AppCompatActivity {

    /**
     * Biến `challengeTitle` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String challengeTitle;
    /**
     * Biến `container` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout container;
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_submissions);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        challengeTitle = getIntent().getStringExtra("CHALLENGE_TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeTitle == null) challengeTitle = "Thử thách";

        Toolbar toolbar = findViewById(R.id.toolbar_challenge_submissions);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        container = findViewById(R.id.ll_submissions_container);
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();

        loadSubmissions();
    }

    @Override
    /**
     * Hàm onResume() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeSubmissionsActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    protected void onResume() {
        super.onResume();
        loadSubmissions();
    }

    /**
     * Hàm loadSubmissions() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadSubmissions() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null) return;
        
        db.collection("Challenge_Submissions")
            .whereEqualTo("challengeTitle", challengeTitle)
            .get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                container.removeAllViews();
                
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (queryDocumentSnapshots.isEmpty()) {
                    TextView tvEmpty = new TextView(this);
                    tvEmpty.setText("Chưa có bài dự thi nào.");
                    tvEmpty.setPadding(32, 32, 32, 32);
                    container.addView(tvEmpty);
                    return;
                }

                LayoutInflater inflater = LayoutInflater.from(this);
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    View itemView = inflater.inflate(R.layout.item_challenge_submission, container, false);
                    
                    String id = doc.getId();
                    String author = doc.getString("userName");
                    String status = doc.getString("status");
                    String imageUrl = doc.getString("imageUrl");

                    TextView tvAuthor = itemView.findViewById(R.id.tv_submission_author);
                    TextView tvStatus = itemView.findViewById(R.id.tv_submission_status);
                    ImageView ivThumb = itemView.findViewById(R.id.iv_submission_thumb);
                    MaterialButton btnGrade = itemView.findViewById(R.id.btn_grade);

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvAuthor != null) tvAuthor.setText(author != null ? author : "Học viên");
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvStatus != null) {
                        if ("GRADED".equals(status)) {
                            tvStatus.setText("Đã chấm");
                            tvStatus.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
                            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#E8F8F5"));
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (btnGrade != null) btnGrade.setText("Xem lại");
                        } else {
                            tvStatus.setText("Đang chờ chấm");
                            tvStatus.setTextColor(android.graphics.Color.parseColor("#E67E22"));
                            tvStatus.setBackgroundColor(android.graphics.Color.parseColor("#FEF5E7"));
                        }
                    }

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ivThumb != null && imageUrl != null && !imageUrl.isEmpty()) {
                        byte[] decodedString = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(decodedString).centerCrop().into(ivThumb);
                    }

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (btnGrade != null) {
                        btnGrade.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(this, GradeSubmissionActivity.class);
                            intent.putExtra("SUBMISSION_ID", id);
                            startActivity(intent);
                        });
                    }
                    
                    container.addView(itemView);
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi tải danh sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
