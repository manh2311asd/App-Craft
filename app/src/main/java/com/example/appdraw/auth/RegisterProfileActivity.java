package com.example.appdraw.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdraw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp RegisterProfileActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file RegisterProfileActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class RegisterProfileActivity extends AppCompatActivity {
    
    /**
     * Biến `etBio` lưu dữ liệu/trạng thái quan trọng kiểu EditText etFullName,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etFullName, etBio;
    /**
     * Biến `ivAvatar` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivAvatar;
    /**
     * Biến `layoutLoading` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View layoutLoading;
    /**
     * Biến `selectedImageUri` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (uri != null) {
                    selectedImageUri = uri;
                    ivAvatar.setImageURI(uri);
                    ivAvatar.setPadding(0, 0, 0, 0); // Xóa padding mặc định khi có ảnh
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    ivAvatar.setImageTintList(null); // Xóa tint trắng mặc định
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
        setContentView(R.layout.activity_register_profile);

        etFullName = findViewById(R.id.et_setup_fullname);
        etBio = findViewById(R.id.et_setup_bio);
        ivAvatar = findViewById(R.id.iv_setup_avatar);
        layoutLoading = findViewById(R.id.layout_loading);

        // Mở thư viện chọn ảnh
        ivAvatar.setOnClickListener(v -> {
            getContent.launch("image/*");
        });

        findViewById(R.id.btn_profile_next).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String bio = etBio.getText().toString().trim();

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (fullName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (user != null) {
                // Hiển thị vòng quay loading và khóa tương tác
                layoutLoading.setVisibility(View.VISIBLE);
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (selectedImageUri != null) {
                    try {
                        android.graphics.Bitmap bitmap;
                        if (android.os.Build.VERSION.SDK_INT >= 28) {
                            android.graphics.ImageDecoder.Source source = android.graphics.ImageDecoder.createSource(getContentResolver(), selectedImageUri);
                            bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                        }
                        
                        android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                        scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, buffer);
                        byte[] fileBytes = buffer.toByteArray();
                        
                        String base64Image = "data:image/jpeg;base64," + android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                        saveData(user.getUid(), fullName, bio, base64Image);
                    } catch (Exception e) {
                        layoutLoading.setVisibility(View.GONE);
                        Toast.makeText(this, "Lỗi nén ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    saveData(user.getUid(), fullName, bio, "");
                }
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    /**
     * Hàm saveData() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param uid tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param fullName tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param bio tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param avatarUrl tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void saveData(String uid, String fullName, String bio, String avatarUrl) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Lưu thông tin chung vào collection "Users"
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        Map<String, Object> userCommon = new HashMap<>();
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userCommon.put("username", fullName);
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userCommon.put("email", com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getEmail());
        
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        Map<String, Object> profile = new HashMap<>();
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        profile.put("fullName", fullName);
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        profile.put("bio", bio);
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        profile.put("avatarUrl", avatarUrl);
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userCommon.put("profile", profile);

        // role sẽ được set ở bước RegisterLevelActivity

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(uid).set(userCommon, SetOptions.merge())
            .addOnSuccessListener(unused -> {
                // 2. Lưu thông tin profile tạm thời vào SharedPreferences
                //    để RegisterLevelActivity biết avatarUrl và bio cần lưu vào Users/Mentors
                getSharedPreferences("RegisterTemp", MODE_PRIVATE).edit()
                    .putString("avatarUrl", avatarUrl)
                    .putString("bio", bio)
                    .apply();

                layoutLoading.setVisibility(View.GONE);
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                startActivity(new Intent(this, RegisterInterestsActivity.class));
            })
            .addOnFailureListener(e -> {
                layoutLoading.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
