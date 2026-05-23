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
public class CreateChallengeActivity extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private LinearLayout llPlaceholder;
    private String selectedImageBase64 = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    ivSelectedImage.setImageURI(uri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                    llPlaceholder.setVisibility(View.GONE);
                    
                    try {
                        android.graphics.Bitmap bitmap = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            android.graphics.ImageDecoder.Source source = android.graphics.ImageDecoder.createSource(getContentResolver(), uri);
                            bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                        } else {
                            bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        }
                        
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);

        Toolbar toolbar = findViewById(R.id.toolbar_create_challenge);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ivSelectedImage = findViewById(R.id.iv_selected_image);
        llPlaceholder = findViewById(R.id.ll_placeholder_image);
        View cardAddImage = findViewById(R.id.card_add_image);

        if (cardAddImage != null) {
            cardAddImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        }

        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);

        if (tvStartDate != null) {
            tvStartDate.setOnClickListener(v -> showDatePicker(tvStartDate));
        }
        if (tvEndDate != null) {
            tvEndDate.setOnClickListener(v -> showDatePicker(tvEndDate));
        }

        MaterialButton btnCreate = findViewById(R.id.btn_create_challenge_submit);
        if (btnCreate != null) {
            btnCreate.setOnClickListener(v -> {
                EditText edtTitle = findViewById(R.id.edt_challenge_title);
                EditText edtRules = findViewById(R.id.edt_challenge_rules);
                EditText edtRewards = findViewById(R.id.edt_challenge_rewards);
                
                String title = edtTitle != null ? edtTitle.getText().toString() : "Thử thách mới";
                String rules = edtRules != null ? edtRules.getText().toString() : "";
                String rewards = edtRewards != null ? edtRewards.getText().toString() : "";
                String startD = tvStartDate != null ? tvStartDate.getText().toString() : "";
                String endD = tvEndDate != null ? tvEndDate.getText().toString() : "";
                
                String dateStr = startD + " - " + endD;
                long endTimeMillis = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000; // Default 1 week
                
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    java.util.Date d = sdf.parse(endD);
                    if (d != null) endTimeMillis = d.getTime();
                } catch (Exception e) {}

                java.util.Map<String, Object> challengeData = new java.util.HashMap<>();
                challengeData.put("title", title);
                challengeData.put("rules", rules);
                challengeData.put("rewards", rewards);
                challengeData.put("dateStr", dateStr);
                challengeData.put("participantsCount", "0 đã tham gia");
                challengeData.put("endTimeMillis", endTimeMillis);
                
                if (selectedImageBase64 != null) {
                    challengeData.put("imageUrl", selectedImageBase64);
                } else {
                    challengeData.put("imageRes", "ve_hoa_mau_nuoc");
                }

                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    challengeData.put("authorId", user.getUid());
                    challengeData.put("author", user.getDisplayName());
                } else {
                    challengeData.put("authorId", "mentor123");
                    challengeData.put("author", "Mentor AI");
                }

                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Challenges")
                    .add(challengeData)
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

    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            targetTextView.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
