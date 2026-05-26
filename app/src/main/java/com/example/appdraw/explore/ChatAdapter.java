package com.example.appdraw.explore;

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

import com.example.appdraw.R;

import java.util.List;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp ChatAdapter thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChatAdapter.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    /**
     * Biến `messages` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    private final List<GeminiVisionService.ChatMessage> messages;

    /**
     * Constructor của lớp ChatAdapter, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param messages tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    public ChatAdapter(List<GeminiVisionService.ChatMessage> messages) {
        this.messages = messages;
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
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    /**
     * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
     * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
     * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        GeminiVisionService.ChatMessage msg = messages.get(position);

        if ("model".equals(msg.role)) {
            holder.llAiMessage.setVisibility(View.VISIBLE);
            holder.llUserMessage.setVisibility(View.GONE);
            holder.tvAiText.setText(formatMarkdown(msg.text));
        } else {
            holder.llUserMessage.setVisibility(View.VISIBLE);
            holder.llAiMessage.setVisibility(View.GONE);
            holder.tvUserText.setText(formatMarkdown(msg.text));

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (msg.base64Image != null && !msg.base64Image.isEmpty()) {
                holder.ivUserImage.setVisibility(View.VISIBLE);
                try {
                    String cleanBase64 = msg.base64Image;
                    if (cleanBase64.contains(",")) {
                        cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                    }
                    byte[] decodedString = Base64.decode(cleanBase64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivUserImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.ivUserImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    /**
     * Hàm getItemCount() trả về số lượng phần tử hiện có trong danh sách của RecyclerView.
     * Giá trị này giúp RecyclerView biết cần tạo và hiển thị bao nhiêu item trên màn hình.
     */
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Hàm formatMarkdown() thực hiện một phần xử lý trong luồng chức năng của lớp ChatAdapter.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param text tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private CharSequence formatMarkdown(String text) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (text == null) return "";
        String html = text;
        
        // Escape HTML
        html = html.replace("<", "&lt;").replace(">", "&gt;");
        
        // Headers
        html = html.replaceAll("(?m)^### (.*?)$", "<b><font color='#1976D2'>$1</font></b>");
        html = html.replaceAll("(?m)^## (.*?)$", "<b><font color='#1976D2'>$1</font></b>");
        html = html.replaceAll("(?m)^# (.*?)$", "<b><font color='#1976D2'>$1</font></b>");
        
        // Bold
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        
        // Bullets
        html = html.replaceAll("(?m)^\\s*\\* (.*?)\\r?$", "&nbsp;&nbsp;&#8226; $1");
        html = html.replaceAll("(?m)^\\s*- (.*?)\\r?$", "&nbsp;&nbsp;&#8226; $1");
        html = html.replaceAll("(?m)^\\s*\\+ (.*?)\\r?$", "&nbsp;&nbsp;&#8226; $1");
        
        // Newline
        html = html.replace("\n", "<br>");
        
        return androidx.core.text.HtmlCompat.fromHtml(html, androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        View llAiMessage, llUserMessage;
        TextView tvAiText, tvUserText;
        ImageView ivUserImage;

        /**
         * Hàm ChatViewHolder() thực hiện một phần xử lý trong luồng chức năng của lớp ChatAdapter.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param itemView tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            llAiMessage = itemView.findViewById(R.id.ll_ai_message);
            llUserMessage = itemView.findViewById(R.id.ll_user_message);
            tvAiText = itemView.findViewById(R.id.tv_ai_text);
            tvUserText = itemView.findViewById(R.id.tv_user_text);
            ivUserImage = itemView.findViewById(R.id.iv_user_image);
        }
    }
}
