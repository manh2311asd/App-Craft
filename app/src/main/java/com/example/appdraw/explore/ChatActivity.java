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
public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter chatAdapter;
    public static List<GeminiVisionService.ChatMessage> globalChatHistory = new ArrayList<>();
    private EditText etMessage;
    private ImageView ivImagePreview;
    
    private String selectedBase64Image = null;
    private GeminiVisionService geminiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        ivImagePreview = findViewById(R.id.iv_image_preview);
        
        
        loadHistory();
        
        if (globalChatHistory.isEmpty()) {
            globalChatHistory.add(new GeminiVisionService.ChatMessage("model", "Chào bạn, tôi là trợ lý Mỹ thuật AI! Bạn có câu hỏi nào về bức tranh của mình không, cứ thoải mái hỏi tôi nhé.", null));
            saveHistory();
        }

        chatAdapter = new ChatAdapter(globalChatHistory);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);

        geminiService = new GeminiVisionService();
        
        findViewById(R.id.btn_clear_chat).setOnClickListener(v -> {
            globalChatHistory.clear();
            globalChatHistory.add(new GeminiVisionService.ChatMessage("model", "Chào bạn, tôi là trợ lý Mỹ thuật AI! Bạn có câu hỏi nào về bức tranh của mình không, cứ thoải mái hỏi tôi nhé.", null));
            saveHistory();
            chatAdapter.notifyDataSetChanged();
        });

        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btn_attach_image).setOnClickListener(v -> openGallery());
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (message.isEmpty() && selectedBase64Image == null) {
            return;
        }

        // Add user message
        GeminiVisionService.ChatMessage userMsg = new GeminiVisionService.ChatMessage("user", message, selectedBase64Image);
        globalChatHistory.add(userMsg);
        chatAdapter.notifyItemInserted(globalChatHistory.size() - 1);
        rvChat.scrollToPosition(globalChatHistory.size() - 1);
        saveHistory();

        // Reset input
        etMessage.setText("");
        ivImagePreview.setVisibility(View.GONE);
        String imageContextBase64 = selectedBase64Image;
        selectedBase64Image = null; // Clear after sending

        // Add loading placeholder
        GeminiVisionService.ChatMessage loadingMsg = new GeminiVisionService.ChatMessage("model", "Đang suy nghĩ...", null);
        globalChatHistory.add(loadingMsg);
        chatAdapter.notifyItemInserted(globalChatHistory.size() - 1);
        rvChat.scrollToPosition(globalChatHistory.size() - 1);

        List<GeminiVisionService.ChatMessage> apiHistory = new ArrayList<>();
        // Bỏ qua tiếng chào đầu tiên (model) và hành động "Đang suy nghĩ..." cứ cuối mảng (model)
        for (int i = 1; i < globalChatHistory.size() - 1; i++) {
            apiHistory.add(globalChatHistory.get(i));
        }

        // Call Gemini API
        geminiService.chat(apiHistory, new GeminiVisionService.ChatCallback() {
            @Override
            public void onSuccess(String reply) {
                // Replace loading message
                globalChatHistory.set(globalChatHistory.size() - 1, new GeminiVisionService.ChatMessage("model", reply, null));
                chatAdapter.notifyItemChanged(globalChatHistory.size() - 1);
                rvChat.scrollToPosition(globalChatHistory.size() - 1);
                saveHistory();
            }

            @Override
            public void onError(String error) {
                globalChatHistory.set(globalChatHistory.size() - 1, new GeminiVisionService.ChatMessage("model", "Xin lỗi, đã có lỗi kết nối: " + error, null));
                chatAdapter.notifyItemChanged(globalChatHistory.size() - 1);
                saveHistory();
            }
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void saveHistory() {
        SharedPreferences prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE);
        JSONArray arr = new JSONArray();
        for (GeminiVisionService.ChatMessage msg : globalChatHistory) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("role", msg.role);
                obj.put("text", msg.text);
                if (msg.base64Image != null) {
                    obj.put("base64Image", msg.base64Image);
                }
                arr.put(obj);
            } catch (Exception e) {}
        }
        prefs.edit().putString("history", arr.toString()).apply();
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences("chat_prefs", MODE_PRIVATE);
        String historyStr = prefs.getString("history", "[]");
        globalChatHistory.clear();
        try {
            JSONArray arr = new JSONArray(historyStr);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String role = obj.getString("role");
                String text = obj.getString("text");
                String base64Image = obj.has("base64Image") ? obj.getString("base64Image") : null;
                globalChatHistory.add(new GeminiVisionService.ChatMessage(role, text, base64Image));
            }
        } catch (Exception e) {}
    }
}
