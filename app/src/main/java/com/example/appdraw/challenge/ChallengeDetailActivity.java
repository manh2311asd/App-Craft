package com.example.appdraw.challenge;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.appdraw.R;

/**
 * Màn hình Chi tiết Thử thách (UC-13).
 * Người thực hiện: Cao Đức Mạnh.
 * Hiển thị thể lệ thử thách, thời gian, giải thưởng và các bài dự thi.
 */
/**
 * Lớp ChallengeDetailActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChallengeDetailActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChallengeDetailActivity extends AppCompatActivity {
    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String title = getIntent().getStringExtra("CHALLENGE_TITLE");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String imageUrl = getIntent().getStringExtra("CHALLENGE_IMAGE_URL");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String rulesStr = getIntent().getStringExtra("CHALLENGE_RULES");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String rewardsStr = getIntent().getStringExtra("CHALLENGE_REWARDS");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        String deadlineStr = getIntent().getStringExtra("CHALLENGE_DEADLINE");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (title != null && !title.isEmpty()) {
            ((TextView) findViewById(R.id.tv_challenge_detail_title)).setText(title);
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rulesStr != null && !rulesStr.isEmpty()) ((TextView) findViewById(R.id.tv_challenge_rules)).setText(rulesStr);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rewardsStr != null && !rewardsStr.isEmpty()) ((TextView) findViewById(R.id.tv_challenge_rewards)).setText(rewardsStr);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (deadlineStr != null && !deadlineStr.isEmpty()) {
            String endDate = deadlineStr.contains("-") ? deadlineStr.split("-")[1].trim() : deadlineStr;
            ((TextView) findViewById(R.id.tv_challenge_deadline)).setText("Deadline: " + endDate);
        }

        android.widget.ImageView ivBanner = findViewById(R.id.iv_challenge_banner_img);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivBanner != null && imageUrl != null && !imageUrl.isEmpty()) {
            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
            com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivBanner);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_challenge_detail);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("Challenges").whereEqualTo("title", title).get().addOnSuccessListener(shots -> {
            boolean isEnded = false;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (!shots.isEmpty()) {
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                com.google.firebase.firestore.DocumentSnapshot doc = shots.getDocuments().get(0);
                Long endTime = doc.getLong("endTimeMillis");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (endTime != null) {
                    if (System.currentTimeMillis() > endTime) isEnded = true;
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                } else if (deadlineStr != null && deadlineStr.contains("-")) {
                    String[] parts = deadlineStr.split("-");
                    if (parts.length > 1) {
                        try {
                            String endDateStr = parts[1].trim();
                            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(endDateStr.length() > 5 ? "dd/MM/yyyy" : "dd/MM", java.util.Locale.getDefault());
                            java.util.Date date = format.parse(endDateStr);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (date != null) {
                                java.util.Calendar cal = java.util.Calendar.getInstance();
                                cal.setTime(date);
                                cal.set(java.util.Calendar.YEAR, java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
                                cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                                cal.set(java.util.Calendar.MINUTE, 59);
                                if (System.currentTimeMillis() > cal.getTimeInMillis()) isEnded = true;
                            }
                        } catch (Exception e) {}
                    }
                }
                String authorId = doc.getString("authorId");
                String author = doc.getString("author");
                setupMoreMenu(doc.getId(), authorId, author);
            }
            checkUserChallengeState(title, isEnded);
            loadPublicSubmissions(title, isEnded);
        }).addOnFailureListener(e -> {
            checkUserChallengeState(title, false);
            loadPublicSubmissions(title, false);
        });
    }

    /**
     * Hàm setupMoreMenu() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param challengeId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param authorId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param author tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupMoreMenu(String challengeId, String authorId, String author) {
        android.widget.ImageView ivMore = findViewById(R.id.iv_challenge_more);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivMore == null) return;
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null) return;
        
        boolean isAuthor = false;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (authorId != null && authorId.equals(user.getUid())) {
            isAuthor = true;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        } else if (authorId == null && author != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Users").document(user.getUid()).get().addOnSuccessListener(userDoc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (userDoc.exists()) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc.get("profile");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profile != null && profile.containsKey("fullName")) {
                        String mentorName = "Mentor: " + profile.get("fullName");
                        if (author.equals(mentorName)) {
                            ivMore.setVisibility(android.view.View.VISIBLE);
                            ivMore.setOnClickListener(v -> showMoreMenu(v, challengeId));
                        }
                    }
                }
            });
            return;
        }
        
        if (isAuthor) {
            ivMore.setVisibility(android.view.View.VISIBLE);
            ivMore.setOnClickListener(v -> showMoreMenu(v, challengeId));
        }
    }

    /**
     * Hàm showMoreMenu() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param challengeId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showMoreMenu(android.view.View view, String challengeId) {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, view);
        popup.getMenu().add(0, 1, 0, "Chỉnh sửa");
        popup.getMenu().add(0, 2, 1, "Xóa thử thách");
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                android.widget.Toast.makeText(this, "Tính năng chỉnh sửa đang phát triển, sắp ra mắt!", android.widget.Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == 2) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Xóa thử thách")
                    .setMessage("Bạn có chắc chắn muốn xóa thử thách này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("Challenges").document(challengeId).delete()
                            .addOnSuccessListener(aVoid -> {
                                android.widget.Toast.makeText(this, "Đã xóa thử thách", android.widget.Toast.LENGTH_SHORT).show();
                                finish();
                            });
                    })
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    .setNegativeButton("Hủy", null)
                    .show();
            }
            return true;
        });
        popup.show();
    }

    /**
     * Hàm checkUserChallengeState() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param isEnded tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void checkUserChallengeState(String title, boolean isEnded) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null || title == null) return;

        android.widget.LinearLayout llJoinedStatus = findViewById(R.id.ll_joined_status);
        android.widget.LinearLayout llTopStatusSection = findViewById(R.id.ll_top_status_section);
        android.widget.TextView tvSubmissionStatus = findViewById(R.id.tv_submission_status);
        android.widget.TextView tvSubmissionStatusInfo = findViewById(R.id.tv_submission_status_info); // The one in the middle of screen
        com.google.android.material.button.MaterialButton btnSubmit = findViewById(R.id.btn_submit_challenge);
        com.google.android.material.button.MaterialButton btnJoin = findViewById(R.id.btn_join_challenge);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(user.getUid()).get().addOnSuccessListener(userDoc -> {
            String role = userDoc.getString("role");
            if ("mentor".equals(role)) {
                // MENTOR VIEW
                btnJoin.setVisibility(android.view.View.VISIBLE);
                btnJoin.setText("Xem danh sách Bài thi");
                btnJoin.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    android.content.Intent intent = new android.content.Intent(this, ChallengeSubmissionsActivity.class);
                    intent.putExtra("CHALLENGE_TITLE", title);
                    startActivity(intent);
                });
            } else {
                // USER VIEW
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                db.collection("Users").document(user.getUid()).collection("joinedChallenges").document(title)
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addSnapshotListener((doc, e) -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (e != null) return;
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (doc != null && doc.exists()) {
                            String status = doc.getString("status");
                            btnJoin.setVisibility(android.view.View.GONE);
                            llJoinedStatus.setVisibility(android.view.View.VISIBLE);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (llTopStatusSection != null) llTopStatusSection.setVisibility(android.view.View.VISIBLE);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvSubmissionStatusInfo != null) tvSubmissionStatusInfo.setText("Bài của bạn: Tham gia thử thách thành công");

                            if ("JOINED".equals(status)) {
                                tvSubmissionStatus.setText("Bài của bạn : Chưa nộp");
                                tvSubmissionStatus.setTextColor(android.graphics.Color.parseColor("#E67E22"));
                                
                                if (isEnded) {
                                    btnSubmit.setText("ĐÃ HẾT HẠN");
                                    btnSubmit.setEnabled(false);
                                    btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E")));
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (tvSubmissionStatusInfo != null) tvSubmissionStatusInfo.setText("Bạn chưa nộp bài và thử thách đã kết thúc");
                                } else {
                                    btnSubmit.setText("NỘP BÀI");
                                    btnSubmit.setEnabled(true);
                                    btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4272D0")));
                                    btnSubmit.setOnClickListener(v -> {
                                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                        android.content.Intent intent = new android.content.Intent(this, SubmitChallengeActivity.class);
                                        intent.putExtra("CHALLENGE_TITLE", title);
                                        startActivity(intent);
                                    });
                                }
                            } else if ("SUBMITTED".equals(status)) {
                                tvSubmissionStatus.setText("Bài của bạn : Đang chờ chấm");
                                tvSubmissionStatus.setTextColor(android.graphics.Color.parseColor("#4272D0"));
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvSubmissionStatusInfo != null) tvSubmissionStatusInfo.setText("Bài của bạn : Đã nộp. Đang chờ kết quả");
                                btnSubmit.setText("ĐÃ NỘP - XEM BÀI");
                                btnSubmit.setEnabled(true);
                                btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                                btnSubmit.setOnClickListener(v -> {
                                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                    android.content.Intent intent = new android.content.Intent(this, UserScoreDetailActivity.class);
                                    intent.putExtra("CHALLENGE_TITLE", title);
                                    startActivity(intent);
                                });
                            } else if ("GRADED".equals(status)) {
                                Number score = doc.getDouble("score");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                tvSubmissionStatus.setText("Điểm: " + (score != null ? score : 0) + "/100 XP - Nhấn để xem chi tiết");
                                tvSubmissionStatus.setTextColor(android.graphics.Color.parseColor("#2ECC71"));
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (tvSubmissionStatusInfo != null) tvSubmissionStatusInfo.setText("Tuyệt vời! Bài của bạn đã có điểm.");
                                btnSubmit.setText("XEM ĐIỂM CHI TIẾT");
                                btnSubmit.setEnabled(true);
                                btnSubmit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2ECC71")));
                                btnSubmit.setOnClickListener(v -> {
                                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                    android.content.Intent intent = new android.content.Intent(this, UserScoreDetailActivity.class);
                                    intent.putExtra("CHALLENGE_TITLE", title);
                                    startActivity(intent);
                                });
                            }
                        } else {
                            // NOT JOINED
                            llJoinedStatus.setVisibility(android.view.View.GONE);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (llTopStatusSection != null) llTopStatusSection.setVisibility(android.view.View.GONE);
                            
                            if (isEnded) {
                                btnJoin.setVisibility(android.view.View.GONE);
                            } else {
                                btnJoin.setVisibility(android.view.View.VISIBLE);
                                btnJoin.setText("THAM GIA THỬ THÁCH");
                                btnJoin.setOnClickListener(v -> {
                                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                    java.util.Map<String, Object> data = new java.util.HashMap<>();
                                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                    data.put("status", "JOINED");
                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                    db.collection("Users").document(user.getUid()).collection("joinedChallenges").document(title).set(data);
                                });
                            }
                        }
                    });
            }
        });
    }

    /**
     * Hàm loadPublicSubmissions() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param isEnded tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void loadPublicSubmissions(String title, boolean isEnded) {
        android.widget.LinearLayout container = findViewById(R.id.ll_public_submissions_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null) return;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        db.collection("Challenge_Submissions")
            .whereEqualTo("challengeTitle", title)
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            .addSnapshotListener((queryDocumentSnapshots, error) -> {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (error != null || queryDocumentSnapshots == null) return;
                
                container.removeAllViews();
                
                android.widget.LinearLayout llPodiumSection = findViewById(R.id.ll_podium_section);
                android.view.View vPodiumDivider = findViewById(R.id.v_podium_divider);
                
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (queryDocumentSnapshots.isEmpty()) {
                    android.widget.TextView tvEmpty = new android.widget.TextView(this);
                    tvEmpty.setText("Chưa có bài dự thi nào.");
                    tvEmpty.setTextColor(android.graphics.Color.parseColor("#888888"));
                    tvEmpty.setPadding(32, 16, 32, 16);
                    container.addView(tvEmpty);
                    return;
                }

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<com.google.firebase.firestore.DocumentSnapshot> publicList = new java.util.ArrayList<>(queryDocumentSnapshots.getDocuments());
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (isEnded && llPodiumSection != null && vPodiumDivider != null) {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.List<com.google.firebase.firestore.DocumentSnapshot> gradedList = new java.util.ArrayList<>();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.DocumentSnapshot doc : publicList) {
                        if ("GRADED".equals(doc.getString("status"))) {
                            gradedList.add(doc);
                        }
                    }
                    if (gradedList.size() > 0) {
                        gradedList.sort((d1, d2) -> {
                            Number s1 = d1.getDouble("score");
                            Number s2 = d2.getDouble("score");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            double s1Val = s1 != null ? s1.doubleValue() : 0;
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            double s2Val = s2 != null ? s2.doubleValue() : 0;
                            return Double.compare(s2Val, s1Val);
                        });
                        
                        llPodiumSection.setVisibility(android.view.View.VISIBLE);
                        vPodiumDivider.setVisibility(android.view.View.VISIBLE);
                        
                        if (gradedList.size() > 0) populatePodiumItem(gradedList.get(0), R.id.iv_top1_avatar, R.id.tv_top1_name, R.id.tv_top1_score);
                        if (gradedList.size() > 1) populatePodiumItem(gradedList.get(1), R.id.iv_top2_avatar, R.id.tv_top2_name, R.id.tv_top2_score);
                        if (gradedList.size() > 2) populatePodiumItem(gradedList.get(2), R.id.iv_top3_avatar, R.id.tv_top3_name, R.id.tv_top3_score);
                    }
                }

                android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : publicList) {
                    android.view.View itemView = inflater.inflate(R.layout.item_challenge_submission_public, container, false);
                    
                    String authorName = doc.getString("userName");
                    String authorAvatar = doc.getString("userAvatar");
                    String imageUrl = doc.getString("imageUrl");
                    String status = doc.getString("status");
                    
                    // Author text
                    android.widget.TextView tvName = itemView.findViewById(R.id.tv_public_user_name);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvName != null) tvName.setText(authorName != null ? authorName : "Học viên");

                    // Avatar
                    android.widget.ImageView ivAvatar = itemView.findViewById(R.id.iv_public_user_avatar);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ivAvatar != null) {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (authorAvatar != null && !authorAvatar.isEmpty()) {
                            if (authorAvatar.startsWith("data:image")) {
                                byte[] decodedString = android.util.Base64.decode(authorAvatar.split(",")[1], android.util.Base64.DEFAULT);
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                com.bumptech.glide.Glide.with(this).load(decodedString).circleCrop().into(ivAvatar);
                            } else {
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                com.bumptech.glide.Glide.with(this).load(authorAvatar).circleCrop().into(ivAvatar);
                            }
                        }
                    }

                    // Artwork
                    android.widget.ImageView ivArtwork = itemView.findViewById(R.id.iv_public_artwork);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ivArtwork != null && imageUrl != null && !imageUrl.isEmpty()) {
                        byte[] decodedString = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                        com.bumptech.glide.Glide.with(this).load(decodedString).centerCrop().into(ivArtwork);
                    }

                    // Score logic
                    if ("GRADED".equals(status)) {
                        android.view.View scoreLayout = itemView.findViewById(R.id.ll_public_score);
                        android.widget.TextView tvScore = itemView.findViewById(R.id.tv_public_score);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (scoreLayout != null) scoreLayout.setVisibility(android.view.View.VISIBLE);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvScore != null) {
                            Number score = doc.getDouble("score");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            tvScore.setText((score != null ? score.intValue() : 0) + "/100");
                        }
                    }

                    // Like button logic
                    android.view.View btnLike = itemView.findViewById(R.id.btn_public_like);
                    android.widget.ImageView ivLikeIcon = itemView.findViewById(R.id.iv_public_like_icon);
                    android.widget.TextView tvLikeCount = itemView.findViewById(R.id.tv_public_like_count);
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    long likesCount = doc.getLong("likesCount") != null ? doc.getLong("likesCount") : 0;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvLikeCount != null) tvLikeCount.setText(String.valueOf(likesCount));
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (btnLike != null) {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        String currentUid = currentUser != null ? currentUser.getUid() : "";
                        
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        java.util.List<String> likedBy = (java.util.List<String>) doc.get("likedBy");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        boolean initiallyLiked = likedBy != null && likedBy.contains(currentUid);
                        
                        // Initial UI state
                        if (initiallyLiked) {
                            ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#E74C3C")); // Red
                            btnLike.setTag(true);
                        } else {
                            ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#888888")); // Gray
                            btnLike.setTag(false);
                        }
                        
                        btnLike.setOnClickListener(v -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (currentUid.isEmpty()) return; // User not logged in, theoretically impossible here
                            
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            boolean isLiked = (v.getTag() != null && (boolean)v.getTag());
                            
                            try {
                                long currentUiCount = Long.parseLong(tvLikeCount.getText().toString());
                                if (!isLiked) {
                                    // Optimistic UI update: Like
                                    ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#E74C3C"));
                                    tvLikeCount.setText(String.valueOf(currentUiCount + 1));
                                    v.setTag(true);
                                    
                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                    db.collection("Challenge_Submissions").document(doc.getId()).update(
                                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                        "likedBy", com.google.firebase.firestore.FieldValue.arrayUnion(currentUid),
                                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                        "likesCount", com.google.firebase.firestore.FieldValue.increment(1)
                                    );
                                } else {
                                    // Optimistic UI update: Unlike
                                    ivLikeIcon.setColorFilter(android.graphics.Color.parseColor("#888888"));
                                    tvLikeCount.setText(String.valueOf(Math.max(0, currentUiCount - 1)));
                                    v.setTag(false);
                                    
                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                    db.collection("Challenge_Submissions").document(doc.getId()).update(
                                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                        "likedBy", com.google.firebase.firestore.FieldValue.arrayRemove(currentUid),
                                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                        "likesCount", com.google.firebase.firestore.FieldValue.increment(-1)
                                    );
                                }
                            } catch (Exception e) {}
                        });
                    }

                    // Comment button logic
                    android.view.View btnComment = itemView.findViewById(R.id.btn_public_comment);
                    android.widget.TextView tvCommentCount = itemView.findViewById(R.id.tv_public_comment_count);
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    long commentsCount = doc.getLong("commentsCount") != null ? doc.getLong("commentsCount") : 0;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvCommentCount != null) tvCommentCount.setText(String.valueOf(commentsCount));

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (btnComment != null) {
                        btnComment.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            android.content.Intent intent = new android.content.Intent(this, SubmissionDetailActivity.class);
                            intent.putExtra("SUBMISSION_ID", doc.getId());
                            startActivity(intent);
                        });
                    }

                    container.addView(itemView);
                }
            });
    }

    /**
     * Hàm populatePodiumItem() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeDetailActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param doc tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param ivId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param tvNameId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param tvScoreId tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private void populatePodiumItem(com.google.firebase.firestore.DocumentSnapshot doc, int ivId, int tvNameId, int tvScoreId) {
        String authorName = doc.getString("userName");
        String authorAvatar = doc.getString("userAvatar");
        Number score = doc.getDouble("score");
        
        android.widget.TextView tvName = findViewById(tvNameId);
        android.widget.TextView tvScore = findViewById(tvScoreId);
        android.widget.ImageView ivAvatar = findViewById(ivId);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvName != null) tvName.setText(authorName != null ? authorName : "Học viên");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvScore != null) tvScore.setText((score != null ? score.intValue() : 0) + " xp");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ivAvatar != null) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (authorAvatar != null && !authorAvatar.isEmpty()) {
                if (authorAvatar.startsWith("data:image")) {
                     byte[] decodedString = android.util.Base64.decode(authorAvatar.split(",")[1], android.util.Base64.DEFAULT);
                     // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                     com.bumptech.glide.Glide.with(this).load(decodedString).circleCrop().into(ivAvatar);
                } else {
                     // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                     com.bumptech.glide.Glide.with(this).load(authorAvatar).circleCrop().into(ivAvatar);
                }
            } else {
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                com.bumptech.glide.Glide.with(this).load(R.drawable.ic_default_user).circleCrop().into(ivAvatar);
            }
        }
    }
}
