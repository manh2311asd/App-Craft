package com.example.appdraw.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
/**
 * Lớp ColorWheelView thuộc module chính của ứng dụng App Draw.
 * Nhiệm vụ chính của lớp là quản lý dữ liệu, trạng thái giao diện và các thao tác xử lý
 * liên quan đến màn hình hoặc thành phần được khai báo trong file ColorWheelView.java.
 * Các phần bên trong lớp có thể kết nối với Firebase, RecyclerView, Intent, Glide hoặc API bên ngoài
 * tùy theo chức năng cụ thể của màn hình trong ứng dụng.
 */
public class ColorWheelView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Biến `selectedColor` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private int selectedColor = Color.BLACK;
    /**
     * Biến `selectedPoint` lưu dữ liệu/trạng thái quan trọng kiểu PointF, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
    private PointF selectedPoint = null;
    /**
     * Biến `listener` lưu danh sách dữ liệu lấy từ Firebase, Intent hoặc dữ liệu người dùng nhập, sau đó dùng để hiển thị nhiều phần tử trên giao diện.
     */
    private OnColorSelectedListener listener;

    /**
     * Biến `NUM_RINGS` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private static final int NUM_RINGS = 8;
    /**
     * Biến `NUM_SEGMENTS` lưu dữ liệu/trạng thái quan trọng kiểu int, được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private static final int NUM_SEGMENTS = 24;

    private final RectF rectF = new RectF();
    /**
     * Biến `hsv` lưu dữ liệu/trạng thái quan trọng kiểu float[], được sử dụng trong các bước xử lý và hiển thị của lớp.
     */
    private final float[] hsv = new float[3];

