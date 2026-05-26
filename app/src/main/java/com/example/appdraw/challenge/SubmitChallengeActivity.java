package com.example.appdraw.challenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.appdraw.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Màn hình Nộp bài dự thi Thử thách (UC-13).
 * Người thực hiện: Cao Đức Mạnh.
 * Xử lý tải ảnh (Base64) hoặc link bài thi và đẩy lên hệ thống Firestore.
 */
/**
 * Lớp SubmitChallengeActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file SubmitChallengeActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class SubmitChallengeActivity extends AppCompatActivity {

    /**
     * Biến `ivArtworkPreview` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private ImageView ivArtworkPreview;
    /**
     * Biến `llUploadPlaceholder` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout llUploadPlaceholder;
    /**
     * Biến `selectedImageUri` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private Uri selectedImageUri = null;
    /**
     * Biến `challengeTitle` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String challengeTitle;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivArtworkPreview.setImageURI(selectedImageUri);
                    ivArtworkPreview.setVisibility(View.VISIBLE);
                    llUploadPlaceholder.setVisibility(View.GONE);
                }
            }
    );

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_challenge);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        challengeTitle = getIntent().getStringExtra("CHALLENGE_TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeTitle == null) {
            challengeTitle = "Thử thách";
        }

        TextView tvTitle = findViewById(R.id.tv_submit_challenge_title);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvTitle != null) {
            tvTitle.setText(challengeTitle);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_submit_challenge);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        ivArtworkPreview = findViewById(R.id.iv_artwork_preview);
        llUploadPlaceholder = findViewById(R.id.ll_upload_placeholder);

        View cardUpload = findViewById(R.id.card_upload_artwork);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cardUpload != null) {
            cardUpload.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                galleryLauncher.launch(intent);
            });
        }

        EditText edtNote = findViewById(R.id.edt_artwork_note);
        MaterialButton btnSubmit = findViewById(R.id.btn_submit_artwork);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (selectedImageUri == null) {
                    Toast.makeText(this, "Vui lòng tải ảnh tác phẩm lên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (user == null) {
                    Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSubmit.setEnabled(false);
                Toast.makeText(this, "Đang nộp bài...", Toast.LENGTH_SHORT).show();

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String note = edtNote != null ? edtNote.getText().toString().trim() : "";
                
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(user.getUid()).get().addOnSuccessListener(userDoc -> {
                    String authorName = "Học viên";
                    String userAvatar = "";
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (userDoc.exists() && userDoc.contains("profile")) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        Map<String, Object> profile = (Map<String, Object>) userDoc.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null) {
                            if (profile.containsKey("fullName")) authorName = (String) profile.get("fullName");
                            if (profile.containsKey("avatarUrl")) userAvatar = (String) profile.get("avatarUrl");
                        }
                    }

                    String finalImageUrl = "";
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        
                        // Resize ảnh để tránh tràn RAM (OOM) và vượt giới hạn dòng 1MB của Firestore
                        int MAX_SIZE = 1200;
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (width > MAX_SIZE || height > MAX_SIZE) {
                            float ratio = Math.min((float) MAX_SIZE / width, (float) MAX_SIZE / height);
                            width = Math.round((float) ratio * width);
                            height = Math.round((float) ratio * height);
                            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                        }

                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        // Nén JPEG chất lượng 70%
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, buffer);
                        byte[] fileBytes = buffer.toByteArray();
                        String base64Image = Base64.encodeToString(fileBytes, Base64.DEFAULT);
                        finalImageUrl = "data:image/jpeg;base64," + base64Image;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi xử lý ảnh, vui lòng thử ảnh khác nhỏ hơn!", Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                        return; // Ngăn chặn việc gửi lên Firestore nếu không có ảnh
                    }

                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    Map<String, Object> submissionData = new HashMap<>();
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("userId", user.getUid());
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("userName", authorName);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("userAvatar", userAvatar);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("note", note);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("imageUrl", finalImageUrl);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("status", "PENDING"); // Waiting for grade
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("score", 0);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("feedback", "");
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    submissionData.put("timestamp", System.currentTimeMillis());

                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // To uniquely identify the challenge document, we can use title as ID or query by title. 
                    // Let's assume standard Challenges structure stores title as ID or we query it.
                    // To keep it safe and since we don't have ID, we'll store in Users/uid/submissions and also globally.
                    // Let's store globally in Challenges_Submissions collection.
                    
                    db.collection("Challenge_Submissions")
                        .add(submissionData)
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        .addOnSuccessListener(documentReference -> {
                            // Link it
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            documentReference.update("challengeTitle", challengeTitle);
                            
                            // Update local user status
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            Map<String, Object> localStatus = new HashMap<>();
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            localStatus.put("status", "SUBMITTED");
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            localStatus.put("submissionId", documentReference.getId());
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Users").document(user.getUid()).collection("joinedChallenges").document(challengeTitle).set(localStatus);

                            Toast.makeText(this, "Nộp bài thành công! Đang chờ chấm điểm.", Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        })
                        .addOnFailureListener(e -> btnSubmit.setEnabled(true));
                });
            });
        }
    }
}
