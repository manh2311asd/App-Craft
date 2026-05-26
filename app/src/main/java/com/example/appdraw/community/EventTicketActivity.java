package com.example.appdraw.community;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdraw.R;
import com.example.appdraw.model.Event;
import com.example.appdraw.model.EventTicket;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Cao Đức Mạnh
 * @version 1.0
 */
/**
 * Lớp EventTicketActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventTicketActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class EventTicketActivity extends AppCompatActivity {

    /**
     * Biến `ivQrCode` lưu dữ liệu/trạng thái quan trọng kiểu ImageView btnBack, btnShare,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ImageView btnBack, btnShare, ivQrCode;
    /**
     * Biến `tvTicketFooterWarning` lưu dữ liệu/trạng thái quan trọng kiểu TextView tvTitle, tvFormat, tvTime, tvLocation, tvTicketCode,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvTitle, tvFormat, tvTime, tvLocation, tvTicketCode, tvTicketFooterWarning;
    /**
     * Biến `btnJoinZoom` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private View btnViewMap, btnRemindMe, btnJoinZoom;
    /**
     * Biến `ivRemindIcon` lưu dữ liệu/trạng thái quan trọng kiểu ImageView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private ImageView ivRemindIcon;
    /**
     * Biến `tvRemindText` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private TextView tvRemindText;

    /**
     * Biến `eventId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String eventId;
    /**
     * Biến `ticketId` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private String ticketId;
    /**
     * Biến `currentEvent` lưu dữ liệu/trạng thái quan trọng kiểu Event, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Event currentEvent;
    
    /**
     * Biến `prefs` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    private SharedPreferences prefs;
    /**
     * Biến `isReminded` lưu dữ liệu/trạng thái quan trọng kiểu boolean, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private boolean isReminded = false;

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_ticket);

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        eventId = getIntent().getStringExtra("EVENT_ID");
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        ticketId = getIntent().getStringExtra("TICKET_ID");
        prefs = getSharedPreferences("EventReminders", Context.MODE_PRIVATE);
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (eventId != null) {
            isReminded = prefs.getBoolean("remind_" + eventId, false);
        }

        initViews();
        setupListeners();
        updateRemindUI();
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (eventId != null) {
            loadTicketData();
        }
    }

    /**
     * Hàm initViews() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back_ticket);
        btnShare = findViewById(R.id.btn_share_ticket);
        tvTitle = findViewById(R.id.tv_ticket_event_title);
        tvFormat = findViewById(R.id.tv_ticket_format);
        tvTime = findViewById(R.id.tv_ticket_time);
        tvLocation = findViewById(R.id.tv_ticket_location);
        tvTicketCode = findViewById(R.id.tv_ticket_code);
        ivQrCode = findViewById(R.id.iv_qr_code);
        tvTicketFooterWarning = findViewById(R.id.tv_ticket_footer_warning);
        btnViewMap = findViewById(R.id.btn_view_map);
        btnJoinZoom = findViewById(R.id.btn_join_zoom);
        btnRemindMe = findViewById(R.id.btn_remind_me);
        ivRemindIcon = findViewById(R.id.iv_remind_icon);
        tvRemindText = findViewById(R.id.tv_remind_text);
    }

    /**
     * Hàm setupListeners() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnShare.setOnClickListener(v -> {
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Tôi đã đăng ký sự kiện thành công trên App Draw!");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ vé"));
        });

        btnViewMap.setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (currentEvent != null && currentEvent.getLocation() != null) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(currentEvent.getLocation()));
                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                mapIntent.setPackage("com.google.android.apps.maps");
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    startActivity(mapIntent);
                } else {
                    // Khối gọi API bên ngoài dùng để gửi yêu cầu, nhận phản hồi JSON và chuyển dữ liệu trả về thành nội dung hiển thị/đánh giá.
                    String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(currentEvent.getLocation());
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });

        btnRemindMe.setOnClickListener(v -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (currentEvent != null) {
                isReminded = !isReminded;
                prefs.edit().putBoolean("remind_" + eventId, isReminded).apply();
                
                if (isReminded) {
                    // Mở dialog thông báo
                    showRemindSuccessDialog();

                    // Optional: Push to Calendar Intent as well
                    long beginTimeMillis = currentEvent.getDateMillis();
                    long endTimeMillis = beginTimeMillis + 3600000; // default +1h
                    try {
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTimeInMillis(beginTimeMillis);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (currentEvent.getStartTime() != null && currentEvent.getStartTime().contains(":")) {
                            String[] parts = currentEvent.getStartTime().split(":");
                            cal.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
                            cal.set(java.util.Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
                            beginTimeMillis = cal.getTimeInMillis();
                        }
                        
                        java.util.Calendar calEnd = java.util.Calendar.getInstance();
                        calEnd.setTimeInMillis(currentEvent.getDateMillis());
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (currentEvent.getEndTime() != null && currentEvent.getEndTime().contains(":")) {
                            String[] parts = currentEvent.getEndTime().split(":");
                            calEnd.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0].trim()));
                            calEnd.set(java.util.Calendar.MINUTE, Integer.parseInt(parts[1].trim()));
                            endTimeMillis = calEnd.getTimeInMillis();
                        } else {
                            endTimeMillis = beginTimeMillis + 3600000;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.Events.TITLE, currentEvent.getTitle())
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, currentEvent.getLocation())
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMillis)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMillis)
                            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    updateRemindUI();
                }
            }
        });
    }

    /**
     * Hàm showRemindSuccessDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void showRemindSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_remind_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        View btnOk = dialog.findViewById(R.id.btn_dialog_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            updateRemindUI();
        });
        
        dialog.show();
    }

    /**
     * Hàm updateRemindUI() thực hiện một phần xử lý trong luồng chức năng của lớp EventTicketActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateRemindUI() {
        if (isReminded) {
            btnRemindMe.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935"))); // Nền đỏ
            tvRemindText.setText("Tắt nhắc");
            tvRemindText.setTextColor(Color.WHITE);
            // Replace with a crossed bell icon if available, or just tint it white
            ivRemindIcon.setImageTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
        } else {
            btnRemindMe.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));
            tvRemindText.setText("Nhắc tôi");
            tvRemindText.setTextColor(Color.parseColor("#333333"));
            ivRemindIcon.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#333333")));
        }
    }

    /**
     * Hàm loadTicketData() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void loadTicketData() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        db.collection("Events").document(eventId).get().addOnSuccessListener(doc -> {
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (doc.exists()) {
                currentEvent = doc.toObject(Event.class);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (currentEvent != null) {
                    tvTitle.setText(currentEvent.getTitle());
                    tvFormat.setText(currentEvent.isOnline() ? "Online" : "Offline");
                    
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTimeInMillis(currentEvent.getDateMillis());
                    int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
                    int month = cal.get(java.util.Calendar.MONTH) + 1;
                    int dow = cal.get(java.util.Calendar.DAY_OF_WEEK);
                    String dayOfWeekStr = getDayOfWeek(dow);
                    
                    tvTime.setText(currentEvent.getStartTime() + " - " + currentEvent.getEndTime() + " - " + dayOfWeekStr + ", " + day + " Th" + month);
                    if (currentEvent.isOnline()) {
                        tvLocation.setText("Phòng học trực tiếp qua nền tảng Zoom / Meet");
                        tvFormat.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E8F5E9")));
                        tvFormat.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                        
                        btnViewMap.setVisibility(View.GONE);
                        btnJoinZoom.setVisibility(View.VISIBLE);
                        
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (ivQrCode != null) ivQrCode.setVisibility(View.GONE);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTicketCode != null) tvTicketCode.setVisibility(View.GONE);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (tvTicketFooterWarning != null) tvTicketFooterWarning.setVisibility(View.GONE);
                        
                        btnJoinZoom.setOnClickListener(v -> {
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (currentEvent.getZoomLink() != null && !currentEvent.getZoomLink().isEmpty()) {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (currentEvent.getZoomPasscode() != null && !currentEvent.getZoomPasscode().isEmpty()) {
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Passcode", currentEvent.getZoomPasscode());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(EventTicketActivity.this, "Đã sao chép Passcode: " + currentEvent.getZoomPasscode(), Toast.LENGTH_LONG).show();
                                }
                                
                                // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEvent.getZoomLink()));
                                startActivity(intent);
                            } else {
                                Toast.makeText(EventTicketActivity.this, "Chưa có đường dẫn cho sự kiện này", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    } else {
                        tvLocation.setText(currentEvent.getLocation());
                        tvFormat.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFEBEE")));
                        tvFormat.setTextColor(android.graphics.Color.parseColor("#E53935"));
                        
                        btnJoinZoom.setVisibility(View.GONE);
                        btnViewMap.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (ticketId != null && !ticketId.isEmpty()) {
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            db.collection("EventRegistrations").document(ticketId).get().addOnSuccessListener(doc -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (doc.exists()) {
                    EventTicket ticket = doc.toObject(EventTicket.class);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (ticket != null) {
                        tvTicketCode.setText("Mã vé : " + ticket.getTicketCode());
                    }
                }
            });
        }
    }
    
    /**
     * Hàm getDayOfWeek() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     * @param dow tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private String getDayOfWeek(int dow) {
        switch (dow) {
            case java.util.Calendar.MONDAY: return "Thứ 2";
            case java.util.Calendar.TUESDAY: return "Thứ 3";
            case java.util.Calendar.WEDNESDAY: return "Thứ 4";
            case java.util.Calendar.THURSDAY: return "Thứ 5";
            case java.util.Calendar.FRIDAY: return "Thứ 6";
            case java.util.Calendar.SATURDAY: return "Thứ 7";
            case java.util.Calendar.SUNDAY: return "CN";
            default: return "";
        }
    }
}
