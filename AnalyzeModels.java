import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Lớp AnalyzeModels thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file AnalyzeModels.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class AnalyzeModels {
    /**
     * Hàm main() thực hiện một phần xử lý trong luồng chức năng của lớp AnalyzeModels.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param args tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("models.json")));
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            JSONObject root = new JSONObject(content);
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            JSONArray models = root.getJSONArray("models");
            for (int i = 0; i < models.length(); i++) {
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject model = models.getJSONObject(i);
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONArray methods = model.optJSONArray("supportedGenerationMethods");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (methods != null) {
                    for (int j = 0; j < methods.length(); j++) {
                        if (methods.getString(j).equals("generateContent")) {
                            System.out.println(model.getString("name"));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