/**
 * Interface OnColorSelectedListener thuộc module chính của ứng dụng App Draw.
 * Interface này định nghĩa hợp đồng xử lý sự kiện/callback để các Activity, Fragment hoặc Adapter
 * có thể giao tiếp với nhau mà không phụ thuộc trực tiếp vào chi tiết triển khai.
 * File liên kết: ColorWheelView.java.
 */
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    /**
     * Hàm ColorWheelView() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param context tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param attrs tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public ColorWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setStrokeWidth(3f);
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
     * Hàm setSelectedColor() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param color tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public void setSelectedColor(int color) {
        this.selectedColor = color;
        Color.colorToHSV(color, hsv);
        updatePointFromColor();
        invalidate();
    }

    /**
     * Hàm updatePointFromColor() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    private void updatePointFromColor() {
        // Reverse calculation: HSV to XY
        post(() -> {
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            float maxRadius = Math.min(centerX, centerY) * 0.9f;
            float innerRadius = maxRadius * 0.2f;
            float ringWidth = (maxRadius - innerRadius) / NUM_RINGS;

            float angle = hsv[0];
            float saturation = hsv[1];
            
            // ringIndex calculation: saturation = 1.0 - ringIndex/NUM_RINGS
            // ringIndex = (1.0 - saturation) * NUM_RINGS
            float ringIndex = (1.0f - saturation) * NUM_RINGS;
            float dist = maxRadius - (ringIndex * ringWidth) - (ringWidth / 2f);

            double rad = Math.toRadians(angle);
            selectedPoint = new PointF(
                (float) (centerX + dist * Math.cos(rad)),
                (float) (centerY + dist * Math.sin(rad))
            );
            invalidate();
        });
    }

    @Override
    /**
     * Hàm onDraw() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param canvas tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    protected void onDraw(Canvas canvas) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float maxRadius = Math.min(centerX, centerY) * 0.9f;
        float innerRadius = maxRadius * 0.2f;
        float ringWidth = (maxRadius - innerRadius) / NUM_RINGS;

        for (int ring = 0; ring < NUM_RINGS; ring++) {
            float rOuter = maxRadius - (ring * ringWidth);
            float rInner = rOuter - ringWidth;
            float saturation = 1.0f - ((float) ring / NUM_RINGS);
            
            for (int seg = 0; seg < NUM_SEGMENTS; seg++) {
                float startAngle = seg * (360f / NUM_SEGMENTS);
                float sweepAngle = 360f / NUM_SEGMENTS;
                
                int color = Color.HSVToColor(new float[]{startAngle, saturation, 1.0f});
                paint.setColor(color);
                paint.setStyle(Paint.Style.FILL);

                Path path = new Path();
                rectF.set(centerX - rOuter, centerY - rOuter, centerX + rOuter, centerY + rOuter);
                path.arcTo(rectF, startAngle, sweepAngle);
                
                rectF.set(centerX - rInner, centerY - rInner, centerX + rInner, centerY + rInner);
                path.arcTo(rectF, startAngle + sweepAngle, -sweepAngle);
                path.close();
                
                canvas.drawPath(path, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(1.5f);
                canvas.drawPath(path, paint);
            }
        }

        // Draw the indicator (Eyedropper/Pen icon)
        // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
        if (selectedPoint != null) {
            drawIndicator(canvas, selectedPoint.x, selectedPoint.y);
        }
    }

    /**
     * Hàm drawIndicator() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param canvas tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param x tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     * @param y tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    private void drawIndicator(Canvas canvas, float x, float y) {
        // Draw a simple "Eyedropper" or "Pen" shape
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(45); // Tilt it like in the image

        // Outer white glow/border
        indicatorPaint.setColor(Color.WHITE);
        indicatorPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, 18, indicatorPaint);
        
        // Inner black border
        indicatorPaint.setColor(Color.BLACK);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        indicatorPaint.setStrokeWidth(2f);
        canvas.drawCircle(0, 0, 18, indicatorPaint);

        // Selected color in the center
        indicatorPaint.setColor(selectedColor);
        indicatorPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, 12, indicatorPaint);
        
        // Draw a small "tip" to make it look more like a pen/eyedropper
        Path tip = new Path();
        tip.moveTo(18, 0);
        tip.lineTo(28, 0);
        tip.lineTo(20, 5);
        tip.close();
        indicatorPaint.setColor(Color.BLACK);
        canvas.drawPath(tip, indicatorPaint);

        canvas.restore();
    }

    @Override
    /**
     * Hàm onTouchEvent() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     * @param event tham số truyền vào hàm, cung cấp dữ liệu hoặc ngữ cảnh cần thiết cho bước xử lý tương ứng.
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            float centerX = getWidth() / 2f;
            float centerY = getHeight() / 2f;
            
            double dx = x - centerX;
            double dy = y - centerY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            float maxRadius = Math.min(centerX, centerY) * 0.9f;
            float innerRadius = maxRadius * 0.2f;

            if (dist >= innerRadius && dist <= maxRadius) {
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) angle += 360;
                
                float ringWidth = (maxRadius - innerRadius) / NUM_RINGS;
                int ringIndex = (int) ((maxRadius - dist) / ringWidth);
                if (ringIndex >= NUM_RINGS) ringIndex = NUM_RINGS - 1;
                
                float saturation = 1.0f - ((float) ringIndex / NUM_RINGS);
                int color = Color.HSVToColor(new float[]{(float) angle, saturation, 1.0f});
                
                selectedColor = color;
                selectedPoint = new PointF(x, y);
                invalidate();
                
                // Kiểm tra null/rỗng/tồn tại giúp tránh lỗi NullPointerException và xử lý trường hợp dữ liệu chưa có hoặc người dùng nhập thiếu.
                if (listener != null) {
                    listener.onColorSelected(color);
                }
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    /**
     * Hàm performClick() thực hiện một phần xử lý trong luồng chức năng của lớp OnColorSelectedListener.
     * Comment này mô tả vai trò tổng quát để người đọc dễ theo dõi khi kết hợp với tên hàm và phần code bên dưới.
     */
    public boolean performClick() {
        return super.performClick();
    }
}
