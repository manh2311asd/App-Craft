package com.example.appdraw.drawing;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdraw.R;
import com.example.appdraw.drawing.ZoomDrawingView;

/**
 * Màn hình Vẽ tác phẩm trên Canvas (UC-10).
 * Người thực hiện: Vũ Quang Vinh.
 * Xử lý các thao tác đồ họa: chọn bút, cọ, màu sắc, Undo/Redo, và thước kẻ ảo.
 */
/**
 * Lớp DrawingActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file DrawingActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class DrawingActivity extends AppCompatActivity {

    /**
     * Biến `drawingView` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private ZoomDrawingView drawingView;
    /**
     * Biến `toolClear` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout toolPen, toolEraser, toolFill,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout toolPen, toolEraser, toolFill, toolClear;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        ImageView ivBack = findViewById(R.id.btnBack);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        drawingView = findViewById(R.id.drawingView);
        toolPen = findViewById(R.id.toolPen);
        toolEraser = findViewById(R.id.toolEraser);
        toolFill = findViewById(R.id.toolFill);
        toolClear = findViewById(R.id.toolClear);

        ImageView btnUndo = findViewById(R.id.btnUndo);
        ImageView btnRedo = findViewById(R.id.btnRedo);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolPen != null) {
            toolPen.setOnClickListener(new View.OnClickListener() {
                /**
                 * Biến `lastClickTime` lưu dữ liệu/trạng thái quan trọng kiểu long, được sử dụng trong các bước xử lý và hiển thị của lớp.
                 */
                private long lastClickTime = 0;

                @Override
                /**
                 * Hàm onClick() xử lý sự kiện khi người dùng bấm vào view hoặc nút chức năng.
                 * Phần xử lý bên trong có thể mở màn hình mới bằng Intent, cập nhật Firebase hoặc thay đổi trạng thái giao diện.
                 * @param v tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onClick(View v) {
                    long clickTime = System.currentTimeMillis();
                    if (clickTime - lastClickTime < 300 || (!drawingView.isEraser() && !drawingView.isFillMode())) {
                        showPenSettingsDialog();
                    } else {
                        selectTool(toolPen);
                        drawingView.setEraser(false);
                        drawingView.setFillMode(false);
                    }
                    lastClickTime = clickTime;
                }
            });
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolEraser != null) {
            toolEraser.setOnClickListener(v -> {
                selectTool(toolEraser);
                drawingView.setEraser(true);
            });
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolFill != null) {
            toolFill.setOnClickListener(v -> {
                selectTool(toolFill);
                drawingView.setFillMode(true);
                drawingView.setEraser(false);
            });
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolClear != null) {
            toolClear.setOnClickListener(v -> {
                drawingView.clearCanvasUndoable();
            });
        }

        com.google.android.material.button.MaterialButton btnSaveProject = findViewById(R.id.btnSaveProject);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSaveProject != null) {
            btnSaveProject.setOnClickListener(v -> showSaveArtworkDialog());
        }

        com.google.android.material.button.MaterialButton btnExportImg = findViewById(R.id.btnExportImg);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnExportImg != null) {
            btnExportImg.setOnClickListener(v -> saveImageToGallery());
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnUndo != null) {
            btnUndo.setOnClickListener(v -> drawingView.undo());
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnRedo != null) {
            btnRedo.setOnClickListener(v -> drawingView.redo());
        }



        android.widget.SeekBar seekSize = findViewById(R.id.seekSize);
        TextView txtSizeVal = findViewById(R.id.txtSizeVal);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (seekSize != null && txtSizeVal != null) {
            seekSize.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                /**
                 * Hàm onProgressChanged() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param progress tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param fromUser tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                    float size = Math.max(1f, progress);
                    txtSizeVal.setText((int) size + "px");
                    drawingView.setBrushSizePx(size);
                }

                @Override
                /**
                 * Hàm onStartTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                }

                @Override
                /**
                 * Hàm onStopTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                }
            });
        }

        android.widget.SeekBar seekOpacity = findViewById(R.id.seekOpacity);
        TextView txtOpacityVal = findViewById(R.id.txtOpacityVal);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (seekOpacity != null && txtOpacityVal != null) {
            seekOpacity.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                /**
                 * Hàm onProgressChanged() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param progress tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param fromUser tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                    txtOpacityVal.setText(progress + "%");
                    drawingView.setBrushOpacityPercent(progress);
                }

                @Override
                /**
                 * Hàm onStartTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                }

                @Override
                /**
                 * Hàm onStopTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                }
            });
        }

        ImageView btnOpenColorPicker = findViewById(R.id.btnOpenColorPicker);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnOpenColorPicker != null) {
            btnOpenColorPicker.setOnClickListener(v -> showColorPickerDialog());
        }

        ImageView btnLayers = findViewById(R.id.btnLayers);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnLayers != null) {
            btnLayers.setOnClickListener(v -> showLayersDialog());
        }

        View zoomHintLayout = findViewById(R.id.zoomHintLayout);
        ImageView btnCloseHint = findViewById(R.id.btnCloseHint);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnCloseHint != null && zoomHintLayout != null) {
            btnCloseHint.setOnClickListener(v -> zoomHintLayout.setVisibility(View.GONE));
        }

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String passedArtworkId = getIntent().getStringExtra("ARTWORK_ID");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (passedArtworkId != null && !passedArtworkId.isEmpty()) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Artworks")
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .document(passedArtworkId)
                    .get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(documentSnapshot -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (documentSnapshot.exists()) {
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            com.example.appdraw.model.Artwork artwork = documentSnapshot
                                    .toObject(com.example.appdraw.model.Artwork.class);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (artwork != null) {
                                TextView txtProjectName = findViewById(R.id.txtProjectName);
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (txtProjectName != null && artwork.getTitle() != null
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        && !artwork.getTitle().isEmpty()) {
                                    txtProjectName.setText(artwork.getTitle());
                                }
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (artwork.getImageUrl() != null && artwork.getImageUrl().startsWith("data:image")) {
                                    byte[] b = android.util.Base64.decode(artwork.getImageUrl().split(",")[1],
                                            android.util.Base64.DEFAULT);
                                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeByteArray(b, 0,
                                            b.length);
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (bmp != null) {
                                        drawingView.post(() -> {
                                            ZoomDrawingView.Layer activeLayer = drawingView
                                                    .getActiveLayer();
                                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                            if (activeLayer != null) {
                                                activeLayer.strokes
                                                        .add(new ZoomDrawingView.Stroke(bmp));
                                                drawingView.invalidate();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Hàm showLayersDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showLayersDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 48, 40, 40);

        TextView title = new TextView(this);
        title.setText("Cấu trúc Lớp (Layers)");
        title.setTextSize(20);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 32);
        container.addView(title);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(listLayout);

        Runnable refreshUI = new Runnable() {
            @Override
            /**
             * Hàm run() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             */
            public void run() {
                listLayout.removeAllViews();
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                java.util.List<ZoomDrawingView.Layer> layers = drawingView.getLayers();
                int activeIndex = drawingView.getActiveLayerIndex();

                for (int i = layers.size() - 1; i >= 0; i--) {
                    final int layerIndex = i;
                    ZoomDrawingView.Layer layer = layers.get(i);

                    LinearLayout row = new LinearLayout(DrawingActivity.this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                    row.setPadding(32, 40, 32, 40);

                    if (layerIndex == activeIndex) {
                        row.setBackgroundColor(Color.parseColor("#E8F0FE")); // Màu xanh nhạt để làm nổi bật lớp đang
                                                                             // chọn
                    } else {
                        row.setBackgroundColor(Color.WHITE);
                    }

                    TextView nameView = new TextView(DrawingActivity.this);
                    nameView.setText(layer.name + (layerIndex == activeIndex ? " (Đang vẽ)" : ""));
                    nameView.setTextSize(16);
                    nameView.setTextColor(
                            layerIndex == activeIndex ? Color.parseColor("#1A73E8") : Color.parseColor("#3C4043"));
                    if (layerIndex == activeIndex)
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        nameView.setTypeface(null, android.graphics.Typeface.BOLD);
                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    nameView.setLayoutParams(nameParams);

                    row.setOnClickListener(v -> {
                        drawingView.setActiveLayerIndex(layerIndex);
                        this.run();
                    });

                    row.addView(nameView);

                    if (layers.size() > 1) {
                        ImageView deleteBtn = new ImageView(DrawingActivity.this);
                        deleteBtn.setImageResource(android.R.drawable.ic_menu_delete);
                        deleteBtn.setColorFilter(Color.parseColor("#D93025"));
                        deleteBtn.setPadding(8, 8, 8, 8);
                        deleteBtn.setOnClickListener(v -> {
                            drawingView.removeLayer(layerIndex);
                            this.run();
                        });
                        row.addView(deleteBtn);
                    }

                    listLayout.addView(row);

                    View divider = new View(DrawingActivity.this);
                    divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                    divider.setBackgroundColor(Color.parseColor("#F1F3F4"));
                    listLayout.addView(divider);
                }
            }
        };

        refreshUI.run();

        // Sử dụng một container nhỏ để wrap Content của list cho dễ
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // Tránh scrollview dài quá full screen
        scrollParams.weight = 1;
        scrollView.setLayoutParams(scrollParams);

        android.widget.Button btnAdd = new android.widget.Button(this);
        btnAdd.setText("+ THÊM TỜ GIẤY MỚI (LAYER)");
        btnAdd.setBackgroundColor(Color.parseColor("#1A73E8"));
        btnAdd.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 32, 0, 0);
        btnAdd.setLayoutParams(btnParams);
        btnAdd.setOnClickListener(v -> {
            drawingView.addLayer();
            refreshUI.run();
        });

        // Use a wrapper to constrain weight behavior in BottomSheet correctly
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(scrollView);

        // Cần đảm bảo có Max height? BottomSheetBehavior tự xử lý
        container.addView(wrapper, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        container.addView(btnAdd);

        dialog.setContentView(container);

        View parent = (View) container.getParent();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (parent != null) {
            parent.setBackgroundColor(Color.WHITE);
        }

        dialog.show();
    }

    /**
     * Hàm showPenSettingsDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showPenSettingsDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        View view = getLayoutInflater().inflate(R.layout.dialog_pen_settings, null);
        dialog.setContentView(view);

        android.widget.GridLayout brushGrid = view.findViewById(R.id.brushGrid);
        android.widget.LinearLayout hardnessGroup = view.findViewById(R.id.hardnessGroup);
        android.widget.SeekBar seekOpacityPen = view.findViewById(R.id.seekOpacityPen);
        android.widget.Button btnCancelPen = view.findViewById(R.id.btnCancelPen);
        android.widget.Button btnSavePen = view.findViewById(R.id.btnSavePen);

        final ZoomDrawingView.BrushType[] selectedType = { drawingView.getBrushType() };
        final ZoomDrawingView.Hardness[] selectedHardness = { drawingView.getHardness() };
        final int[] selectedOpacity = { drawingView.getBrushOpacityPercent() };

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (seekOpacityPen != null) {
            seekOpacityPen.setProgress(selectedOpacity[0]);
        }

        Runnable updateBrushSelectionUI = () -> {
            for (int i = 0; i < brushGrid.getChildCount(); i++) {
                View child = brushGrid.getChildAt(i);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (child.getTag() != null && child.getTag().equals(selectedType[0].name())) {
                    child.setSelected(true);
                } else {
                    child.setSelected(false);
                }
            }
        };

        for (int i = 0; i < brushGrid.getChildCount(); i++) {
            View child = brushGrid.getChildAt(i);
            child.setOnClickListener(v -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (v.getTag() != null) {
                    selectedType[0] = ZoomDrawingView.BrushType.valueOf(v.getTag().toString());
                    updateBrushSelectionUI.run();
                }
            });
        }
        updateBrushSelectionUI.run();

        Runnable updateHardnessSelectionUI = () -> {
            for (int i = 0; i < hardnessGroup.getChildCount(); i++) {
                View child = hardnessGroup.getChildAt(i);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (child.getTag() != null && child.getTag().equals(selectedHardness[0].name())) {
                    child.setSelected(true);
                    if (child instanceof TextView)
                        ((TextView) child).setTextColor(Color.WHITE);
                } else {
                    child.setSelected(false);
                    if (child instanceof TextView)
                        ((TextView) child).setTextColor(Color.parseColor("#757575"));
                }
            }
        };

        for (int i = 0; i < hardnessGroup.getChildCount(); i++) {
            View child = hardnessGroup.getChildAt(i);
            child.setOnClickListener(v -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (v.getTag() != null) {
                    selectedHardness[0] = ZoomDrawingView.Hardness.valueOf(v.getTag().toString());
                    updateHardnessSelectionUI.run();
                }
            });
        }
        updateHardnessSelectionUI.run();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnCancelPen != null)
            btnCancelPen.setOnClickListener(v -> dialog.dismiss());
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSavePen != null) {
            btnSavePen.setOnClickListener(v -> {
                drawingView.setBrushType(selectedType[0]);
                drawingView.setHardness(selectedHardness[0]);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (seekOpacityPen != null) {
                    drawingView.setBrushOpacityPercent(seekOpacityPen.getProgress());
                }

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (toolPen != null) {
                    ImageView icon = (ImageView) toolPen.getChildAt(0);
                    TextView text = (TextView) toolPen.getChildAt(1);

                    int resId = R.drawable.ic_brush_ink;
                    String name = "Bút mực";
                    switch (selectedType[0]) {
                        case PENCIL:
                            resId = R.drawable.ic_brush_pencil;
                            name = "Bút chì";
                            break;
                        case WATERCOLOR:
                            resId = R.drawable.ic_brush_watercolor;
                            name = "Bút lông";
                            break;
                        case CRAYON:
                            resId = R.drawable.ic_brush_crayon;
                            name = "Bút sáp";
                            break;
                        case INK:
                            resId = R.drawable.ic_brush_ink;
                            name = "Bút mực";
                            break;
                        case MARKER:
                            resId = R.drawable.ic_brush_marker;
                            name = "Đánh dấu";
                            break;
                        case AIRBRUSH:
                            resId = R.drawable.ic_brush_airbrush;
                            name = "Xịt sơn";
                            break;
                        case CALLIGRAPHY:
                            resId = R.drawable.ic_brush_calligraphy;
                            name = "Thư pháp";
                            break;
                        case OIL:
                            resId = R.drawable.ic_brush_oil;
                            name = "Bút dầu";
                            break;
                    }
                    icon.setImageResource(resId);
                    text.setText(name);
                }

                selectTool(toolPen);
                dialog.dismiss();
            });
        }

        View parent = (View) view.getParent();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (parent != null) {
            parent.setBackgroundColor(Color.WHITE);
        }

        dialog.show();
    }

    /**
     * Hàm showColorPickerDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showColorPickerDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        View view = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        dialog.setContentView(view);

        ColorPickerView colorPicker = view.findViewById(R.id.colorWheel);
        android.widget.SeekBar seekOpacity = view.findViewById(R.id.dialogOpacity);
        android.widget.Button btnCancel = view.findViewById(R.id.btnCancel);
        android.widget.Button btnSave = view.findViewById(R.id.btnSave);

        // Base colors removed from XML, so skip baseGrid

        android.content.SharedPreferences prefs = getSharedPreferences("ArtCraftPrefs", MODE_PRIVATE);
        String recentStr = prefs.getString("recent_colors_new", "-11559441,-13369549,-898075,-16777216,-1509930");
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        java.util.List<Integer> recentColors = new java.util.ArrayList<>();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (!recentStr.isEmpty()) {
            for (String s : recentStr.split(",")) {
                try {
                    recentColors.add(Integer.parseInt(s));
                } catch (Exception ignored) {
                }
            }
        }

        LinearLayout recentGrid = view.findViewById(R.id.recentColorsGrid);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (recentGrid != null) {
            recentGrid.removeAllViews();
            for (int color : recentColors) {
                View colorView = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(90, 90); // ~32dp
                lp.setMarginEnd(24);
                colorView.setLayoutParams(lp);
                colorView.setBackgroundColor(color);
                colorView.setClickable(true);
                colorView.setOnClickListener(v -> {
                    colorPicker.setCurrentColor(color);
                    drawingView.setBrushColor(color);
                });
                recentGrid.addView(colorView);
            }
        }

        colorPicker.setOnColorSelectedListener(color -> {
            drawingView.setBrushColor(color);
        });

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (seekOpacity != null) {
            seekOpacity.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
                @Override
                /**
                 * Hàm onProgressChanged() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param progress tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param fromUser tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                    drawingView.setBrushOpacityPercent(progress);
                }

                @Override
                /**
                 * Hàm onStartTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
                }

                @Override
                /**
                 * Hàm onStopTrackingTouch() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param seekBar tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                }
            });
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnCancel != null)
            btnCancel.setOnClickListener(v -> dialog.dismiss());
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                int selected = colorPicker.getCurrentColor();
                if (recentColors.contains(selected))
                    recentColors.remove((Integer) selected);
                recentColors.add(0, selected);
                if (recentColors.size() > 7)
                    recentColors.remove(recentColors.size() - 1);

                StringBuilder sb = new StringBuilder();
                for (int c : recentColors)
                    sb.append(c).append(",");
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);

                prefs.edit().putString("recent_colors_new", sb.toString()).apply();

                dialog.dismiss();
            });
        }

        View parent = (View) view.getParent();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (parent != null) {
            parent.setBackgroundColor(Color.WHITE);
        }

        dialog.show();
    }

    /**
     * Hàm selectTool() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param selectedTool tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void selectTool(LinearLayout selectedTool) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolPen != null)
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            toolPen.setBackground(null);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolEraser != null)
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            toolEraser.setBackground(null);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolFill != null)
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            toolFill.setBackground(null);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolClear != null)
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            toolClear.setBackground(null);

        resetToolColor(toolPen, "#5F6368");
        resetToolColor(toolEraser, "#5F6368");
        resetToolColor(toolFill, "#5F6368");
        resetToolColor(toolClear, "#D93025");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (selectedTool != null) {
            selectedTool.setBackgroundResource(R.drawable.selected_tool_bg);
            resetToolColor(selectedTool, "#1A73E8");
        }
    }

    /**
     * Hàm resetToolColor() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param tool tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param colorStr tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void resetToolColor(LinearLayout tool, String colorStr) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tool == null)
            return;
        int color = Color.parseColor(colorStr);
        for (int i = 0; i < tool.getChildCount(); i++) {
            View child = tool.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(color);
            } else if (child instanceof TextView) {
                TextView tv = (TextView) child;
                tv.setTextColor(color);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                tv.setTypeface(null,
                        colorStr.equals("#1A73E8") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            }
        }
    }

    /**
     * Hàm saveImageToGallery() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void saveImageToGallery() {
        android.graphics.Bitmap bitmap = drawingView.exportBitmap();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (bitmap == null) {
            android.widget.Toast.makeText(this, "Không có nội dung để xuất", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        java.io.OutputStream fos;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                android.content.ContentResolver resolver = getContentResolver();
                android.content.ContentValues contentValues = new android.content.ContentValues();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                contentValues.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME,
                        "ArtCraft_" + System.currentTimeMillis() + ".png");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                contentValues.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                contentValues.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH,
                        android.os.Environment.DIRECTORY_PICTURES + "/ArtCraft");
                android.net.Uri imageUri = resolver
                        .insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);
            } else {
                String imagesDir = android.os.Environment
                        .getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES).toString();
                java.io.File dir = new java.io.File(imagesDir, "ArtCraft");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (!dir.exists())
                    dir.mkdirs();
                java.io.File image = new java.io.File(dir, "ArtCraft_" + System.currentTimeMillis() + ".png");
                fos = new java.io.FileOutputStream(image);
            }

            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fos);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (fos != null) {
                fos.flush();
                fos.close();
                android.widget.Toast.makeText(this, "Đã lưu ảnh vào Thư viện", android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Exception e) {
            android.widget.Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * Hàm showSaveArtworkDialog() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void showSaveArtworkDialog() {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String passedProjectId = getIntent().getStringExtra("PROJECT_ID");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String passedArtworkId = getIntent().getStringExtra("ARTWORK_ID");

        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(
                this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        View view = getLayoutInflater().inflate(R.layout.dialog_save_artwork, null);
        dialog.setContentView(view);

        android.widget.EditText etArtworkName = view.findViewById(R.id.et_artwork_name);
        
        TextView txtProjectName = findViewById(R.id.txtProjectName);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (txtProjectName != null && txtProjectName.getText() != null && !txtProjectName.getText().toString().trim().isEmpty() && !txtProjectName.getText().toString().equals("Tên dự án") && !txtProjectName.getText().toString().equals("Tên tác phẩm")) {
            etArtworkName.setText(txtProjectName.getText().toString().trim());
        }

        LinearLayout layoutProjectSelection = view.findViewById(R.id.layout_project_selection);
        android.widget.Spinner spinnerProjects = view.findViewById(R.id.spinner_projects);
        LinearLayout layoutNewProjectName = view.findViewById(R.id.layout_new_project_name);
        android.widget.EditText etNewProjectName = view.findViewById(R.id.et_new_project_name);
        com.google.android.material.button.MaterialButton btnConfirm = view.findViewById(R.id.btn_confirm_save_artwork);

        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        java.util.List<com.example.appdraw.model.Project> fetchedProjects = new java.util.ArrayList<>();
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        java.util.List<String> spinnerAdapterList = new java.util.ArrayList<>();
        android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerAdapterList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        spinnerProjects.setAdapter(spinnerAdapter);

        android.widget.RadioGroup rgStatus = view.findViewById(R.id.rg_status);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (passedProjectId != null) {
            layoutProjectSelection.setVisibility(View.GONE);
            layoutNewProjectName.setVisibility(View.GONE);
        } else {
            layoutProjectSelection.setVisibility(View.VISIBLE);
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (uid != null) {
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Projects")
                        .whereEqualTo("uid", uid)
                        .get()
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            fetchedProjects.clear();
                            spinnerAdapterList.clear();
                            spinnerAdapterList.add("[ + Tạo dự án mới ]");
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                try {
                                    com.example.appdraw.model.Project p = doc
                                            .toObject(com.example.appdraw.model.Project.class);
                                    fetchedProjects.add(p);
                                    spinnerAdapterList.add(p.getName());
                                } catch (Exception ignored) {
                                }
                            }
                            spinnerAdapter.notifyDataSetChanged();
                        });
            }

            spinnerProjects.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                /**
                 * Hàm onItemSelected() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 * @param id tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        layoutNewProjectName.setVisibility(View.VISIBLE);
                    } else {
                        layoutNewProjectName.setVisibility(View.GONE);
                    }
                }

                @Override
                /**
                 * Hàm onNothingSelected() thực hiện một phần xử lý trong luồng chức năng của lớp DrawingActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                }
            });
        }

        btnConfirm.setOnClickListener(v -> {
            String artworkName = etArtworkName.getText().toString().trim();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (artworkName.isEmpty()) {
                artworkName = "Không tên";
            }

            String selectedStatus = com.example.appdraw.model.Artwork.STATUS_DRAFT;
            int selectedId = rgStatus.getCheckedRadioButtonId();
            if (selectedId == R.id.rb_completed) {
                selectedStatus = com.example.appdraw.model.Artwork.STATUS_COMPLETED;
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (passedProjectId != null) {
                dialog.dismiss();
                doSaveArtworkProcess(passedProjectId, passedArtworkId, artworkName, selectedStatus);
            } else {
                int selectedPos = spinnerProjects.getSelectedItemPosition();
                if (selectedPos == 0) {
                    String newProjName = etNewProjectName.getText().toString().trim();
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (newProjName.isEmpty()) {
                        android.widget.Toast
                                .makeText(this, "Vui lòng nhập tên dự án mới", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    dialog.dismiss();
                    createNewProjectAndSave(newProjName, artworkName, selectedStatus);
                } else if (selectedPos > 0) {
                    dialog.dismiss();
                    com.example.appdraw.model.Project selectedProj = fetchedProjects.get(selectedPos - 1);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    doSaveArtworkProcess(selectedProj.getId(), null, artworkName, selectedStatus);
                } else {
                    android.widget.Toast
                            .makeText(this, "Vui lòng đợi dữ liệu tải xong", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });

        View parent = (View) view.getParent();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (parent != null) {
            parent.setBackgroundColor(Color.WHITE);
        }

        dialog.show();
    }

    /**
     * Hàm createNewProjectAndSave() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param projName tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param artworkName tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param selectedStatus tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void createNewProjectAndSave(String projName, String artworkName, String selectedStatus) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null)
            return;

        android.widget.Toast.makeText(this, "Đang khởi tạo dự án...", android.widget.Toast.LENGTH_SHORT).show();

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                .getInstance();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String docId = db.collection("Projects").document().getId();

        com.example.appdraw.model.Project newProj = new com.example.appdraw.model.Project(docId, uid, projName, "", "",
                "", 0, System.currentTimeMillis());

        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Projects").document(docId).set(newProj)
                .addOnSuccessListener(aVoid -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    doSaveArtworkProcess(docId, null, artworkName, selectedStatus);
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast
                            .makeText(this, "Lỗi tạo dự án: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT)
                            .show();
                });
    }

    /**
     * Hàm doSaveArtworkProcess() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param projectId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param artworkId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param artworkName tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param selectedStatus tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void doSaveArtworkProcess(String projectId, String artworkId, String artworkName, String selectedStatus) {
        android.graphics.Bitmap bitmap = drawingView.exportBitmap();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (bitmap == null)
            return;

        android.widget.Toast.makeText(this, "Đang lưu tác phẩm...", android.widget.Toast.LENGTH_SHORT).show();

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                .getInstance();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        final String targetArtworkId = (artworkId != null && !artworkId.isEmpty()) ? artworkId
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                : db.collection("Artworks").document().getId();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        // Nén dung lượng nhỏ gọn để nhét vừa Firestore (Max 1MB)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        String base64Image = "data:image/jpeg;base64,"
                + android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (artworkId != null && !artworkId.isEmpty()) {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Artworks").document(artworkId)
                    .update("title", artworkName, "imageUrl", base64Image, "status", selectedStatus)
                    .addOnSuccessListener(aVoid -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Projects").document(projectId).update("coverImageUrl", base64Image);
                        android.widget.Toast
                                .makeText(this, "Đã lưu tác phẩm thành công!", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        android.content.Intent resultIntent = new android.content.Intent();
                        resultIntent.putExtra("SAVED_BASE64", base64Image);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }).addOnFailureListener(e -> {
                        android.widget.Toast
                                .makeText(this, "Lỗi lưu DB: " + e.getMessage(), android.widget.Toast.LENGTH_LONG)
                                .show();
                    });
        } else {
            com.example.appdraw.model.Artwork newArtwork = new com.example.appdraw.model.Artwork(targetArtworkId, uid,
                    projectId, artworkName, base64Image, selectedStatus, System.currentTimeMillis());
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Artworks").document(targetArtworkId).set(newArtwork)
                    .addOnSuccessListener(aVoid -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Projects").document(projectId).update("artworkCount",
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                com.google.firebase.firestore.FieldValue.increment(1),
                                "coverImageUrl", base64Image);
                        android.widget.Toast
                                .makeText(this, "Đã lưu tác phẩm thành công!", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        android.content.Intent resultIntent = new android.content.Intent();
                        resultIntent.putExtra("SAVED_BASE64", base64Image);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }).addOnFailureListener(e -> {
                        android.widget.Toast
                                .makeText(this, "Lỗi lưu DB: " + e.getMessage(), android.widget.Toast.LENGTH_LONG)
                                .show();
                    });
        }
    }
}
