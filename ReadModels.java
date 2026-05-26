import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lớp ReadModels thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ReadModels.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ReadModels {
    /**
     * Hàm main() thực hiện một phần xử lý trong luồng chức năng của lớp ReadModels.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param args tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("models.json")));
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            Pattern p = Pattern.compile("\"name\":\\s*\"(models/gemini-.*?)\"");
            Matcher m = p.matcher(content);
            while(m.find()) {
                System.out.println(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
