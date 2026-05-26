package com.example.appdraw.challenge;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.Calendar;

/**
 * Màn hình Tạo Thử thách mới dành cho Mentor (UC-14).
 * Người thực hiện: Đặng Thị Hồng Vân.
 * Xử lý nhập thông tin, luật lệ thi và lưu Thử thách lên hệ thống Firestore.
 */
/**
 * Lớp CreateChallengeActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file CreateChallengeActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class CreateChallengeActivity extends AppCompatActivity {

    /**
     * Biến `ivSelectedImage` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivSelectedImage;
    /**
     * Biến `llPlaceholder` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout llPlaceholder;
    /**
     * Biến `selectedImageBase64` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String selectedImageBase64 = null;

    /**
     * Biến `imagePickerLauncher` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (uri != null) {
                    ivSelectedImage.setImageURI(uri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                    llPlaceholder.setVisibility(View.GONE);
                    
                    try {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        android.graphics.Bitmap bitmap = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            android.graphics.ImageDecoder.Source source = android.graphics.ImageDecoder.createSource(getContentResolver(), uri);
                            bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        }
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (bitmap != null) {
                            float aspectRatio = (float) bitmap.getHeight() / bitmap.getWidth();
                            android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, 400, (int)(400 * aspectRatio), true);
                            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] imageBytes = baos.toByteArray();
                            selectedImageBase64 = "data:image/jpeg;base64," + android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        android.widget.Toast.makeText(CreateChallengeActivity.this, "Lỗi khi tải ảnh. Vui lòng chọn ảnh khác!", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        Toolbar toolbar = findViewById(R.id.toolbar_create_challenge);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivSelectedImage = findViewById(R.id.iv_selected_image);
        llPlaceholder = findViewById(R.id.ll_placeholder_image);
        View cardAddImage = findViewById(R.id.card_add_image);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cardAddImage != null) {
            cardAddImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvStartDate != null) {
            tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvEndDate != null) {
            tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));
        }

        MaterialButton btnCreate = findViewById(R.id.btn_create_challenge_submit);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
                EditText edtTitle = findViewById(R.id.edt_challenge_title);
                EditText edtRules = findViewById(R.id.edt_challenge_rules);
                EditText edtRewards = findViewById(R.id.edt_challenge_rewards);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String title = edtTitle != null ? edtTitle.getText().toString() : "Thử thách mới";
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String rules = edtRules != null ? edtRules.getText().toString() : "";
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String rewards = edtRewards != null ? edtRewards.getText().toString() : "";
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String startD = tvStartDate != null ? tvStartDate.getText().toString() : "";
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String endD = tvEndDate != null ? tvEndDate.getText().toString() : "";
                
                String dateStr = startD + " - " + endD;
                long endTimeMillis = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000; // Default 1 week
                
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d = sdf.parse(endD);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (d != null) endTimeMillis = d.getTime();
                } catch (Exception e) {}

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> challengeData = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("title", title);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("rules", rules);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("rewards", rewards);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("dateStr", dateStr);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("participantsCount", "0 đã tham gia");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                challengeData.put("endTimeMillis", endTimeMillis);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (selectedImageBase64 != null) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("imageUrl", selectedImageBase64);
                } else {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("imageRes", "ve_hoa_mau_nuoc");
                }

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (user != null) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("authorId", user.getUid());
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("author", user.getDisplayName());
                } else {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("authorId", "mentor123");
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    challengeData.put("author", "Mentor AI");
                }

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Challenges")
                    .add(challengeData)
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thử thách đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi tạo thử thách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            });
        }
    }

    /**
     * Hàm showDatePicker() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param targetTextView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            targetTextView.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
