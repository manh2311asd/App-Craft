package com.example.appdraw.notification;

import com.example.appdraw.R;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdraw.community.OtherUserProfileActivity;
import com.example.appdraw.community.PostDetailActivity;
import com.example.appdraw.community.EventScheduleActivity;
import com.example.appdraw.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
/**
 * Lớp NotificationAdapter thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file NotificationAdapter.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    /**
     * Biến `notificationList` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Notification> notificationList;
    /**
     * Biến `context` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private Context context;

    /**
     * Constructor của lớp NotificationAdapter, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param notificationList tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
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
        Notification notif = notificationList.get(position);

        // Name Formatting (Bold)
        String boldName = "<b>" + notif.getSenderName() + "</b>";
        String contentText = "";
        switch (notif.getType()) {
            case "LIKE":
                contentText = boldName + " đã thích bài viết của bạn.";
                break;
            case "COMMENT":
                contentText = boldName + " đã bình luận về bài viết của bạn.";
                break;
            case "FOLLOW":
                contentText = boldName + " đánh giá cao tác phẩm và bắt đầu theo dõi bạn.";
                break;
            case "EVENT":
                contentText = "Hệ thống: " + notif.getMessage();
                break;
            default:
                contentText = boldName + " " + notif.getMessage();
        }
        holder.tvMessage.setText(Html.fromHtml(contentText));

        // Format Time
        long elapsed = System.currentTimeMillis() - notif.getTimestamp();
        long minutes = elapsed / 60000;
        if (minutes < 60) holder.tvTime.setText(minutes + " phút trước");
        else if (minutes < 1440) holder.tvTime.setText((minutes / 60) + " giờ trước");
        else holder.tvTime.setText((minutes / 1440) + " ngày trước");

        // Set Avatar
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (notif.getSenderAvatar() != null && !notif.getSenderAvatar().isEmpty()) {
            if (notif.getSenderAvatar().startsWith("data:image")) {
                byte[] b = android.util.Base64.decode(notif.getSenderAvatar().split(",")[1], android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(context).load(b).circleCrop().into(holder.ivAvatar);
            } else {
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(context).load(notif.getSenderAvatar()).circleCrop().into(holder.ivAvatar);
            }
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_default_user);
        }

        // Action button for Follow
        if ("FOLLOW".equals(notif.getType())) {
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("+ Theo dõi"); // Cấu hình ban đầu
            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4272D0")));
            holder.btnAction.setTextColor(android.graphics.Color.WHITE);
            holder.btnAction.setEnabled(true);

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseAuth auth = FirebaseAuth.getInstance();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (auth.getCurrentUser() != null) {
                String currentUid = auth.getUid();
                
                // Kiểm tra xem đã theo dõi hay chưa
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                FirebaseFirestore.getInstance().collection("Follows")
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .document(currentUid + "_" + notif.getSenderId())
                    .get()
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addOnSuccessListener(documentSnapshot -> {
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (documentSnapshot.exists()) {
                            holder.btnAction.setText("Đang theo dõi");
                            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFEFEF")));
                            holder.btnAction.setTextColor(android.graphics.Color.parseColor("#1A1A1A"));
                            holder.btnAction.setEnabled(false);
                        }
                    });

                // Sự kiện click
                holder.btnAction.setOnClickListener(v -> {
                    holder.btnAction.setEnabled(false);
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    Map<String, Object> data = new HashMap<>();
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    data.put("follower", currentUid);
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    data.put("following", notif.getSenderId());
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    data.put("timestamp", System.currentTimeMillis());
                    
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    db.collection("Follows").document(currentUid + "_" + notif.getSenderId())
                        .set(data).addOnSuccessListener(aVoid -> {
                            holder.btnAction.setText("Đang theo dõi");
                            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EFEFEF")));
                            holder.btnAction.setTextColor(android.graphics.Color.parseColor("#1A1A1A"));
                            Toast.makeText(context, "Đã theo dõi lại", Toast.LENGTH_SHORT).show();
                        });
                });
            }
        } else {
            holder.btnAction.setVisibility(View.GONE);
        }

        // Parent click action
        holder.itemView.setOnClickListener(v -> {
            // Mark as read in Firestore
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Notifications").document(notif.getId()).update("isRead", true);
            holder.itemView.setBackgroundResource(R.drawable.ripple_bg_white);

            if ("LIKE".equals(notif.getType()) || "COMMENT".equals(notif.getType())) {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", notif.getTargetId());
                context.startActivity(intent);
            } else if ("FOLLOW".equals(notif.getType())) {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(context, OtherUserProfileActivity.class);
                intent.putExtra("USER_ID", notif.getSenderId());
                context.startActivity(intent);
            } else if ("EVENT".equals(notif.getType())) {
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                Intent intent = new Intent(context, EventScheduleActivity.class);
                context.startActivity(intent);
            }
        });

        if (!notif.isRead()) {
            holder.itemView.setBackgroundResource(R.drawable.ripple_bg_white);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.ripple_bg_white);
        }
    }

    @Override
    /**
     * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
     * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
     */
    public int getItemCount() {
        return notificationList.size();
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvMessage, tvTime, btnAction;
        /**
         * Hàm ViewHolder() thực hiện một phần xử lý trong luồng chức năng của lớp NotificationAdapter.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_notif_avatar);
            tvMessage = itemView.findViewById(R.id.tv_notif_message);
            tvTime = itemView.findViewById(R.id.tv_notif_time);
            btnAction = itemView.findViewById(R.id.btn_notif_action);
        }
    }
}

