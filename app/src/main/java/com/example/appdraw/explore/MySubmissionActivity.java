package com.example.appdraw.explore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdraw.R;
import com.example.appdraw.explore.HomeworkActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp MySubmissionActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file MySubmissionActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class MySubmissionActivity extends AppCompatActivity {

    /**
     * Biến `lessonTitle` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String lessonTitle;
    /**
     * Biến `ivMySubmission` lưu dữ liệu/trạng thái quan trọng kiểu ImageView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ImageView ivMySubmission;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_submission);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_my_submission);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivMySubmission = findViewById(R.id.iv_my_submission);
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        lessonTitle = getIntent().getStringExtra("LESSON_TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (lessonTitle == null) {
            lessonTitle = "Unknown Lesson";
        }

        findViewById(R.id.btn_study_other).setOnClickListener(v -> finish());
        findViewById(R.id.btn_redraw).setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(MySubmissionActivity.this, HomeworkActivity.class);
            intent.putExtra("LESSON_TITLE", lessonTitle);
            startActivity(intent);
            finish();
        });
        


        fetchMySubmission();
    }

    /**
     * Hàm fetchMySubmission() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchMySubmission() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(uid)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .collection("lessonProgress").document(lessonTitle)
                .get()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addOnSuccessListener(documentSnapshot -> {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (documentSnapshot.exists()) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String base64Url = documentSnapshot.getString("imageUrl");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (base64Url != null && base64Url.startsWith("data:image")) {
                            String cleanBase64 = base64Url.substring(base64Url.indexOf(",") + 1);
                            byte[] decodedString = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT);
                            android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ivMySubmission.setImageBitmap(decodedByte);
                            
                            // Handle AI Auto Grading
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String existingFeedback = documentSnapshot.getString("aiFeedback");
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String existingTip = documentSnapshot.getString("aiTip");
                            
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (existingFeedback != null && !existingFeedback.isEmpty()) {
                                // Already graded, just show it
                                findViewById(R.id.ll_ai_feedback_container).setVisibility(android.view.View.VISIBLE);
                                ((android.widget.TextView) findViewById(R.id.tv_ai_feedback_text)).setText(existingFeedback);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                ((android.widget.TextView) findViewById(R.id.tv_ai_tip_text)).setText(existingTip != null ? existingTip : "");
                            } else {
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                // Not graded yet, call Gemini
                                findViewById(R.id.pb_ai_loading).setVisibility(android.view.View.VISIBLE);
                                findViewById(R.id.tv_ai_loading_text).setVisibility(android.view.View.VISIBLE);
                                
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                GeminiVisionService geminiService = new GeminiVisionService();
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                geminiService.gradeArtwork(lessonTitle, base64Url, new GeminiVisionService.GeminiCallback() {
                                    @Override
                                    /**
                                     * Hàm onSuccess() thực hiện một phần xử lý trong luồng chức năng của lớp MySubmissionActivity.
                                     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                                     * @param feedback tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                                     * @param tip tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                                     */
                                    public void onSuccess(String feedback, String tip) {
                                        findViewById(R.id.pb_ai_loading).setVisibility(android.view.View.GONE);
                                        findViewById(R.id.tv_ai_loading_text).setVisibility(android.view.View.GONE);
                                        
                                        findViewById(R.id.ll_ai_feedback_container).setVisibility(android.view.View.VISIBLE);
                                        ((android.widget.TextView) findViewById(R.id.tv_ai_feedback_text)).setText(feedback);
                                        ((android.widget.TextView) findViewById(R.id.tv_ai_tip_text)).setText(tip);
                                        
                                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                        // Save to Firestore to avoid calling API again
                                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                        db.collection("Users").document(uid)
                                          // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                          .collection("lessonProgress").document(lessonTitle)
                                          .update("aiFeedback", feedback, "aiTip", tip);
                                    }

                                    @Override
                                    /**
                                     * Hàm onError() thực hiện một phần xử lý trong luồng chức năng của lớp MySubmissionActivity.
                                     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                                     * @param error tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                                     */
                                    public void onError(String error) {
                                        findViewById(R.id.pb_ai_loading).setVisibility(android.view.View.GONE);
                                        findViewById(R.id.tv_ai_loading_text).setVisibility(android.view.View.GONE);
                                        Toast.makeText(MySubmissionActivity.this, "Chấm điểm tạm thời bị lỗi: " + error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
