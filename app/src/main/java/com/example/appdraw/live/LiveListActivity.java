package com.example.appdraw.live;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.example.appdraw.model.LiveRoom;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Danh sách Livestream đang diễn ra (UC-15).
 * Người thực hiện: Đặng Thị Hồng Vân.
 * Hiển thị các phòng xem Live để người học có thể chọn tham gia.
 */
/**
 * Lớp LiveListActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file LiveListActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class LiveListActivity extends AppCompatActivity {

    /**
     * Biến `rvLiveList` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvLiveList;
    /**
     * Biến `tvEmptyLive` lưu dữ liệu/trạng thái quan trọng kiểu TextView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvEmptyLive;
    /**
     * Biến `fabCreateLive` lưu dữ liệu/trạng thái quan trọng kiểu FloatingActionButton, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private FloatingActionButton fabCreateLive;

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
     * Biến `adapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private LiveRoomAdapter adapter;
    /**
     * Biến `liveRooms` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<LiveRoom> liveRooms;

    /**
     * Biến `currentUserID` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String currentUserID;
    /**
     * Biến `currentUserName` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String currentUserName = "User";

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);

        Toolbar toolbar = findViewById(R.id.toolbar_live);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvLiveList = findViewById(R.id.rv_live_list);
        tvEmptyLive = findViewById(R.id.tv_empty_live);
        fabCreateLive = findViewById(R.id.fab_create_live);

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        db = FirebaseFirestore.getInstance();
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        auth = FirebaseAuth.getInstance();
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        liveRooms = new ArrayList<>();

        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvLiveList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LiveRoomAdapter(liveRooms);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvLiveList.setAdapter(adapter);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (auth.getCurrentUser() != null) {
            currentUserID = auth.getCurrentUser().getUid();
            checkUserRole();
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
        }

        fabCreateLive.setOnClickListener(v -> {
            showCreateLiveDialog();
        });

        listenForLiveRooms();
    }

    /**
     * Hàm checkUserRole() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     */
    private void checkUserRole() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Users").document(currentUserID).get().addOnSuccessListener(doc -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (doc.exists()) {
                String role = doc.getString("role");
                if ("mentor".equals(role)) {
                    fabCreateLive.setVisibility(View.VISIBLE);
                }
                
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                Map<String, Object> profile = (Map<String, Object>) doc.get("profile");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (profile != null && profile.containsKey("fullName")) {
                    currentUserName = (String) profile.get("fullName");
                }
            }
        });
    }

    /**
     * Hàm listenForLiveRooms() thực hiện một phần xử lý trong luồng chức năng của lớp LiveListActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void listenForLiveRooms() {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Livestreams").addSnapshotListener((value, error) -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (error != null) {
                return;
            }
            liveRooms.clear();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (value != null && !value.isEmpty()) {
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                for (QueryDocumentSnapshot doc : value) {
                    LiveRoom room = doc.toObject(LiveRoom.class);
                    room.roomId = doc.getId();
                    liveRooms.add(room);
                }
            }
            adapter.notifyDataSetChanged();

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (liveRooms.isEmpty()) {
                tvEmptyLive.setVisibility(View.VISIBLE);
                rvLiveList.setVisibility(View.GONE);
            } else {
                tvEmptyLive.setVisibility(View.GONE);
                rvLiveList.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Hàm showCreateLiveDialog() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void showCreateLiveDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_live, null);
        dialog.setContentView(view);
        
        com.google.android.material.textfield.TextInputEditText etTitle = view.findViewById(R.id.et_live_title);
        com.google.android.material.button.MaterialButton btnStart = view.findViewById(R.id.btn_start_live);
        
        btnStart.setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (title.isEmpty()) {
                title = "Livestream của " + currentUserName;
            }
            dialog.dismiss();
            startHostLivestream(title);
        });
        
        dialog.show();
    }

    /**
     * Hàm startHostLivestream() thực hiện một phần xử lý trong luồng chức năng của lớp LiveListActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void startHostLivestream(String title) {
        // Tạo Live ID ngẫu nhiên hoặc dùng UID làm host
        String liveID = "room_" + currentUserID;
        
        LiveRoom myRoom = new LiveRoom();
        myRoom.hostId = currentUserID;
        myRoom.hostName = currentUserName;
        myRoom.roomName = title;
        // Optionally add an avatar
        
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Livestreams").document(liveID).set(myRoom).addOnSuccessListener(aVoid -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(this, LiveActivity.class);
            intent.putExtra("IS_HOST", true);
            intent.putExtra("LIVE_ID", liveID);
            intent.putExtra("USER_ID", currentUserID);
            intent.putExtra("USER_NAME", currentUserName);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tạo phòng Live: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class LiveRoomAdapter extends RecyclerView.Adapter<LiveRoomAdapter.ViewHolder> {
        /**
         * Biến `rooms` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
         */
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        private List<LiveRoom> rooms;

        /**
         * Hàm LiveRoomAdapter() thực hiện một phần xử lý trong luồng chức năng của lớp LiveListActivity.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param rooms tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        public LiveRoomAdapter(List<LiveRoom> rooms) {
            this.rooms = rooms;
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_room, parent, false);
            return new ViewHolder(view);
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
            LiveRoom room = rooms.get(position);
            holder.tvRoomName.setText(room.roomName);
            holder.tvHostName.setText("Mentor: " + room.hostName);

            // Set avatar if host has one. Here we just query it locally or set default.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("Users").document(room.hostId).get().addOnSuccessListener(doc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (doc.exists()) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    Map<String, Object> profile = (Map<String, Object>) doc.get("profile");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (profile != null && profile.containsKey("avatarUrl")) {
                        String avatarUrl = (String) profile.get("avatarUrl");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (avatarUrl != null && !avatarUrl.isEmpty() && avatarUrl.startsWith("data:image")) {
                            byte[] b = android.util.Base64.decode(avatarUrl.split(",")[1], android.util.Base64.DEFAULT);
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            Glide.with(LiveListActivity.this).load(b).circleCrop().into(holder.ivAvatar);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        } else if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                            Glide.with(LiveListActivity.this).load(avatarUrl).circleCrop().into(holder.ivAvatar);
                        }
                    }
                }
            });

            holder.btnJoinLive.setOnClickListener(v -> {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(LiveListActivity.this, LiveActivity.class);
                intent.putExtra("IS_HOST", false);
                intent.putExtra("LIVE_ID", room.roomId);
                intent.putExtra("USER_ID", currentUserID);
                intent.putExtra("USER_NAME", currentUserName);
                startActivity(intent);
            });
        }

        @Override
        /**
         * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
         * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
         */
        public int getItemCount() {
            return rooms.size();
        }

/**
 * Lớp ViewHolder thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file LiveListActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvRoomName, tvHostName;
            ImageView ivAvatar;
            MaterialButton btnJoinLive;

            /**
             * Constructor của lớp ViewHolder, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
             * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRoomName = itemView.findViewById(R.id.tv_room_name);
                tvHostName = itemView.findViewById(R.id.tv_host_name);
                ivAvatar = itemView.findViewById(R.id.iv_host_avatar);
                btnJoinLive = itemView.findViewById(R.id.btn_join_live);
            }
        }
    }
}
