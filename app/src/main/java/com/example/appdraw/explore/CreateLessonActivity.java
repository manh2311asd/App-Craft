package com.example.appdraw.explore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdraw.R;
import com.example.appdraw.model.Lesson;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Activity dành cho Mentor để Tạo và Xuất bản Bài học mới (UC-19).
 * Người thực hiện: Lê Thùy Linh.
 * Xử lý nhập thông tin khóa học, chọn ảnh bìa (mã hóa Base64) và đẩy dữ liệu lên Firestore.
 */
/**
 * Lớp CreateLessonActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file CreateLessonActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class CreateLessonActivity extends AppCompatActivity {

    /**
     * Biến `PICK_IMAGE_REQUEST` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private static final int PICK_IMAGE_REQUEST = 71;

    /**
     * Biến `ivThumbnail` lưu dữ liệu/trạng thái quan trọng kiểu ImageView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ImageView ivThumbnail;
    /**
     * Biến `cvThumbnail` lưu dữ liệu/trạng thái quan trọng kiểu MaterialCardView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private MaterialCardView cvThumbnail;
    /**
     * Biến `filePath` lưu dữ liệu/trạng thái quan trọng kiểu Uri, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Uri filePath;

    /**
     * Biến `etDuration` lưu dữ liệu/trạng thái quan trọng kiểu TextInputEditText etTitle, etDesc,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextInputEditText etTitle, etDesc, etDuration;
    /**
     * Biến `spinnerCategory` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private Spinner spinnerLevel, spinnerCategory;
    /**
     * Biến `chipGroupMaterials` lưu dữ liệu/trạng thái quan trọng kiểu ChipGroup, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ChipGroup chipGroupMaterials;
    /**
     * Biến `btnSubmit` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private MaterialButton btnSubmit;

    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `storage` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseStorage storage;
    /**
     * Biến `storageReference` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private StorageReference storageReference;
    /**
     * Biến `auth` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseAuth auth;

    /**
     * Biến `authorName` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    private String authorName = "Giảng viên Vô danh";

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        storage = FirebaseStorage.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        storageReference = storage.getReference();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        auth = FirebaseAuth.getInstance();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (documentSnapshot.exists()) {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    authorName = documentSnapshot.getString("name");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (authorName == null || authorName.isEmpty()) authorName = "Giảng viên";
                }
            });
        }

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        ivThumbnail = findViewById(R.id.iv_thumbnail);
        cvThumbnail = findViewById(R.id.cv_thumbnail);
        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);
        etDuration = findViewById(R.id.et_duration);
        spinnerLevel = findViewById(R.id.spinner_level);
        spinnerCategory = findViewById(R.id.spinner_category);
        chipGroupMaterials = findViewById(R.id.chip_group_materials);

        loadCategories();
        btnSubmit = findViewById(R.id.btn_submit);

        cvThumbnail.setOnClickListener(v -> chooseImage());
        btnSubmit.setOnClickListener(v -> submitLesson());
    }

    /**
     * Hàm loadCategories() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadCategories() {
        db.collection("Categories").orderBy("order").get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(querySnapshot -> {
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                List<String> categoriesList = new ArrayList<>();
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot) {
                    String title = doc.getString("title");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (title != null) categoriesList.add(title);
                }
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (categoriesList.isEmpty()) categoriesList.add("Chung");
                
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, categoriesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
                spinnerCategory.setAdapter(adapter);
            });
    }

    /**
     * Hàm chooseImage() thực hiện một phần xử lý trong luồng chức năng của lớp CreateLessonActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void chooseImage() {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh bìa"), PICK_IMAGE_REQUEST);
    }

    @Override
    /**
     * Hàm onActivityResult() thực hiện một phần xử lý trong luồng chức năng của lớp CreateLessonActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param requestCode tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param resultCode tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param data tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivThumbnail.setImageBitmap(bitmap);
                ivThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hàm submitLesson() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void submitLesson() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (title.isEmpty() || desc.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin cơ bản", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("ĐANG TẢI LÊN...");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (filePath != null) {
            uploadImageAndSaveLesson(title, desc, durationStr);
        } else {
            saveLessonData(title, desc, durationStr, ""); // empty thumbnail
        }
    }

    /**
     * Mã hóa ảnh bìa khóa học thành chuỗi Base64 và tiến hành lưu dữ liệu
     * bài học mới (Lesson) lên Firestore.
     * @param title Tiêu đề bài học
     * @param desc Mô tả bài học
     * @param durationStr Thời lượng dự kiến
     */
    /**
     * Hàm uploadImageAndSaveLesson() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param desc tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param durationStr tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void uploadImageAndSaveLesson(String title, String desc, String durationStr) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Nén ảnh chất lượng 50% để Base64 không quá nặng
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();
            
            if (data.length == 0) {
                throw new Exception("Dữ liệu ảnh rỗng");
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // By-pass Firebase Storage, chuyển ảnh thành Base64 lưu thẳng vào Firestore
            String base64Image = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP);
            String finalImageUrl = "data:image/jpeg;base64," + base64Image;
            
            saveLessonData(title, desc, durationStr, finalImageUrl);

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            btnSubmit.setText("XUẤT BẢN KHÓA HỌC");
        }
    }

    /**
     * Hàm saveLessonData() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param desc tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param durationStr tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param thumbUrl tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void saveLessonData(String title, String desc, String durationStr, String thumbUrl) {
        String authorId = auth.getCurrentUser().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        String level = spinnerLevel.getSelectedItem() != null ? spinnerLevel.getSelectedItem().toString() : "Dễ";
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Chung";
        int duration = Integer.parseInt(durationStr);

        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        List<String> materials = new ArrayList<>();
        for (int i = 0; i < chipGroupMaterials.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupMaterials.getChildAt(i);
            if (chip.isChecked()) {
                materials.add(chip.getText().toString());
            }
        }

        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        List<Lesson.Step> stepsList = new ArrayList<>();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String lessonId = db.collection("Lessons").document().getId();
        Lesson lesson = new Lesson(lessonId, title, authorName, level, duration, 5.0f, desc, materials, stepsList);
        lesson.setCreatedAt(System.currentTimeMillis());
        lesson.setAuthorId(authorId);
        lesson.setThumbnailUrl(thumbUrl);
        lesson.setCategory(category);

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Lessons").document(lessonId).set(lesson)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(CreateLessonActivity.this, "Đã xuất bản khóa học thành công!", Toast.LENGTH_LONG).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(CreateLessonActivity.this, "Lỗi xuất bản: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                btnSubmit.setText("XUẤT BẢN KHÓA HỌC");
            });
    }
}
