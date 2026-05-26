package com.example.appdraw.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.example.appdraw.model.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Màn hình Đăng bài viết mới (UC-06).
 * Người thực hiện: Cao Đức Mạnh.
 * Xử lý người dùng viết nội dung, đính kèm hình ảnh và lưu Post vào Firestore.
 */
/**
 * Lớp CreatePostActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file CreatePostActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class CreatePostActivity extends AppCompatActivity {

    /**
     * Biến `etContent` lưu dữ liệu/trạng thái quan trọng kiểu EditText, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etContent;
    /**
     * Biến `btnRemoveImage` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivImage, btnRemoveImage;
    /**
     * Biến `btnPickGallery` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View btnPickGallery;
    /**
     * Biến `selectedLocalUri` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private Uri selectedLocalUri;

    /**
     * Biến `rgCategory` lưu dữ liệu/trạng thái quan trọng kiểu RadioGroup, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private RadioGroup rgCategory;
    /**
     * Biến `swComment` lưu dữ liệu/trạng thái quan trọng kiểu Switch swShare,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Switch swShare, swComment;

    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `currentUid` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String currentUid;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private java.util.List<String> selectedTopics = new java.util.ArrayList<>();

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedLocalUri = result.getData().getData();
                    showSelectedImage(selectedLocalUri.toString());
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
        setContentView(R.layout.activity_create_post);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            currentUid = FirebaseAuth.getInstance().getUid();
        }

        Toolbar toolbar = findViewById(R.id.toolbar_create_post);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etContent = findViewById(R.id.et_post_content);
        ivImage = findViewById(R.id.iv_post_image);
        btnPickGallery = findViewById(R.id.btn_pick_gallery);
        btnRemoveImage = findViewById(R.id.btn_remove_image);
        rgCategory = findViewById(R.id.rg_category);
        swShare = findViewById(R.id.sw_share);
        swComment = findViewById(R.id.sw_comment);

        // Populate text if coming from Homework Submission
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String prefillText = getIntent().getStringExtra("PREFILL_TEXT");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (prefillText != null && !prefillText.isEmpty()) {
            etContent.setText(prefillText);
        }

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String prefillImageUriStr = getIntent().getStringExtra("PREFILL_IMAGE_URI");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (prefillImageUriStr != null && !prefillImageUriStr.isEmpty()) {
            selectedLocalUri = Uri.parse(prefillImageUriStr);
            showSelectedImage(prefillImageUriStr);
        }

        btnPickGallery.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        btnRemoveImage.setOnClickListener(v -> removeImage());

        setupTopicFilters();

        MaterialButton btnPublish = findViewById(R.id.btn_publish);
        btnPublish.setOnClickListener(v -> publishPost());
    }

    /**
     * Hàm setupTopicFilters() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupTopicFilters() {
        int colorSelectedBg = android.graphics.Color.parseColor("#4272D0");
        int colorSelectedText = android.graphics.Color.WHITE;
        
        int[] tvIds = new int[]{R.id.tv_topic_watercolor, R.id.tv_topic_sketch, R.id.tv_topic_handmade, R.id.tv_topic_more};
        
        for (int id : tvIds) {
            android.widget.TextView tv = findViewById(id);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tv != null) {
                // Save original colors
                final android.content.res.ColorStateList oriText = tv.getTextColors();
                final android.graphics.drawable.Drawable oriBg = tv.getBackground();
                final android.content.res.ColorStateList oriBgTint = tv.getBackgroundTintList();
                tv.setTag(false); // Initially false

                tv.setOnClickListener(v -> {
                    boolean isSelected = (boolean) tv.getTag();
                    if (!isSelected) {
                        tv.setTag(true);
                        tv.setBackgroundResource(R.drawable.rounded_bg_gray);
                        tv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorSelectedBg));
                        tv.setTextColor(colorSelectedText);
                        selectedTopics.add(tv.getText().toString());
                    } else {
                        tv.setTag(false);
                        tv.setBackground(oriBg);
                        tv.setBackgroundTintList(oriBgTint);
                        tv.setTextColor(oriText);
                        selectedTopics.remove(tv.getText().toString());
                    }
                });
            }
        }
    }

    /**
     * Hàm showSelectedImage() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param uri tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showSelectedImage(String uri) {
        btnPickGallery.setVisibility(View.GONE);
        View cvImagePreview = findViewById(R.id.cv_image_preview);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cvImagePreview != null) cvImagePreview.setVisibility(View.VISIBLE);
        else ivImage.setVisibility(View.VISIBLE);
        btnRemoveImage.setVisibility(View.VISIBLE);
        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
        Glide.with(this).load(uri).into(ivImage);
    }

    /**
     * Hàm removeImage() thực hiện một phần xử lý trong luồng chức năng của lớp CreatePostActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void removeImage() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        selectedLocalUri = null;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        ivImage.setImageDrawable(null);
        View cvImagePreview = findViewById(R.id.cv_image_preview);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cvImagePreview != null) cvImagePreview.setVisibility(View.GONE);
        else ivImage.setVisibility(View.GONE);
        btnRemoveImage.setVisibility(View.GONE);
        btnPickGallery.setVisibility(View.VISIBLE);
    }

    /**
     * Hàm publishPost() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void publishPost() {
        String content = etContent.getText().toString().trim();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (content.isEmpty() && selectedLocalUri == null) {
            Toast.makeText(this, "Bài viết cần có nội dung hoặc màn vẽ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (currentUid == null) {
            Toast.makeText(this, "Yêu cầu đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.btn_publish).setEnabled(false);
        Toast.makeText(this, "Đang đăng...", Toast.LENGTH_SHORT).show();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (selectedLocalUri != null) {
            try {
                android.graphics.Bitmap bitmap;
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    android.graphics.ImageDecoder.Source source = android.graphics.ImageDecoder.createSource(getContentResolver(), selectedLocalUri);
                    bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                } else {
                    bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), selectedLocalUri);
                }
                
                java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, buffer);
                byte[] fileBytes = buffer.toByteArray();
                
                if (fileBytes.length == 0) {
                    throw new Exception("Dữ liệu ảnh rỗng hoặc lỗi khi đọc ảnh");
                }

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // By-pass hoàn toàn Firebase Storage để không cần thẻ Visa
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Chuyển ảnh thành chuỗi văn bản Base64 rồi lưu trực tiếp vào Firestore Document.
                String base64Image = android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                String finalImageUrl = "data:image/jpeg;base64," + base64Image;
                
                savePostToFirestore(content, finalImageUrl);
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi nén ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                findViewById(R.id.btn_publish).setEnabled(true);
            }
        } else {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            savePostToFirestore(content, null);
        }
    }

    /**
     * Hàm savePostToFirestore() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param content tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param finalImageUrl tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void savePostToFirestore(String content, String finalImageUrl) {
        String category = "Tác phẩm";
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rgCategory != null) {
            int checkedId = rgCategory.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_tips) category = "Tips";
            else if (checkedId == R.id.rb_progress) category = "Tiến độ";
            else if (checkedId == R.id.rb_handmade) category = "Thủ công";
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        boolean share = swShare != null && swShare.isChecked();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        boolean allowComment = swComment != null && swComment.isChecked();

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String docId = db.collection("Posts").document().getId();
        Post post = new Post(docId, currentUid, content, finalImageUrl, category, selectedTopics, share, allowComment, System.currentTimeMillis());
        // share = isPublic. If false, it's hidden from community feed.

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Posts").document(docId).set(post)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật postCount cho User
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Users").document(currentUid).update("postCount", com.google.firebase.firestore.FieldValue.increment(1));
                    
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(this, com.example.appdraw.MainActivity.class);
                    // Dùng cờ này để MainActivity nhận onNewIntent (nếu đã mở) thay vì tạo mới activity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("NAVIGATE_TO_COMMUNITY", true);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi đăng bài!", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btn_publish).setEnabled(true);
                });
    }
}
