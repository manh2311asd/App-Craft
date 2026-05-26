package com.example.appdraw.profile;

import com.example.appdraw.R;

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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Màn hình Chỉnh sửa Thông tin cá nhân (UC-02).
 * Người thực hiện: Vũ Quang Vinh.
 * Hỗ trợ người dùng cập nhật avatar, bio và link portfolio.
 */
/**
 * Lớp EditProfileActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EditProfileActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class EditProfileActivity extends AppCompatActivity {

    /**
     * Biến `ivAvatar` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivAvatar;
    /**
     * Biến `etBio` lưu dữ liệu/trạng thái quan trọng kiểu EditText etFullName,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etFullName, etBio;
    /**
     * Biến `btnSave` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private MaterialButton btnSave;
    /**
     * Biến `layoutLoading` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View layoutLoading;

    /**
     * Biến `currentAvatarBase64` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String currentAvatarBase64 = null;
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `user` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseUser user;

    private final ActivityResultLauncher<Intent> avatarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    processImage(result.getData().getData());
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
        setContentView(R.layout.activity_edit_profile);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        user = FirebaseAuth.getInstance().getCurrentUser();

        initViews();
        loadCurrentData();
    }

    /**
     * Hàm initViews() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_edit_profile);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.iv_edit_avatar);
        etFullName = findViewById(R.id.et_edit_fullname);
        etBio = findViewById(R.id.et_edit_bio);
        btnSave = findViewById(R.id.btn_save_profile);
        layoutLoading = findViewById(R.id.layout_loading);

        ivAvatar.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            avatarLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveProfile());
    }

    /**
     * Hàm loadCurrentData() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadCurrentData() {
        // Pre-fill
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null) return;
        layoutLoading.setVisibility(View.VISIBLE);
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(user.getUid()).get()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addOnSuccessListener(documentSnapshot -> {
                    layoutLoading.setVisibility(View.GONE);
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (documentSnapshot.exists() && documentSnapshot.contains("profile")) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null) {
                            String fullName = (String) profile.get("fullName");
                            String bio = (String) profile.get("bio");
                            String avatarUrl = (String) profile.get("avatarUrl");

                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (fullName != null) etFullName.setText(fullName);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (bio != null) etBio.setText(bio);

                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                currentAvatarBase64 = avatarUrl;
                                if (avatarUrl.startsWith("data:image")) {
                                    byte[] decodedBytes = Base64.decode(avatarUrl.split(",")[1], Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(this).load(decodedBytes).circleCrop().into(ivAvatar);
                                } else {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> layoutLoading.setVisibility(View.GONE));
    }

    /**
     * Hàm processImage() thực hiện một phần xử lý trong luồng chức năng của lớp EditProfileActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param uri tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void processImage(Uri uri) {
        try {
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 28) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, buffer);
            byte[] fileBytes = buffer.toByteArray();

            currentAvatarBase64 = "data:image/jpeg;base64," + Base64.encodeToString(fileBytes, Base64.DEFAULT);
            byte[] decodedBytes = Base64.decode(currentAvatarBase64.split(",")[1], Base64.DEFAULT);
            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
            Glide.with(this).load(decodedBytes).circleCrop().into(ivAvatar);
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hàm saveProfile() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void saveProfile() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null) return;
        String newName = etFullName.getText().toString().trim();
        String newBio = etBio.getText().toString().trim();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (newName.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutLoading.setVisibility(View.VISIBLE);
        
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        Map<String, Object> profileUpdates = new HashMap<>();
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        profileUpdates.put("fullName", newName);
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        profileUpdates.put("bio", newBio);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (currentAvatarBase64 != null) {
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            profileUpdates.put("avatarUrl", currentAvatarBase64);
        }

        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        Map<String, Object> userUpdates = new HashMap<>();
        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
        userUpdates.put("profile", profileUpdates);

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(user.getUid())
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                .set(userUpdates, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã lưu hồ sơ", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    layoutLoading.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

