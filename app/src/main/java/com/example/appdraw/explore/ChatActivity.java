package com.example.appdraw.explore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdraw.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Lê Thùy Linh
 * @version 1.0
 */
/**
 * Lớp ChatActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ChatActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ChatActivity extends AppCompatActivity {

    /**
     * Biến `rvChat` lưu dữ liệu/trạng thái quan trọng kiểu RecyclerView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvChat;
    /**
     * Biến `chatAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ChatAdapter chatAdapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    public static List<GeminiVisionService.ChatMessage> globalChatHistory = new ArrayList<>();
    /**
     * Biến `etMessage` lưu dữ liệu/trạng thái quan trọng kiểu EditText, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private EditText etMessage;
    /**
     * Biến `ivImagePreview` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    private ImageView ivImagePreview;
    
    /**
     * Biến `selectedBase64Image` lưu đường dẫn, Uri hoặc dữ liệu hình ảnh để hiển thị ảnh trên giao diện hoặc gửi ảnh lên hệ thống.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private String selectedBase64Image = null;
    /**
     * Biến `geminiService` lưu thông tin phục vụ gọi API bên ngoài hoặc cấu hình model, dùng khi ứng dụng cần phân tích/tạo nội dung từ dịch vụ ngoài.
     */
    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
    private GeminiVisionService geminiService;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        ivImagePreview = findViewById(R.id.iv_image_preview);
        
        
        loadHistory();
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (globalChatHistory.isEmpty()) {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            globalChatHistory.add(new GeminiVisionService.ChatMessage("model", "Chào bạn, tôi là trợ lý Mỹ thuật AI! Bạn có câu hỏi nào về bức tranh của mình không, cứ thoải mái hỏi tôi nhé.", null));
            saveHistory();
        }

        chatAdapter = new ChatAdapter(globalChatHistory);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvChat.setAdapter(chatAdapter);

        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        geminiService = new GeminiVisionService();
        
        findViewById(R.id.btn_clear_chat).setOnClickListener(v -> {
            globalChatHistory.clear();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            globalChatHistory.add(new GeminiVisionService.ChatMessage("model", "Chào bạn, tôi là trợ lý Mỹ thuật AI! Bạn có câu hỏi nào về bức tranh của mình không, cứ thoải mái hỏi tôi nhé.", null));
            saveHistory();
            chatAdapter.notifyDataSetChanged();
        });

        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_attach_image).setOnClickListener(v -> openGallery());
    }

    /**
     * Hàm sendMessage() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (message.isEmpty() && selectedBase64Image == null) {
            return;
        }

        // Add user message
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        GeminiVisionService.ChatMessage userMsg = new GeminiVisionService.ChatMessage("user", message, selectedBase64Image);
        globalChatHistory.add(userMsg);
        chatAdapter.notifyItemInserted(globalChatHistory.size() - 1);
        rvChat.scrollToPosition(globalChatHistory.size() - 1);
        saveHistory();

        // Reset input
        etMessage.setText("");
        ivImagePreview.setVisibility(View.GONE);
        String imageContextBase64 = selectedBase64Image;
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        selectedBase64Image = null; // Clear after sending

        // Add loading placeholder
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        GeminiVisionService.ChatMessage loadingMsg = new GeminiVisionService.ChatMessage("model", "Đang suy nghĩ...", null);
        globalChatHistory.add(loadingMsg);
        chatAdapter.notifyItemInserted(globalChatHistory.size() - 1);
        rvChat.scrollToPosition(globalChatHistory.size() - 1);

        // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        List<GeminiVisionService.ChatMessage> apiHistory = new ArrayList<>();
        // Bỏ qua tiếng chào đầu tiên (model) và hành động "Đang suy nghĩ..." cứ cuối mảng (model)
        for (int i = 1; i < globalChatHistory.size() - 1; i++) {
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            apiHistory.add(globalChatHistory.get(i));
        }

        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        // Call Gemini API
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        geminiService.chat(apiHistory, new GeminiVisionService.ChatCallback() {
            @Override
            /**
             * Hàm onSuccess() thực hiện một phần xử lý trong luồng chức năng của lớp ChatActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param reply tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public void onSuccess(String reply) {
                // Replace loading message
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                globalChatHistory.set(globalChatHistory.size() - 1, new GeminiVisionService.ChatMessage("model", reply, null));
                chatAdapter.notifyItemChanged(globalChatHistory.size() - 1);
                rvChat.scrollToPosition(globalChatHistory.size() - 1);
                saveHistory();
            }

            @Override
            /**
             * Hàm onError() thực hiện một phần xử lý trong luồng chức năng của lớp ChatActivity.
             * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
             * @param error tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
             */
            public void onError(String error) {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                globalChatHistory.set(globalChatHistory.size() - 1, new GeminiVisionService.ChatMessage("model", "Xin lỗi, đã có lỗi kết nối: " + error, null));
                chatAdapter.notifyItemChanged(globalChatHistory.size() - 1);
                saveHistory();
            }
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        
                        // Compress to base64
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] b = baos.toByteArray();
                        selectedBase64Image = Base64.encodeToString(b, Base64.DEFAULT);

                        ivImagePreview.setImageURI(selectedImageUri);
                        ivImagePreview.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /**
     * Hàm openGallery() thực hiện một phần xử lý trong luồng chức năng của lớp ChatActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void openGallery() {
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    /**
     * Hàm saveHistory() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     */
    private void saveHistory() {
        SharedPreferences prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE);
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        JSONArray arr = new JSONArray();
        // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
        for (GeminiVisionService.ChatMessage msg : globalChatHistory) {
            try {
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject obj = new JSONObject();
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                obj.put("role", msg.role);
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                obj.put("text", msg.text);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (msg.base64Image != null) {
                    // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                    obj.put("base64Image", msg.base64Image);
                }
                // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                arr.put(obj);
            } catch (Exception e) {}
        }
        prefs.edit().putString("history", arr.toString()).apply();
    }

    /**
     * Hàm loadHistory() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE);
        String historyStr = prefs.getString("history", "[]");
        globalChatHistory.clear();
        try {
            // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
            JSONArray arr = new JSONArray(historyStr);
            for (int i = 0; i < arr.length(); i++) {
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                JSONObject obj = arr.getJSONObject(i);
                String role = obj.getString("role");
                String text = obj.getString("text");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                String base64Image = obj.has("base64Image") ? obj.getString("base64Image") : null;
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                globalChatHistory.add(new GeminiVisionService.ChatMessage(role, text, base64Image));
            }
        } catch (Exception e) {}
    }
}
