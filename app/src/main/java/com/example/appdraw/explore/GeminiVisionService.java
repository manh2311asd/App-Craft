package com.example.appdraw.explore;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp GeminiVisionService thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file GeminiVisionService.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class GeminiVisionService {

    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    // Using Custom API Key from .env
    /**
     * Biến `API_KEY` lưu thông tin phục vụ gọi API bên ngoài hoặc cấu hình model, dùng khi ứng dụng cần phân tích/tạo nội dung từ dịch vụ ngoài.
     */
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    private static final String API_KEY = com.example.appdraw.BuildConfig.AI_API_KEY;
    /**
     * Biến `API_URL` lưu thông tin phục vụ gọi API bên ngoài hoặc cấu hình model, dùng khi ứng dụng cần phân tích/tạo nội dung từ dịch vụ ngoài.
     */
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    private final String API_URL = "https://chat.trollllm.xyz/v1/chat/completions";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

/**
 * Interface GeminiCallback thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: GeminiVisionService.java.
 */
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    public interface GeminiCallback {
        void onSuccess(String feedback, String tip);

        void onError(String error);
    }

/**
 * Interface ChatCallback thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: GeminiVisionService.java.
 */
    public interface ChatCallback {
        void onSuccess(String reply);

        void onError(String error);
    }

    public static class ChatMessage {
        /**
         * Biến `role` lưu dữ liệu/trạng thái quan trọng kiểu String, được sử dụng trong các bước xử lý và hiển thị của lớp.
         */
        public String role; // "user" or "model"
        /**
         * Biến `text` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
         */
        public String text;
        /**
         * Biến `base64Image` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
         */
        public String base64Image; // only for user, optional

        /**
         * Hàm ChatMessage() thực hiện một phần xử lý trong luồng chức năng của lớp ChatCallback.
         * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
         * @param role tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param text tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param base64Image tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        public ChatMessage(String role, String text, String base64Image) {
            this.role = role;
            this.text = text;
            this.base64Image = base64Image;
        }
    }

    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    // Call API for grading artwork
    /**
     * Hàm gradeArtwork() thực hiện một phần xử lý trong luồng chức năng của lớp ChatCallback.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param lessonTitle tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param base64ImageWithPrefix tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param callback tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    public void gradeArtwork(String lessonTitle, String base64ImageWithPrefix, GeminiCallback callback) {
        executorService.execute(() -> {
            try {
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                URL url = new URL(API_URL);
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("HTTP-Referer", "https://github.com/artcraft");
                conn.setRequestProperty("X-Title", "ArtCraft");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000); // 30s
                conn.setReadTimeout(60000);    // 60s

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject payload = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("model", "claude-haiku-4-5-20251001");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("max_tokens", 4096);

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONArray messages = new JSONArray();

                // User Request
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject userMessage = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                userMessage.put("role", "user");

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONArray contentParts = new JSONArray();

                // Text part
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject textPart = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                textPart.put("type", "text");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                textPart.put("text", "[SYSTEM INSTRUCTION: Bạn là một Giảng viên Mỹ thuật AI tận tâm và chuyên nghiệp.]\n\n" +
                        "Dưới đây là bài thực hành của học viên cho khóa học vẽ: '" + lessonTitle + "'. " +
                        "Hãy quan sát ảnh chụp tác phẩm và đưa ra nhận xét ngắn gọn dưới định dạng JSON nguyên thủy (không có markdown box). " +
                        "Cấu trúc JSON yêu cầu bao gồm đúng 2 trường:\n" +
                        "\"feedback\": \"(Nhận xét ngắn về ưu điểm, biểu cảm hoặc kỹ thuật nét vẽ/tô màu)\",\n" +
                        "\"tip\": \"(Một mẹo thật ngắn 1 câu để giúp học viên cải thiện điểm yếu)\"");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                contentParts.put(textPart);

                // Image part
                String cleanBase64 = base64ImageWithPrefix;
                if (cleanBase64.startsWith("data:")) {
                    cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                }
                cleanBase64 = cleanBase64.replaceAll("\\s+", "");

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject imagePart = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                imagePart.put("type", "image_url");
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject imageUrlObj = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                imageUrlObj.put("url", "data:image/jpeg;base64," + cleanBase64);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                imagePart.put("image_url", imageUrlObj);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                contentParts.put(imagePart);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                userMessage.put("content", contentParts);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                messages.put(userMessage);

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("messages", messages);

                String jsonInputString = payload.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonInputString.getBytes("utf-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode >= 200 && responseCode < 300) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder responseString = new StringBuilder();
                    String responseLine;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    while ((responseLine = br.readLine()) != null) responseString.append(responseLine.trim());
                    br.close();

                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    JSONObject root = new JSONObject(responseString.toString());
                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    JSONArray choices = root.optJSONArray("choices");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (choices != null && choices.length() > 0) {
                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                        JSONObject msg = choices.getJSONObject(0).optJSONObject("message");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (msg != null) {
                            String textResponse = msg.optString("content");

                            // Strip markdown json block if exists
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            textResponse = textResponse.replace("```json", "").replace("```", "").trim();
                            // Fix potential bad parsing
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            if (textResponse.contains("{") && textResponse.contains("}")) {
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                if (!textResponse.startsWith("{")) {
                                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                    textResponse = textResponse.substring(textResponse.indexOf("{"));
                                }
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                if (!textResponse.endsWith("}")) {
                                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                    textResponse = textResponse.substring(0, textResponse.lastIndexOf("}")+1);
                                }
                            }

                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            JSONObject resultJson;
                            try {
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                resultJson = new JSONObject(textResponse);
                            } catch (Exception e) {
                                // Fallback if AI didn't follow JSON format correctly
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                resultJson = new JSONObject();
                                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                resultJson.put("feedback", "Tác phẩm rất đẹp và đầy sáng tạo!");
                                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                                resultJson.put("tip", "Tiếp tục phát huy và luyện tập thật nhiều nhé.");
                            }

                            String feedback = resultJson.optString("feedback", "Tác phẩm rất đẹp và sáng tạo!");
                            String tip = resultJson.optString("tip", "Tiếp tục luyện tập mỗi ngày nhé.");

                            mainHandler.post(() -> callback.onSuccess(feedback, tip));
                            return;
                        }
                    }
                    mainHandler.post(() -> callback.onError("Không có nội dung phản hồi từ AI"));
                } else {
                    String responseMsg = "";
                    try { responseMsg = conn.getResponseMessage(); } catch (Exception ignored) {}
                    
                    String errorBody = "";
                    try {
                        java.io.InputStream errorStream = conn.getErrorStream();
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (errorStream != null) {
                            BufferedReader brError = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
                            StringBuilder errorStr = new StringBuilder();
                            String errorLine;
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            while ((errorLine = brError.readLine()) != null) errorStr.append(errorLine.trim());
                            brError.close();
                            errorBody = errorStr.toString();
                        }
                    } catch (Exception ignored) {}
                    
                    final String finalMsg = responseMsg + " | " + errorBody;
                    mainHandler.post(() -> callback.onError("Lỗi kết nối từ server: " + responseCode + " - " + finalMsg));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError("Lỗi xử lý: " + e.getMessage()));
            }
        });
    }

    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    // Call API for Chatting
    /**
     * Hàm chat() thực hiện một phần xử lý trong luồng chức năng của lớp ChatCallback.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param history tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param callback tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    public void chat(List<ChatMessage> history, ChatCallback callback) {
        executorService.execute(() -> {
            try {
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                URL url = new URL(API_URL);
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("HTTP-Referer", "https://github.com/artcraft");
                conn.setRequestProperty("X-Title", "ArtCraft");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(60000);

                boolean hasImage = false;
                for (ChatMessage msg : history) {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (msg.base64Image != null && !msg.base64Image.isEmpty()) {
                        hasImage = true;
                        break;
                    }
                }

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject payload = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("model", "claude-haiku-4-5-20251001");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("max_tokens", 4096);

                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONArray messages = new JSONArray();
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject systemMsg = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                systemMsg.put("role", "system");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                systemMsg.put("content", "Bạn là Trợ lý Mỹ thuật AI. LUÔN LUÔN trả lời bằng TIẾNG VIỆT 100%. Tuyệt đối KHÔNG hiển thị thẻ <thought> hay quá trình suy nghĩ! BẠN CHỈ LÀ TRỢ LÝ VĂN BẢN, bạn KHÔNG THỂ tạo hay vẽ ra hình ảnh (chỉ hướng dẫn bằng lời). Tuyệt đối KHÔNG sinh ra các ký hiệu như <image> hay giả vờ đã vẽ xong.");
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                messages.put(systemMsg);

                boolean hasSeenUser = false;
                for (ChatMessage msg : history) {
                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    JSONObject msgObj = new JSONObject();
                    String role = msg.role.equals("model") ? "assistant" : "user";
                    
                    if (role.equals("user")) {
                        hasSeenUser = true;
                    }
                    
                    // Anthropic strictly requires messages to start with "user". Skip leading "assistant" messages.
                    if (!hasSeenUser && role.equals("assistant")) {
                        continue;
                    }
                    
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    msgObj.put("role", role);
                    
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    String textContent = msg.text != null ? msg.text : "";

                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (msg.base64Image != null && !msg.base64Image.isEmpty()) {
                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                        JSONArray contentParts = new JSONArray();
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (!textContent.isEmpty()) {
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            JSONObject textPart = new JSONObject();
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            textPart.put("type", "text");
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            textPart.put("text", textContent);
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            contentParts.put(textPart);
                        }

                        String cleanBase64 = msg.base64Image;
                        if (cleanBase64.startsWith("data:")) {
                            cleanBase64 = cleanBase64.substring(cleanBase64.indexOf(",") + 1);
                        }
                        cleanBase64 = cleanBase64.replaceAll("\\s+", "");

                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                        JSONObject imagePart = new JSONObject();
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        imagePart.put("type", "image_url");
                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                        JSONObject imgUrl = new JSONObject();
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        imgUrl.put("url", "data:image/jpeg;base64," + cleanBase64);
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        imagePart.put("image_url", imgUrl);
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        contentParts.put(imagePart);
                        
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        msgObj.put("content", contentParts);
                    } else {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        msgObj.put("content", textContent);
                    }
                    
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    messages.put(msgObj);
                }

                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                payload.put("messages", messages);

                String jsonInputString = payload.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonInputString.getBytes("utf-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode >= 200 && responseCode < 300) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    StringBuilder responseString = new StringBuilder();
                    String responseLine;
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    while ((responseLine = br.readLine()) != null) responseString.append(responseLine.trim());
                    br.close();

                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    JSONObject root = new JSONObject(responseString.toString());
                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    JSONArray choices = root.optJSONArray("choices");
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (choices != null && choices.length() > 0) {
                        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                        JSONObject msg = choices.getJSONObject(0).optJSONObject("message");
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (msg != null) {
                            String textResponse = msg.optString("content");

                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            if (textResponse.contains("<thought>")) {
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                textResponse = textResponse.replaceAll("(?s)<thought>.*?</thought>", "").trim();
                            }
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            if (textResponse.contains("<think>")) {
                                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                                textResponse = textResponse.replaceAll("(?s)<think>.*?</think>", "").trim();
                            }
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            textResponse = textResponse.replaceAll("<thought>", "").replaceAll("</thought>", "");
                            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                            textResponse = textResponse.replaceAll("<think>", "").replaceAll("</think>", "");

                            final String finalResponse = textResponse;
                            mainHandler.post(() -> callback.onSuccess(finalResponse));
                            return;
                        }
                    }
                    mainHandler.post(() -> callback.onError("Không có nội dung phản hồi từ AI"));
                } else {
                    String responseMsg = "";
                    try { responseMsg = conn.getResponseMessage(); } catch (Exception ignored) {}
                    
                    String errorBody = "";
                    try {
                        java.io.InputStream errorStream = conn.getErrorStream();
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (errorStream != null) {
                            BufferedReader brError = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
                            StringBuilder errorStr = new StringBuilder();
                            String errorLine;
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            while ((errorLine = brError.readLine()) != null) errorStr.append(errorLine.trim());
                            brError.close();
                            errorBody = errorStr.toString();
                        }
                    } catch (Exception ignored) {}
                    
                    final String finalMsg = responseMsg + " | " + errorBody;
                    mainHandler.post(() -> callback.onError("Lỗi kết nối từ server: " + responseCode + " - " + finalMsg));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onError("Lỗi xử lý: " + e.getMessage()));
            }
        });
    }
}
