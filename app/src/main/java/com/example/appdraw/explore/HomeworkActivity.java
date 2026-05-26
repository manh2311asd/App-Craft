package com.example.appdraw.explore;

import com.example.appdraw.R;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity xử lý Nộp bài Thực hành (UC-11).
 * Người thực hiện: Lê Thùy Linh.
 * Hỗ trợ người dùng chọn ảnh từ thư viện hoặc trích xuất trực tiếp ảnh dạng Base64 
 * từ màn hình Drawing Canvas để nộp bài và tự động chia sẻ lên mạng xã hội.
 */
/**
 * Lớp HomeworkActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file HomeworkActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class HomeworkActivity extends AppCompatActivity {

    /**
     * Biến `ivUploadedImage` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivUploadedImage;
    /**
     * Biến `llUploadPlaceholder` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout llUploadPlaceholder;
    /**
     * Biến `isImageUploaded` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private boolean isImageUploaded = false;

    /**
     * Biến `lessonTitle` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String lessonTitle;
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu com.google.firebase.firestore.FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private com.google.firebase.firestore.FirebaseFirestore db;
    /**
     * Biến `uid` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String uid;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        Toolbar toolbar = findViewById(R.id.toolbar_homework);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        lessonTitle = getIntent().getStringExtra("LESSON_TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (lessonTitle == null) {
            lessonTitle = "Unknown Lesson";
        }

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String imageResStr = getIntent().getStringExtra("IMAGE_RES");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (imageResStr != null && !imageResStr.isEmpty()) {
            ImageView ivBg = findViewById(R.id.iv_homework_bg);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivBg != null) {
                int resId = getResources().getIdentifier(imageResStr, "drawable", getPackageName());
                if (resId != 0) {
                    ivBg.setImageResource(resId);
                }
            }
        }

        com.example.appdraw.utils.HomeworkHelper.HomeworkDetails details = com.example.appdraw.utils.HomeworkHelper.getHomeworkDetails(lessonTitle);
        TextView tvContent = findViewById(R.id.tv_homework_content);
        TextView tvCriteria1 = findViewById(R.id.tv_criteria_1);
        TextView tvCriteria2 = findViewById(R.id.tv_criteria_2);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvContent != null && details.desc != null) tvContent.setText(details.desc);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvCriteria1 != null && details.criteria1 != null) tvCriteria1.setText(details.criteria1);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvCriteria2 != null && details.criteria2 != null) tvCriteria2.setText(details.criteria2);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            uid = "guest";
        }

        ivUploadedImage = findViewById(R.id.iv_uploaded_image);
        llUploadPlaceholder = findViewById(R.id.ll_upload_placeholder);

        findViewById(R.id.card_upload).setOnClickListener(v -> showSubmissionChoiceBottomSheet());

        Button btnSubmit = findViewById(R.id.btn_submit_homework);
        btnSubmit.setOnClickListener(v -> {
            if (!isImageUploaded) {
                Toast.makeText(this, "Vui lòng tải ảnh bài vẽ lên trước!", Toast.LENGTH_SHORT).show();
            } else {
                markLessonCompleted();
            }
        });

        fetchProgressFromFirestore();
    }

    /**
     * Biến `selectedImageUri` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private android.net.Uri selectedImageUri = null;

    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> galleryLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivUploadedImage.setImageURI(selectedImageUri);
                    ivUploadedImage.setVisibility(View.VISIBLE);
                    llUploadPlaceholder.setVisibility(View.GONE);
                    isImageUploaded = true;
                    Toast.makeText(this, "Đã tải ảnh lên thành công!", Toast.LENGTH_SHORT).show();
                }
            });

    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> drawingLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String base64Url = result.getData().getStringExtra("SAVED_BASE64");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (base64Url != null && base64Url.startsWith("data:image")) {
                        String cleanBase64 = base64Url.substring(base64Url.indexOf(",") + 1);
                        byte[] decodedString = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT);
                        android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory
                                .decodeByteArray(decodedString, 0, decodedString.length);
                        ivUploadedImage.setImageBitmap(decodedByte);

                        String path = android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                decodedByte, "Homework_" + System.currentTimeMillis(), null);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (path != null)
                            selectedImageUri = android.net.Uri.parse(path);

                        ivUploadedImage.setVisibility(View.VISIBLE);
                        llUploadPlaceholder.setVisibility(View.GONE);
                        isImageUploaded = true;
                        Toast.makeText(this, "Đã cập nhật bài vẽ!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    /**
     * Hàm fetchProgressFromFirestore() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchProgressFromFirestore() {
        if ("guest".equals(uid))
            return;
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(uid).collection("lessonProgress").document(lessonTitle)
                .get()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addOnSuccessListener(documentSnapshot -> {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (documentSnapshot.exists()) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String status = documentSnapshot.getString("status");
                        if ("COMPLETED".equals(status)) {
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String base64Url = documentSnapshot.getString("imageUrl");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (base64Url != null && base64Url.startsWith("data:image")) {
                                String cleanBase64 = base64Url.substring(base64Url.indexOf(",") + 1);
                                byte[] decodedString = android.util.Base64.decode(cleanBase64,
                                        android.util.Base64.DEFAULT);
                                android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory
                                        .decodeByteArray(decodedString, 0, decodedString.length);
                                ivUploadedImage.setImageBitmap(decodedByte);

                                // Tạo local cache URI để có thể Share bài
                                String path = android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        decodedByte, "Homework_" + System.currentTimeMillis(), null);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (path != null)
                                    selectedImageUri = android.net.Uri.parse(path);
                            } else {
                                ivUploadedImage.setImageResource(R.drawable.ve_hoa_mau_nuoc); // Fallback
                            }

                            ivUploadedImage.setVisibility(View.VISIBLE);
                            llUploadPlaceholder.setVisibility(View.GONE);
                            isImageUploaded = true;

                            Button btnSubmit = findViewById(R.id.btn_submit_homework);
                            btnSubmit.setText("Cập nhật bài nộp");
                            btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2ECC71)); // Xanh
                                                                                                                     // lá

                            // Show Grade
                            findViewById(R.id.card_grade_result).setVisibility(View.VISIBLE);
                            TextView tvScore = findViewById(R.id.tv_homework_score);
                            TextView tvFeedback = findViewById(R.id.tv_homework_feedback);

                            int hash = Math.abs((uid + lessonTitle).hashCode());
                            float mockScore = 8.0f + (hash % 20) / 10.0f; // 8.0 to 9.9
                            tvScore.setText(String.format(java.util.Locale.getDefault(), "%.1f / 10", mockScore));

                            String[] feeds = {
                                    "Nét vẽ rất tự nhiên và loang màu mượt mà. Tuyệt vời!",
                                    "Bố cục hoàn hảo, bạn đã nắm bắt được trọng tâm bài học.",
                                    "Màu sắc rất có hồn, bạn đang tiến bộ rất nhanh đấy!",
                                    "Chú ý một chút ở kỹ thuật đi nét mỏng, còn lại rất xuất sắc!"
                            };
                            tvFeedback.setText("Nhận xét từ Giảng viên: " + feeds[hash % feeds.length]);

                            // findViewById(R.id.card_upload).setClickable(false); // Cho phép nộp lại ảnh
                            // mới
                        }
                    }
                });
    }

    /**
     * Đánh dấu Bài học là hoàn thành và lưu ảnh bài nộp (Base64) lên Firestore.
     * Cập nhật bản ghi tiến độ vào Collection "lessonProgress".
     */
    /**
     * Hàm markLessonCompleted() thực hiện một phần xử lý trong luồng chức năng của lớp HomeworkActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void markLessonCompleted() {
        if (!"guest".equals(uid)) {
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            data.put("status", "COMPLETED");
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            data.put("lastUpdated", System.currentTimeMillis());

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (selectedImageUri != null) {
                try {
                    android.graphics.Bitmap bitmap;
                    if (android.os.Build.VERSION.SDK_INT >= 28) {
                        android.graphics.ImageDecoder.Source source = android.graphics.ImageDecoder
                                .createSource(getContentResolver(), selectedImageUri);
                        bitmap = android.graphics.ImageDecoder.decodeBitmap(source);
                    } else {
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(),
                                selectedImageUri);
                    }
                    java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, buffer);
                    byte[] fileBytes = buffer.toByteArray();
                    String base64Image = android.util.Base64.encodeToString(fileBytes, android.util.Base64.DEFAULT);
                    String finalImageUrl = "data:image/jpeg;base64," + base64Image;

                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    data.put("imageUrl", finalImageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Users").document(uid).collection("lessonProgress").document(lessonTitle)
                    .update(data)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Cập nhật đè nếu chưa tồn tại
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Users").document(uid).collection("lessonProgress").document(lessonTitle)
                                    .set(data);
                        }
                    });
        }
        showSuccessDialog();
    }

    /**
     * Hàm simulateImageUpload() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void simulateImageUpload() {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Hàm showSubmissionChoiceBottomSheet() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showSubmissionChoiceBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_submission_choice, null);
        bottomSheetDialog.setContentView(dialogView);

        dialogView.findViewById(R.id.card_draw_canvas).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            android.content.Intent intent = new android.content.Intent(HomeworkActivity.this,
                    com.example.appdraw.drawing.DrawingActivity.class);
            drawingLauncher.launch(intent);
        });

        dialogView.findViewById(R.id.card_upload_photo).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            simulateImageUpload();
        });

        bottomSheetDialog.show();
    }

    /**
     * Hiển thị Dialog thông báo Nộp bài thành công và cung cấp tùy chọn
     * tự động chia sẻ tác phẩm vừa vẽ lên Bảng tin cộng đồng (Feed).
     */
    /**
     * Hàm showSuccessDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showSuccessDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_lesson_complete); // Sử dụng lại layout thành công
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle = dialog.findViewById(R.id.tv_completion_title);
        TextView tvSubTitle = dialog.findViewById(R.id.tv_completion_subtitle);
        Button btnMain = dialog.findViewById(R.id.btn_do_homework_now);
        Button btnClose = dialog.findViewById(R.id.btn_later);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvTitle != null)
            tvTitle.setText("Nộp bài thành công!");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvSubTitle != null)
            tvSubTitle.setText("Bài vẽ của bạn đã được gửi đi.\nHãy chia sẻ với Cộng đồng nào!");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnMain != null)
            btnMain.setText("Chia sẻ tác phẩm");

        btnMain.setOnClickListener(v -> {
            dialog.dismiss();
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            android.content.Intent intent = new android.content.Intent(HomeworkActivity.this,
                    com.example.appdraw.community.CreatePostActivity.class);
            intent.putExtra("PREFILL_TEXT", "#" + lessonTitle.replaceAll("\\s+", "")
                    + " \nĐây là tác phẩm bài tập của mình. Mọi người nhận xét giúp nhé!");
            if (isImageUploaded) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (selectedImageUri != null) {
                    intent.putExtra("PREFILL_IMAGE_URI", selectedImageUri.toString());
                } else {
                    android.net.Uri imageUri = android.net.Uri
                            .parse("android.resource://" + getPackageName() + "/" + R.drawable.ve_hoa_mau_nuoc);
                    intent.putExtra("PREFILL_IMAGE_URI", imageUri.toString());
                }
            }
            startActivity(intent);
            finish();
        });

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }
}

