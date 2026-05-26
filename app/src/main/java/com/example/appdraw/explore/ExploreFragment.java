package com.example.appdraw.explore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.appdraw.R;

/**
 * Fragment hiển thị trang Khám phá (Explore) của ứng dụng App-Draw.
 * Người thực hiện: Lê Thùy Linh (UC-04, UC-08).
 * Quản lý giao diện tìm kiếm, các danh mục bài học động và danh sách tác phẩm nổi bật (Trending).
 */
/**
 * Lớp ExploreFragment thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ExploreFragment.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ExploreFragment extends Fragment {

    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    @Nullable
    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // --- Search Bar ---
        View cardSearch = view.findViewById(R.id.card_search);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cardSearch != null) {
            cardSearch.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            });
        }

        // --- Category Chips ---
        setupCategoryChips(view);

        // --- Banner ---
        View btnExploreNow = view.findViewById(R.id.btn_explore_now);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnExploreNow != null) {
            btnExploreNow.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), LessonListActivity.class);
                intent.putExtra("TITLE", "Khám phá màu nước");
                startActivity(intent);
            });
        }

        setupDynamicCategories(view);
        setupTrendingData(view);
        setupDynamicMentors(view);

        // --- View All Handlers ---
        View tvViewAllCategories = view.findViewById(R.id.tv_view_all_categories);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewAllCategories != null) {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            tvViewAllCategories.setOnClickListener(v -> startActivity(new Intent(getActivity(), AllCategoriesActivity.class)));
        }

        return view;
    }

    /**
     * Khởi tạo các sự kiện cho các khối (chip) phân loại danh mục (Chủ đề, Kỹ thuật...).
     * @param view View gốc của Fragment chứa các chip.
     */
    /**
     * Hàm setupCategoryChips() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupCategoryChips(View view) {
        View chipTopic = view.findViewById(R.id.chip_topic);
        View chipTechnique = view.findViewById(R.id.chip_technique);
        View chipMaterials = view.findViewById(R.id.chip_materials);
        View chipLevel = view.findViewById(R.id.chip_level);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (chipTopic != null)
            chipTopic.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn Chủ đề", Toast.LENGTH_SHORT).show());
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (chipTechnique != null)
            chipTechnique
                    .setOnClickListener(v -> Toast.makeText(getContext(), "Chọn Kỹ thuật", Toast.LENGTH_SHORT).show());
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (chipMaterials != null)
            chipMaterials
                    .setOnClickListener(v -> Toast.makeText(getContext(), "Chọn Vật liệu", Toast.LENGTH_SHORT).show());
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (chipLevel != null)
            chipLevel.setOnClickListener(v -> Toast.makeText(getContext(), "Chọn Level", Toast.LENGTH_SHORT).show());
    }

    /**
     * Tải và hiển thị danh sách các Danh mục học tập từ Firestore (Collection "Categories").
     * @param view View gốc của Fragment.
     */
    /**
     * Hàm setupDynamicCategories() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupDynamicCategories(View view) {
        android.widget.LinearLayout container = view.findViewById(R.id.ll_categories_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null)
            return;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                .getInstance();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Categories").orderBy("order").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            boolean isCorrupted = !queryDocumentSnapshots.isEmpty()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    && queryDocumentSnapshots.getDocuments().get(0).getString("imageRes") != null
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    && queryDocumentSnapshots.getDocuments().get(0).getString("imageRes").matches("-?\\d+");

            boolean hasOrigami = false;
            boolean hasOrderProp = false;
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (!queryDocumentSnapshots.isEmpty()) {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                hasOrderProp = queryDocumentSnapshots.getDocuments().get(0).contains("order");
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    if ("Nghệ thuật gấp giấy Origami".equals(doc.getString("title"))) {
                        hasOrigami = true;
                        break;
                    }
                }
            }

            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (queryDocumentSnapshots.isEmpty() || isCorrupted || hasOrigami || !hasOrderProp) {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete();
                    }
                }

                // Auto seed default categories
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> c1 = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c1.put("title", "Dành cho người mới bắt đầu");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c1.put("courseCount", "7 bài học");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c1.put("imageRes", "ve_hoa_mau_nuoc");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c1.put("order", 1);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> c2 = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c2.put("title", "Nghệ thuật vẽ Chibi");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c2.put("courseCount", "8 bài học");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c2.put("imageRes", "tp_trending_3");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c2.put("order", 2);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> c3 = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c3.put("title", "Vẽ thiên nhiên");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c3.put("courseCount", "10 bài học");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c3.put("imageRes", "ve_thien_nhien");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c3.put("order", 3);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> c4 = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c4.put("title", "Khám phá màu nước");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c4.put("courseCount", "7 bài học");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c4.put("imageRes", "banner_watercolor");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c4.put("order", 4);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                java.util.Map<String, Object> c5 = new java.util.HashMap<>();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c5.put("title", "Chân dung Manga");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c5.put("courseCount", "7 bài học");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c5.put("imageRes", "tp_trending_2");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                c5.put("order", 5);

                db.collection("Categories").add(c1);
                db.collection("Categories").add(c2);
                db.collection("Categories").add(c3);
                db.collection("Categories").add(c4);
                db.collection("Categories").add(c5)
                        .addOnSuccessListener(dr -> container.postDelayed(() -> setupDynamicCategories(view), 2500)); // Re-fetch
                return;
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (getContext() == null)
                return;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            container.removeAllViews();

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                String title = doc.getString("title");
                String courseCount = doc.getString("courseCount");
                String imageResStr = doc.getString("imageRes");
                String imageUrl = doc.getString("imageUrl");

                View categoryView = inflater.inflate(R.layout.item_explore_category, container, false);
                TextView tvTitle = categoryView.findViewById(R.id.tv_category_name);
                TextView tvCount = categoryView.findViewById(R.id.tv_category_count);
                ImageView ivCat = categoryView.findViewById(R.id.iv_category);

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvTitle != null)
                    tvTitle.setText(title);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvCount != null)
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    tvCount.setText(courseCount != null ? courseCount : "0 bài học");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (ivCat != null) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageResStr != null && !imageResStr.isEmpty() && !imageResStr.matches("-?\\d+")) {
                        try {
                            int resId = getResources().getIdentifier(imageResStr, "drawable",
                                    getContext().getPackageName());
                            if (resId != 0)
                                ivCat.setImageResource(resId);
                        } catch (Exception e) {
                        }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    } else if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivCat);
                    }
                }

                categoryView.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(getActivity(), LessonListActivity.class);
                    intent.putExtra("TITLE", title);
                    startActivity(intent);
                });

                container.addView(categoryView);
            }
        });
    }

    /**
     * Tải và hiển thị danh sách các tác phẩm nghệ thuật nổi bật (Trending) 
     * dựa trên lượt thích (likesCount) từ Firestore (Collection "Posts").
     // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
     * API Firestore được gọi: db.collection("Posts").orderBy("likesCount").
     * @param view View gốc của Fragment.
     */
    /**
     * Hàm setupTrendingData() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupTrendingData(View view) {
        android.widget.LinearLayout container = view.findViewById(R.id.ll_trending_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null) return;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        
        // Lấy 10 bài viết để đảm bảo lọc ra được 5 bài có ảnh
        db.collection("Posts")
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            .orderBy("likesCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (getContext() == null) return;
                LayoutInflater inflater = LayoutInflater.from(getContext());
                container.removeAllViews();
                
                int count = 0;
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    if (count >= 5) break; // Chỉ hiển thị 5 tác phẩm
                    
                    com.example.appdraw.model.Post post = doc.toObject(com.example.appdraw.model.Post.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (post == null) continue;
                    
                    String imageUrl = post.getImageUrl();
                    // Tác phẩm trending phải có ảnh
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageUrl == null || imageUrl.isEmpty()) continue;
                    
                    count++;
                    
                    View artworkView = inflater.inflate(R.layout.item_trending_artwork, container, false);
                    TextView tvTitle = artworkView.findViewById(R.id.tv_trending_title);
                    TextView tvAuthor = artworkView.findViewById(R.id.tv_trending_author);
                    TextView tvLikes = artworkView.findViewById(R.id.tv_likes_count);
                    ImageView ivArt = artworkView.findViewById(R.id.iv_trending_art);
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvTitle != null) {
                        String content = post.getContent();
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        tvTitle.setText((content != null && !content.isEmpty()) ? content : "Không có tiêu đề");
                    }
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvLikes != null) {
                        long likes = post.getLikesCount();
                        if (likes >= 1000) {
                            tvLikes.setText(String.format(java.util.Locale.US, "%.1fk", likes / 1000.0));
                        } else {
                            tvLikes.setText(String.valueOf(likes));
                        }
                    }
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ivArt != null) {
                        if (imageUrl.startsWith("data:image")) {
                            try {
                                byte[] decodedBytes = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                com.bumptech.glide.Glide.with(this).load(decodedBytes).centerCrop().into(ivArt);
                            } catch (Exception e) {}
                        } else {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivArt);
                        }
                    }
                    
                    // Fetch Author Name
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (post.getUid() != null) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        db.collection("Users").document(post.getUid()).get().addOnSuccessListener(userDoc -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (userDoc.exists() && userDoc.contains("profile")) {
                                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (profile != null && profile.containsKey("fullName")) {
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (tvAuthor != null) tvAuthor.setText("Bởi " + profile.get("fullName"));
                                } else {
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (tvAuthor != null) tvAuthor.setText("Bởi Người dùng");
                                }
                            }
                        });
                    }
                    
                    artworkView.setOnClickListener(v -> {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(getActivity(), com.example.appdraw.community.PostDetailActivity.class);
                        intent.putExtra("POST_ID", post.getId());
                        startActivity(intent);
                    });
                    
                    container.addView(artworkView);
                }
            });
    }

    /**
     * Tải và hiển thị danh sách các Chuyên gia (Mentor) động từ Firestore.
     // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
     * API Firestore được gọi: db.collection("Users").whereEqualTo("role", "mentor").
     * @param view View gốc của Fragment.
     */
    /**
     * Hàm setupDynamicMentors() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupDynamicMentors(View view) {
        android.widget.LinearLayout container = view.findViewById(R.id.ll_mentors_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null)
            return;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("Users")
                .whereEqualTo("role", "mentor")
                .limit(5)
                .get()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (getContext() == null)
                        return;
                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile == null)
                            continue;

                        String name = (String) profile.get("fullName");
                        String bio = (String) profile.get("bio");
                        String avatarUrl = (String) profile.get("avatarUrl");
                        String artistId = doc.getId();

                        View artistView = inflater.inflate(R.layout.item_artist, container, false);
                        TextView tvName = artistView.findViewById(R.id.tv_artist_name);
                        ImageView ivArtist = artistView.findViewById(R.id.iv_artist);
                        ImageView ivVerified = artistView.findViewById(R.id.iv_verified);

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvName != null)
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            tvName.setText(name != null ? name : "Chuyên gia");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivVerified != null)
                            ivVerified.setVisibility(View.VISIBLE); // Hiển thị tích xanh

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivArtist != null && avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(this).load(avatarUrl).circleCrop().into(ivArtist);
                        }

                        artistView.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(getActivity(), ArtistDetailActivity.class);
                            intent.putExtra("ARTIST_ID", artistId);
                            intent.putExtra("ARTIST_NAME", name);
                            intent.putExtra("ARTIST_BIO", bio);
                            intent.putExtra("ARTIST_AVATAR", avatarUrl);
                            startActivity(intent);
                        });

                        container.addView(artistView);
                    }
                });
    }
}
