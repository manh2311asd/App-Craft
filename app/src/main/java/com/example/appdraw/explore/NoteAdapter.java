package com.example.appdraw.explore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appdraw.R;
import com.example.appdraw.model.Note;
import java.util.List;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp NoteAdapter thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file NoteAdapter.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    /**
     * Biến `noteList` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Note> noteList;
    /**
     * Biến `listener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private OnNoteClickListener listener;

/**
 * Interface OnNoteClickListener thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: NoteAdapter.java.
 */
    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    /**
     * Hàm NoteAdapter() thực hiện một phần xử lý trong luồng chức năng của lớp OnNoteClickListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param noteList tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param listener tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public NoteAdapter(List<Note> noteList, OnNoteClickListener listener) {
        this.noteList = noteList;
        this.listener = listener;
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
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    /**
     * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
     * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
     * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvTime.setText(note.getTimestampFormatted());
        holder.tvStep.setText("Bước " + note.getStepIndex());
        holder.tvContent.setText(note.getContent());
        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (listener != null)
                listener.onNoteClick(note);
        });
    }

    @Override
    /**
     * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
     * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
     */
    public int getItemCount() {
        return noteList.size();
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvStep, tvContent;

        NoteViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_note_time);
            tvStep = itemView.findViewById(R.id.tv_note_step);
            tvContent = itemView.findViewById(R.id.tv_note_content);
        }
    }
}
