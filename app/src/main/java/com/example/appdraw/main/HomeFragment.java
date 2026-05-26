package com.example.appdraw.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.Map;

import com.example.appdraw.notification.NotificationsActivity;
import com.example.appdraw.profile.ProfileActivity;
import com.example.appdraw.R;
import com.example.appdraw.challenge.ChallengeActivity;
import com.example.appdraw.challenge.ChallengeDetailActivity;
import com.example.appdraw.community.EventScheduleActivity;
import com.example.appdraw.drawing.DrawingActivity;
import com.example.appdraw.explore.LessonDetailActivity;
import com.example.appdraw.explore.LessonListActivity;
import com.example.appdraw.explore.SearchActivity;
import com.example.appdraw.model.Event;
import com.example.appdraw.model.EventTicket;
import com.google.android.material.button.MaterialButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp HomeFragment thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file HomeFragment.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class HomeFragment extends Fragment {

    /**
     * Biến `challengeListenerReg` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private ListenerRegistration challengeListenerReg;
    /**
     * Biến `savedLessonsListenerReg` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
    private com.google.firebase.firestore.ListenerRegistration savedLessonsListenerReg;
    /**
     * Biến `reseedAttempted` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean reseedAttempted = false;
    /**
     * Biến `isLoadingSuggested` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isLoadingSuggested = false;

    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    @Nullable
    @Override
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // --- Fetch User Role and Profile from Firestore ---
        TextView tvGreeting = view.findViewById(R.id.tv_greeting);
        View layoutBadgeMentor = view.findViewById(R.id.layout_badge_mentor);
        ImageView ivAvatarHome = view.findViewById(R.id.iv_avatar_home);

        ImageView btnAddChallenge = view.findViewById(R.id.btn_add_challenge);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnAddChallenge != null) {
            btnAddChallenge.setVisibility(View.GONE);
            btnAddChallenge.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), com.example.appdraw.challenge.CreateChallengeActivity.class);
                startActivity(intent);
            });
        }

        ImageView btnAddEvent = view.findViewById(R.id.btn_add_event);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnAddEvent != null) {
            btnAddEvent.setVisibility(View.GONE);
            btnAddEvent.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), com.example.appdraw.community.CreateEventActivity.class);
                startActivity(intent);
            });
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(documentSnapshot -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (documentSnapshot.exists()) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            Map<String, Object> profile = (Map<String, Object>) documentSnapshot.get("profile");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null) {
                                if (profile.containsKey("fullName")) {
                                    String name = (String) profile.get("fullName");
                                    String shortName = name;
                                    if (name.contains(" ")) {
                                        shortName = name.substring(name.lastIndexOf(" ") + 1);
                                    }
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (tvGreeting != null)
                                        tvGreeting.setText("Chào " + shortName + "!");
                                }
                                if (profile.containsKey("avatarUrl")) {
                                    String avatarUrl = (String) profile.get("avatarUrl");
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (ivAvatarHome != null && getContext() != null) {
                                        ivAvatarHome.setPadding(0, 0, 0, 0);
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        if (avatarUrl != null && !avatarUrl.isEmpty()
                                                && avatarUrl.startsWith("data:image")) {
                                            byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1],
                                                    android.util.Base64.DEFAULT);
                                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                            Glide.with(getContext()).load(b).circleCrop().into(ivAvatarHome);
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                            Glide.with(getContext()).load(avatarUrl).circleCrop().into(ivAvatarHome);
                                        } else {
                                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                            Glide.with(getContext()).load(R.drawable.ic_default_user).circleCrop()
                                                    .into(ivAvatarHome);
                                        }
                                    }
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                } else if (ivAvatarHome != null && getContext() != null) {
                                    ivAvatarHome.setPadding(0, 0, 0, 0);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    Glide.with(getContext()).load(R.drawable.ic_default_user).circleCrop()
                                            .into(ivAvatarHome);
                                }
                            } else {
                                // Fallback cho user chưa có profile map
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                String name = documentSnapshot.getString("username");
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (name != null && !name.isEmpty()) {
                                    String shortName = name;
                                    if (name.contains(" ")) {
                                        shortName = name.substring(name.lastIndexOf(" ") + 1);
                                    }
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (tvGreeting != null)
                                        tvGreeting.setText("Chào " + shortName + "!");
                                }
                                
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                String avatarUrl = documentSnapshot.getString("avatar_url");
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (avatarUrl == null || avatarUrl.isEmpty()) avatarUrl = documentSnapshot.getString("avatar");
                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (avatarUrl == null || avatarUrl.isEmpty()) avatarUrl = documentSnapshot.getString("photoUrl");
                                
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (ivAvatarHome != null && getContext() != null) {
                                    ivAvatarHome.setPadding(0, 0, 0, 0);
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (avatarUrl != null && !avatarUrl.isEmpty()
                                            && avatarUrl.startsWith("data:image")) {
                                        byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1],
                                                android.util.Base64.DEFAULT);
                                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                        Glide.with(getContext()).load(b).circleCrop().into(ivAvatarHome);
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                        Glide.with(getContext()).load(avatarUrl).circleCrop().into(ivAvatarHome);
                                    } else {
                                        // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                        Glide.with(getContext()).load(R.drawable.ic_default_user).circleCrop()
                                                .into(ivAvatarHome);
                                    }
                                }
                            }

                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            String role = documentSnapshot.getString("role");
                            if ("mentor".equals(role)) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (layoutBadgeMentor != null)
                                    layoutBadgeMentor.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // User deleted from DB -> logout
                            Toast.makeText(getContext(), "Tài khoản của bạn không tồn tại hoặc đã bị xóa.",
                                    Toast.LENGTH_LONG).show();
                            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                            FirebaseAuth.getInstance().signOut();
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(getActivity(),
                                    com.example.appdraw.auth.LoginOptionsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (getActivity() != null)
                                getActivity().finish();
                        }
                    });
        }

        // Nút thông báo + badge chấm đỏ
        View btnNotifications = view.findViewById(R.id.btn_notifications);
        View notificationBadge = view.findViewById(R.id.notification_badge);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                // Ẩn badge khi mở trang thông báo
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (notificationBadge != null)
                    notificationBadge.setVisibility(View.GONE);
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), NotificationsActivity.class);
                startActivity(intent);
            });
        }

        // Lắng nghe real-time thông báo chưa đọc để hiện chấm đỏ
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user != null && notificationBadge != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("Notifications")
                    .whereEqualTo("userId", user.getUid())
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addSnapshotListener((snapshots, err) -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (err != null || snapshots == null)
                            return;
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (getView() == null)
                            return;
                        // Lọc client-side để tránh cần composite index
                        boolean hasUnread = false;
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots) {
                            Boolean isRead = doc.getBoolean("isRead");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (isRead == null || !isRead) {
                                hasUnread = true;
                                break;
                            }
                        }
                        View badge = getView().findViewById(R.id.notification_badge);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (badge != null) {
                            badge.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                        }
                    });
        }

        // Nút Livestream
        View btnLivestream = view.findViewById(R.id.btn_livestream);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnLivestream != null) {
            btnLivestream.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), com.example.appdraw.live.LiveListActivity.class);
                startActivity(intent);
            });
        }

        // Nút tìm kiếm
        // View btnSearch = view.findViewById(R.id.btn_search);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        // if (btnSearch != null) {
        //     btnSearch.setOnClickListener(v -> {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        //         Intent intent = new Intent(getActivity(), SearchActivity.class);
        //         startActivity(intent);
        //     });
        // }

        // Bắt đầu vẽ ngay
        View btnStartDrawing = view.findViewById(R.id.btnStartDrawingFragment);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (btnStartDrawing != null) {
            btnStartDrawing.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), DrawingActivity.class);
                startActivity(intent);
            });
        }

        // Xem lịch
        View tvViewCalendar = view.findViewById(R.id.tv_view_calendar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewCalendar != null) {
            tvViewCalendar.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), EventScheduleActivity.class);
                startActivity(intent);
            });
        }

        // Xem tất cả bài học
        View tvViewAllLessons = view.findViewById(R.id.tv_view_all_lessons);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewAllLessons != null) {
            tvViewAllLessons.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), LessonListActivity.class);
                startActivity(intent);
            });
        }

        // Bài học gợi ý sẽ được load trong onResume để tránh gọi 2 lần

        // Xem tất cả thử thách
        View tvViewAllChallenges = view.findViewById(R.id.tv_view_all_challenges);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewAllChallenges != null) {
            tvViewAllChallenges.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), ChallengeActivity.class);
                startActivity(intent);
            });
        }

        // --- Thử Thách ---
        setupChallenges(view);

        // --- Bài Học Yêu Thích ---
        setupSavedLessons(view);

        // --- Sự kiện sắp tới ---
        setupHomeEvents(view);

        // Xem tất cả sự kiện (Sự kiện sắp tới)
        View tvViewAllEvents = view.findViewById(R.id.tv_view_all_events);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewAllEvents != null) {
            tvViewAllEvents.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), EventScheduleActivity.class);
                intent.putExtra("OPEN_EXPLORE", true);
                startActivity(intent);
            });
        }

        // Xem lịch (Lịch của bạn)
        View tvViewSchedule = view.findViewById(R.id.tv_view_calendar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (tvViewSchedule != null) {
            tvViewSchedule.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(getActivity(), EventScheduleActivity.class);
                intent.putExtra("OPEN_EXPLORE", false);
                startActivity(intent);
            });
        }

        return view;
    }

    @Override
    /**
     * Hàm onResume() thực hiện một phần xử lý trong luồng chức năng của lớp HomeFragment.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void onResume() {
        super.onResume();
        // Chỉ reload các phần dùng get() một lần để tránh stale UI, 
        // riêng challenge thì dùng listener rồi nên không cần nạp lại.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getView() != null) {
            setupHomeEvents(getView());
            setupSuggestedLessons(getView());
            setupSavedLessons(getView());
        }
    }

    @Override
    /**
     * Hàm onDestroyView() thực hiện một phần xử lý trong luồng chức năng của lớp HomeFragment.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy listener khi fragment bị destroy để tránh memory leak
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeListenerReg != null) {
            challengeListenerReg.remove();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            challengeListenerReg = null;
        }
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (savedLessonsListenerReg != null) {
            savedLessonsListenerReg.remove();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            savedLessonsListenerReg = null;
        }
    }

    /**
     * Hàm setupSuggestedLessons() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupSuggestedLessons(View view) {
        // Tránh gọi trùng khi 2 callback async cùng chạy
        if (isLoadingSuggested) return;
        isLoadingSuggested = true;

        android.widget.LinearLayout container = view.findViewById(R.id.ll_suggested_lessons_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null) { isLoadingSuggested = false; return; }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getContext() == null) { isLoadingSuggested = false; return; }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        container.removeAllViews();

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        String uid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null) { isLoadingSuggested = false; return; }

        // B1: Lấy danh sách bài đã học xong
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(uid).collection("lessonProgress").get().addOnSuccessListener(progSnap -> {
            java.util.Set<String> completedTitles = new java.util.HashSet<>();
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            for (com.google.firebase.firestore.DocumentSnapshot d : progSnap) {
                if ("COMPLETED".equals(d.getString("status"))) {
                    completedTitles.add(d.getId());
                }
            }

            // B2: Lấy toàn bộ bài học và lọc theo chủ đề
            db.collection("Lessons").get().addOnSuccessListener(lessonSnap -> {
                // Sắp xếp các bài học theo thứ tự tăng dần (dựa vào Order hoặc ID)
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<com.google.firebase.firestore.DocumentSnapshot> allDocs = new java.util.ArrayList<>();
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot d : lessonSnap) allDocs.add(d);
                allDocs.sort((d1, d2) -> {
                    Long c1 = d1.getLong("createdAt");
                    Long c2 = d2.getLong("createdAt");
                    // Xử lý đầy đủ 4 trường hợp để tránh IllegalArgumentException
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (c1 != null && c2 != null) return Long.compare(c1, c2);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (c1 != null) return -1; // d1 có createdAt → lên trước
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (c2 != null) return 1;  // d2 có createdAt → d1 xuống sau

                    // Cả hai đều không có createdAt → so sánh theo ID
                    String id1 = d1.getId();
                    String id2 = d2.getId();
                    try {
                        int index1 = Integer.parseInt(id1.substring(id1.lastIndexOf("_") + 1));
                        int index2 = Integer.parseInt(id2.substring(id2.lastIndexOf("_") + 1));
                        return Integer.compare(index1, index2);
                    } catch (Exception e) {
                        return id1.compareTo(id2);
                    }
                });

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.Map<String, java.util.List<com.google.firebase.firestore.DocumentSnapshot>> lessonsByCategory = new java.util.HashMap<>();
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : allDocs) {
                    String cat = doc.getString("category");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (cat == null) cat = "Khác";
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    if (!lessonsByCategory.containsKey(cat)) lessonsByCategory.put(cat, new java.util.ArrayList<>());
                    lessonsByCategory.get(cat).add(doc);
                }

                // Nhóm 5 danh mục chính
                String[] coreCategories = {
                    "Dành cho người mới bắt đầu", 
                    "Vẽ thiên nhiên", 
                    "Khám phá màu nước", 
                    "Nghệ thuật vẽ Chibi", 
                    "Chân dung Manga"
                };

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                java.util.List<com.google.firebase.firestore.DocumentSnapshot> finalSuggestions = new java.util.ArrayList<>();
                
                // Lấy bài đầu tiên CHƯA hoàn thành của từng danh mục
                for (String cat : coreCategories) {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    java.util.List<com.google.firebase.firestore.DocumentSnapshot> catLessons = lessonsByCategory.get(cat);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (catLessons != null) {
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : catLessons) {
                            String title = doc.getString("title");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (title != null && !completedTitles.contains(title)) {
                                finalSuggestions.add(doc);
                                break;
                            }
                        }
                    }
                }

                // Render UI
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : finalSuggestions) {
                    String title = doc.getString("title");
                    String author = doc.getString("author");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (author == null) author = doc.getString("authorName");
                    String imageResStr = doc.getString("imageRes");
                    String imageUrl = doc.getString("thumbnailUrl");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        imageUrl = doc.getString("imageUrl");
                    }
                    String category = doc.getString("category");

                    // Override ảnh theo title 
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

                    View lessonView = inflater.inflate(R.layout.item_lesson_preview, container, false);

                    TextView tvTitle = lessonView.findViewById(R.id.tv_lesson_title);
                    TextView tvAuthor = lessonView.findViewById(R.id.tv_lesson_author);
                    ImageView ivThumb = lessonView.findViewById(R.id.iv_lesson_thumb);
                    TextView tvStatus = lessonView.findViewById(R.id.tv_lesson_status);
                    TextView tvDuration = lessonView.findViewById(R.id.tv_duration);

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvTitle != null) tvTitle.setText(title);
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
                                int resId = getResources().getIdentifier(imageResStr, "drawable", getContext().getPackageName());
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                if (resId != 0) com.bumptech.glide.Glide.with(HomeFragment.this).load(resId).centerCrop().into(ivThumb);
                            } catch (Exception e) {}
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        } else if (imageUrl != null && !imageUrl.isEmpty()) {
                            if (imageUrl.startsWith("data:image")) {
                                try {
                                    byte[] imageByteArray = android.util.Base64.decode(imageUrl.split(",")[1], android.util.Base64.DEFAULT);
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(this).load(imageByteArray).centerCrop().into(ivThumb);
                                } catch (Exception e) {
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(HomeFragment.this).load(R.drawable.ve_hoa_mau_nuoc).centerCrop().into(ivThumb);
                                }
                            } else {
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                com.bumptech.glide.Glide.with(this).load(imageUrl).centerCrop().into(ivThumb);
                            }
                        } else {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(HomeFragment.this).load(R.drawable.ve_hoa_mau_nuoc).centerCrop().into(ivThumb);
                        }
                    }

                    // Mặc định cho bài gợi ý là "Đang học" hoặc "Chưa học"
                    tvStatus.setText("Gợi ý");
                    tvStatus.setBackgroundResource(R.drawable.bg_badge_pending);
                    tvStatus.setTextColor(Color.parseColor("#808080"));

                    android.widget.RatingBar rb = lessonView.findViewById(R.id.rating_bar);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (rb != null) rb.setRating(4.5f);

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (tvDuration != null) {
                        Long actualDuration = doc.getLong("durationMin");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (actualDuration != null && actualDuration > 0) {
                            tvDuration.setText(actualDuration + " min");
                        } else {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (category != null && (category.toLowerCase().contains("mới bắt đầu"))) tvDuration.setText("20 min");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            else if (category != null && category.toLowerCase().contains("thiên nhiên")) tvDuration.setText("45 min");
                            else tvDuration.setText("60 min");
                        }
                    }

                    // Check progress để biết Đang học
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Users").document(uid).collection("lessonProgress").document(title)
                        .get().addOnSuccessListener(progDoc -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (progDoc.exists()) {
                                String status = progDoc.getString("status");
                                if ("IN_PROGRESS".equals(status) || "WAITING_FOR_HOMEWORK".equals(status)) {
                                    tvStatus.setText("Đang học");
                                    tvStatus.setBackgroundResource(R.drawable.bg_badge_in_progress);
                                    tvStatus.setTextColor(Color.WHITE);
                                }
                            }
                        });

                    final String finalAuthor = author;
                    final String finalImageResStr = imageResStr;
                    lessonView.setOnClickListener(v -> {
                        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                        Intent intent = new Intent(getActivity(), LessonDetailActivity.class);
                        intent.putExtra("LESSON_TITLE", title);
                        intent.putExtra("CATEGORY", category);
                        intent.putExtra("IMAGE_RES", finalImageResStr);
                        intent.putExtra("AUTHOR", finalAuthor);
                        intent.putExtra("LESSON_ID", doc.getId());
                        startActivity(intent);
                    });

                    container.addView(lessonView);
                }
                isLoadingSuggested = false; // Reset để lần sau onResume có thể refresh
            });
        });
    }

    /**
     * Hàm setupSavedLessons() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupSavedLessons(View view) {
        android.widget.LinearLayout container = view.findViewById(R.id.ll_saved_lessons_container);
        android.widget.LinearLayout header = view.findViewById(R.id.ll_saved_lessons_header);
        android.widget.HorizontalScrollView hsv = view.findViewById(R.id.hsv_saved_lessons);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null || header == null || hsv == null)
            return;

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (auth.getCurrentUser() == null)
            return;
        String uid = auth.getCurrentUser().getUid();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (savedLessonsListenerReg != null) {
            savedLessonsListenerReg.remove();
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        savedLessonsListenerReg = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .collection("Users").document(uid).collection("savedLessons")
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                .orderBy("savedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(5)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (e != null || queryDocumentSnapshots == null)
                        return;

                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (queryDocumentSnapshots.isEmpty()) {
                        header.setVisibility(View.GONE);
                        hsv.setVisibility(View.GONE);
                        return;
                    }

                    header.setVisibility(View.VISIBLE);
                    hsv.setVisibility(View.VISIBLE);

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (getContext() == null)
                        return;
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    container.removeAllViews();

                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        String title = doc.getString("title");
                        String category = doc.getString("category");
                        String imageResStr = doc.getString("imageRes");
                        String author = doc.getString("author");

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (author == null || author.isEmpty()) {
                            author = "Phong Artist"; // default
                        }

                        View lessonView = inflater.inflate(R.layout.item_lesson_preview, container, false);
                        TextView tvTitle = lessonView.findViewById(R.id.tv_lesson_title);
                        TextView tvAuthor = lessonView.findViewById(R.id.tv_lesson_author);
                        ImageView ivThumb = lessonView.findViewById(R.id.iv_lesson_thumb);
                        TextView tvStatus = lessonView.findViewById(R.id.tv_lesson_status);
                        TextView tvDuration = lessonView.findViewById(R.id.tv_duration);
                        android.widget.RatingBar rb = lessonView.findViewById(R.id.rating_bar);

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTitle != null && title != null)
                            tvTitle.setText(title);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvAuthor != null)
                            tvAuthor.setText(author);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (rb != null)
                            rb.setRating(5.0f);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvStatus != null) {
                            tvStatus.setText("Đã lưu");
                            tvStatus.setBackgroundResource(R.drawable.rounded_bg_gray);
                            tvStatus.setTextColor(android.graphics.Color.parseColor("#808080"));

                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (uid != null && title != null) {
                                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                        .collection("Users").document(uid).collection("lessonProgress").document(title)
                                        .get().addOnSuccessListener(progDoc -> {
                                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                            if (progDoc.exists()) {
                                                String status = progDoc.getString("status");
                                                if ("COMPLETED".equals(status)) {
                                                    tvStatus.setText("Hoàn thành");
                                                    tvStatus.setBackgroundResource(R.drawable.bg_badge_completed);
                                                    tvStatus.setTextColor(android.graphics.Color.WHITE);
                                                } else if ("IN_PROGRESS".equals(status)
                                                        || "WAITING_FOR_HOMEWORK".equals(status)) {
                                                    tvStatus.setText("Đang học");
                                                    tvStatus.setBackgroundResource(R.drawable.bg_badge_in_progress);
                                                    tvStatus.setTextColor(android.graphics.Color.WHITE);
                                                }
                                            }
                                        });
                            }
                        }

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvDuration != null) {
                            tvDuration.setText("♥");
                        }

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivThumb != null && imageResStr != null && !imageResStr.isEmpty()) {
                            try {
                                int resId = getResources().getIdentifier(imageResStr, "drawable",
                                        getContext().getPackageName());
                                if (resId != 0)
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(HomeFragment.this).load(resId).centerCrop().into(ivThumb);
                            } catch (Exception ex) {
                            }
                        }

                        final String finalAuthor = author;
                        final String finalImageResStr = imageResStr;
                        lessonView.setOnClickListener(v -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (tvStatus != null && "Hoàn thành".equals(tvStatus.getText().toString())) {
                                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                Intent intent = new Intent(getActivity(), com.example.appdraw.explore.MySubmissionActivity.class);
                                intent.putExtra("LESSON_TITLE", title);
                                startActivity(intent);
                            } else {
                                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                Intent intent = new Intent(getActivity(), LessonDetailActivity.class);
                                intent.putExtra("LESSON_TITLE", title);
                                intent.putExtra("CATEGORY", category);
                                intent.putExtra("IMAGE_RES", finalImageResStr);
                                intent.putExtra("AUTHOR", finalAuthor);
                                intent.putExtra("LESSON_ID", doc.getId());
                                startActivity(intent);
                            }
                        });

                        container.addView(lessonView);
                    }
                });
    }

    /**
     * Hàm setupHomeEvents() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupHomeEvents(View view) {
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        RecyclerView rvMySchedule = view.findViewById(R.id.rv_home_my_schedule);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        RecyclerView rvExploreEvents = view.findViewById(R.id.rv_home_explore_events);
        TextView tvEmptySchedule = view.findViewById(R.id.tv_empty_schedule);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (rvMySchedule == null || rvExploreEvents == null)
            return;

        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvMySchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExploreEvents.setVisibility(View.GONE);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseUser user = auth.getCurrentUser();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user == null)
            return;

        rvMySchedule.setVisibility(View.VISIBLE);
        db.collection("EventRegistrations")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(ticketDocs -> {
                    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                    List<EventTicket> myTickets = new ArrayList<>();
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (com.google.firebase.firestore.DocumentSnapshot doc : ticketDocs) {
                        EventTicket t = doc.toObject(EventTicket.class);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (t != null)
                            myTickets.add(t);
                    }

                    db.collection("Events").get().addOnSuccessListener(eventDocs -> {
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        List<Event> upcomingEvents = new ArrayList<>();
                        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                        List<Event> exploreEvents = new ArrayList<>();
                        long now = System.currentTimeMillis();
                        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (com.google.firebase.firestore.DocumentSnapshot doc : eventDocs) {
                            Event e = doc.toObject(Event.class);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (e != null) {
                                boolean isExpired = false;
                                try {
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    if (e.getEndTime() != null && e.getEndTime().contains(":")) {
                                        String[] parts = e.getEndTime().split(":");
                                        int hour = Integer.parseInt(parts[0].trim());
                                        int min = Integer.parseInt(parts[1].trim());
                                        java.util.Calendar cal = java.util.Calendar.getInstance();
                                        cal.setTimeInMillis(e.getDateMillis());
                                        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                                        cal.set(java.util.Calendar.MINUTE, min);
                                        if (cal.getTimeInMillis() < now) isExpired = true;
                                    } else if (e.getDateMillis() + 24 * 60 * 60 * 1000L < now) {
                                        isExpired = true;
                                    }
                                } catch (Exception ex) {
                                    if (e.getDateMillis() + 24 * 60 * 60 * 1000L < now) isExpired = true;
                                }

                                if (!isExpired) {
                                    boolean hasTicket = false;
                                    for (EventTicket t : myTickets) {
                                        if (t.getEventId().equals(e.getId())) {
                                            hasTicket = true;
                                            break;
                                        }
                                    }
                                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                    boolean isAuthor = e.getAuthorId() != null && e.getAuthorId().equals(user.getUid());
                                    if (hasTicket || isAuthor || "Live".equals(e.getEventType())) {
                                        upcomingEvents.add(e);
                                    } else {
                                        exploreEvents.add(e);
                                    }
                                }
                            }
                        }

                        java.util.Collections.sort(upcomingEvents,
                                (e1, e2) -> Long.compare(e1.getDateMillis(), e2.getDateMillis()));
                        java.util.Collections.sort(exploreEvents,
                                (e1, e2) -> Long.compare(e1.getDateMillis(), e2.getDateMillis()));

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (upcomingEvents.isEmpty()) {
                            tvEmptySchedule.setText("Bạn chưa có lịch học nào. Nhấn Xem lịch để khám phá!");
                            tvEmptySchedule.setVisibility(View.VISIBLE);
                            rvMySchedule.setVisibility(View.GONE);
                        } else {
                            tvEmptySchedule.setVisibility(View.GONE);
                            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
                            rvMySchedule.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                            rvMySchedule.setVisibility(View.VISIBLE);
                            int limit = Math.min(10, upcomingEvents.size());
                            // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
                            List<Event> displayEvents = upcomingEvents.subList(0, limit);
                            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
                            rvMySchedule.setAdapter(new HomeExploreEventAdapter(displayEvents, myTickets));
                        }

                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (exploreEvents.isEmpty()) {
                            rvExploreEvents.setVisibility(View.GONE);
                        } else {
                            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
                            rvExploreEvents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            rvExploreEvents.setVisibility(View.VISIBLE);
                            int limit = Math.min(5, exploreEvents.size());
                            // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
                            rvExploreEvents.setAdapter(new HomeExploreEventAdapter(exploreEvents.subList(0, limit), myTickets));
                        }
                    });
                });
    }

    private class HomeExploreEventAdapter extends HomeScheduleAdapter {
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        HomeExploreEventAdapter(List<Event> l, List<EventTicket> myT) {
            super(l, myT);
        }

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
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            VH holder = super.onCreateViewHolder(parent, viewType);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (lp != null) {
                lp.width = (int) (300 * parent.getContext().getResources().getDisplayMetrics().density);
                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) lp).rightMargin = (int) (12 * parent.getContext().getResources().getDisplayMetrics().density);
                    ((ViewGroup.MarginLayoutParams) lp).leftMargin = (int) (4 * parent.getContext().getResources().getDisplayMetrics().density);
                }
                holder.itemView.setLayoutParams(lp);
            }
            return holder;
        }
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class HomeScheduleAdapter extends RecyclerView.Adapter<HomeScheduleAdapter.VH> {
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        List<Event> list;
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        List<EventTicket> tickets;

        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        HomeScheduleAdapter(List<Event> l, List<EventTicket> myT) {
            list = l;
            tickets = myT;
        }

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
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_schedule, parent, false));
        }

        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Event e = list.get(position);
            holder.tvTitle.setText(e.getTitle());

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTimeInMillis(e.getDateMillis());
            String endTimeStr = e.getEndTime();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                holder.tvTime.setText(e.getStartTime() + " - " + endTimeStr + " • " + cal.get(java.util.Calendar.DAY_OF_MONTH) + "/"
                        + (cal.get(java.util.Calendar.MONTH) + 1));
            } else {
                holder.tvTime.setText(e.getStartTime() + " - " + cal.get(java.util.Calendar.DAY_OF_MONTH) + "/"
                        + (cal.get(java.util.Calendar.MONTH) + 1));
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Users").document(e.getAuthorId())
                    .get().addOnSuccessListener(doc -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (doc.exists() && holder.tvSubtitle != null) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                            String fullName = "Người ẩn danh";
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null && profile.containsKey("fullName")) {
                                fullName = (String) profile.get("fullName");
                            }
                            holder.tvSubtitle.setText(fullName + " - " + (e.isOnline() ? "Online" : "Offline"));
                        }
                    });

            if ("Live".equals(e.getEventType())) {
                holder.tvBadge.setText("Live");
                holder.tvBadge
                        .setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935")));
                holder.btnAction.setText("Tham gia");
                holder.btnAction.setOnClickListener(
                        v -> Toast.makeText(getContext(), "Đang vào phòng Live...", Toast.LENGTH_SHORT).show());
            } else {
                holder.tvBadge.setText("Workshop");
                holder.tvBadge
                        .setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F57C00")));

                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                String uid = FirebaseAuth.getInstance().getUid();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                boolean isAuthor = uid != null && uid.equals(e.getAuthorId());

                if (isAuthor) {
                    holder.btnAction.setText("Xem");
                    holder.btnAction.setOnClickListener(
                            v -> Toast.makeText(getContext(), "Bạn là nhà tổ chức", Toast.LENGTH_SHORT).show());
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    String myTicketId = null;
                    for (EventTicket t : tickets) {
                        if (t.getEventId().equals(e.getId())) {
                            myTicketId = t.getId();
                        }
                    }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (myTicketId != null) {
                        holder.btnAction.setText("Xem vé");
                        String finalMyTicketId = myTicketId;
                        holder.btnAction.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(getActivity(),
                                    com.example.appdraw.community.EventTicketActivity.class);
                            intent.putExtra("EVENT_ID", e.getId());
                            intent.putExtra("TICKET_ID", finalMyTicketId);
                            startActivity(intent);
                        });
                    } else {
                        holder.btnAction.setText("Đăng ký");
                        holder.btnAction.setOnClickListener(v -> registerHomeEvent(e));
                    }
                }
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (e.getCoverImageBase64() != null && e.getCoverImageBase64().startsWith("data:image")) {
                byte[] b = android.util.Base64.decode(e.getCoverImageBase64().split(",")[1],
                        android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(HomeFragment.this).load(b).centerCrop().into(holder.ivCover);
            }
        }

        @Override
        /**
         * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
         * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
         */
        public int getItemCount() {
            return list.size();
        }

/**
 * Lớp VH thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file HomeFragment.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle, tvTime, tvBadge, btnAction;
            com.google.android.material.imageview.ShapeableImageView ivCover;

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            VH(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_event_title);
                tvSubtitle = itemView.findViewById(R.id.tv_event_subtitle);
                tvTime = itemView.findViewById(R.id.tv_event_time);
                tvBadge = itemView.findViewById(R.id.tv_event_badge);
                btnAction = itemView.findViewById(R.id.btn_event_action);
                ivCover = itemView.findViewById(R.id.iv_event_cover);
            }
        }
    }

    /**
     * Hàm setupChallenges() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param view tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void setupChallenges(View view) {
        android.widget.LinearLayout container = view.findViewById(R.id.ll_challenges_container);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (container == null)
            return;

        // Hủy listener cũ nếu có, tránh tích lũy
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (challengeListenerReg != null) {
            challengeListenerReg.remove();
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        challengeListenerReg = db.collection("Challenges").addSnapshotListener((queryDocumentSnapshots, error) -> {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (error != null || queryDocumentSnapshots == null)
                return;

            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (queryDocumentSnapshots.isEmpty()) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (getContext() != null) {
                    container.removeAllViews();
                    TextView tvEmpty = new TextView(getContext());
                    tvEmpty.setText("Chưa có thử thách nào diễn ra.");
                    tvEmpty.setPadding(32, 32, 32, 32);
                    container.addView(tvEmpty);
                }
                return;
            } else {
                // Tự động quét và xoá 3 bài rác cũ khỏi Server
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                    String t = doc.getString("title");
                    if ("Vẽ cây ngày trái đất".equals(t) || "14 ngày ký họa phong cảnh".equals(t)
                            || "Thử thách Anime 30 ngày".equals(t)) {
                        doc.getReference().delete();
                    }
                }
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (getContext() == null)
                return;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            container.removeAllViews();

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseAuth auth = FirebaseAuth.getInstance();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            String uid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

            int count = 0;
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            java.util.List<com.google.firebase.firestore.DocumentSnapshot> docs = new java.util.ArrayList<>(queryDocumentSnapshots.getDocuments());
            docs.sort((doc1, doc2) -> {
                Long end1 = doc1.getLong("endTimeMillis");
                Long end2 = doc2.getLong("endTimeMillis");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (end1 == null) end1 = Long.MAX_VALUE;
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (end2 == null) end2 = Long.MAX_VALUE;
                long now = System.currentTimeMillis();
                boolean a1 = end1 > now;
                boolean a2 = end2 > now;
                if (a1 && !a2) return -1;
                if (!a1 && a2) return 1;
                return end1.compareTo(end2);
            });

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            for (com.google.firebase.firestore.DocumentSnapshot doc : docs) {
                if (count >= 1)
                    break; // Only display 1 challenge dynamically

                String title = doc.getString("title");
                String author = doc.getString("author");
                String dateStr = doc.getString("dateStr");
                String participantsCount = doc.getString("participantsCount");
                String imageResStr = doc.getString("imageRes");
                String imageUrl = doc.getString("imageUrl");
                String rulesStr = doc.getString("rules");
                String rewardsStr = doc.getString("rewards");

                Long endTimeMillis = doc.getLong("endTimeMillis");
                boolean isExpired = false;
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (endTimeMillis != null) {
                    if (System.currentTimeMillis() > endTimeMillis) {
                        isExpired = true;
                    }
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                } else if (dateStr != null && dateStr.contains("-")) {
                    try {
                        String endDate = dateStr.split("-")[1].trim();
                        if (endDate.length() <= 5) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
                            java.util.Date date = sdf.parse(endDate);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (date != null) {
                                java.util.Calendar cal = java.util.Calendar.getInstance();
                                int currentYear = cal.get(java.util.Calendar.YEAR);
                                cal.setTime(date);
                                cal.set(java.util.Calendar.YEAR, currentYear);
                                cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                                cal.set(java.util.Calendar.MINUTE, 59);
                                if (System.currentTimeMillis() > cal.getTimeInMillis()) {
                                    isExpired = true;
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
                final boolean finalIsExpired = isExpired;

                View cardView = inflater.inflate(R.layout.item_challenge_card, container, false);

                TextView tvTitle = cardView.findViewById(R.id.tv_challenge_title);
                TextView tvAuthor = cardView.findViewById(R.id.tv_challenge_author);
                TextView tvDate = cardView.findViewById(R.id.tv_challenge_date);
                TextView tvParticipants = cardView.findViewById(R.id.tv_participants_count);
                ImageView ivImage = cardView.findViewById(R.id.iv_challenge_image);
                MaterialButton btnJoin = cardView.findViewById(R.id.btnJoinChallenge);

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvTitle != null)
                    tvTitle.setText("Thử thách: " + title);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvAuthor != null)
                    tvAuthor.setText(author);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvDate != null)
                    tvDate.setText(dateStr);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (tvParticipants != null)
                    tvParticipants.setText(participantsCount);

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (ivImage != null) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        if (imageUrl.startsWith("data:image")) {
                            try {
                                String base64Data = imageUrl.substring(imageUrl.indexOf(",") + 1);
                                byte[] b = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                Glide.with(this).asBitmap().load(b).centerCrop().error(R.drawable.ve_hoa_mau_nuoc).into(ivImage);
                            } catch (Exception e) {
                                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                Glide.with(this).load(R.drawable.ve_hoa_mau_nuoc).centerCrop().into(ivImage);
                            }
                        } else {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            Glide.with(this).load(imageUrl).centerCrop().into(ivImage);
                        }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    } else if (imageResStr != null && !imageResStr.isEmpty()) {
                        try {
                            int resId = Integer.parseInt(imageResStr);
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            com.bumptech.glide.Glide.with(HomeFragment.this).load(resId).centerCrop().into(ivImage);
                        } catch (Exception e) {
                            // imageResStr là tên drawable string
                            try {
                                int resId = getResources().getIdentifier(imageResStr, "drawable",
                                        requireContext().getPackageName());
                                if (resId != 0)
                                    // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                                    com.bumptech.glide.Glide.with(HomeFragment.this).load(resId).centerCrop().into(ivImage);
                            } catch (Exception ex) {
                            }
                        }
                    }
                }

                String authorId = doc.getString("authorId");

                // Check role and status locally
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (uid != null) {
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Users").document(uid).get().addOnSuccessListener(userDoc -> {
                        String role = userDoc.getString("role");

                        String mentorName = "Mentor";
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (userDoc.exists()) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> profile = (java.util.Map<String, Object>) userDoc
                                    .get("profile");
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null && profile.containsKey("fullName")) {
                                mentorName = "Mentor: " + profile.get("fullName");
                            }
                        }

                        if ("mentor".equals(role)) {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (btnJoin != null) {
                                boolean isAuthor = false;
                                if (uid.equals(authorId))
                                    isAuthor = true;
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                else if (authorId == null && author != null && author.equals(mentorName))
                                    isAuthor = true; // Fallback cho bài cũ

                                if (isAuthor) {
                                    btnJoin.setText("Quản lý");
                                    btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2D5A9E")));
                                } else {
                                    btnJoin.setText("Chấm điểm bài");
                                    btnJoin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                                }
                            }
                        } else {
                            // Check if joined or submitted
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (btnJoin != null && finalIsExpired) {
                                btnJoin.setText("Đã kết thúc");
                                btnJoin.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E")));
                            }
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Users").document(uid).collection("joinedChallenges").document(title)
                                    .get().addOnSuccessListener(chalDoc -> {
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        if (chalDoc.exists() && btnJoin != null) {
                                            String status = chalDoc.getString("status");
                                            if ("SUBMITTED".equals(status) || "GRADED".equals(status)) {
                                                btnJoin.setText("Đã nộp");
                                                btnJoin.setBackgroundTintList(
                                                        ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
                                            } else {
                                                btnJoin.setText(finalIsExpired ? "Đã kết thúc" : "Tiếp tục");
                                                btnJoin.setBackgroundTintList(
                                                        ColorStateList.valueOf(Color.parseColor(finalIsExpired ? "#9E9E9E" : "#E67E22"))); // Gray or Orange
                                            }
                                        }
                                    });
                        }
                    });
                }

                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (btnJoin != null) {
                    btnJoin.setOnClickListener(v -> {
                        String currentText = btnJoin.getText().toString();
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if ("Tham gia".equals(currentText) && uid != null) {
                            if (finalIsExpired) {
                                Toast.makeText(getContext(), "Thử thách này đã kết thúc, không thể tham gia!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            btnJoin.setEnabled(false); // Ngăn double-click

                            // Check if previously joined to prevent multiple increments
                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                            db.collection("Users").document(uid).collection("joinedChallenges").document(title)
                                    .get().addOnSuccessListener(chalDoc -> {
                                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                        if (!chalDoc.exists()) {
                                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                            java.util.Map<String, Object> joinData = new java.util.HashMap<>();
                                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                            joinData.put("status", "JOINED");
                                            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                            db.collection("Users").document(uid).collection("joinedChallenges")
                                                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                                    .document(title).set(joinData);

                                            // Increment global counter
                                            try {
                                                String countStr = participantsCount;
                                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                                if (countStr != null)
                                                    countStr = countStr.replaceAll("[^0-9]", "");
                                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                                int currentCount = (countStr == null || countStr.isEmpty()) ? 0
                                                        : Integer.parseInt(countStr);
                                                currentCount++;
                                                String newCountStr = currentCount + " đã tham gia";
                                                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                                                db.collection("Challenges").document(doc.getId())
                                                        .update("participantsCount", newCountStr);
                                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                                if (tvParticipants != null)
                                                    tvParticipants.setText(newCountStr);
                                            } catch (Exception e) {
                                            }

                                            Toast.makeText(getContext(), "Đã tham gia thử thách!", Toast.LENGTH_SHORT)
                                                    .show();
                                        }

                                        btnJoin.setEnabled(true);
                                        btnJoin.setText("Tiếp tục");
                                        btnJoin.setBackgroundTintList(
                                                ColorStateList.valueOf(Color.parseColor("#E67E22")));
                                    })
                                    .addOnFailureListener(e -> btnJoin.setEnabled(true));
                        } else {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(getActivity(), ChallengeDetailActivity.class);
                            intent.putExtra("CHALLENGE_TITLE", title);
                            intent.putExtra("CHALLENGE_IMAGE_URL", imageUrl);
                            intent.putExtra("CHALLENGE_RULES", rulesStr);
                            intent.putExtra("CHALLENGE_REWARDS", rewardsStr);
                            intent.putExtra("CHALLENGE_DEADLINE", dateStr);
                            startActivity(intent);
                        }
                    });
                }

                // Also make the whole card clickable
                cardView.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(getActivity(), ChallengeDetailActivity.class);
                    intent.putExtra("CHALLENGE_TITLE", title);
                    intent.putExtra("CHALLENGE_IMAGE_URL", imageUrl);
                    intent.putExtra("CHALLENGE_RULES", rulesStr);
                    intent.putExtra("CHALLENGE_REWARDS", rewardsStr);
                    intent.putExtra("CHALLENGE_DEADLINE", dateStr);
                    startActivity(intent);
                });

                container.addView(cardView);
                count++;
            }
        });
    }

    /**
     * Hàm registerHomeEvent() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void registerHomeEvent(Event event) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null) return;
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String ticketId = FirebaseFirestore.getInstance().collection("EventRegistrations").document().getId();
        String ticketCode = "TKT" + String.format("%04d", (int)(Math.random() * 10000));
        EventTicket ticket = new EventTicket(ticketId, event.getId(), uid, ticketCode, System.currentTimeMillis());
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        FirebaseFirestore.getInstance().collection("EventRegistrations").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (getView() != null) setupHomeEvents(getView());
                    showSuccessDialog(event, ticket);
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (event.getAuthorId() != null && !event.getAuthorId().equals(uid)) {
                        com.example.appdraw.utils.NotificationHelper.sendNotification(event.getAuthorId(), "EVENT", "Một người dùng vừa đăng ký sự kiện: " + event.getTitle(), event.getId());
                    }
                    com.example.appdraw.utils.NotificationHelper.sendNotification(uid, "EVENT", "Bạn đã đăng ký thành công sự kiện: " + event.getTitle(), event.getId());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi đăng ký", Toast.LENGTH_SHORT).show());
    }

    /**
     * Hàm showSuccessDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param ticket tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showSuccessDialog(Event event, EventTicket ticket) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getContext() == null) return;
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_event_registered);
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_event_title);
        TextView tvTime = dialog.findViewById(R.id.tv_dialog_event_time);
        TextView tvLocation = dialog.findViewById(R.id.tv_dialog_event_location);
        TextView tvFormat = dialog.findViewById(R.id.tv_dialog_event_format);
        TextView tvPrice = dialog.findViewById(R.id.tv_dialog_event_price);
        View btnViewTicket = dialog.findViewById(R.id.btn_dialog_view_ticket);
        View btnBackSchedule = dialog.findViewById(R.id.btn_dialog_back_schedule);

        tvTitle.setText(event.getTitle());
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTimeInMillis(event.getDateMillis());
        tvTime.setText(event.getStartTime() + " - " + event.getEndTime() + " - " + cal.get(java.util.Calendar.DAY_OF_MONTH) + "/" + (cal.get(java.util.Calendar.MONTH)+1));
        
        tvLocation.setText(event.getLocation());
        tvFormat.setText(event.isOnline() ? "Online" : "Offline");
        tvPrice.setText(event.getPrice());

        btnViewTicket.setOnClickListener(v -> {
            dialog.dismiss();
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(getActivity(), com.example.appdraw.community.EventTicketActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            intent.putExtra("TICKET_ID", ticket.getId());
            startActivity(intent);
        });

        btnBackSchedule.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
