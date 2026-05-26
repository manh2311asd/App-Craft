package com.example.appdraw.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.example.appdraw.model.Artwork;

import java.util.List;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp ArtworkAdapter thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ArtworkAdapter.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ViewHolder> {
    /**
     * Biến `artworks` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Artwork> artworks;
    /**
     * Biến `listener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private OnItemClickListener listener;

/**
 * Interface OnItemClickListener thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: ArtworkAdapter.java.
 */
    public interface OnItemClickListener {
        void onItemClick(Artwork artwork);
    }

/**
 * Interface OnItemLongClickListener thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: ArtworkAdapter.java.
 */
    public interface OnItemLongClickListener {
        void onItemLongClick(Artwork artwork);
    }

    /**
     * Biến `longClickListener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private OnItemLongClickListener longClickListener;

    /**
     * Hàm ArtworkAdapter() thực hiện một phần xử lý trong luồng chức năng của lớp OnItemLongClickListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param artworks tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param listener tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public ArtworkAdapter(List<Artwork> artworks, OnItemClickListener listener) {
        this.artworks = artworks;
        this.listener = listener;
    }

    /**
     * Hàm setOnItemLongClickListener() thực hiện một phần xử lý trong luồng chức năng của lớp OnItemLongClickListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param longClickListener tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artwork_card, parent, false);
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
        Artwork artwork = artworks.get(position);
        holder.tvTitle.setText(artwork.getTitle());
        
        if (Artwork.STATUS_COMPLETED.equals(artwork.getStatus())) {
            holder.tvStatus.setText("Hoàn thành");
            holder.tvStatus.setTextColor(0xFF2ECC71); // Xanh lá
        } else {
            holder.tvStatus.setText("Đang làm");
            holder.tvStatus.setTextColor(0xFFF0B259); // Vàng
        }

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (artwork.getImageUrl() != null && !artwork.getImageUrl().isEmpty()) {
            if (artwork.getImageUrl().startsWith("data:image")) {
                byte[] b = android.util.Base64.decode(artwork.getImageUrl().split(",")[1], android.util.Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(holder.itemView.getContext())
                        .load(b)
                        .fitCenter()
                        .into(holder.ivThumbnail);
            } else {
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(holder.itemView.getContext())
                        .load(artwork.getImageUrl())
                        .fitCenter()
                        .into(holder.ivThumbnail);
            }
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.backgroud_app_draw); // Ảnh mặc định
        }

        // Format Date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        String dateStr = sdf.format(new java.util.Date(artwork.getCreatedAt()));
        holder.tvDate.setText(dateStr);

        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (listener != null) listener.onItemClick(artwork);
        });

        holder.itemView.setOnLongClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (longClickListener != null) {
                longClickListener.onItemLongClick(artwork);
                return true;
            }
            return false;
        });

        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (holder.btnOptions != null) {
            holder.btnOptions.setOnClickListener(v -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(artwork);
                }
            });
        }
    }

    @Override
    /**
     * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
     * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
     */
    public int getItemCount() {
        return artworks.size();
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail, btnOptions;
        TextView tvTitle, tvStatus, tvDate;

        /**
         * Hàm ViewHolder() thực hiện một phần xử lý trong luồng chức năng của lớp OnItemLongClickListener.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_artwork);
            btnOptions = itemView.findViewById(R.id.btn_artwork_options);
            tvTitle = itemView.findViewById(R.id.tv_artwork_title);
            tvStatus = itemView.findViewById(R.id.tv_artwork_status);
            tvDate = itemView.findViewById(R.id.tv_artwork_date);
        }
    }
}
