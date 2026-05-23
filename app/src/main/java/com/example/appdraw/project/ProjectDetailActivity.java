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
public class ProjectDetailActivity extends AppCompatActivity {
    private String projectId;
    private String projectName;

    private RecyclerView rvArtworks;
    private ArtworkAdapter adapter;
    private List<Artwork> artworkList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        db = FirebaseFirestore.getInstance();
        projectId = getIntent().getStringExtra("PROJECT_ID");
        projectName = getIntent().getStringExtra("PROJECT_NAME");
        String projectDesc = getIntent().getStringExtra("PROJECT_DESC");
        String projectCover = getIntent().getStringExtra("PROJECT_COVER");

        Toolbar toolbar = findViewById(R.id.toolbar_project_detail);
        TextView tvTitleToolbar = findViewById(R.id.tv_project_title_toolbar);
        TextView tvTitleExpanded = findViewById(R.id.tv_project_title_expanded);
        TextView tvDescExpanded = findViewById(R.id.tv_project_desc_expanded);
        android.widget.ImageView ivCover = findViewById(R.id.iv_project_cover_detail);
        com.google.android.material.appbar.AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        
        if (projectName != null) {
            tvTitleToolbar.setText(projectName);
            if (tvTitleExpanded != null) tvTitleExpanded.setText(projectName);
        }

        if (projectDesc != null && !projectDesc.isEmpty() && tvDescExpanded != null) {
            tvDescExpanded.setText(projectDesc);
        } else if (tvDescExpanded != null) {
            tvDescExpanded.setVisibility(android.view.View.GONE);
        }

        if (projectCover != null && !projectCover.isEmpty() && ivCover != null) {
            if (projectCover.startsWith("data:image")) {
                byte[] b = android.util.Base64.decode(projectCover.split(",")[1], android.util.Base64.DEFAULT);
                com.bumptech.glide.Glide.with(this).load(b).centerCrop().into(ivCover);
            } else {
                com.bumptech.glide.Glide.with(this).load(projectCover).centerCrop().into(ivCover);
            }
        }

        // Fade in/out toolbar title based on scroll
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvArtworks = findViewById(R.id.rv_artworks);
        rvArtworks.setLayoutManager(new androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL));
        adapter = new ArtworkAdapter(artworkList, artwork -> {
            // Mở DrawingActivity
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra("ARTWORK_ID", artwork.getId());
            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra("STATUS", artwork.getStatus());
            startActivity(intent);
        });
        adapter.setOnItemLongClickListener(artwork -> {
            showArtworkOptionsDialog(artwork);
        });
        rvArtworks.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_artwork);
        fab.setOnClickListener(v -> createNewArtwork());

        android.widget.ImageView btnChangeCover = findViewById(R.id.btn_change_cover);
        if (btnChangeCover != null) {
            btnChangeCover.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
            });
        }

        loadArtworks();
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.net.Uri imageUri = result.getData().getData();
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

                            if (projectId != null) {
                                db.collection("Projects").document(projectId).update("coverImageUrl", base64Image)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Đã cập nhật ảnh bìa", Toast.LENGTH_SHORT).show();
                                            android.widget.ImageView ivCover = findViewById(R.id.iv_project_cover_detail);
                                            com.bumptech.glide.Glide.with(this).load(data).centerCrop().into(ivCover);
                                        });
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private void loadArtworks() {
        if (projectId == null) return;
        db.collection("Artworks")
                .whereEqualTo("projectId", projectId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải ảnh: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    artworkList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Artwork a = doc.toObject(Artwork.class);
                            artworkList.add(a);
                        }
                        
                        // Sắp xếp local
                        java.util.Collections.sort(artworkList, (a1, a2) -> Long.compare(a2.getCreatedAt(), a1.getCreatedAt()));

                        // Tự động đồng bộ số lượng tác phẩm thật ngoài bìa dự án (Self-healing count)
                        db.collection("Projects").document(projectId).update("artworkCount", artworkList.size());
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void createNewArtwork() {
        if (projectId == null) return;
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        String docId = db.collection("Artworks").document().getId();
        Artwork newArtwork = new Artwork(docId, uid, projectId, "Trang trắng", "", Artwork.STATUS_DRAFT, System.currentTimeMillis());

        db.collection("Artworks").document(docId).set(newArtwork)
                .addOnSuccessListener(aVoid -> {
                    db.collection("Projects").document(projectId).update("artworkCount", com.google.firebase.firestore.FieldValue.increment(1));
                    // Mở luôn trang vẽ
                    Intent intent = new Intent(this, DrawingActivity.class);
                    intent.putExtra("ARTWORK_ID", docId);
                    intent.putExtra("PROJECT_ID", projectId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tạo bản vẽ", Toast.LENGTH_SHORT).show();
                });
    }

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
                    if (!newName.isEmpty()) {
                        db.collection("Artworks").document(artwork.getId())
                                .update("title", newName)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã đổi tên", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteArtworkConfirmDialog(Artwork artwork) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa tác phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa tác phẩm này vĩnh viễn?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    db.collection("Artworks").document(artwork.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xóa tác phẩm", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
