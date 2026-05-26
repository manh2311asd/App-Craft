package com.example.appdraw.utils;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp HomeworkHelper thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file HomeworkHelper.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class HomeworkHelper {

    public static class HomeworkDetails {
        /**
         * Biến `desc` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public String desc;
        /**
         * Biến `criteria1` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public String criteria1;
        /**
         * Biến `criteria2` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public String criteria2;

        /**
         * Hàm HomeworkDetails() thực hiện một phần xử lý trong luồng chức năng của lớp HomeworkHelper.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param desc tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param criteria1 tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param criteria2 tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public HomeworkDetails(String desc, String criteria1, String criteria2) {
            this.desc = desc;
            this.criteria1 = criteria1;
            this.criteria2 = criteria2;
        }
    }

    /**
     * Hàm getHomeworkDetails() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param lessonTitle tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public static HomeworkDetails getHomeworkDetails(String lessonTitle) {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (lessonTitle == null) {
            return getDefault();
        }

        String lowerTitle = lessonTitle.toLowerCase();

        if (lowerTitle.contains("chân dung") || lowerTitle.contains("khuôn mặt") || lowerTitle.contains("mắt") || lowerTitle.contains("môi")) {
            return new HomeworkDetails(
                    "Thực hành vẽ chân dung / ngũ quan theo góc độ đã học. Chú ý tỷ lệ kích thước các bộ phận và sắc độ đậm nhạt để tạo khối.",
                    "Độ chính xác về tỷ lệ và hình dáng",
                    "Xử lý sắc độ sáng tối tạo chiều sâu"
            );
        } else if (lowerTitle.contains("phong cảnh") || lowerTitle.contains("cảnh đêm") || lowerTitle.contains("cây") || lowerTitle.contains("bầu trời") || lowerTitle.contains("mây")) {
            return new HomeworkDetails(
                    "Vẽ lại bức phong cảnh theo phong cách của bạn. Hãy chú trọng vào lớp nền (background) và quy luật xa gần (Perspective).",
                    "Xử lý không gian và quy luật xa gần",
                    "Hiệu ứng ánh sáng và phối màu mượt mà"
            );
        } else if (lowerTitle.contains("màu nước") || lowerTitle.contains("loang màu") || lowerTitle.contains("chuyển màu")) {
            return new HomeworkDetails(
                    "Thực hành kỹ thuật loang màu nước (wet-on-wet hoặc wet-on-dry) như hướng dẫn trong bài học.",
                    "Kiểm soát lượng nước",
                    "Độ mượt mà của vệt loang màu"
            );
        } else if (lowerTitle.contains("anime") || lowerTitle.contains("chibi") || lowerTitle.contains("manga")) {
            return new HomeworkDetails(
                    "Dựng hình nhân vật Anime/Manga yêu thích của bạn. Chú ý tỷ lệ mắt to đặc trưng và cấu trúc xương hàm.",
                    "Sự sinh động của biểu cảm nhân vật",
                    "Độ gọn và sắc nét của nét viền (Lineart)"
            );
        } else if (lowerTitle.contains("động vật") || lowerTitle.contains("chó") || lowerTitle.contains("mèo") || lowerTitle.contains("chim")) {
            return new HomeworkDetails(
                    "Vẽ lại con vật trong bài học. Hãy thể hiện rõ chất liệu lông hoặc vảy bằng các nét bút (texture).",
                    "Thể hiện đúng kết cấu chất liệu (texture)",
                    "Tỷ lệ cơ thể động vật tự nhiên"
            );
        } else if (lowerTitle.contains("cơ bản") || lowerTitle.contains("bắt đầu") || lowerTitle.contains("cầm bút") || lowerTitle.contains("đường nét")) {
            return new HomeworkDetails(
                    "Thực hành vẽ các nét cơ bản, kiểm soát lực nhấn bút và độ dứt khoát của từng đường nét.",
                    "Độ tự tin và dứt khoát của đường nét",
                    "Sự gọn gàng trong bài tập"
            );
        }

        // Default
        return getDefault();
    }

    /**
     * Hàm getDefault() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private static HomeworkDetails getDefault() {
        return new HomeworkDetails(
                "Thực hành lại tác phẩm trong bài học theo góc nhìn và phong cách của bạn. Đừng ngại sáng tạo thêm các chi tiết mới nhé!",
                "Bố cục tổng thể cân đối",
                "Cách lựa chọn và phối màu sắc"
        );
    }
}
