package com.example.appdraw.explore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdraw.R;
import com.example.appdraw.model.Lesson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.content.SharedPreferences;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

/**
 * Activity xử lý tìm kiếm và bộ lọc (UC-04).
 * Người thực hiện: Lê Thùy Linh.
 * Hỗ trợ tìm kiếm Bài học, Dự án và Nghệ sĩ từ cơ sở dữ liệu Firestore.
 */
/**
 * Lớp SearchActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file SearchActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class SearchActivity extends AppCompatActivity {

    /**
     * Biến `etSearch` lưu dữ liệu/trạng thái quan trọng kiểu EditText, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etSearch;
    /**
     * Biến `llSuggestions` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout llSuggestions;
    /**
     * Biến `llEmpty` lưu dữ liệu/trạng thái quan trọng kiểu LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private LinearLayout llEmpty;
    /**
     * Biến `rvResults` lưu dữ liệu/trạng thái quan trọng kiểu RecyclerView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvResults;
    /**
     * Biến `adapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private UnifiedSearchAdapter adapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<SearchResultItem> unifiedResults = new ArrayList<>();
    private android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView ivBack = findViewById(R.id.iv_back_search);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        etSearch = findViewById(R.id.et_search_input);
        llSuggestions = findViewById(R.id.ll_search_suggestions);
        llEmpty = findViewById(R.id.ll_empty_search);
        rvResults = findViewById(R.id.rv_search_results);

        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UnifiedSearchAdapter();
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvResults.setAdapter(adapter);

        setupSearchControls();
        setupClickableSuggestions();
        loadSearchHistory();

        // Auto-focus keyboard
        etSearch.requestFocus();
        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        etSearch.postDelayed(() -> {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(
                    android.content.Context.INPUT_METHOD_SERVICE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (imm != null)
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
        }, 200);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            /**
             * Hàm beforeTextChanged() thực hiện một phần xử lý trong luồng chức năng của lớp SearchActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param s tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param start tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param count tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param after tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            /**
             * Hàm onTextChanged() thực hiện một phần xử lý trong luồng chức năng của lớp SearchActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param s tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param start tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param before tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             * @param count tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                searchHandler.removeCallbacksAndMessages(null);
                String q = s.toString().trim();

                ImageView ivClear = findViewById(R.id.iv_clear_text);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (ivClear != null) {
                    ivClear.setVisibility(q.length() > 0 ? View.VISIBLE : View.GONE);
                }

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (q.isEmpty()) {
                    performSearch(q); // immediately clear
                } else {
                    searchHandler.postDelayed(() -> performSearch(q), 500);
                }
            }

            @Override
            /**
             * Hàm afterTextChanged() thực hiện một phần xử lý trong luồng chức năng của lớp SearchActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param s tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Hàm setupSearchControls() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupSearchControls() {
        ImageView ivClear = findViewById(R.id.iv_clear_text);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivClear != null) {
            ivClear.setOnClickListener(v -> etSearch.setText(""));
        }

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (!query.isEmpty()) {
                    saveSearchHistory(query);
                }
                // Hide keyboard
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(
                        android.content.Context.INPUT_METHOD_SERVICE);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (imm != null)
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        TextView tvClearHistory = findViewById(R.id.tv_clear_history);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvClearHistory != null) {
            tvClearHistory.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("SearchAppData", MODE_PRIVATE);
                prefs.edit().remove("search_history").apply();
                loadSearchHistory();
                Toast.makeText(this, "Đã xóa lịch sử", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Hàm setupClickableSuggestions() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupClickableSuggestions() {
        View.OnClickListener suggestionListener = v -> {
            String q = "";
            if (v instanceof Chip) {
                q = ((Chip) v).getText().toString();
            } else if (v.getId() == R.id.card_suggestion_1) {
                q = "Vẽ hoàng hôn";
            } else if (v.getId() == R.id.card_suggestion_2) {
                q = "Phác thảo cơ bản";
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (!q.isEmpty()) {
                etSearch.setText(q);
                etSearch.setSelection(q.length());
                saveSearchHistory(q);
            }
        };

        ChipGroup cgCategory = findViewById(R.id.cg_categories);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cgCategory != null) {
            // Need single selection enabled in xml, but let's toggle manually or just
            // trigger search
            for (int i = 0; i < cgCategory.getChildCount(); i++) {
                View child = cgCategory.getChildAt(i);
                if (child instanceof Chip) {
                    ((Chip) child).setCheckable(true);
                    ((Chip) child).setOnCheckedChangeListener((buttonView, isChecked) -> {
                        performSearch(etSearch.getText().toString().trim());
                    });
                }
            }
        }

        ChipGroup cgTrending = findViewById(R.id.cg_trending);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cgTrending != null) {
            for (int i = 0; i < cgTrending.getChildCount(); i++) {
                cgTrending.getChildAt(i).setOnClickListener(suggestionListener);
            }
        }

        View card1 = findViewById(R.id.card_suggestion_1);
        View card2 = findViewById(R.id.card_suggestion_2);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (card1 != null)
            card1.setOnClickListener(suggestionListener);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (card2 != null)
            card2.setOnClickListener(suggestionListener);
    }

    /**
     * Hàm loadSearchHistory() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadSearchHistory() {
        ChipGroup cgHistory = findViewById(R.id.cg_history);
        LinearLayout llHistory = findViewById(R.id.ll_history_header);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cgHistory == null || llHistory == null)
            return;

        cgHistory.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("SearchAppData", MODE_PRIVATE);
        String histStr = prefs.getString("search_history", "");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (histStr.isEmpty()) {
            llHistory.setVisibility(View.GONE);
            cgHistory.setVisibility(View.GONE);
        } else {
            llHistory.setVisibility(View.VISIBLE);
            cgHistory.setVisibility(View.VISIBLE);
            String[] items = histStr.split(";;;");
            for (String item : items) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (item.trim().isEmpty())
                    continue;
                Chip chip = new Chip(this);
                chip.setText(item);
                chip.setOnClickListener(v -> {
                    etSearch.setText(item);
                    etSearch.setSelection(item.length());
                });
                cgHistory.addView(chip);
            }
        }
    }

    /**
     * Hàm saveSearchHistory() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param query tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void saveSearchHistory(String query) {
        SharedPreferences prefs = getSharedPreferences("SearchAppData", MODE_PRIVATE);
        String histStr = prefs.getString("search_history", "");
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        List<String> items = new ArrayList<>();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (!histStr.isEmpty()) {
            items.addAll(Arrays.asList(histStr.split(";;;")));
        }

        // Remove if exists to push to front
        items.remove(query);
        items.add(0, query);

        // Keep max 10
        if (items.size() > 10) {
            items = items.subList(0, 10);
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            b.append(items.get(i));
            if (i < items.size() - 1)
                b.append(";;;");
        }

        prefs.edit().putString("search_history", b.toString()).apply();
        loadSearchHistory();
    }



    /**
     * Thực hiện truy vấn tìm kiếm dữ liệu (Bài học, Dự án, Nghệ sĩ) từ Firestore
     * dựa trên từ khóa người dùng nhập vào.
     * @param query Từ khóa tìm kiếm
     */
    /**
     * Hàm performSearch() thực hiện một phần xử lý trong luồng chức năng của lớp SearchActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param query tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void performSearch(String query) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (query.isEmpty()) {
            llSuggestions.setVisibility(View.VISIBLE);
            rvResults.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
            return;
        }

        llSuggestions.setVisibility(View.GONE);
        unifiedResults.clear();
        adapter.notifyDataSetChanged();

        String normalizedQuery = removeAccents(query.toLowerCase());

        ChipGroup cgCategory = findViewById(R.id.cg_categories);
        boolean searchLessons = true;
        boolean searchProjects = true;
        boolean searchMentors = true;

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (cgCategory != null) {
            for (int i = 0; i < cgCategory.getChildCount(); i++) {
                View child = cgCategory.getChildAt(i);
                if (child instanceof Chip && ((Chip) child).isChecked()) {
                    String chipText = ((Chip) child).getText().toString();
                    if ("Bài học".equals(chipText)) {
                        searchProjects = false;
                        searchMentors = false;
                    } else if ("Dự án".equals(chipText) || "Tác phẩm".equals(chipText) || "Hashtag".equals(chipText)) {
                        searchLessons = false;
                        searchMentors = false;
                    } else if ("Nghệ sĩ".equals(chipText)) {
                        searchLessons = false;
                        searchProjects = false;
                    }
                    break; // Use the first checked
                }
            }
        }

        if (searchLessons) {
            java.util.Set<String> seenLessonTitles = new java.util.HashSet<>();
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Lessons")
                    .get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            String title = doc.getString("title");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (title == null) continue;
                            String tnorm = removeAccents(title.toLowerCase());
                            if (tnorm.contains(normalizedQuery)) {
                                if (seenLessonTitles.contains(title)) continue;
                                seenLessonTitles.add(title);
                                
                                String authorName = doc.getString("authorName");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (authorName == null) authorName = doc.getString("author");
                                String imageUrl = doc.getString("imageRes");
                                if ("Đêm trăng sáng trên đồi".equals(title)) { imageUrl = "dem_trang_sang_tren_doi"; }
                                else if ("Khu vườn nhiệt đới".equals(title)) { imageUrl = "khu_vuon_nhiet_doi"; }
                                else if ("Thung lũng sương mù".equals(title)) { imageUrl = "thung_lung_suong_mu"; }
                                else if ("Vẽ rừng cây mùa thu".equals(title)) { imageUrl = "ve_rung_cay_mua_thu"; }
                                else if ("Tổng hợp phong cảnh".equals(title)) { imageUrl = "tong_hop_phong_canh"; }
                                else if ("Bãi biển lúc hoàng hôn".equals(title)) { imageUrl = "bai_bien_luc_hoang_hon"; }
                                else if ("Núi non trùng điệp".equals(title)) { imageUrl = "nui_non_trung_diep"; }
                                else if ("Dòng suối nhỏ trong vắt".equals(title)) { imageUrl = "dong_suoi_nho_trong_vat"; }
                                else if ("Thảo nguyên xanh mướt".equals(title)) { imageUrl = "thao_nguyen_xanh_muot"; }
                                else if ("Vẽ thác nước hùng vĩ".equals(title)) { imageUrl = "ve_thac_nuoc_hung_vi"; }
                                else if ("Làm quen với Brush".equals(title)) { imageUrl = "lam_quen_voi_brush"; }
                                else if ("Khái niệm hình học".equals(title)) { imageUrl = "khai_niem_hinh_hoc"; }
                                else if ("Đánh bóng và chiếu sáng".equals(title)) { imageUrl = "danh_bong_va_chieu_sang"; }
                                else if ("Kỹ thuật đan nét cọ".equals(title)) { imageUrl = "ki_thuat_dan_net_co"; }
                                else if ("Vẽ tĩnh vật quả táo".equals(title)) { imageUrl = "ve_tinh_vat_qua_tao"; }
                                else if ("Xây dựng khối 3D".equals(title)) { imageUrl = "xay_dung_khoi_3d"; }
                                else if ("Luyện tập tổng hợp".equals(title)) { imageUrl = "luyen_tap_tong_hop"; }
                                else if ("Palette pha màu cơ bản".equals(title)) { imageUrl = "palette_pha_mau_co_ban"; }
                                else if ("Kỹ thuật loang màu ẩm".equals(title)) { imageUrl = "ki_thuat_loang_mau_am"; }
                                else if ("Vẽ bầu trời gợn mây".equals(title)) { imageUrl = "ve_bau_troi_gon_may"; }
                                else if ("Tĩnh vật cốc cà phê".equals(title)) { imageUrl = "tinh_vat_coc_ca_phe"; }
                                else if ("Bông cẩm tú cầu".equals(title)) { imageUrl = "bong_cam_tu_cau"; }
                                else if ("Sơn thủy hữu tình".equals(title)) { imageUrl = "son_thuy_huu_tinh"; }
                                else if ("Ánh tà dương hoàng hôn".equals(title)) { imageUrl = "anh_ta_duong_hoang_hon"; }
                                else if ("Phác thảo khuôn mặt Chibi".equals(title)) { imageUrl = "phac_thao_khuon_mat_chibi"; }
                                else if ("Tỷ lệ cơ thể đầu to".equals(title)) { imageUrl = "ty_le_co_the_dau_to"; }
                                else if ("Vẽ mắt to tròn đáng yêu".equals(title)) { imageUrl = "ve_mat_to_tron_dang_yeu"; }
                                else if ("Biểu cảm khuôn mặt dễ thương".equals(title)) { imageUrl = "bieu_cam_khuon_mat_de_thuong"; }
                                else if ("Vẽ tóc bồng bềnh".equals(title)) { imageUrl = "ve_toc_bong_benh"; }
                                else if ("Phối đồ phong cách basic".equals(title)) { imageUrl = "phoi_do_phong_cach_basic"; }
                                else if ("Lên màu pastel cơ bản".equals(title)) { imageUrl = "len_mau_pastel_co_ban"; }
                                else if ("Hoàn thiện nhân vật".equals(title)) { imageUrl = "hoan_thien_nhan_vat"; }
                                else if ("Core tỷ lệ khuôn mặt".equals(title)) { imageUrl = "core_ty_le_khuon_mat"; }
                                else if ("Vẽ mắt Manga mượt mà".equals(title)) { imageUrl = "ve_mat_manga_muot_ma"; }
                                else if ("Kiểu tóc nam và nữ cơ bản".equals(title)) { imageUrl = "kieu_toc_nam_va_nu_co_ban"; }
                                else if ("Mảng biểu cảm vui buồn".equals(title)) { imageUrl = "mang_bieu_cam_vui_buon"; }
                                else if ("Góc nghiêng thần thánh".equals(title)) { imageUrl = "goc_nghieng_than_thanh"; }
                                else if ("Phác họa nhân vật nữ".equals(title)) { imageUrl = "phac_hoa_nhan_vat_nu"; }
                                else if ("Phác họa nhân vật nam".equals(title)) { imageUrl = "phac_hoa_nhan_vat_nam"; }
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (imageUrl == null || imageUrl.isEmpty() || imageUrl.matches("-?\\d+")) {
                                    imageUrl = doc.getString("thumbnailUrl");
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (imageUrl == null || imageUrl.isEmpty()) imageUrl = doc.getString("imageUrl");
                                }
                                String category = doc.getString("category");
                                Double rating = doc.getDouble("rating");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                String ratingStr = rating != null ? String.format(java.util.Locale.US, "%.1f★", rating) : "4.5★";
                                
                                unifiedResults.add(new SearchResultItem("LESSON", title,
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        authorName != null ? (authorName.startsWith("Bởi") ? authorName : "Bởi " + authorName) : "Bởi AppDraw",
                                        ratingStr, imageUrl, doc.getId(), category));
                            }
                        }
                        checkEmptyAndNotify();
                    });
        }

        if (searchProjects) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            String title = doc.getString("title");
                            String desc = doc.getString("description");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            String tnorm = title != null ? removeAccents(title.toLowerCase()) : "";
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            String dnorm = desc != null ? removeAccents(desc.toLowerCase()) : "";
                            if (tnorm.contains(normalizedQuery) || dnorm.contains(normalizedQuery)) {
                                String authorName = doc.getString("authorName");
                                String imageUrl = doc.getString("imageUrl");
                                unifiedResults.add(new SearchResultItem("PROJECT", title,
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        authorName != null ? authorName : "Thành viên", "Dự án", imageUrl,
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        doc.getId(), null));
                            }
                        }
                        checkEmptyAndNotify();
                    });
        }

        if (searchMentors) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Users")
                    .whereEqualTo("role", "mentor")
                    .get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null) {
                                String name = (String) profile.get("fullName");
                                String bio = (String) profile.get("bio");
                                String avatarUrl = (String) profile.get("avatarUrl");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                String nnorm = name != null ? removeAccents(name.toLowerCase()) : "";

                                if (nnorm.contains(normalizedQuery)) {
                                    unifiedResults.add(new SearchResultItem("ARTIST", name,
                                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                            bio != null ? bio : "Họa sĩ", "Nghệ sĩ", avatarUrl, doc.getId(), null));
                                }
                            }
                        }
                        checkEmptyAndNotify();
                    });
        }
    }

    /**
     * Hàm checkEmptyAndNotify() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     */
    private void checkEmptyAndNotify() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (unifiedResults.isEmpty()) {
            rvResults.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        } else {
            rvResults.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Loại bỏ dấu tiếng Việt (Normalizer) để hỗ trợ tìm kiếm không dấu.
     * @param s Chuỗi cần chuẩn hóa
     * @return Chuỗi đã loại bỏ dấu tiếng Việt
     */
    /**
     * Hàm removeAccents() thực hiện một phần xử lý trong luồng chức năng của lớp SearchActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param s tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private String removeAccents(String s) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (s == null)
            return "";
        String normalized = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private static class SearchResultItem {
        String type;
        String title;
        String subtitle;
        String extraInfo;
        String imageUrl;
        String id;
        String category;

        public SearchResultItem(String type, String title, String subtitle, String extraInfo, String imageUrl,
                String id, String category) {
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
            this.extraInfo = extraInfo;
            this.imageUrl = imageUrl;
            this.id = id;
            this.category = category;
        }
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class UnifiedSearchAdapter extends RecyclerView.Adapter<UnifiedSearchAdapter.ViewHolder> {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        @NonNull
        @Override
        /**
         * Hàm onCreateViewHolder() tạo ViewHolder cho RecyclerView bằng cách inflate layout item.
         * ViewHolder giúp tái sử dụng view, giảm chi phí tạo giao diện khi danh sách cuộn nhiều phần tử.
         * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param viewType tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(v);
        }

        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SearchResultItem item = unifiedResults.get(position);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            holder.tvTitle.setText(item.title != null ? item.title : "");
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            holder.tvSubtitle.setText(item.subtitle != null ? item.subtitle : "");
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            holder.tvType.setText(item.extraInfo != null ? item.extraInfo : "");

            if ("LESSON".equals(item.type)) {
                holder.tvType.setTextColor(android.graphics.Color.parseColor("#E67E22"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (item.imageUrl != null && !item.imageUrl.isEmpty() && !item.imageUrl.startsWith("http") && !item.imageUrl.startsWith("data:")) {
                    try {
                        int resId = holder.itemView.getContext().getResources().getIdentifier(item.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
                        if (resId != 0) holder.ivImage.setImageResource(resId);
                        else holder.ivImage.setImageResource(R.drawable.ve_thien_nhien);
                    } catch (Exception e) {}
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                } else if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                    if (item.imageUrl.startsWith("data:image")) {
                        try {
                            byte[] decodedBytes = android.util.Base64.decode(item.imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(decodedBytes).centerCrop().into(holder.ivImage);
                        } catch (Exception e) {}
                    } else {
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(item.imageUrl).centerCrop().into(holder.ivImage);
                    }
                } else {
                    holder.ivImage.setImageResource(R.drawable.ve_thien_nhien);
                }
            } else if ("PROJECT".equals(item.type)) {
                holder.tvType.setTextColor(android.graphics.Color.parseColor("#4272D0"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(item.imageUrl).centerCrop()
                            .into(holder.ivImage);
                } else
                    holder.ivImage.setImageResource(R.mipmap.ic_launcher);
            } else if ("ARTIST".equals(item.type)) {
                holder.tvType.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                    com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(item.imageUrl).circleCrop()
                            .into(holder.ivImage);
                } else
                    holder.ivImage.setImageResource(R.drawable.ic_default_user);
            }

            holder.itemView.setOnClickListener(v -> {
                if ("LESSON".equals(item.type)) {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(SearchActivity.this, LessonDetailActivity.class);
                    intent.putExtra("LESSON_TITLE", item.title);
                    intent.putExtra("LESSON_ID", item.id);
                    intent.putExtra("IMAGE_RES", item.imageUrl);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (item.category != null) {
                        intent.putExtra("CATEGORY", item.category);
                    }
                    startActivity(intent);
                } else if ("PROJECT".equals(item.type)) {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(SearchActivity.this,
                            com.example.appdraw.project.ProjectDetailActivity.class);
                    intent.putExtra("PROJECT_ID", item.id);
                    startActivity(intent);
                } else if ("ARTIST".equals(item.type)) {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(SearchActivity.this, ArtistDetailActivity.class);
                    intent.putExtra("ARTIST_ID", item.id);
                    intent.putExtra("ARTIST_NAME", item.title);
                    intent.putExtra("ARTIST_BIO", item.subtitle);
                    intent.putExtra("ARTIST_AVATAR", item.imageUrl);
                    startActivity(intent);
                }
            });
        }

        @Override
        /**
         * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
         * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
         */
        public int getItemCount() {
            return unifiedResults.size();
        }

/**
 * Lớp ViewHolder thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file SearchActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle, tvType;
            ImageView ivImage;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_result_title);
                tvSubtitle = itemView.findViewById(R.id.tv_result_subtitle);
                tvType = itemView.findViewById(R.id.tv_result_type);
                ivImage = itemView.findViewById(R.id.iv_result_image);
            }
        }
    }
}
