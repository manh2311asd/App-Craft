package com.example.appdraw.model;

import java.util.List;

/**
 * Lớp đại diện cho một Bài học (Lesson) trong hệ thống App-Draw.
 * Được thiết kế và quản lý bởi Lê Thùy Linh.
 * Lớp này ánh xạ trực tiếp với Collection "Lessons" trên Firestore.
 */
/**
 * Lớp Lesson thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file Lesson.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class Lesson {
    /**
     * Biến `id` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String id;
    /**
     * Biến `title` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String title;
    /**
     * Biến `category` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String category;
    /**
     * Biến `author` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    private String author; // Tên tác giả
    /**
     * Biến `authorId` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    private String authorId; // ID của mentor tạo bài
    /**
     * Biến `thumbnailUrl` lưu thông tin phục vụ gọi API bên ngoài hoặc cấu hình model, dùng khi ứng dụng cần phân tích/tạo nội dung từ dịch vụ ngoài.
     */
    private String thumbnailUrl; // URL ảnh bìa
    /**
     * Biến `level` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String level;
    /**
     * Biến `durationMin` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int durationMin;
    /**
     * Biến `rating` lưu dữ liệu/trạng thái quan trọng kiểu float, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float rating;
    /**
     * Biến `description` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private String description;
    /**
     * Biến `materials` lưu dữ liệu/trạng thái quan trọng kiểu List<String>, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<String> materials;
    /**
     * Biến `steps` lưu dữ liệu/trạng thái quan trọng kiểu List<Step>, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Step> steps;
    /**
     * Biến `createdAt` lưu dữ liệu/trạng thái quan trọng kiểu long, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private long createdAt; // Thời gian tạo khóa học

    /**
     * Constructor mặc định cần thiết cho Firestore để deserialize dữ liệu.
     */
    /**
     * Constructor của lớp Lesson, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     */
    public Lesson() {
    }

    /**
     * Constructor của lớp Lesson, dùng để khởi tạo đối tượng với các dữ liệu cần thiết trước khi sử dụng.
     * @param id tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param author tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param level tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param durationMin tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param rating tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param description tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param materials tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param steps tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public Lesson(String id, String title, String author, String level, int durationMin, float rating, String description, List<String> materials, List<Step> steps) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.level = level;
        this.durationMin = durationMin;
        this.rating = rating;
        this.description = description;
        this.materials = materials;
        this.steps = steps;
        this.category = "Chung";
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }
    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public List<String> getMaterials() { return materials; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public void setMaterials(List<String> materials) { this.materials = materials; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public List<Step> getSteps() { return steps; }
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public void setSteps(List<Step> steps) { this.steps = steps; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Lớp nội bộ đại diện cho một Bước học (Step) trong Bài học.
     * Mỗi bài học có thể bao gồm nhiều bước vẽ chi tiết.
     */
    public static class Step {
        /**
         * Biến `title` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        private String title;
        /**
         * Biến `description` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        private String description;
        /**
         * Biến `videoUrl` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
         */
        private String videoUrl;

        /**
         * Hàm Step() thực hiện một phần xử lý trong luồng chức năng của lớp Lesson.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         */
        public Step() {
        }

        /**
         * Hàm Step() thực hiện một phần xử lý trong luồng chức năng của lớp Lesson.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param title tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param description tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param videoUrl tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public Step(String title, String description, String videoUrl) {
            this.title = title;
            this.description = description;
            this.videoUrl = videoUrl;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    }
}
