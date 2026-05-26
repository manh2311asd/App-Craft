package com.example.appdraw.challenge;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appdraw.R;

/**
 * Màn hình Danh sách Thử thách nghệ thuật (UC-13).
 * Người thực hiện: Cao Đức Mạnh.
 * Hiển thị các cuộc thi đang diễn ra, người dùng có thể xem và tham gia.
 */
/**
 * Lớp ChallengeActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChallengeActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChallengeActivity extends AppCompatActivity {
    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        Toolbar toolbar = findViewById(R.id.toolbar_challenge);
        toolbar.setNavigationOnClickListener(v -> finish());

        android.view.View btnAddChallenge = findViewById(R.id.btn_add_challenge);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnAddChallenge != null) {
            btnAddChallenge.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                android.content.Intent intent = new android.content.Intent(this, com.example.appdraw.challenge.CreateChallengeActivity.class);
                startActivity(intent);
            });
        }
        
        setupData();
    }
    
    /**
     * Biến `rvChallenges` lưu dữ liệu/trạng thái quan trọng kiểu androidx.recyclerview.widget.RecyclerView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private androidx.recyclerview.widget.RecyclerView rvChallenges;
    /**
     * Biến `challengeAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ChallengeAdapter challengeAdapter;
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private java.util.List<com.google.firebase.firestore.DocumentSnapshot> challengeList = new java.util.ArrayList<>();
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private java.util.List<com.google.firebase.firestore.DocumentSnapshot> allChallengeList = new java.util.ArrayList<>();
    /**
     * Biến `shimmerContainer` lưu dữ liệu/trạng thái quan trọng kiểu com.facebook.shimmer.ShimmerFrameLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private com.facebook.shimmer.ShimmerFrameLayout shimmerContainer;
    /**
     * Biến `llEmptyState` lưu dữ liệu/trạng thái quan trọng kiểu android.widget.LinearLayout, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private android.widget.LinearLayout llEmptyState;

    /**
     * Hàm setupData() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupData() {
        rvChallenges = findViewById(R.id.rv_challenges);
        shimmerContainer = findViewById(R.id.shimmer_view_container);
        llEmptyState = findViewById(R.id.ll_empty_state);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (shimmerContainer != null) {
            shimmerContainer.setVisibility(android.view.View.VISIBLE);
            shimmerContainer.startShimmer();
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvChallenges != null) rvChallenges.setVisibility(android.view.View.GONE);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (llEmptyState != null) llEmptyState.setVisibility(android.view.View.GONE);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvChallenges != null) {
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvChallenges.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            challengeAdapter = new ChallengeAdapter(this, challengeList);
            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
            rvChallenges.setAdapter(challengeAdapter);
            
            setupTabs();
        }
    }

    @Override
    /**
     * Hàm onResume() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    protected void onResume() {
        super.onResume();
        loadChallengesFromFirestore();
    }

    /**
     * Hàm setupTabs() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupTabs() {
        com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_challenge);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new com.google.android.material.tabs.TabLayout.OnTabSelectedListener() {
                @Override
                /**
                 * Hàm onTabSelected() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeActivity.
                 * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
                 * @param tab tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
                 */
                public void onTabSelected(com.google.android.material.tabs.TabLayout.Tab tab) {
                    filterListByTab(tab.getPosition());
                }
                @Override
                public void onTabUnselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
                @Override
                public void onTabReselected(com.google.android.material.tabs.TabLayout.Tab tab) {}
            });
        }
    }
    
    /**
     * Hàm filterListByTab() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param tabIndex tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void filterListByTab(int tabIndex) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (allChallengeList.isEmpty()) return;
        
        challengeList.clear();
        long now = System.currentTimeMillis();
        long oneWeek = 7L * 24 * 60 * 60 * 1000;
        long oneMonth = 30L * 24 * 60 * 60 * 1000;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        for (com.google.firebase.firestore.DocumentSnapshot doc : allChallengeList) {
            Long endTime = doc.getLong("endTimeMillis");

            // Nếu không có endTimeMillis, thử parse từ dateStr (vd: "13/04 - 20/04")
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (endTime == null) {
                String dateStr = doc.getString("dateStr");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (dateStr != null && dateStr.contains(" - ")) {
                    try {
                        String endPart = dateStr.split(" - ")[1].trim(); // "20/04"
                        String[] parts = endPart.split("/");
                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]) - 1; // Calendar month 0-based
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.set(java.util.Calendar.DAY_OF_MONTH, day);
                        cal.set(java.util.Calendar.MONTH, month);
                        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                        cal.set(java.util.Calendar.MINUTE, 59);
                        cal.set(java.util.Calendar.SECOND, 59);
                        endTime = cal.getTimeInMillis();
                    } catch (Exception e) {
                        endTime = Long.MAX_VALUE;
                    }
                } else {
                    endTime = Long.MAX_VALUE;
                }
            }
            
            if (tabIndex == 0) { // Tất cả
                challengeList.add(doc);
            } else if (tabIndex == 1) { // Tuần này - còn hiệu lực và kết thúc trong 7 ngày tới
                if (endTime > now && (endTime - now) <= oneWeek) {
                    challengeList.add(doc);
                }
            } else if (tabIndex == 2) { // Tháng này - còn hiệu lực và kết thúc trong 30 ngày tới
                if (endTime > now && (endTime - now) <= oneMonth) {
                    challengeList.add(doc);
                }
            } else if (tabIndex == 3) { // Đã kết thúc
                if (endTime <= now) {
                    challengeList.add(doc);
                }
            } else if (tabIndex == 4) { // Chấm điểm
                challengeList.add(doc);
            }
        }
        
        updateUIState();
    }
    
    /**
     * Hàm updateUIState() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateUIState() {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (shimmerContainer != null) {
            shimmerContainer.stopShimmer();
            shimmerContainer.setVisibility(android.view.View.GONE);
        }
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeList.isEmpty()) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (rvChallenges != null) rvChallenges.setVisibility(android.view.View.GONE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llEmptyState != null) llEmptyState.setVisibility(android.view.View.VISIBLE);
        } else {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (rvChallenges != null) rvChallenges.setVisibility(android.view.View.VISIBLE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (llEmptyState != null) llEmptyState.setVisibility(android.view.View.GONE);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (challengeAdapter != null) challengeAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Hàm loadChallengesFromFirestore() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadChallengesFromFirestore() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user != null) {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Users").document(user.getUid()).get()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addOnSuccessListener(documentSnapshot -> {
                    boolean isMentorUser = false;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    String mentorNameStr = null;
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (documentSnapshot.exists()) {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        String role = documentSnapshot.getString("role");
                        isMentorUser = "mentor".equalsIgnoreCase(role);
                        
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) documentSnapshot.get("profile");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null && profile.containsKey("fullName")) {
                            mentorNameStr = "Mentor: " + profile.get("fullName");
                        }
                    }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (challengeAdapter != null) {
                        challengeAdapter.setMentor(isMentorUser, mentorNameStr);
                    }
                    
                    android.view.View btnAddChallenge = findViewById(R.id.btn_add_challenge);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (btnAddChallenge != null) {
                        btnAddChallenge.setVisibility(isMentorUser ? android.view.View.VISIBLE : android.view.View.GONE);
                    }
                    
                    com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_challenge);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tabLayout != null && !isMentorUser && tabLayout.getTabCount() >= 5) {
                        tabLayout.removeTabAt(4);
                    }

                    fetchChallengesData(db);
                })
                .addOnFailureListener(e -> fetchChallengesData(db));
        } else {
            fetchChallengesData(db);
            
            android.view.View btnAddChallenge = findViewById(R.id.btn_add_challenge);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (btnAddChallenge != null) btnAddChallenge.setVisibility(android.view.View.GONE);
            
            com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_challenge);
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (tabLayout != null && tabLayout.getTabCount() >= 5) tabLayout.removeTabAt(4);
        }
    }

    /**
     * Hàm fetchChallengesData() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param db tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private void fetchChallengesData(com.google.firebase.firestore.FirebaseFirestore db) {
        db.collection("Challenges")
            .get()
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addOnSuccessListener(queryDocumentSnapshots -> {
                allChallengeList.clear();
                
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<com.google.firebase.firestore.DocumentSnapshot> docs = new java.util.ArrayList<>(queryDocumentSnapshots.getDocuments());
                docs.sort((doc1, doc2) -> {
                    Long end1 = doc1.getLong("endTimeMillis");
                    Long end2 = doc2.getLong("endTimeMillis");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if(end1 == null) end1 = Long.MAX_VALUE;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if(end2 == null) end2 = Long.MAX_VALUE;
                    
                    long now = System.currentTimeMillis();
                    boolean active1 = end1 > now;
                    boolean active2 = end2 > now;
                    
                    if(active1 && !active2) return -1;
                    if(!active1 && active2) return 1;
                    return end1.compareTo(end2);
                });
                
                allChallengeList.addAll(docs);
                
                com.google.android.material.tabs.TabLayout tabLayout = findViewById(R.id.tab_layout_challenge);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                int selectedTab = tabLayout != null ? tabLayout.getSelectedTabPosition() : 0;
                filterListByTab(selectedTab);
            })
            .addOnFailureListener(e -> {
                updateUIState();
                android.widget.Toast.makeText(this, "Lỗi khi tải thử thách", android.widget.Toast.LENGTH_SHORT).show();
            });
    }
}
