package com.example.appdraw.challenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
/**
 * Lớp ChallengeAdapter thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChallengeAdapter.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder> {

    /**
     * Biến `context` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private final Context context;
    /**
     * Biến `challengeList` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    private final List<DocumentSnapshot> challengeList;
    /**
     * Biến `isMentor` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isMentor = false;
    /**
     * Biến `mentorName` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String mentorName = null;

    /**
     * Constructor của lớp ChallengeAdapter, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param challengeList tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
    public ChallengeAdapter(Context context, List<DocumentSnapshot> challengeList) {
        this.context = context;
        this.challengeList = challengeList;
    }

    /**
     * Hàm setMentor() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeAdapter.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param mentor tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param mentorName tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setMentor(boolean mentor, String mentorName) {
        isMentor = mentor;
        this.mentorName = mentorName;
        notifyDataSetChanged();
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
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_challenge_list, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    /**
     * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
     * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
     * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        DocumentSnapshot doc = challengeList.get(position);
        
        String title = doc.getString("title");
        String dateStr = doc.getString("dateStr");
        String participantsCount = doc.getString("participantsCount");
        String imageUrl = doc.getString("imageUrl");
        String imageRes = doc.getString("imageRes");

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (title != null) holder.tvTitle.setText("Thử thách: " + title);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (dateStr != null) holder.tvDeadline.setText("Thời gian: " + dateStr);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (participantsCount != null) holder.tvParticipants.setText(participantsCount);

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (imageUrl != null && imageUrl.startsWith("http")) {
            // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
            Glide.with(context).load(imageUrl).centerCrop().into(holder.ivThumb);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        } else if (imageUrl != null && imageUrl.contains(",")) {
            try {
                String base64Image = imageUrl.split(",")[1];
                byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(context)
                     .asBitmap()
                     .load(decodedString)
                     .centerCrop()
                     .error(R.drawable.ve_hoa_mau_nuoc)
                     .into(holder.ivThumb);
            } catch (Exception e) {
                holder.ivThumb.setImageResource(R.drawable.ve_hoa_mau_nuoc);
            }
        } else {
            holder.ivThumb.setImageResource(R.drawable.ve_hoa_mau_nuoc);
        }

        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String authorId = doc.getString("authorId");
        String author = doc.getString("author");

        holder.btnSecondary.setVisibility(View.GONE);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        holder.btnSecondary.setOnClickListener(null);

        Long endTimeMillis = doc.getLong("endTimeMillis");
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        boolean isEnded = (endTimeMillis != null && endTimeMillis < System.currentTimeMillis());

        boolean isAuthor = false;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (user != null && authorId != null && authorId.equals(user.getUid())) {
            isAuthor = true;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        } else if (authorId == null && author != null && mentorName != null && author.equals(mentorName)) {
            isAuthor = true;
        }

        if (isEnded) {
            holder.btnAction.setText("Xem kết quả");
            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#9E9E9E")));
        } else if (isAuthor) {
            holder.btnAction.setText("Quản lý");
            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2D5A9E")));
        } else {
            holder.btnAction.setText("Tham gia");
            holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2D5A9E")));
            if (isMentor) {
                holder.btnSecondary.setVisibility(View.GONE);
                holder.btnAction.setText("Chấm điểm bài");
                holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            } else if (user != null) {
                String challengeTitle = doc.getString("title");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (challengeTitle != null) {
                    // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        .collection("Users").document(user.getUid())
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        .collection("joinedChallenges").document(challengeTitle)
                        .get().addOnSuccessListener(shot -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (shot.exists()) {
                                String status = shot.getString("status");
                                if ("SUBMITTED".equals(status) || "GRADED".equals(status)) {
                                    holder.btnAction.setText("Đã nộp");
                                    holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
                                } else if ("JOINED".equals(status)) {
                                    holder.btnAction.setText("Tiếp tục");
                                }
                            }
                        });
                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            android.content.Intent intent = new android.content.Intent(context, ChallengeDetailActivity.class);
            intent.putExtra("CHALLENGE_ID", doc.getId());
            intent.putExtra("CHALLENGE_TITLE", doc.getString("title"));
            intent.putExtra("CHALLENGE_IMAGE_URL", doc.getString("imageUrl"));
            intent.putExtra("CHALLENGE_RULES", doc.getString("rules"));
            intent.putExtra("CHALLENGE_REWARDS", doc.getString("rewards"));
            intent.putExtra("CHALLENGE_DEADLINE", doc.getString("dateStr"));
            context.startActivity(intent);
        });

        holder.btnAction.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            android.content.Intent intent = new android.content.Intent(context, ChallengeDetailActivity.class);
            intent.putExtra("CHALLENGE_ID", doc.getId());
            intent.putExtra("CHALLENGE_TITLE", doc.getString("title"));
            intent.putExtra("CHALLENGE_IMAGE_URL", doc.getString("imageUrl"));
            intent.putExtra("CHALLENGE_RULES", doc.getString("rules"));
            intent.putExtra("CHALLENGE_REWARDS", doc.getString("rewards"));
            intent.putExtra("CHALLENGE_DEADLINE", doc.getString("dateStr"));
            context.startActivity(intent);
        });
    }

    @Override
    /**
     * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
     * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
     */
    public int getItemCount() {
        return challengeList.size();
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvDeadline, tvParticipants;
        MaterialButton btnAction, btnSecondary;

        /**
         * Hàm ChallengeViewHolder() thực hiện một phần xử lý trong luồng chức năng của lớp ChallengeAdapter.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_challenge_thumb);
            tvTitle = itemView.findViewById(R.id.tv_challenge_title);
            tvDeadline = itemView.findViewById(R.id.tv_challenge_deadline);
            tvParticipants = itemView.findViewById(R.id.tv_challenge_participants);
            btnAction = itemView.findViewById(R.id.btn_challenge_action);
            btnSecondary = itemView.findViewById(R.id.btn_challenge_action_secondary);
        }
    }
}
