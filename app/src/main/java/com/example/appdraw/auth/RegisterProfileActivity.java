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
public class RegisterProfileActivity extends AppCompatActivity {
    
    private EditText etFullName, etBio;
    private ImageView ivAvatar;
    private View layoutLoading;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivAvatar.setImageURI(uri);
                    ivAvatar.setPadding(0, 0, 0, 0); // Xóa padding mặc định khi có ảnh
                    ivAvatar.setImageTintList(null); // Xóa tint trắng mặc định
                }
            });

    @Override
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

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Hiển thị vòng quay loading và khóa tương tác
                layoutLoading.setVisibility(View.VISIBLE);
                
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
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void saveData(String uid, String fullName, String bio, String avatarUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Lưu thông tin chung vào collection "Users"
        Map<String, Object> userCommon = new HashMap<>();
        userCommon.put("username", fullName);
        userCommon.put("email", com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getEmail());
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("fullName", fullName);
        profile.put("bio", bio);
        profile.put("avatarUrl", avatarUrl);
        userCommon.put("profile", profile);

        // role sẽ được set ở bước RegisterLevelActivity

        db.collection("Users").document(uid).set(userCommon, SetOptions.merge())
            .addOnSuccessListener(unused -> {
                // 2. Lưu thông tin profile tạm thời vào SharedPreferences
                //    để RegisterLevelActivity biết avatarUrl và bio cần lưu vào Users/Mentors
                getSharedPreferences("RegisterTemp", MODE_PRIVATE).edit()
                    .putString("avatarUrl", avatarUrl)
                    .putString("bio", bio)
                    .apply();

                layoutLoading.setVisibility(View.GONE);
                startActivity(new Intent(this, RegisterInterestsActivity.class));
            })
            .addOnFailureListener(e -> {
                layoutLoading.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
