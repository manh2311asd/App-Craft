package com.example.appdraw.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp Post thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file Post.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class Post {
    /**
     * Biến `id` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String id;
    /**
     * Biến `uid` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String uid;
    /**
     * Biến `content` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String content;
    /**
     * Biến `imageUrl` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private String imageUrl;
    /**
     * Biến `category` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String category;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<String> topics = new ArrayList<>();
    /**
     * Biến `likesCount` lưu dữ liệu/trạng thái quan trọng kiểu long, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private long likesCount;
    /**
     * Biến `commentsCount` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private long commentsCount;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<String> likedBy = new ArrayList<>();
    /**
     * Biến `isPublic` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isPublic;
    /**
     * Biến `allowComment` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean allowComment;
    /**
     * Biến `createdAt` lưu dữ liệu/trạng thái quan trọng kiểu long, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private long createdAt;

    public Post() {}

    /**
     * Constructor của lớp Post, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param id tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param uid tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param content tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param imageUrl tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param category tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param topics tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param isPublic tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param allowComment tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param createdAt tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public Post(String id, String uid, String content, String imageUrl, String category, List<String> topics, boolean isPublic, boolean allowComment, long createdAt) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.imageUrl = imageUrl;
        this.category = category;
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        this.topics = topics != null ? topics : new ArrayList<>();
        this.isPublic = isPublic;
        this.allowComment = allowComment;
        this.likesCount = 0;
        this.commentsCount = 0;
        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        this.likedBy = new ArrayList<>();
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public List<String> getTopics() { return topics; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public void setTopics(List<String> topics) { this.topics = topics; }

    public long getLikesCount() { return likesCount; }
    public void setLikesCount(long likesCount) { this.likesCount = likesCount; }

    public long getCommentsCount() { return commentsCount; }
    public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }

    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public List<String> getLikedBy() { return likedBy; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public void setLikedBy(List<String> likedBy) { this.likedBy = likedBy; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    public boolean isAllowComment() { return allowComment; }
    public void setAllowComment(boolean allowComment) { this.allowComment = allowComment; }
}
