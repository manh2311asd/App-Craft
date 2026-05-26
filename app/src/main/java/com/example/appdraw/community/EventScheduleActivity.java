package com.example.appdraw.community;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appdraw.R;
import com.example.appdraw.model.Event;
import com.example.appdraw.model.EventTicket;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Màn hình Xem Lịch trình & Sự kiện (UC-17).
 * Người thực hiện: Đặng Thị Hồng Vân.
 * Hiển thị lịch dạng tháng/ngày, liệt kê các sự kiện nghệ thuật sắp diễn ra.
 */
/**
 * Lớp EventScheduleActivity thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventScheduleActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class EventScheduleActivity extends AppCompatActivity {

    /**
     * Biến `btnAddEvent` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private ImageView btnBack, btnAddEvent;
    /**
     * Biến `tvTabExplore` lưu dữ liệu/trạng thái quan trọng kiểu TextView tvTabSchedule,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvTabSchedule, tvTabExplore;
    /**
     * Biến `containerExplore` lưu dữ liệu/trạng thái quan trọng kiểu View containerSchedule,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private View containerSchedule, containerExplore;

    // Explore Container Views
    /**
     * Biến `tvFilterOffline` lưu dữ liệu/trạng thái quan trọng kiểu TextView tvFilterLatest, tvFilterOnline,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvFilterLatest, tvFilterOnline, tvFilterOffline;
    /**
     * Biến `rvExploreEvents` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvExploreEvents;
    /**
     * Biến `exploreEventAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ExploreEventAdapter exploreEventAdapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Event> allEventsList = new ArrayList<>();
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Event> exploreEventsList = new ArrayList<>();
    /**
     * Biến `currentExploreFilter` giữ đối tượng Firebase hoặc tham chiếu dữ liệu, dùng để đăng nhập, đọc, ghi hoặc đồng bộ dữ liệu với backend.
     */
    private String currentExploreFilter = "Mới nhất";

    // Schedule Container Views
    /**
     * Biến `tvSelectedDateLabel` lưu dữ liệu/trạng thái quan trọng kiểu TextView tvCalendarMonth,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private TextView tvCalendarMonth, tvSelectedDateLabel;
    /**
     * Biến `btnNextWeek` tham chiếu tới thành phần giao diện trong layout XML, dùng để nhận thao tác người dùng hoặc cập nhật nội dung hiển thị.
     */
    private ImageView btnPrevWeek, btnNextWeek;
    /**
     * Biến `rvHorizontalCalendar` lưu dữ liệu/trạng thái quan trọng kiểu RecyclerView, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvHorizontalCalendar;
    /**
     * Biến `rvScheduleEvents` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private RecyclerView rvScheduleEvents;
    /**
     * Biến `calendarAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private HorizontalCalendarAdapter calendarAdapter;
    /**
     * Biến `scheduleEventAdapter` quản lý danh sách hiển thị bằng RecyclerView/Adapter, giúp ánh xạ dữ liệu nguồn lên từng item trên giao diện.
     */
    private ScheduleEventAdapter scheduleEventAdapter;
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Calendar> currentWeekList = new ArrayList<>();
    /**
     * Biến `selectedCalendar` lưu dữ liệu/trạng thái quan trọng kiểu Calendar, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Calendar selectedCalendar;
    
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<EventTicket> myTickets = new ArrayList<>();
    // Dữ liệu dạng List lưu nhiều phần tử cùng loại, thường dùng làm nguồn dữ liệu cho RecyclerView hoặc vòng lặp hiển thị.
    private List<Event> myScheduleList = new ArrayList<>();

    @Override
    /**
     * Hàm onCreate() được gọi khi Activity bắt đầu được tạo.
     * Tại đây lớp thiết lập layout, ánh xạ view, nhận dữ liệu từ Intent và khởi tạo các thành phần xử lý chính.
     * @param savedInstanceState tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);
        
        selectedCalendar = Calendar.getInstance();

        initViews();
        setupTabs();
        setupExploreContainer();
        setupScheduleContainer();
        
        checkMentorRole();
        fetchMyTickets();
        fetchAllEvents();

        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        boolean openExplore = getIntent().getBooleanExtra("OPEN_EXPLORE", false);
        if (openExplore) {
            tvTabExplore.performClick();
        } else {
            tvTabSchedule.performClick();
        }
    }

    /**
     * Hàm initViews() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void initViews() {
        btnBack = findViewById(R.id.btn_back_events);
        btnAddEvent = findViewById(R.id.btn_add_event);
        tvTabSchedule = findViewById(R.id.tv_tab_schedule);
        tvTabExplore = findViewById(R.id.tv_tab_explore);
        containerSchedule = findViewById(R.id.container_schedule);
        containerExplore = findViewById(R.id.container_explore);

        btnBack.setOnClickListener(v -> finish());
        // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
        btnAddEvent.setOnClickListener(v -> startActivity(new Intent(this, CreateEventActivity.class)));
    }

    /**
     * Hàm checkMentorRole() kiểm tra dữ liệu hoặc trạng thái trước khi thực hiện xử lý chính.
     * Việc kiểm tra này giúp tránh lỗi null, dữ liệu rỗng hoặc thao tác không hợp lệ khi chạy ứng dụng.
     */
    private void checkMentorRole() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnSuccessListener(d -> {
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (d.exists() && "mentor".equals(d.getString("role"))) {
                    btnAddEvent.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Hàm setupTabs() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupTabs() {
        tvTabSchedule.setOnClickListener(v -> {
            tvTabSchedule.setBackgroundResource(R.drawable.rounded_bg_white);
            tvTabSchedule.setTextColor(Color.parseColor("#333333"));
            tvTabExplore.setBackgroundResource(0);
            tvTabExplore.setTextColor(Color.parseColor("#757575"));
            
            containerSchedule.setVisibility(View.VISIBLE);
            containerExplore.setVisibility(View.GONE);
        });

        tvTabExplore.setOnClickListener(v -> {
            tvTabExplore.setBackgroundResource(R.drawable.rounded_bg_white);
            tvTabExplore.setTextColor(Color.parseColor("#333333"));
            tvTabSchedule.setBackgroundResource(0);
            tvTabSchedule.setTextColor(Color.parseColor("#757575"));
            
            containerSchedule.setVisibility(View.GONE);
            containerExplore.setVisibility(View.VISIBLE);
        });
    }

    // ================= EXPLORE LOGIC =================
    /**
     * Hàm setupExploreContainer() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupExploreContainer() {
        tvFilterLatest = findViewById(R.id.tv_filter_latest);
        tvFilterOnline = findViewById(R.id.tv_filter_online);
        tvFilterOffline = findViewById(R.id.tv_filter_offline);

        View.OnClickListener filterListener = v -> {
            tvFilterLatest.setBackgroundTintList(ColorStateListHelper.white());
            tvFilterLatest.setTextColor(Color.parseColor("#333333"));
            tvFilterOnline.setBackgroundTintList(ColorStateListHelper.white());
            tvFilterOnline.setTextColor(Color.parseColor("#333333"));
            tvFilterOffline.setBackgroundTintList(ColorStateListHelper.white());
            tvFilterOffline.setTextColor(Color.parseColor("#333333"));

            TextView tv = (TextView) v;
            tv.setBackgroundTintList(ColorStateListHelper.blue());
            tv.setTextColor(Color.WHITE);
            currentExploreFilter = tv.getText().toString();
            filterExploreEvents();
        };

        tvFilterLatest.setOnClickListener(filterListener);
        tvFilterOnline.setOnClickListener(filterListener);
        tvFilterOffline.setOnClickListener(filterListener);

        rvExploreEvents = findViewById(R.id.rv_explore_events);
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvExploreEvents.setLayoutManager(new LinearLayoutManager(this));
        exploreEventAdapter = new ExploreEventAdapter();
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvExploreEvents.setAdapter(exploreEventAdapter);
    }

    /**
     * Hàm fetchAllEvents() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchAllEvents() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        FirebaseFirestore.getInstance().collection("Events")
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                .addSnapshotListener((value, error) -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (error != null || value == null) return;
                    allEventsList.clear();
                    long now = System.currentTimeMillis();
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    for (DocumentSnapshot doc : value) {
                        Event e = doc.toObject(Event.class);
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (e != null) {
                            boolean isExpired = false;
                            try {
                                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                                if (e.getEndTime() != null && e.getEndTime().contains(":")) {
                                    String[] parts = e.getEndTime().split(":");
                                    int hour = Integer.parseInt(parts[0].trim());
                                    int min = Integer.parseInt(parts[1].trim());
                                    java.util.Calendar cal = java.util.Calendar.getInstance();
                                    cal.setTimeInMillis(e.getDateMillis());
                                    cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
                                    cal.set(java.util.Calendar.MINUTE, min);
                                    if (cal.getTimeInMillis() < now) isExpired = true;
                                } else if (e.getDateMillis() + 24 * 60 * 60 * 1000L < now) {
                                    isExpired = true;
                                }
                            } catch (Exception ex) {
                                if (e.getDateMillis() + 24 * 60 * 60 * 1000L < now) isExpired = true;
                            }
                            if (!isExpired) {
                                allEventsList.add(e);
                            }
                        }
                    }
                    filterExploreEvents();
                    updateScheduleViewForSelectedDate(); // Also updates schedule
                });
    }

    /**
     * Hàm filterExploreEvents() thực hiện một phần xử lý trong luồng chức năng của lớp EventScheduleActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void filterExploreEvents() {
        exploreEventsList.clear();
        for (Event e : allEventsList) {
            if ("Mới nhất".equals(currentExploreFilter)) {
                exploreEventsList.add(e);
            } else if ("Online".equals(currentExploreFilter) && e.isOnline()) {
                exploreEventsList.add(e);
            } else if ("Offline".equals(currentExploreFilter) && !e.isOnline()) {
                exploreEventsList.add(e);
            }
        }
        exploreEventAdapter.notifyDataSetChanged();
    }

    // ================= SCHEDULE LOGIC =================
    /**
     * Hàm setupScheduleContainer() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void setupScheduleContainer() {
        tvCalendarMonth = findViewById(R.id.tv_calendar_month);
        tvSelectedDateLabel = findViewById(R.id.tv_selected_date_label);
        btnPrevWeek = findViewById(R.id.btn_prev_week);
        btnNextWeek = findViewById(R.id.btn_next_week);
        rvHorizontalCalendar = findViewById(R.id.rv_horizontal_calendar);
        rvScheduleEvents = findViewById(R.id.rv_schedule_events);

        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvHorizontalCalendar.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 7));
        calendarAdapter = new HorizontalCalendarAdapter();
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvHorizontalCalendar.setAdapter(calendarAdapter);

        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvScheduleEvents.setLayoutManager(new LinearLayoutManager(this));
        scheduleEventAdapter = new ScheduleEventAdapter();
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        rvScheduleEvents.setAdapter(scheduleEventAdapter);

        calculateWeekOffset(0);

        btnPrevWeek.setOnClickListener(v -> calculateWeekOffset(-1));
        btnNextWeek.setOnClickListener(v -> calculateWeekOffset(1));
    }
    
    /**
     * Hàm fetchMyTickets() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    private void fetchMyTickets() {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid != null) {
            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            FirebaseFirestore.getInstance().collection("EventRegistrations")
                    .whereEqualTo("userId", uid)
                    // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                    .addSnapshotListener((value, error) -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (error != null || value == null) return;
                        myTickets.clear();
                        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                        for (DocumentSnapshot doc : value) {
                            EventTicket t = doc.toObject(EventTicket.class);
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (t != null) myTickets.add(t);
                        }
                        updateScheduleViewForSelectedDate();
                    });
        }
    }

    /**
     * Hàm calculateWeekOffset() thực hiện một phần xử lý trong luồng chức năng của lớp EventScheduleActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param offsetFactor tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void calculateWeekOffset(int offsetFactor) {
        if (offsetFactor != 0) {
            selectedCalendar.add(Calendar.DAY_OF_YEAR, offsetFactor * 28);
        }
        
        tvCalendarMonth.setText("Tháng " + (selectedCalendar.get(Calendar.MONTH) + 1) + ", " + selectedCalendar.get(Calendar.YEAR));
        
        Calendar weekStart = (Calendar) selectedCalendar.clone();
        int dow = weekStart.get(Calendar.DAY_OF_WEEK);
        if (dow == Calendar.SUNDAY) {
            weekStart.add(Calendar.DAY_OF_YEAR, -6);
        } else {
            weekStart.add(Calendar.DAY_OF_YEAR, - (dow - Calendar.MONDAY));
        }

        currentWeekList.clear();
        for (int i = 0; i < 28; i++) {
            Calendar day = (Calendar) weekStart.clone();
            day.add(Calendar.DAY_OF_YEAR, i);
            currentWeekList.add(day);
        }
        
        calendarAdapter.notifyDataSetChanged();
        updateScheduleViewForSelectedDate();
    }

    /**
     * Hàm updateScheduleViewForSelectedDate() thực hiện một phần xử lý trong luồng chức năng của lớp EventScheduleActivity.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateScheduleViewForSelectedDate() {
        Calendar today = Calendar.getInstance();
        boolean isToday = today.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                          today.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR);
        
        String prefix = isToday ? "Hôm nay, " : "Ngày ";
        tvSelectedDateLabel.setText(prefix + selectedCalendar.get(Calendar.DAY_OF_MONTH) + " tháng " + (selectedCalendar.get(Calendar.MONTH) + 1));
        
        myScheduleList.clear();
        
        // Find events that match tickets and this day
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        
        for (Event e : allEventsList) {
            boolean hasTicket = false;
            for (EventTicket t : myTickets) {
                if (t.getEventId().equals(e.getId())) {
                    hasTicket = true;
                    break;
                }
            }
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            boolean isAuthor = uid != null && uid.equals(e.getAuthorId());
            
            if (!hasTicket && !isAuthor && !("Live".equals(e.getEventType()))) {
                continue; // Include Live by default, authored events, and ticketed events
            }
            
            // Check if day matches
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTimeInMillis(e.getDateMillis());
            if (eventCal.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                eventCal.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)) {
                myScheduleList.add(e);
            }
        }
        scheduleEventAdapter.notifyDataSetChanged();
    }

    // ================= HELPER & DIALOG =================
    /**
     * Hàm registerEvent() ghi, gửi hoặc tạo dữ liệu mới dựa trên thao tác của người dùng.
     * Các bước thường gồm kiểm tra dữ liệu đầu vào, chuẩn bị Object/Map và lưu lên Firebase hoặc API liên quan.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void registerEvent(Event event) {
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        String uid = FirebaseAuth.getInstance().getUid();
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (uid == null) return;
        
        // Check if already registered
        for (EventTicket t : myTickets) {
            if (t.getEventId().equals(event.getId())) {
                Toast.makeText(this, "Bạn đã đăng ký sự kiện này rồi", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        String ticketId = FirebaseFirestore.getInstance().collection("EventRegistrations").document().getId();
        String ticketCode = "TKT" + String.format("%04d", (int)(Math.random() * 10000));
        EventTicket ticket = new EventTicket(ticketId, event.getId(), uid, ticketCode, System.currentTimeMillis());
        
        // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
        // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
        FirebaseFirestore.getInstance().collection("EventRegistrations").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    myTickets.add(ticket);
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (exploreEventAdapter != null) exploreEventAdapter.notifyDataSetChanged();
                    showSuccessDialog(event, ticket);
                    
                    // Gửi thông báo cho tác giả (Author)
                    if (!event.getAuthorId().equals(uid)) {
                        com.example.appdraw.utils.NotificationHelper.sendNotification(event.getAuthorId(), "EVENT", "Một người dùng vừa đăng ký sự kiện: " + event.getTitle(), event.getId());
                    }
                    // Thông báo hệ thống cho người đăng ký
                    com.example.appdraw.utils.NotificationHelper.sendNotification(uid, "EVENT", "Bạn đã đăng ký thành công sự kiện: " + event.getTitle(), event.getId());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi đăng ký", Toast.LENGTH_SHORT).show());
    }

    /**
     * Hàm showSuccessDialog() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param ticket tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void showSuccessDialog(Event event, EventTicket ticket) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_event_registered);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        
        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_event_title);
        TextView tvTime = dialog.findViewById(R.id.tv_dialog_event_time);
        TextView tvLocation = dialog.findViewById(R.id.tv_dialog_event_location);
        TextView tvFormat = dialog.findViewById(R.id.tv_dialog_event_format);
        TextView tvPrice = dialog.findViewById(R.id.tv_dialog_event_price);
        View btnViewTicket = dialog.findViewById(R.id.btn_dialog_view_ticket);
        View btnBackSchedule = dialog.findViewById(R.id.btn_dialog_back_schedule);

        tvTitle.setText(event.getTitle());
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(event.getDateMillis());
        tvTime.setText(event.getStartTime() + " - " + event.getEndTime() + " - " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1));
        
        tvLocation.setText(event.getLocation());
        tvFormat.setText(event.isOnline() ? "Online" : "Offline");
        tvPrice.setText(event.getPrice());

        btnViewTicket.setOnClickListener(v -> {
            dialog.dismiss();
            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
            Intent intent = new Intent(this, EventTicketActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            intent.putExtra("TICKET_ID", ticket.getId());
            startActivity(intent);
        });
        
        btnBackSchedule.setOnClickListener(v -> {
            dialog.dismiss();
            // Switch to schedule tab
            tvTabSchedule.performClick();
        });

        dialog.show();
    }

    // ================= ADAPTERS =================
    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class HorizontalCalendarAdapter extends RecyclerView.Adapter<HorizontalCalendarAdapter.VH> {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        @NonNull
        @Override
        /**
         * Hàm onCreateViewHolder() tạo ViewHolder cho RecyclerView bằng cách inflate layout item.
         * ViewHolder giúp tái sử dụng view, giảm chi phí tạo giao diện khi danh sách cuộn nhiều phần tử.
         * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param viewType tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false));
        }
        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Calendar day = currentWeekList.get(position);
            
            holder.tvDate.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));
            switch (day.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY: holder.tvDow.setText("MON"); break;
                case Calendar.TUESDAY: holder.tvDow.setText("TUE"); break;
                case Calendar.WEDNESDAY: holder.tvDow.setText("WED"); break;
                case Calendar.THURSDAY: holder.tvDow.setText("THU"); break;
                case Calendar.FRIDAY: holder.tvDow.setText("FRI"); break;
                case Calendar.SATURDAY: holder.tvDow.setText("SAT"); break;
                case Calendar.SUNDAY: holder.tvDow.setText("SUN"); break;
            }
            
            boolean isSelected = day.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR) &&
                                 day.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR);
                                 
            if (isSelected) {
                holder.tvDate.setBackgroundResource(R.drawable.rounded_bg_red); 
                holder.tvDate.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935")));
                holder.tvDate.setTextColor(Color.WHITE);
                holder.tvDow.setTextColor(Color.parseColor("#E53935"));
            } else {
                holder.tvDate.setBackgroundResource(0);
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                holder.tvDate.setBackgroundTintList(null);
                holder.tvDate.setTextColor(Color.parseColor("#333333"));
                holder.tvDow.setTextColor(Color.parseColor("#AAAAAA"));
            }

            if (day.get(Calendar.MONTH) != selectedCalendar.get(Calendar.MONTH)) {
                holder.tvDate.setAlpha(0.3f);
                holder.tvDow.setAlpha(0.3f);
            } else {
                holder.tvDate.setAlpha(1.0f);
                holder.tvDow.setAlpha(1.0f);
            }
            
            holder.itemView.setOnClickListener(v -> {
                selectedCalendar = (Calendar) day.clone();
                notifyDataSetChanged();
                updateScheduleViewForSelectedDate();
            });
        }
        @Override
        public int getItemCount() { return currentWeekList.size(); }
/**
 * Lớp VH thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventScheduleActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class VH extends RecyclerView.ViewHolder {
            TextView tvDow, tvDate;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            VH(@NonNull View itemView) {
                super(itemView);
                tvDow = itemView.findViewById(R.id.tv_day_of_week);
                tvDate = itemView.findViewById(R.id.tv_date_number);
            }
        }
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class ScheduleEventAdapter extends RecyclerView.Adapter<ScheduleEventAdapter.VH> {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        @NonNull
        @Override
        /**
         * Hàm onCreateViewHolder() tạo ViewHolder cho RecyclerView bằng cách inflate layout item.
         * ViewHolder giúp tái sử dụng view, giảm chi phí tạo giao diện khi danh sách cuộn nhiều phần tử.
         * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param viewType tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_schedule, parent, false));
        }
        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Event e = myScheduleList.get(position);
            holder.tvTitle.setText(e.getTitle());
            String endTimeStr = e.getEndTime();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (endTimeStr != null && !endTimeStr.isEmpty()) {
                holder.tvTime.setText(e.getStartTime() + " - " + endTimeStr);
            } else {
                holder.tvTime.setText(e.getStartTime());
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
            FirebaseFirestore.getInstance().collection("Users").document(e.getAuthorId())
                .get().addOnSuccessListener(doc -> {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (doc.exists() && holder.tvSubtitle != null) {
                        // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                        java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                        String fullName = "Người ẩn danh";
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (profile != null && profile.containsKey("fullName")) {
                            fullName = (String) profile.get("fullName");
                        }
                        holder.tvSubtitle.setText(fullName + " - " + (e.isOnline() ? "Online" : "Offline"));
                    }
                });
            
            if ("Live".equals(e.getEventType())) {
                holder.tvBadge.setText("Live");
                holder.tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935")));
                holder.btnAction.setText("Tham gia ngay");
                holder.btnAction.setOnClickListener(v -> {
                    Toast.makeText(EventScheduleActivity.this, "Đang vào phòng Live...", Toast.LENGTH_SHORT).show();
                });
            } else {
                holder.tvBadge.setText("Workshop");
                holder.tvBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#F57C00")));
                
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                String uid = FirebaseAuth.getInstance().getUid();
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                boolean isAuthor = uid != null && uid.equals(e.getAuthorId());
                
                if (isAuthor) {
                    holder.btnAction.setText("Của bạn");
                    holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
                    holder.btnAction.setOnClickListener(v -> Toast.makeText(EventScheduleActivity.this, "Bạn là nhà tổ chức (Đang phát triển)", Toast.LENGTH_SHORT).show());
                } else {
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    String myTicketId = null;
                    for (EventTicket t : myTickets) {
                        if (t.getEventId().equals(e.getId())) myTicketId = t.getId();
                    }
                    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                    if (myTicketId != null) {
                        holder.btnAction.setText("Xem vé");
                        String finalMyTicketId = myTicketId;
                        holder.btnAction.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(EventScheduleActivity.this, EventTicketActivity.class);
                            intent.putExtra("EVENT_ID", e.getId());
                            intent.putExtra("TICKET_ID", finalMyTicketId);
                            startActivity(intent);
                        });
                    } else {
                        holder.btnAction.setText("Đăng ký");
                        holder.btnAction.setOnClickListener(v -> {
                            // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                            Intent intent = new Intent(EventScheduleActivity.this, EventTicketActivity.class);
                            intent.putExtra("EVENT_ID", e.getId());
                            startActivity(intent);
                        });
                    }
                }
            }

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (e.getCoverImageBase64() != null && e.getCoverImageBase64().startsWith("data:image")) {
                byte[] b = Base64.decode(e.getCoverImageBase64().split(",")[1], Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(EventScheduleActivity.this).load(b).centerCrop().into(holder.ivCover);
            }
        }
        @Override
        public int getItemCount() { return myScheduleList.size(); }
/**
 * Lớp VH thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventScheduleActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle, tvTime, tvBadge, btnAction;
            ShapeableImageView ivCover;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            VH(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_event_title);
                tvSubtitle = itemView.findViewById(R.id.tv_event_subtitle);
                tvTime = itemView.findViewById(R.id.tv_event_time);
                tvBadge = itemView.findViewById(R.id.tv_event_badge);
                btnAction = itemView.findViewById(R.id.btn_event_action);
                ivCover = itemView.findViewById(R.id.iv_event_cover);
            }
        }
    }

    // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
    private class ExploreEventAdapter extends RecyclerView.Adapter<ExploreEventAdapter.VH> {
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        @NonNull
        @Override
        /**
         * Hàm onCreateViewHolder() tạo ViewHolder cho RecyclerView bằng cách inflate layout item.
         * ViewHolder giúp tái sử dụng view, giảm chi phí tạo giao diện khi danh sách cuộn nhiều phần tử.
         * @param parent tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param viewType tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_explore, parent, false));
        }
        @Override
        /**
         * Hàm onBindViewHolder() gắn dữ liệu tại một vị trí cụ thể vào item của RecyclerView.
         * Dữ liệu thường lấy từ List/Map/Object đã tải từ Firebase hoặc được truyền từ màn hình trước.
         * @param holder tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         * @param position tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
         */
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Event e = exploreEventsList.get(position);
            holder.tvTitle.setText(e.getTitle());
            holder.tvFormat.setText(e.isOnline() ? "Online" : "Offline");
            holder.tvPrice.setText(e.getPrice());
            
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (holder.tvSubtitle != null) {
                // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
                // Dữ liệu snapshot/document là kết quả trả về từ Firebase, cần đọc đúng trường và kiểm tra null trước khi hiển thị.
                FirebaseFirestore.getInstance().collection("Users").document(e.getAuthorId())
                    .get().addOnSuccessListener(doc -> {
                        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                        if (doc.exists()) {
                            // Dữ liệu dạng Map được dùng để gom các trường thông tin theo khóa - giá trị trước khi lưu Firebase hoặc truyền qua API.
                            java.util.Map<String, Object> profile = (java.util.Map<String, Object>) doc.get("profile");
                            String fullName = "Người ẩn danh";
                            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                            if (profile != null && profile.containsKey("fullName")) {
                                fullName = (String) profile.get("fullName");
                            }
                            holder.tvSubtitle.setText(fullName + " - " + (e.isOnline() ? "Online" : "Offline"));
                        }
                    });
            }
            
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(e.getDateMillis());
            holder.tvTime.setText(e.getStartTime() + " - " + e.getEndTime() + " - " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1));

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (e.getCoverImageBase64() != null && e.getCoverImageBase64().startsWith("data:image")) {
                byte[] b = Base64.decode(e.getCoverImageBase64().split(",")[1], Base64.DEFAULT);
                // Glide được dùng để tải ảnh từ URL/Uri lên ImageView, giúp ảnh hiển thị bất đồng bộ và tối ưu bộ nhớ.
                Glide.with(EventScheduleActivity.this).load(b).centerCrop().into(holder.ivCover);
            }

            // Khối xử lý Firebase dùng để đọc/ghi dữ liệu, xác thực người dùng hoặc lưu trữ tài nguyên trên backend của ứng dụng.
            String uid = FirebaseAuth.getInstance().getUid();
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            boolean isAuthor = uid != null && uid.equals(e.getAuthorId());
            boolean isRegistered = false;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            String myTicketId = null;
            for (EventTicket t : myTickets) {
                if (t.getEventId().equals(e.getId())) {
                    isRegistered = true;
                    myTicketId = t.getId();
                    break;
                }
            }

            if (isAuthor) {
                holder.btnAction.setText("Của bạn");
                holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#888888")));
                holder.btnAction.setOnClickListener(v -> {
                    Toast.makeText(EventScheduleActivity.this, "Bạn là người tạo sự kiện này", Toast.LENGTH_SHORT).show();
                });
            } else if (isRegistered) {
                holder.btnAction.setText("Xem vé");
                holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                String finalTicketId = myTicketId;
                holder.btnAction.setOnClickListener(v -> {
                    // Intent được dùng để điều hướng sang màn hình khác hoặc truyền dữ liệu cần thiết giữa các Activity.
                    Intent intent = new Intent(EventScheduleActivity.this, EventTicketActivity.class);
                    intent.putExtra("EVENT_ID", e.getId());
                    intent.putExtra("TICKET_ID", finalTicketId);
                    startActivity(intent);
                });
            } else {
                holder.btnAction.setText("Đăng ký");
                holder.btnAction.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4272D0")));
                holder.btnAction.setOnClickListener(v -> registerEvent(e));
            }
        }
        @Override
        public int getItemCount() { return exploreEventsList.size(); }
/**
 * Lớp VH thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file EventScheduleActivity.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
        // RecyclerView/Adapter/LayoutManager được cấu hình để hiển thị danh sách dữ liệu theo dạng cuộn trên giao diện.
        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvFormat, tvPrice, tvTime, btnAction, tvSubtitle;
            ShapeableImageView ivCover;
            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            VH(@NonNull View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_event_title);
                tvSubtitle = v.findViewById(R.id.tv_event_subtitle);
                tvFormat = v.findViewById(R.id.tv_event_format);
                tvPrice = v.findViewById(R.id.tv_event_price);
                tvTime = v.findViewById(R.id.tv_event_time);
                btnAction = v.findViewById(R.id.btn_event_action);
                ivCover = v.findViewById(R.id.iv_event_cover);
            }
        }
    }

    // Workaround helper
    static class ColorStateListHelper {
        static android.content.res.ColorStateList white() { return android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF")); }
        static android.content.res.ColorStateList blue() { return android.content.res.ColorStateList.valueOf(Color.parseColor("#4272D0")); }
    }
}
