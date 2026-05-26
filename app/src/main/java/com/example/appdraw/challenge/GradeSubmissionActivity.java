package com.example.appdraw.challenge;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp GradeSubmissionActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file GradeSubmissionActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class GradeSubmissionActivity extends AppCompatActivity {

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
     * Biến `submissionUserId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String submissionUserId;
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
        setContentView(R.layout.activity_grade_submission);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        submissionId = getIntent().getStringExtra("SUBMISSION_ID");
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar_grade_submission);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (submissionId == null) {
            Toast.makeText(this, "Lỗi ID bài nộp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSubmissionDetails();
        
        MaterialButton btnSubmitGrade = findViewById(R.id.btn_submit_grade);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSubmitGrade != null) {
            btnSubmitGrade.setOnClickListener(v -> submitGrade());
        }
    }

    /**
     * Hàm loadSubmissionDetails() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadSubmissionDetails() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Challenge_Submissions").document(submissionId).get()
            .addOnSuccessListener(doc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (!doc.exists()) return;
                
                submissionUserId = doc.getString("userId");
                challengeTitle = doc.getString("challengeTitle");
                
                String userName = doc.getString("userName");
                String userAvatar = doc.getString("userAvatar");
                String note = doc.getString("note");
                String imageUrl = doc.getString("imageUrl");
                
                // Pre-fill existing grades if any
                String existingStatus = doc.getString("status");
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                String currentUserUid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                java.util.List<Map<String, Object>> grades = (java.util.List<Map<String, Object>>) doc.get("grades");
                
                android.widget.LinearLayout llPreviousFeedbacks = findViewById(R.id.ll_previous_mentor_feedbacks);
                android.widget.LinearLayout llPreviousFeedbacksContainer = findViewById(R.id.ll_previous_feedbacks_container);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llPreviousFeedbacks != null) llPreviousFeedbacks.removeAllViews();
                
                boolean hasOtherFeedbacks = false;
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (grades != null) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    for (Map<String, Object> g : grades) {
                        String mId = (String) g.get("mentorId");
                        String mName = (String) g.get("mentorName");
                        Number mScore = (Number) g.get("score");
                        String mFeedback = (String) g.get("feedback");
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (currentUserUid != null && currentUserUid.equals(mId)) {
                            // Của chính mình thì tự điền vào Form
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (mScore != null) {
                                ((EditText) findViewById(R.id.edt_grade_score)).setText(String.valueOf(mScore.intValue()));
                            }
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (mFeedback != null) {
                                ((EditText) findViewById(R.id.edt_grade_feedback)).setText(mFeedback);
                            }
                        } else {
                            // Của Mentor khác -> add vào View
                            hasOtherFeedbacks = true;
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (llPreviousFeedbacks != null) {
                                TextView tvFb = new TextView(this);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                tvFb.setText("• " + mName + " chấm " + (mScore != null ? mScore.intValue() : 0) + " điểm: \"" + mFeedback + "\"");
                                tvFb.setTextColor(android.graphics.Color.parseColor("#444444"));
                                tvFb.setTextSize(13);
                                tvFb.setPadding(0, 4, 0, 4);
                                llPreviousFeedbacks.addView(tvFb);
                            }
                        }
                    }
                } else if ("GRADED".equals(existingStatus)) {
                    // Fallback tương thích ngược (nếu dữ liệu cũ)
                    Number scoreObj = doc.getDouble("score");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (scoreObj != null) {
                        ((EditText) findViewById(R.id.edt_grade_score)).setText(String.valueOf(scoreObj.intValue()));
                    }
                    String existingFeedback = doc.getString("feedback");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (existingFeedback != null) {
                        ((EditText) findViewById(R.id.edt_grade_feedback)).setText(existingFeedback);
                    }
                }
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (hasOtherFeedbacks && llPreviousFeedbacksContainer != null) {
                    llPreviousFeedbacksContainer.setVisibility(android.view.View.VISIBLE);
                }
                
                android.widget.LinearLayout llProfile = findViewById(R.id.ll_grade_user_profile);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (llProfile != null) {
                     llProfile.setOnClickListener(v -> {
                         // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                         android.content.Intent intent = new android.content.Intent(this, com.example.appdraw.community.OtherUserProfileActivity.class);
                         intent.putExtra("USER_ID", submissionUserId);
                         startActivity(intent);
                     });
                }

                TextView tvName = findViewById(R.id.tv_grade_user_name);
                TextView tvTitle = findViewById(R.id.tv_grade_challenge_title);
                TextView tvNote = findViewById(R.id.tv_grade_artwork_note);
                ImageView ivAvatar = findViewById(R.id.iv_grade_user_avatar);
                ImageView ivArtwork = findViewById(R.id.iv_grade_artwork);

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvName != null) tvName.setText(userName);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvTitle != null) tvTitle.setText("Thử thách: " + challengeTitle);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvNote != null) tvNote.setText((note != null && !note.trim().isEmpty()) ? note : "Không có user mô tả.");

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (ivArtwork != null && imageUrl != null && !imageUrl.isEmpty()) {
                    byte[] decodedString = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    Glide.with(this).asBitmap().load(decodedString).fitCenter().into(ivArtwork);
                }
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (ivAvatar != null) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (userAvatar != null && !userAvatar.isEmpty()) {
                        if (userAvatar.startsWith("data:image")) {
                             byte[] decodedString = android.util.Base64.decode(userAvatar.split(",")[1], android.util.Base64.DEFAULT);
                             // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                             Glide.with(this).load(decodedString).circleCrop().into(ivAvatar);
                        } else {
                             // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                             Glide.with(this).load(userAvatar).circleCrop().into(ivAvatar);
                        }
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
                    }
                }
            });
    }

    /**
     * Hàm submitGrade() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void submitGrade() {
        EditText edtScore = findViewById(R.id.edt_grade_score);
        EditText edtFeedback = findViewById(R.id.edt_grade_feedback);
        
        String scoreStr = edtScore.getText().toString().trim();
        String feedback = edtFeedback.getText().toString().trim();
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (scoreStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int score = 0;
        try {
            score = Integer.parseInt(scoreStr);
            if (score < 0 || score > 100) {
                 Toast.makeText(this, "Điểm phải từ 0 - 100", Toast.LENGTH_SHORT).show();
                 return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Điểm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialButton btnSubmitGrade = findViewById(R.id.btn_submit_grade);
        btnSubmitGrade.setEnabled(false);
        btnSubmitGrade.setText("Đang lưu...");

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (currentUser == null) return;
        
        final int finalScore = score;
        final String finalFeedback = feedback;
        
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(currentUser.getUid()).get()
            .addOnSuccessListener(mentorDoc -> {
                String mentorName = "Mentor";
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (mentorDoc.exists()) {
                     // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                     java.util.Map<String, Object> profile = (java.util.Map<String, Object>) mentorDoc.get("profile");
                     // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                     if (profile != null && profile.containsKey("fullName")) {
                          mentorName = (String) profile.get("fullName");
                     }
                }
                
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                Map<String, Object> gradeEntry = new HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                gradeEntry.put("mentorId", currentUser.getUid());
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                gradeEntry.put("mentorName", mentorName);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                gradeEntry.put("score", finalScore);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                gradeEntry.put("feedback", finalFeedback);
                
                db.runTransaction(transaction -> {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentReference submissionRef = db.collection("Challenge_Submissions").document(submissionId);
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    com.google.firebase.firestore.DocumentSnapshot snapshot = transaction.get(submissionRef);
                    
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (!snapshot.exists()) return null;
                    
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.List<Map<String, Object>> gradesList = (java.util.List<Map<String, Object>>) snapshot.get("grades");
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    java.util.List<Map<String, Object>> grades = new java.util.ArrayList<>();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (gradesList != null) {
                        grades.addAll(gradesList);
                    }
                    
                    // Xóa điểm cũ của chính mentor này (nếu họ sửa điểm)
                    for (int i = 0; i < grades.size(); i++) {
                        if (currentUser.getUid().equals(grades.get(i).get("mentorId"))) {
                            grades.remove(i);
                            break;
                        }
                    }
                    
                    grades.add(gradeEntry);
                    
                    // Tính điểm trung bình mới
                    long totalScore = 0;
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    for (Map<String, Object> g : grades) {
                        totalScore += ((Number) g.get("score")).longValue();
                    }
                    long newAverageScore = totalScore / grades.size();
                    
                    transaction.update(submissionRef, "grades", grades);
                    transaction.update(submissionRef, "score", newAverageScore);
                    transaction.update(submissionRef, "status", "GRADED");
                    return newAverageScore;
                }).addOnSuccessListener(avgScore -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (submissionUserId != null && challengeTitle != null) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        Map<String, Object> userUpdates = new HashMap<>();
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        userUpdates.put("status", "GRADED");
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        userUpdates.put("score", avgScore); // Save average
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        userUpdates.put("feedback", finalFeedback); 
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Users").document(submissionUserId)
                          // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                          .collection("joinedChallenges").document(challengeTitle)
                          .update(userUpdates);
                    }
                    
                    Toast.makeText(this, "Chấm điểm thành công (TB: " + avgScore + " điểm)!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                     Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                     btnSubmitGrade.setEnabled(true);
                     btnSubmitGrade.setText("HOÀN TẤT CHẤM ĐIỂM");
                });
            });
    }
}
