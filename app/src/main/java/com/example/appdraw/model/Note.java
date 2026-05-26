package com.example.appdraw.model;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp Note thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file Note.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class Note {
    /**
     * Biến `id` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String id;
    /**
     * Biến `content` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String content;
    private int stepIndex; // Which step this note is recorded on (1-4)
    /**
     * Biến `timestampMs` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int timestampMs; // The video timestamp in milliseconds
    /**
     * Biến `timestampFormatted` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String timestampFormatted; // e.g. "01:24"

    /**
     * Constructor của lớp Note, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     */
    public Note() {
    }

    /**
     * Constructor của lớp Note, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param id tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param content tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param stepIndex tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param timestampMs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param timestampFormatted tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public Note(String id, String content, int stepIndex, int timestampMs, String timestampFormatted) {
        this.id = id;
        this.content = content;
        this.stepIndex = stepIndex;
        this.timestampMs = timestampMs;
        this.timestampFormatted = timestampFormatted;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getStepIndex() { return stepIndex; }
    public void setStepIndex(int stepIndex) { this.stepIndex = stepIndex; }
    public int getTimestampMs() { return timestampMs; }
    public void setTimestampMs(int timestampMs) { this.timestampMs = timestampMs; }
    public String getTimestampFormatted() { return timestampFormatted; }
    public void setTimestampFormatted(String timestampFormatted) { this.timestampFormatted = timestampFormatted; }
}
