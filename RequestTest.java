import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Lớp RequestTest thuộc nhóm kiểm thử hoặc file thử nghiệm phục vụ kiểm tra nhanh chức năng.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file RequestTest.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class RequestTest {
    /**
     * Hàm main() thực hiện một phần xử lý trong luồng chức năng của lớp RequestTest.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param args tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public static void main(String[] args) {
        try {
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            String apiKey = "AIzaSyBByaD4nOJI3knu5dhwzvecXnn_G8yqN4s";
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
            URL url = new URL(urlStr);
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
            conn.setDoOutput(true);
            
            String json = "{\"contents\":[{\"parts\":[{\"text\":\"hi\"}]}]}";
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
            
            int responseCode = conn.getResponseCode();
            System.out.println("Code: " + responseCode);
            
            java.io.InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
