package com.example.appdraw.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdraw.R;
import com.example.appdraw.drawing.DrawingActivity;
import com.example.appdraw.model.Artwork;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Quản lý Chi tiết Dự án (UC-12).
 * Người thực hiện: Vũ Quang Vinh.
 * Cho phép người dùng theo dõi và quản lý các tác phẩm (Artwork) thuộc dự án.
 */
/**
 * Lớp ProjectDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ProjectDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ProjectDetailActivity extends AppCompatActivity {
    /**
     * Biến `projectId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String projectId;
    /**
     * Biến `projectName` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String projectName;

    /**
     * Biến `rvArtworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvArtworks;
    /**
     * Biến `adapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ArtworkAdapter adapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Artwork> artworkList = new ArrayList<>();
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        projectId = getIntent().getStringExtra("PROJECT_ID");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        projectName = getIntent().getStringExtra("PROJECT_NAME");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String projectDesc = getIntent().getStringExtra("PROJECT_DESC");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String projectCover = getIntent().getStringExtra("PROJECT_COVER");

        Toolbar toolbar = findViewById(R.id.toolbar_project_detail);
        TextView tvTitleToolbar = findViewById(R.id.tv_project_title_toolbar);
        TextView tvTitleExpanded = findViewById(R.id.tv_project_title_expanded);
        TextView tvDescExpanded = findViewById(R.id.tv_project_desc_expanded);
        android.widget.ImageView ivCover = findViewById(R.id.iv_project_cover_detail);
        com.google.android.material.appbar.AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectName != null) {
            tvTitleToolbar.setText(projectName);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvTitleExpanded != null) tvTitleExpanded.setText(projectName);
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectDesc != null && !projectDesc.isEmpty() && tvDescExpanded != null) {
            tvDescExpanded.setText(projectDesc);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        } else if (tvDescExpanded != null) {
            tvDescExpanded.setVisibility(android.view.View.GONE);
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectCover != null && !projectCover.isEmpty() && ivCover != null) {
            if (projectCover.startsWith("data:image")) {
                byte[] b = android.util.Base64.decode(projectCover.split(",")[1], android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                com.bumptech.glide.Glide.with(this).load(b).centerCrop().into(ivCover);
            } else {
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                com.bumptech.glide.Glide.with(this).load(projectCover).centerCrop().into(ivCover);
            }
        }

        // Fade in/out toolbar title based on scroll
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (appBarLayout != null && tvTitleToolbar != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
                if (Math.abs(verticalOffset) >= appBarLayout1.getTotalScrollRange() - 50) {
                    tvTitleToolbar.setAlpha(1.0f);
                } else {
                    tvTitleToolbar.setAlpha(0.0f);
                }
            });
        }

        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvArtworks = findViewById(R.id.rv_artworks);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvArtworks.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
        adapter = new ArtworkAdapter(artworkList, artwork -> {
            // Mở DrawingActivity
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra("ARTWORK_ID", artwork.getId());
            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra("STATUS", artwork.getStatus());
            startActivity(intent);
        });
        adapter.setOnItemLongClickListener(artwork -> {
            showArtworkOptionsDialog(artwork);
        });
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvArtworks.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_artwork);
        fab.setOnClickListener(v -> createNewArtwork());

        android.widget.ImageView btnChangeCover = findViewById(R.id.btn_change_cover);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnChangeCover != null) {
            btnChangeCover.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            });
        }

        loadArtworks();
    }

    /**
     * Biến `pickImageLauncher` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private final androidx.activity.result.ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.net.Uri imageUri = result.getData().getData();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageUri != null) {
                        try {
                            // Downscale the image to prevent Firestore 1MB limit issues
                            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            android.graphics.Bitmap originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
                            
                            int maxDim = 800;
                            int width = originalBitmap.getWidth();
                            int height = originalBitmap.getHeight();
                            if (width > maxDim || height > maxDim) {
                                float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                                width = Math.round(width * ratio);
                                height = Math.round(height * ratio);
                            }
                            android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, true);
                            
                            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] data = baos.toByteArray();
                            String base64Image = "data:image/jpeg;base64," + android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (projectId != null) {
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                db.collection("Projects").document(projectId).update("coverImageUrl", base64Image)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Đã cập nhật ảnh bìa", Toast.LENGTH_SHORT).show();
                                            android.widget.ImageView ivCover = findViewById(R.id.iv_project_cover_detail);
                                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                            com.bumptech.glide.Glide.with(this).load(data).centerCrop().into(ivCover);
                                        });
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    /**
     * Hàm loadArtworks() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadArtworks() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectId == null) return;
        db.collection("Artworks")
                .whereEqualTo("projectId", projectId)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener((value, error) -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải ảnh: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    artworkList.clear();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (value != null) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (QueryDocumentSnapshot doc : value) {
                            Artwork a = doc.toObject(Artwork.class);
                            artworkList.add(a);
                        }
                        
                        // Sắp xếp local
                        java.util.Collections.sort(artworkList, (a1, a2) -> Long.compare(a2.getCreatedAt(), a1.getCreatedAt()));

                        // Tự động đồng bộ số lượng tác phẩm thật ngoài bìa dự án (Self-healing count)
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Projects").document(projectId).update("artworkCount", artworkList.size());
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    /**
     * Hàm createNewArtwork() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void createNewArtwork() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (projectId == null) return;
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null) return;

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String docId = db.collection("Artworks").document().getId();
        Artwork newArtwork = new Artwork(docId, uid, projectId, "Trang trắng", "", Artwork.STATUS_DRAFT, System.currentTimeMillis());

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Artworks").document(docId).set(newArtwork)
                .addOnSuccessListener(aVoid -> {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Projects").document(projectId).update("artworkCount", com.google.firebase.firestore.FieldValue.increment(1));
                    // Mở luôn trang vẽ
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(this, DrawingActivity.class);
                    intent.putExtra("ARTWORK_ID", docId);
                    intent.putExtra("PROJECT_ID", projectId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tạo bản vẽ", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Hàm showArtworkOptionsDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param artwork tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showArtworkOptionsDialog(Artwork artwork) {
        String[] options = {"✏️ Đổi tên tác phẩm", "🗑️ Xóa tác phẩm"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tùy chọn Tác phẩm")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameArtworkDialog(artwork);
                    } else if (which == 1) {
                        showDeleteArtworkConfirmDialog(artwork);
                    }
                })
                .show();
    }

    /**
     * Hàm showRenameArtworkDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param artwork tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showRenameArtworkDialog(Artwork artwork) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(artwork.getTitle());
        input.setSingleLine(true);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đổi tên tác phẩm")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (!newName.isEmpty()) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Artworks").document(artwork.getId())
                                .update("title", newName)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã đổi tên", Toast.LENGTH_SHORT).show());
                    }
                })
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Hàm showDeleteArtworkConfirmDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param artwork tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showDeleteArtworkConfirmDialog(Artwork artwork) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa tác phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa tác phẩm này vĩnh viễn?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Artworks").document(artwork.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa tác phẩm", Toast.LENGTH_SHORT).show());
                })
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                .setNegativeButton("Hủy", null)
                .show();
    }
}
