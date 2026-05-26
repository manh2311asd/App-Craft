package com.example.appdraw.explore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp LessonListActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file LessonListActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class LessonListActivity extends AppCompatActivity {
    /**
     * Biến `db` lưu dữ liệu/trạng thái quan trọng kiểu FirebaseFirestore, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseFirestore db;
    /**
     * Biến `auth` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private FirebaseAuth auth;
    /**
     * Biến `isSeeding` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isSeeding = false;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String titleHeader = getIntent().getStringExtra("TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (titleHeader == null)
            titleHeader = "Bài học gợi ý";
        ((TextView) findViewById(R.id.tv_toolbar_title)).setText(titleHeader);

        Toolbar toolbar = findViewById(R.id.toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        auth = FirebaseAuth.getInstance();

        // Xóa loadAllLessons() trong onCreate vì onResume() sẽ chạy ngay sau đó,
        // tránh đúp truy vấn gây race-condition.
    }

    /**
     * Hàm loadAllLessons() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadAllLessons() {
        android.widget.LinearLayout container = findViewById(R.id.lesson_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null) return;
        container.removeAllViews();

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String titleHeader = getIntent().getStringExtra("TITLE");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (titleHeader == null) titleHeader = "Bài học gợi ý";

        final String finalTitleHeader = titleHeader;
        String uid = auth.getUid();

        if ("Bài học gợi ý".equals(finalTitleHeader)) {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            db.collection("Users").document(uid != null ? uid : "null").collection("lessonProgress").get().addOnSuccessListener(progSnap -> {
                java.util.Set<String> completedTitles = new java.util.HashSet<>();
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (DocumentSnapshot d : progSnap) {
                    if ("COMPLETED".equals(d.getString("status"))) completedTitles.add(d.getId());
                }

                db.collection("Lessons").get().addOnSuccessListener(lessonSnap -> {
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.List<DocumentSnapshot> allDocs = new java.util.ArrayList<>();
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (DocumentSnapshot d : lessonSnap) allDocs.add(d);
                    allDocs.sort((d1, d2) -> {
                        Long c1 = d1.getLong("createdAt");
                        Long c2 = d2.getLong("createdAt");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (c1 != null && c2 != null) return Long.compare(c1, c2);
                        
                        String id1 = d1.getId(); String id2 = d2.getId();
                        try {
                            int idx1 = Integer.parseInt(id1.substring(id1.lastIndexOf("_") + 1));
                            int idx2 = Integer.parseInt(id2.substring(id2.lastIndexOf("_") + 1));
                            return Integer.compare(idx1, idx2);
                        } catch (Exception e) { return id1.compareTo(id2); }
                    });

                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.Map<String, java.util.List<DocumentSnapshot>> lessonsByCategory = new java.util.HashMap<>();
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (DocumentSnapshot doc : allDocs) {
                        String cat = doc.getString("category");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (cat == null) cat = "Khác";
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        if (!lessonsByCategory.containsKey(cat)) lessonsByCategory.put(cat, new java.util.ArrayList<>());
                        lessonsByCategory.get(cat).add(doc);
                    }

                    String[] coreCategories = { "Dành cho người mới bắt đầu", "Vẽ thiên nhiên", "Khám phá màu nước", "Nghệ thuật vẽ Chibi", "Chân dung Manga" };
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.List<DocumentSnapshot> displayDocs = new java.util.ArrayList<>();
                    for (String cat : coreCategories) {
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        java.util.List<DocumentSnapshot> catLessons = lessonsByCategory.get(cat);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (catLessons != null) {
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            for (DocumentSnapshot doc : catLessons) {
                                String t = doc.getString("title");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (t != null && !completedTitles.contains(t)) {
                                    displayDocs.add(doc);
                                    break;
                                }
                            }
                        }
                    }
                    renderLessons(displayDocs, finalTitleHeader, container, uid);
                });
            });
        } else {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Lessons").whereEqualTo("category", finalTitleHeader).get().addOnSuccessListener(queryDocumentSnapshots -> {
                boolean needsReseed = false;
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (queryDocumentSnapshots.isEmpty()) {
                    needsReseed = true;
                } else {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String t = doc.getString("title");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (t != null && (t.contains("Khởi động với " + finalTitleHeader) ||
                                t.contains("Thực hành " + finalTitleHeader) ||
                                t.contains("Nâng cao " + finalTitleHeader) ||
                                t.contains("Kiểm tra cuối khóa " + finalTitleHeader) ||
                                t.equals("Bài 1: Khởi động với Dành cho người mới bắt đầu") ||
                                t.matches("^Bài \\d+:.*") ||
                                t.equals("Bài tập ôn luyện") ||
                                t.contains("/"))) {
                            needsReseed = true;
                            break;
                        }
                    }
                }

                if (needsReseed) {
                    seedLessonsForCategory(finalTitleHeader);
                    return;
                }

                patchLessonImages(finalTitleHeader);

                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<DocumentSnapshot> displayDocs = new java.util.ArrayList<>();
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (DocumentSnapshot d : queryDocumentSnapshots) displayDocs.add(d);
                displayDocs.sort((d1, d2) -> {
                        Long c1 = d1.getLong("createdAt");
                        Long c2 = d2.getLong("createdAt");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (c1 != null && c2 != null) return Long.compare(c1, c2);
                        
                    String id1 = d1.getId(); String id2 = d2.getId();
                    try {
                        int idx1 = Integer.parseInt(id1.substring(id1.lastIndexOf("_") + 1));
                        int idx2 = Integer.parseInt(id2.substring(id2.lastIndexOf("_") + 1));
                        return Integer.compare(idx1, idx2);
                    } catch (Exception e) { return id1.compareTo(id2); }
                });

                renderLessons(displayDocs, finalTitleHeader, container, uid);
            });
        }
    }

    /**
     * Hàm renderLessons() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param displayDocs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param finalTitleHeader tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param container tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param uid tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private void renderLessons(java.util.List<DocumentSnapshot> displayDocs, String finalTitleHeader, android.widget.LinearLayout container, String uid) {
        LayoutInflater inflater = LayoutInflater.from(this);
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        for (DocumentSnapshot doc : displayDocs) {
            String title = doc.getString("title");

            String author = doc.getString("author");
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (author == null)
                author = doc.getString("authorName");

            String imageResStr = doc.getString("imageRes");
            String imageUrl = doc.getString("thumbnailUrl");
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = doc.getString("imageUrl");
            }

            if ("Đêm trăng sáng trên đồi".equals(title)) {
                imageResStr = "dem_trang_sang_tren_doi";
            } else if ("Khu vườn nhiệt đới".equals(title)) {
                imageResStr = "khu_vuon_nhiet_doi";
            } else if ("Thung lũng sương mù".equals(title)) {
                imageResStr = "thung_lung_suong_mu";
            } else if ("Vẽ rừng cây mùa thu".equals(title)) {
                imageResStr = "ve_rung_cay_mua_thu";
            } else if ("Tổng hợp phong cảnh".equals(title)) {
                imageResStr = "tong_hop_phong_canh";
            } else if ("Bãi biển lúc hoàng hôn".equals(title)) {
                imageResStr = "bai_bien_luc_hoang_hon";
            } else if ("Núi non trùng điệp".equals(title)) {
                imageResStr = "nui_non_trung_diep";
            } else if ("Dòng suối nhỏ trong vắt".equals(title)) {
                imageResStr = "dong_suoi_nho_trong_vat";
            } else if ("Thảo nguyên xanh mướt".equals(title)) {
                imageResStr = "thao_nguyen_xanh_muot";
            } else if ("Vẽ thác nước hùng vĩ".equals(title)) {
                imageResStr = "ve_thac_nuoc_hung_vi";
            } else if ("Làm quen với Brush".equals(title)) {
                imageResStr = "lam_quen_voi_brush";
            } else if ("Khái niệm hình học".equals(title)) {
                imageResStr = "khai_niem_hinh_hoc";
            } else if ("Đánh bóng và chiếu sáng".equals(title)) {
                imageResStr = "danh_bong_va_chieu_sang";
            } else if ("Kỹ thuật đan nét cọ".equals(title)) {
                imageResStr = "ki_thuat_dan_net_co";
            } else if ("Vẽ tĩnh vật quả táo".equals(title)) {
                imageResStr = "ve_tinh_vat_qua_tao";
            } else if ("Xây dựng khối 3D".equals(title)) {
                imageResStr = "xay_dung_khoi_3d";
            } else if ("Luyện tập tổng hợp".equals(title)) {
                imageResStr = "luyen_tap_tong_hop";
            } else if ("Palette pha màu cơ bản".equals(title)) {
                imageResStr = "palette_pha_mau_co_ban";
            } else if ("Kỹ thuật loang màu ẩm".equals(title)) {
                imageResStr = "ki_thuat_loang_mau_am";
            } else if ("Vẽ bầu trời gợn mây".equals(title)) {
                imageResStr = "ve_bau_troi_gon_may";
            } else if ("Tĩnh vật cốc cà phê".equals(title)) {
                imageResStr = "tinh_vat_coc_ca_phe";
            } else if ("Bông cẩm tú cầu".equals(title)) {
                imageResStr = "bong_cam_tu_cau";
            } else if ("Sơn thủy hữu tình".equals(title)) {
                imageResStr = "son_thuy_huu_tinh";
            } else if ("Ánh tà dương hoàng hôn".equals(title)) {
                imageResStr = "anh_ta_duong_hoang_hon";
            } else if ("Phác thảo khuôn mặt Chibi".equals(title)) {
                imageResStr = "phac_thao_khuon_mat_chibi";
            } else if ("Tỷ lệ cơ thể đầu to".equals(title)) {
                imageResStr = "ty_le_co_the_dau_to";
            } else if ("Vẽ mắt to tròn đáng yêu".equals(title)) {
                imageResStr = "ve_mat_to_tron_dang_yeu";
            } else if ("Biểu cảm khuôn mặt dễ thương".equals(title)) {
                imageResStr = "bieu_cam_khuon_mat_de_thuong";
            } else if ("Vẽ tóc bồng bềnh".equals(title)) {
                imageResStr = "ve_toc_bong_benh";
            } else if ("Phối đồ phong cách basic".equals(title)) {
                imageResStr = "phoi_do_phong_cach_basic";
            } else if ("Lên màu pastel cơ bản".equals(title)) {
                imageResStr = "len_mau_pastel_co_ban";
            } else if ("Hoàn thiện nhân vật".equals(title)) {
                imageResStr = "hoan_thien_nhan_vat";
            } else if ("Core tỷ lệ khuôn mặt".equals(title)) {
                imageResStr = "core_ty_le_khuon_mat";
            } else if ("Vẽ mắt Manga mượt mà".equals(title)) {
                imageResStr = "ve_mat_manga_muot_ma";
            } else if ("Kiểu tóc nam và nữ cơ bản".equals(title)) {
                imageResStr = "kieu_toc_nam_va_nu_co_ban";
            } else if ("Mảng biểu cảm vui buồn".equals(title)) {
                imageResStr = "mang_bieu_cam_vui_buon";
            } else if ("Góc nghiêng thần thánh".equals(title)) {
                imageResStr = "goc_nghieng_than_thanh";
            } else if ("Phác họa nhân vật nữ".equals(title)) {
                imageResStr = "phac_hoa_nhan_vat_nu";
            } else if ("Phác họa nhân vật nam".equals(title)) {
                imageResStr = "phac_hoa_nhan_vat_nam";
            }

            View lessonView = inflater.inflate(R.layout.item_lesson_list, container, false);

            TextView tvTitle = lessonView.findViewById(R.id.tv_lesson_title);
            TextView tvAuthor = lessonView.findViewById(R.id.tv_author);
            ImageView ivThumb = lessonView.findViewById(R.id.iv_lesson_thumb);
            TextView tvStatus = lessonView.findViewById(R.id.tv_status);
            TextView tvDuration = lessonView.findViewById(R.id.tv_duration);

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvTitle != null)
                tvTitle.setText(title);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvAuthor != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (author != null && !author.toLowerCase().startsWith("bởi")) {
                    tvAuthor.setText("Bởi " + author);
                } else {
                    tvAuthor.setText(author);
                }
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (ivThumb != null) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (imageResStr != null && !imageResStr.isEmpty() && !imageResStr.matches("-?\\d+")) {
                    try {
                        int resId = getResources().getIdentifier(imageResStr, "drawable", getPackageName());
                        if (resId != 0)
                            ivThumb.setImageResource(resId);
                    } catch (Exception e) {
                    }
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                } else if (imageUrl != null && !imageUrl.isEmpty()) {
                    if (imageUrl.startsWith("data:image")) {
                        try {
                            byte[] imageByteArray = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            Glide.with(this).load(imageByteArray).centerCrop().into(ivThumb);
                        } catch (Exception e) {}
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        Glide.with(this).load(imageUrl).centerCrop().into(ivThumb);
                    }
                }
            }

            tvStatus.setText("Chưa học");
            tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
            tvStatus.setTextColor(Color.parseColor("#808080"));

            RatingBar rb = lessonView.findViewById(R.id.rating_bar);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (rb != null) {
                rb.setRating(4.5f);
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tvDuration != null) {
                Long actualDuration = doc.getLong("durationMin");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (actualDuration != null && actualDuration > 0) {
                    tvDuration.setText(actualDuration + " min");
                } else {
                    String catCheck = finalTitleHeader.toLowerCase();
                    if (catCheck.contains("mới bắt đầu") || catCheck.contains("beginner")) {
                        tvDuration.setText("20 min");
                    } else if (catCheck.contains("thiên nhiên") || catCheck.contains("màu nước")) {
                        tvDuration.setText("45 min");
                    } else {
                        tvDuration.setText("60 min");
                    }
                }
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (uid != null && title != null && !title.contains("/")) {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(uid).collection("lessonProgress").document(title)
                        .get().addOnSuccessListener(progDoc -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (progDoc.exists()) {
                                String status = progDoc.getString("status");
                                if ("COMPLETED".equals(status)) {
                                    tvStatus.setText("Hoàn thành");
                                    tvStatus.setBackgroundResource(R.drawable.bg_badge_completed);
                                    tvStatus.setTextColor(Color.WHITE);
                                } else if ("IN_PROGRESS".equals(status) || "WAITING_FOR_HOMEWORK".equals(status)) {
                                    tvStatus.setText("Đang học");
                                    tvStatus.setBackgroundResource(R.drawable.bg_badge_in_progress);
                                    tvStatus.setTextColor(Color.WHITE);
                                }
                            }
                        });
            }

            final String finalImageRes = imageResStr;
            final String finalAuthor = author;
            final String finalDocId = doc.getId();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            String linkCategory = doc.getString("category") != null ? doc.getString("category") : finalTitleHeader;
            
            lessonView.setOnClickListener(v -> {
                String currentStatus = tvStatus.getText().toString();
                if ("Hoàn thành".equals(currentStatus)) {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(this, com.example.appdraw.explore.MySubmissionActivity.class);
                    intent.putExtra("LESSON_TITLE", title);
                    startActivity(intent);
                } else {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(this, LessonDetailActivity.class);
                    intent.putExtra("LESSON_TITLE", title);
                    intent.putExtra("CATEGORY", linkCategory);
                    intent.putExtra("IMAGE_RES", finalImageRes);
                    intent.putExtra("AUTHOR", finalAuthor);
                    intent.putExtra("LESSON_ID", finalDocId);
                    startActivity(intent);
                }
            });

            container.addView(lessonView);
        }
    }

    /**
     * Hàm seedLessonsForCategory() thực hiện một phần xử lý trong luồng chức năng của lớp LessonListActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param category tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void seedLessonsForCategory(String category) {
        if (isSeeding)
            return;
        isSeeding = true;

        db.collection("Lessons").whereEqualTo("category", category).get().addOnSuccessListener(snap -> {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            for (DocumentSnapshot doc : snap) {
                doc.getReference().delete();
            }

            String author = "Bởi Hải Nam";
            String[] titles;
            String[] images; // Mỗi bài một ảnh riêng

            if (category.contains("thiên nhiên")) {
                author = "Bởi Thu Thủy";
                titles = new String[] {
                        "Vẽ rừng cây mùa thu",
                        "Dòng suối nhỏ trong vắt",
                        "Núi non trùng điệp",
                        "Bãi biển lúc hoàng hôn",
                        "Thảo nguyên xanh mướt",
                        "Đêm trăng sáng trên đồi",
                        "Thung lũng sương mù",
                        "Khu vườn nhiệt đới",
                        "Vẽ thác nước hùng vĩ",
                        "Tổng hợp phong cảnh"
                };
                images = new String[] {
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "dem_trang_sang_tren_doi",
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "ve_thien_nhien",
                        "ve_thien_nhien"
                };
            } else if (category.contains("Chibi")) {
                author = "Bởi Minh Khang";
                titles = new String[] {
                        "Phác thảo khuôn mặt Chibi",
                        "Tỷ lệ cơ thể đầu to",
                        "Vẽ mắt to tròn đáng yêu",
                        "Biểu cảm khuôn mặt dễ thương",
                        "Vẽ tóc bồng bềnh",
                        "Phối đồ phong cách basic",
                        "Lên màu pastel cơ bản",
                        "Hoàn thiện nhân vật"
                };
                images = new String[] { "tp_trending_3", "tp_trending_3", "tp_trending_3", "tp_trending_3",
                        "tp_trending_3", "tp_trending_3", "tp_trending_3", "tp_trending_3" };
            } else if (category.contains("Manga")) {
                author = "Bởi Hương Lan";
                titles = new String[] {
                        "Core tỷ lệ khuôn mặt",
                        "Vẽ mắt Manga mượt mà",
                        "Kiểu tóc nam và nữ cơ bản",
                        "Mảng biểu cảm vui buồn",
                        "Góc nghiêng thần thánh",
                        "Phác họa nhân vật nữ",
                        "Phác họa nhân vật nam"
                };
                images = new String[] { "core_ty_le_khuon_mat", "tp_trending_2", "tp_trending_2", "tp_trending_2",
                        "tp_trending_2", "tp_trending_2", "tp_trending_2" };
            } else if (category.contains("màu nước")) {
                author = "Bởi Tuấn Vũ";
                titles = new String[] {
                        "Palette pha màu cơ bản",
                        "Kỹ thuật loang màu ẩm",
                        "Vẽ bầu trời gợn mây",
                        "Tĩnh vật cốc cà phê",
                        "Bông cẩm tú cầu",
                        "Sơn thủy hữu tình",
                        "Ánh tà dương hoàng hôn"
                };
                images = new String[] { "banner_watercolor", "banner_watercolor", "banner_watercolor",
                        "banner_watercolor", "banner_watercolor", "banner_watercolor", "banner_watercolor" };
            } else { // Người mới
                author = "Bởi Phong Artist";
                titles = new String[] {
                        "Làm quen với Brush",
                        "Khái niệm hình học",
                        "Đánh bóng và chiếu sáng",
                        "Kỹ thuật đan nét cọ",
                        "Vẽ tĩnh vật quả táo",
                        "Xây dựng khối 3D",
                        "Luyện tập tổng hợp"
                };
                images = new String[] { "ve_hoa_mau_nuoc", "ve_hoa_mau_nuoc", "ve_hoa_mau_nuoc", "ve_hoa_mau_nuoc",
                        "ve_hoa_mau_nuoc", "ve_hoa_mau_nuoc", "ve_hoa_mau_nuoc" };
            }

            for (int i = 0; i < titles.length; i++) {
                String title = titles[i];
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                data.put("title", title);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                data.put("authorName", author);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                data.put("imageRes", images[i]);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                data.put("category", category);

                String safeDocId = "lesson_" + Math.abs(category.hashCode()) + "_" + i;
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Lessons").document(safeDocId).set(data);
            }

            // Gọi tải lại dữ liệu đảm bảo không trễ UI
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                isSeeding = false;
                loadAllLessons();
            }, 1000);
        });
    }

    /** Patch ảnh đúng cho các bài học đã seed trước đó (chạy 1 lần khi load) */
    /**
     * Hàm patchLessonImages() thực hiện một phần xử lý trong luồng chức năng của lớp LessonListActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param category tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void patchLessonImages(String category) {
        if (category.contains("thiên nhiên")) {
            String docId = "lesson_" + Math.abs(category.hashCode()) + "_5";
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Lessons").document(docId).get().addOnSuccessListener(doc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (doc.exists()) {
                    String currentImg = doc.getString("imageRes");
                    if (!"dem_trang_sang_tren_doi".equals(currentImg)) {
                        doc.getReference().update("imageRes", "dem_trang_sang_tren_doi");
                    }
                }
            });
        } else if (category.contains("Manga")) {
            String docId = "lesson_" + Math.abs(category.hashCode()) + "_0";
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Lessons").document(docId).get().addOnSuccessListener(doc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (doc.exists()) {
                    String currentImg = doc.getString("imageRes");
                    if (!"core_ty_le_khuon_mat".equals(currentImg)) {
                        doc.getReference().update("imageRes", "core_ty_le_khuon_mat");
                    }
                }
            });
        }
    }

    @Override
    /**
     * Hàm onResume() thực hiện một phần xử lý trong luồng chức năng của lớp LessonListActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    protected void onResume() {
        super.onResume();
        if (!isSeeding) {
            loadAllLessons();
        }
    }
}
