package com.example.appdraw.drawing;

import com.example.appdraw.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp ColorPickerView thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ColorPickerView.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ColorPickerView extends View {
    /**
     * Biến `arcPaint` lưu dữ liệu/trạng thái quan trọng kiểu Paint, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private Paint arcPaint;
    /**
     * Biến `gridPaint` lưu mã định danh hoặc thông tin người dùng, giúp truy vấn đúng bản ghi trong Firebase và truyền dữ liệu giữa các màn hình.
     */
    private Paint gridPaint;
    /**
     * Biến `colors` lưu dữ liệu/trạng thái quan trọng kiểu int[], được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int[] colors;
    /**
     * Biến `listener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private OnColorSelectedListener listener;
    /**
     * Biến `ringThickness` lưu dữ liệu/trạng thái quan trọng kiểu float cx, cy, radius, innerRadius,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float cx, cy, radius, innerRadius, ringThickness;
    /**
     * Biến `currentColor` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int currentColor = Color.RED;
    /**
     * Biến `selY` lưu dữ liệu/trạng thái quan trọng kiểu float selX,, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private float selX, selY;

/**
 * Interface OnColorSelectedListener thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: ColorPickerView.java.
 */
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    /**
     * Hàm ColorPickerView() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    /**
     * Hàm ColorPickerView() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param attrs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Hàm init() chuẩn bị hoặc cập nhật giao diện hiển thị cho người dùng.
     * Hàm thường ánh xạ dữ liệu từ Object/List/Map vào TextView, ImageView, RecyclerView hoặc các view khác.
     */
    private void init() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.WHITE);
        gridPaint.setStrokeWidth(3f);
        gridPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Hàm setOnColorSelectedListener() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param listener tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    /**
     * Hàm getCurrentColor() tải hoặc lấy dữ liệu cần thiết cho màn hình.
     * Dữ liệu có thể đến từ Firebase, Intent, danh sách nội bộ hoặc API bên ngoài rồi được đưa lên giao diện.
     */
    public int getCurrentColor() {
        return currentColor;
    }

    /**
     * Hàm setCurrentColor() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param color tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setCurrentColor(int color) {
        this.currentColor = color;
        if (innerRadius > 0 && ringThickness > 0) {
            updateSelectorFromColor();
        }
        invalidate();
    }

    /**
     * Hàm updateSelectorFromColor() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updateSelectorFromColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(currentColor, hsv);
        float hue = hsv[0];
        float sat = hsv[1];

        float shiftedAngle = hue + 15f;
        if (shiftedAngle >= 360) shiftedAngle -= 360;
        int sliceIndex = (int) (shiftedAngle / 30f);

        int ringIndex = Math.round(sat * 5f) - 1;
        if (ringIndex < 0) ringIndex = 0;
        if (ringIndex > 4) ringIndex = 4;

        float snapAngleRad = (float) Math.toRadians(sliceIndex * 30f);
        float snapDist = innerRadius + ringThickness * (ringIndex + 0.5f);
        selX = cx + (float) Math.cos(snapAngleRad) * snapDist;
        selY = cy + (float) Math.sin(snapAngleRad) * snapDist;
    }

    @Override
    /**
     * Hàm onSizeChanged() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param w tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param h tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param oldw tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param oldh tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2f;
        cy = h / 2f;
        radius = Math.min(cx, cy) - 20;
        innerRadius = radius * 0.35f;
        ringThickness = (radius - innerRadius) / 5f;

        updateSelectorFromColor();
    }

    @Override
    /**
     * Hàm onDraw() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param canvas tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onDraw(Canvas canvas) {
        // Draw 5 rings x 12 slices
        arcPaint.setStrokeWidth(ringThickness);
        for (int r = 0; r < 5; r++) {
            float rCenter = innerRadius + ringThickness * (r + 0.5f);
            android.graphics.RectF oval = new android.graphics.RectF(cx - rCenter, cy - rCenter, cx + rCenter, cy + rCenter);
            float sat = (r + 1) / 5f;

            for (int i = 0; i < 12; i++) {
                float hue = i * 30f;
                int color = Color.HSVToColor(new float[]{hue, sat, 1f});
                arcPaint.setColor(color);
                canvas.drawArc(oval, i * 30f - 15f, 30.5f, false, arcPaint);
            }
        }

        // Draw white grid lines (concentric)
        for (int r = 0; r <= 5; r++) {
            float rBoundary = innerRadius + ringThickness * r;
            canvas.drawCircle(cx, cy, rBoundary, gridPaint);
        }

        // Draw white grid lines (radial)
        for (int i = 0; i < 12; i++) {
            float angleRad = (float) Math.toRadians(i * 30f - 15f);
            float startX = cx + (float) Math.cos(angleRad) * innerRadius;
            float startY = cy + (float) Math.sin(angleRad) * innerRadius;
            float endX = cx + (float) Math.cos(angleRad) * radius;
            float endY = cy + (float) Math.sin(angleRad) * radius;
            canvas.drawLine(startX, startY, endX, endY, gridPaint);
        }

        // Draw pipette selector
        android.graphics.drawable.Drawable pipette = androidx.core.content.ContextCompat.getDrawable(getContext(), com.example.appdraw.R.drawable.ic_pipette);
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (pipette != null) {
            int w = pipette.getIntrinsicWidth();
            int h = pipette.getIntrinsicHeight();
            int offset = 4;
            pipette.setBounds((int)selX - offset, (int)selY - h + offset, (int)selX + w - offset, (int)selY + offset);
            pipette.draw(canvas);
        }
    }

    @Override
    /**
     * Hàm onTouchEvent() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX() - cx;
            float y = event.getY() - cy;
            float dist = (float) Math.sqrt(x * x + y * y);

            if (dist < innerRadius) dist = innerRadius + 0.1f;
            if (dist > radius - 0.1f) dist = radius - 0.1f;

            float angle = (float) Math.toDegrees(Math.atan2(y, x));
            if (angle < 0) angle += 360;

            float shiftedAngle = angle + 15f;
            if (shiftedAngle >= 360) shiftedAngle -= 360;
            int sliceIndex = (int) (shiftedAngle / 30f);
            float hue = sliceIndex * 30f;

            int ringIndex = (int) ((dist - innerRadius) / ringThickness);
            if (ringIndex < 0) ringIndex = 0;
            if (ringIndex > 4) ringIndex = 4;
            float sat = (ringIndex + 1) / 5f;

            currentColor = Color.HSVToColor(new float[]{hue, sat, 1f});

            float snapAngleRad = (float) Math.toRadians(sliceIndex * 30f);
            float snapDist = innerRadius + ringThickness * (ringIndex + 0.5f);
            selX = cx + (float) Math.cos(snapAngleRad) * snapDist;
            selY = cy + (float) Math.sin(snapAngleRad) * snapDist;

            // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
            if (listener != null) listener.onColorSelected(currentColor);
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}

