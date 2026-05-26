package com.example.appdraw.community;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import java.io.OutputStream;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
/**
 * Lớp FullScreenImageActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file FullScreenImageActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    /**
     * Biến `imageUrl` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private String imageUrl;
    /**
     * Biến `currentBitmap` lưu dữ liệu dạng Map theo cặp khóa - giá trị, dùng để gom dữ liệu từ Firebase/API hoặc truyền dữ liệu lên màn hình.
     */
    private Bitmap currentBitmap;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        imageUrl = getIntent().getStringExtra("IMAGE_URL");
        ImageView ivFullscreen = findViewById(R.id.iv_fullscreen);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("data:image")) {
                byte[] decodedBytes = Base64.decode(imageUrl.split(",")[1], Base64.DEFAULT);
                currentBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(this).load(currentBitmap).into(ivFullscreen);
            } else {
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(this).load(imageUrl).into(ivFullscreen);
            }
        }

        findViewById(R.id.btn_close_fullscreen).setOnClickListener(v -> finish());
        findViewById(R.id.btn_download).setOnClickListener(v -> downloadImage());
    }

    /**
     * Hàm downloadImage() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void downloadImage() {
        ImageView ivFullscreen = findViewById(R.id.iv_fullscreen);
        Bitmap bitmapToSave = currentBitmap;

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (bitmapToSave == null && ivFullscreen != null
                && ivFullscreen.getDrawable() instanceof android.graphics.drawable.BitmapDrawable) {
            bitmapToSave = ((android.graphics.drawable.BitmapDrawable) ivFullscreen.getDrawable()).getBitmap();
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (bitmapToSave == null) {
            Toast.makeText(this, "Đang tải ảnh, vui lòng thử lại sau giây lát...", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String filename = "AppDraw_" + System.currentTimeMillis() + ".jpg";
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString();
                java.io.File image = new java.io.File(imagesDir, filename);
                fos = new java.io.FileOutputStream(image);
            }
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (fos != null)
                fos.close();
            Toast.makeText(this, "Đã lưu ảnh vào thư viện", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
